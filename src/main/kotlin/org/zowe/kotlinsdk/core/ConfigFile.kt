/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.core

import com.fasterxml.jackson.annotation.JsonProperty
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Paths
import kotlin.io.path.pathString
import com.fasterxml.jackson.module.kotlin.readValue

val CURRENT_DIR: String = Paths.get("").toAbsolutePath().pathString

/** Config type representation. Maybe either a user config type, or a team config type */
enum class ConfigType {
  USER_CONFIG,
  TEAM_CONFIG
}

/**
 * Profile datatype is used by ConfigFile to return Profile Data along with metadata,
 * such as the profile [name] and [missingSecureProps]
 * Based on https://github.com/zowe/zowe-client-python-sdk/blob/main/src/core/zowe/core_for_zowe_sdk/config_file.py
 */
data class Profile(
  val data: Map<String, Any> = mapOf(),
  val name: String = "",
  val missingSecureProps: List<String> = listOf()
)

/** zowe.config.json in JSONC format, parsed to a Kotlin class */
data class ZoweConfigJsonc(
  @field:JsonProperty("\$schema")
  val schema: String? = null,
  val profiles: Map<String, Any>? = null,
  val defaults: Map<String, String>? = null
)

/**
 * Class used to represent a single config file.
 * Mainly it will have the following details:
 *    1. Type ("User Config" or "Team Config")
 *       -------
 *       User Configs override Team Configs.
 *       User Configs are used to have personalised config details
 *       that the user don't want to have in the Team Config.
 *    2. Directory in which the file is located.
 *    3. Name (excluding .config.json or .config.user.json)
 *    4. Contents of the file.
 *       4.1 Profiles
 *       4.2 Defaults
 *       4.3 Schema Property
 *    5. Secure Properties associated with the file.
 * Based on https://github.com/zowe/zowe-client-python-sdk/blob/main/src/core/zowe/core_for_zowe_sdk/config_file.py
 */
data class ConfigFile(
  val type: ConfigType,
  val name: String,
  private var _location: String? = null,
  private var _profiles: Map<String, Any>? = null,
  private var _defaults: Map<String, String>? = null,
  private var _schemaPath: String? = null,
  private var _secureProps: Map<String, Any>? = null,
  private var _jsonc: ZoweConfigJsonc? = null,
  private val missingSecureProps: MutableList<String> = mutableListOf()
) {
  private val logger = LoggerFactory.getLogger(javaClass)

  val fileName: String
    get() = when(type) {
      ConfigType.TEAM_CONFIG -> "$name.config.json"
      ConfigType.USER_CONFIG -> "$name.config.user.json"
    }

  var location: String?
    get() = _location
    set(dirname) {
      _location = if (dirname != null) {
        val dir = File(dirname)
        if (dir.isDirectory) {
          dirname
        } else {
          logger.error("given path $dirname is not valid")
          throw FileNotFoundException("given path $dirname is not valid")
        }
      } else {
        null
      }
    }

  val filePath: String?
    get() = location?.let { File(it, fileName).path }

  var profiles: Map<String, Any>?
    get() = _profiles
    private set(value) {
      _profiles = value
    }

  var defaults: Map<String, String>?
    get() = _defaults
    private set(value) {
      _defaults = value
    }

  var schemaPath: String?
    get() = _schemaPath
    private set(value) {
      _schemaPath = value
    }

  var secureProps: Map<String, Any>?
    get() = _secureProps
    private set(value) {
      _secureProps = value
    }

  var jsonc: ZoweConfigJsonc?
    get() = _jsonc
    private set(value) {
      _jsonc = value
    }

  /** Suppress warnings in config files. "false" = warnings are not shown (default = "true") */
  var suppressConfigFileWarnings: Boolean = true

  /**
   * Auto-discover Zowe Team Config files by going up the path from current working directory.
   * Sets path if it finds the config directory, raises an Exception otherwise
   */
  private fun autoDiscoverConfigDir() {
    var currentDir = CURRENT_DIR

    while (true) {
      if (File(currentDir, fileName).isFile) {
        location = currentDir
        return
      }

      // Check if we have arrived at the root directory
      val parentDir = File(currentDir).parent
      if (currentDir == parentDir) {
        break
      }

      currentDir = parentDir ?: break
    }

    throw FileNotFoundException("Could not find the file $fileName")
  }

  /** Get the $schema_property from the config and load the schema */
  private fun validateSchema() {
    schemaPath
      ?.let {
        validateConfigJson(jsonc as Any, it, location ?: "")
      }
      ?: run {
        if (!suppressConfigFileWarnings) {
          logger.warn("Could not find \$schema property")
        }
      }
  }

  /** Inject secure properties that have been loaded from the vault into the profiles object */
  private fun loadSecureProperties() {
    secureProps = (CredentialManager.secureProps[filePath ?: ""] as? Map<String, Any>) ?: mapOf()
    for ((key, value) in secureProps ?: mapOf()) {
      val segments = key.split(".")
        .filterIndexed { index, _ -> index % 2 == 1 }
        .toMutableList()

      var profilesObj: Any? = profiles
      val propertyName = segments.removeLastOrNull() ?: continue

      for ((i, profileName) in segments.withIndex()) {
        if (profilesObj == null || profilesObj !is Map<*, *>) {
          break
        }

        if (profileName in profilesObj) {
          profilesObj = profilesObj[profileName]

          if (profilesObj !is Map<*, *>) {
            break
          }

          if (i == segments.size - 1) {
            @Suppress("UNCHECKED_CAST")
            val mutableProfilesObj = profilesObj as MutableMap<String, Any>

            val properties = mutableProfilesObj.getOrPut("properties") {
              mutableMapOf<String, Any>()
            } as MutableMap<String, Any>

            properties[propertyName] = value
          }
        } else {
          break
        }
      }
    }
  }

  /**
   * Return a profile name from the given profile type as defined in the team config profile
   * @param profileType the type of the profile
   * @return the exact profile name of the profile to load from the mentioned type
   */
  private fun getProfileNameFromProfileType(profileType: String): String {
    // Try to get the profile name from defaults
    val nonNullDefaults = defaults ?: mapOf()
    if (nonNullDefaults.keys.contains(profileType)) {
      return nonNullDefaults[profileType]
        ?: throw Exception("'defaults' value for '$profileType' must not be null")
    } else {
      if (!suppressConfigFileWarnings) {
        logger.warn("Given profile type '$profileType' has no default profile name")
      }
    }

    // Ensure profiles exist before iterating
    val nonNullProfiles = profiles ?: run {
      val errorNoProfilesMsg = "No profiles found in the configuration"
      val errorNoProfilesException = Exception(errorNoProfilesMsg)
      logger.error(errorNoProfilesMsg, errorNoProfilesException)
      throw errorNoProfilesException
    }

    // Iterate through the profiles and check if profile is found
    return nonNullProfiles.entries
      .find { (profileName, profileDescription) ->
        (profileDescription as? Map<String, Any>)
          ?.get("type")
          ?.let { currProfileType -> currProfileType == profileType }
          ?: run {
            if (!suppressConfigFileWarnings) {
              logger.warn("Profile '$profileName' has no attribute 'type'")
            }
            false
          }
      }
      ?.key
      ?: run {
        // If no profile with matching type found, we raise an exception
        val profileNotFoundMsg = "No profile with matching profile type '$profileType' is found"
        val profileNotFoundException = Exception(profileNotFoundMsg)
        logger.error(profileNotFoundMsg, profileNotFoundException)
        throw profileNotFoundException
      }
  }

  /**
   * Load profile properties given profile name including secure properties.
   * Load exact profile properties (without prepopulated fields from base profile)
   * from the profile dict and populate fields from the secure credentials storage
   * @param profileName the name of the profile
   * @return map, containing profile properties
   */
  private fun loadProfileProperties(profileName: String): Map<String, Any> {
    var props: Map<String, Any> = mapOf()
    val profileNameSegments = profileName.split(".").toMutableList()
    val secureFields: MutableList<String> = mutableListOf()

    // Prevent null issue
    val nonNullProfiles = profiles ?: run { throw Exception("Profiles have not been initialized") }

    while (profileNameSegments.isNotEmpty()) {
      val currentProfileName = profileNameSegments.joinToString(".")
      val profile = findProfile(currentProfileName, nonNullProfiles)

      if (profile != null) {
        // Merge properties: profile properties first, then existing props (props override)
        props = (profile["properties"] as? Map<String, Any> ?: mapOf()) + props
        // Add secure fields
        secureFields.addAll(profile["secure"] as? List<String> ?: listOf())
      } else {
        if (!suppressConfigFileWarnings) {
          logger.warn("Profile $currentProfileName not found")
        }
      }

      profileNameSegments.removeAt(profileNameSegments.size - 1)
    }

    return props
  }

  /**
   * Extract secure properties from the profiles object for storage in the vault
   * @param profilesObj the profiles object from which secure properties are extracted
   * @param jsonPath the JSON path used as a base in the vault for storing secure properties
   * @return a map of secure properties keyed by JSON path in the vault
   */
  private fun extractSecureProperties(profilesObj: Map<String, Any>, jsonPath: String = "profiles"): Map<String, Any> {
    val secureProps = mutableMapOf<String, Any>()

    for ((key, value) in profilesObj) {
      val valueMap = value as? Map<String, Any> ?: continue
      val secureList = valueMap["secure"] as? List<String> ?: emptyList()
      val properties = valueMap["properties"] as? Map<String, Any> ?: emptyMap()

      for (propertyName in secureList) {
        properties[propertyName]?.let {
          secureProps["$jsonPath.$key.properties.$propertyName"] = it
        }
      }

      (valueMap["profiles"] as? Map<String, Any>)?.let { nestedProfiles ->
        secureProps
          .putAll(
            extractSecureProperties(nestedProfiles, "$jsonPath.$key.profiles")
          )
      }
    }

    return secureProps
  }

  /**
   * Initialize the class variable after setting filepath (or if not set, auto-discover the file)
   * @param shouldValidateSchema should the json schema be validated or not ("true" if validation is preferred, is a default)
   */
  fun initFromFile(shouldValidateSchema: Boolean = true) {
    if (filePath == null) {
      try {
        autoDiscoverConfigDir()
      } catch (_: FileNotFoundException) {}
    }

    val nonNullFilePath = filePath
      ?.let { if (File(it).isFile) it else null }
      ?: run {
        if (!suppressConfigFileWarnings) {
          logger.warn("Config file does not exist at $filePath")
        }
        return
      }

    val configJsonc: ZoweConfigJsonc = File(nonNullFilePath).inputStream()
      .use { input -> jsoncMapper.readValue(input, ZoweConfigJsonc::class.java) }

    profiles = configJsonc.profiles ?: emptyMap()
    schemaPath = configJsonc.schema
    defaults = configJsonc.defaults ?: emptyMap()
    jsonc = configJsonc

    if (shouldValidateSchema) {
      validateSchema()
    }

    CredentialManager.loadSecureProps()
    loadSecureProperties()
  }

  /**
   * Load the schema properties in a sorted order according to the priority
   * @param cwd current working directory
   * @return properties from schema
   */
  fun schemaList(cwd: String? = null): List<Map<String, Any>> {
    val schemaJsonAsText = try {
      retrieveSchemaJsonAsText(
        schemaPath ?: "",
        location ?: cwd ?: ""
      )
    } catch (e: SchemaNotResolvedException) {
      if (!suppressConfigFileWarnings) {
        logger.warn(e.message)
      }
      return listOf()
    }

    val schemaJson: Map<String, Any> = jsoncMapper.readValue(schemaJsonAsText)
    var profileProps: Map<String, Any> = mapOf()

    try {
      val properties = schemaJson["properties"] as? Map<String, Any> ?: return emptyList()
      val profiles = properties["profiles"] as? Map<String, Any> ?: return emptyList()
      val patternProperties = profiles["patternProperties"] as? Map<String, Any> ?: return emptyList()
      val pattern = patternProperties["^\\S*$"] as? Map<String, Any> ?: return emptyList()
      val allOf = pattern["allOf"] as? List<*> ?: return emptyList()

      for (props in allOf) {
          val propsMap = props as? Map<String, Any> ?: continue
          var thenMap = propsMap["then"] as? Map<String, Any> ?: continue

          // Navigate through nested "properties"
          while (thenMap.containsKey("properties")) {
              val nextProps = thenMap["properties"] as? Map<String, Any> ?: break
              thenMap = nextProps
              profileProps = nextProps
          }
      }
    } catch (_: Exception) {
        return listOf()
    }

    return if (profileProps.isNotEmpty()) listOf(profileProps) else listOf()
  }

  /**
   * Load given profile including secure properties and excluding values from base profile
   * @param profileName name of the profile (could be null)
   * @param profileType type of the profile
   * @param shouldValidateSchema true if validation is preferred
   * @return the requested [Profile]
   */
  fun getProfile(profileName: String? = null, profileType: String, shouldValidateSchema: Boolean = true): Profile {
    if (profiles == null) {
      initFromFile(shouldValidateSchema)
    }

    val resolvedProfileName = profileName ?: getProfileNameFromProfileType(profileType)
    val resolvedProfileProps = loadProfileProperties(resolvedProfileName)

    return Profile(resolvedProfileProps, resolvedProfileName, missingSecureProps)
  }

  /**
   * Find a profile at a specified location from within a set of nested profiles
   * @param path the location to look for the profile (separated by dots)
   * @param profiles a dict of nested profiles
   * @return the profile object that was found, null otherwise
   */
  fun findProfile(path: String, profiles: Map<String, Any>): Map<String, Any>? {
    val segments = path.split(".").toMutableList()

    for ((profileName, profileData) in profiles) {
      if (profileData !is Map<*, *>) { // Ensure profileData is a Map
        if (!suppressConfigFileWarnings) {
          logger.warn("Invalid profile passed when schema validation is off")
        }
      } else {
        if (segments[0] == profileName) {
          return if (segments.size == 1) {
            profileData as Map<String, Any> // Ensured to be Map<String, Any>
          } else {
            val nestedProfiles = profileData["profiles"]
            if (nestedProfiles is Map<*, *>) {
              // Recursive call
              findProfile(
                segments.subList(1, segments.size).joinToString("."),
                nestedProfiles as Map<String, Any>
              )
            } else {
              null
            }
          }
        }
      }
    }

    return null
  }

  /*
    def __set_or_create_nested_profile(self, profile_name: str, profile_data: dict[str, Any]) -> None:
        """
        Set or create a nested profile within the profiles structure.

        Parameters
        ----------
        profile_name : str
            The dot-separated path name of the profile to set or create.
        profile_data : dict[str, Any]
            The data to set in the specified profile.
        """
        path = self.get_profile_path_from_name(profile_name)
        keys = path.split(".")[1:]
        nested_profiles = self.profiles
        if not isinstance(nested_profiles, dict):
            return
        for key in keys:
            nested_profiles = nested_profiles.setdefault(key, {})
        nested_profiles.update(profile_data)

    def __is_secure(self, json_path: str, property_name: str) -> bool:
        """
        Determine if a property should be stored securely based on its presence in the secure list.

        Parameters
        ----------
        json_path : str
            The JSON path of the property within the profiles structure.
        property_name : str
            The name of the property to check for secure storage requirements.

        Returns
        -------
        bool
            True if the property is listed to be stored securely, False otherwise.
        """
        profile = self.find_profile(json_path, self.profiles)
        if profile and profile.get("secure"):
            return property_name in profile["secure"]
        return False

    def set_property(self, json_path: str, value: str, secure: Optional[bool] = None) -> None:
        """
        Set a property in the profile, storing it securely if necessary.

        Parameters
        ----------
        json_path: str
            The JSON path of the property to set.
        value: str
            The value to be set for the property.
        secure: Optional[bool]
            If True, the property will be stored securely. Default is None.
        """
        if self.profiles is None:
            self.init_from_file()

        # Checking whether the property should be stored securely or in plain text
        property_name = json_path.split(".")[-1]
        profile_name = self.get_profile_name_from_path(json_path)
        # check if the property is already secure
        is_property_secure = self.__is_secure(profile_name, property_name)
        is_secure = secure if secure is not None else is_property_secure

        current_profile = self.find_profile(profile_name, self.profiles) or {}

        if not isinstance(current_profile, dict):
            current_profile = {}

        current_properties = current_profile.setdefault("properties", {})
        current_secure = current_profile.setdefault("secure", [])
        current_properties[property_name] = value
        if is_secure and not is_property_secure:
            current_secure.append(property_name)
        elif not is_secure and is_property_secure:
            current_secure.remove(property_name)

        current_profile["properties"] = current_properties
        current_profile["secure"] = current_secure
        self.__set_or_create_nested_profile(profile_name, current_profile)

    def set_profile(self, profile_path: str, profile_data: dict[str, Any]) -> None:
        """
        Set a profile in the config file.

        Parameters
        ----------
        profile_path: str
            The path of the profile to be set. eg: profiles.zosmf
        profile_data: dict[str, Any]
            The data to be set for the profile.
        """
        if self.profiles is None:
            self.init_from_file()
        profile_name = self.get_profile_name_from_path(profile_path)
        if "secure" in profile_data:
            # Checking if the profile has a 'secure' field with values
            secure_fields = profile_data["secure"]
            current_profile = self.find_profile(profile_name, self.profiles) or {}
            existing_secure_fields = current_profile.get("secure", [])
            new_secure_fields = [field for field in secure_fields if field not in existing_secure_fields]

            # Updating the 'secure' field of the profile with the combined list of secure fields
            profile_data["secure"] = existing_secure_fields + new_secure_fields
            # If a field is provided in the 'secure' list and its value exists in 'profile_data', remove it
            profile_data["properties"] = {
                **current_profile.get("properties", {}),
                **profile_data.get("properties", {}),
            }
        self.__set_or_create_nested_profile(profile_name, profile_data)

    def save(self, update_secure_props: Optional[bool] = True) -> None:
        """
        Save the config file to disk. and secure props to vault.

        Parameters
        ----------
        update_secure_props: Optional[bool]
            If True, the secure properties will be stored in the vault. Default is True.

        Raises
        ------
        ValueError
            Filepath must be set and valid.
        """
        if not isinstance(self.filepath, str):
            raise ValueError("Filepath is not set or invalid")

        # Updating the config file with any changes
        if not any(self.profiles.values()):
            return

        profiles_temp = deepcopy(self.profiles)
        secure_props = self.__extract_secure_properties(profiles_temp)

        CredentialManager.secure_props[self.filepath] = secure_props
        with open(self.filepath, "w") as file:
            self.jsonc["profiles"] = profiles_temp
            commentjson.dump(self.jsonc, file, indent=4)
        if update_secure_props:
            CredentialManager.save_secure_props()

    def get_profile_name_from_path(self, path: str) -> str:
        """
        Get the name of the profile from the given path.

        Parameters
        ----------
        path: str
            The location to look for the profile

        Returns
        -------
        str
            Returns the profile name
        """
        segments = path.split(".")
        profile_name = ".".join(segments[i] for i in range(1, len(segments), 2) if segments[i - 1] != "properties")
        return profile_name

    def get_profile_path_from_name(self, short_path: str) -> str:
        """
        Get the path of the profile from the given name.

        Parameters
        ----------
        short_path: str
            Partial path of profile

        Returns
        -------
        str
            Returns the full profile path
        """
        return re.sub(r"(^|\.)", r"\1profiles.", short_path)
   */
}

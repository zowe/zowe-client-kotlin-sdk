/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.config

import kotlinx.serialization.json.*
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Paths
import kotlin.collections.get
import kotlin.collections.iterator
import kotlin.io.path.pathString

const val GLOBAL_CONFIG_NAME = "zowe"
const val BASE_PROFILE = "base"

val HOME: String = System.getProperty("user.home")
val GLOBAL_CONFIG_LOCATION: String = Paths.get(HOME, ".zowe").pathString
val CURRENT_DIR: String = Paths.get("").toAbsolutePath().pathString

/** Config type representation. Maybe either a user config type, or a team config type */
enum class ConfigType {
  USER_CONFIG,
  TEAM_CONFIG,
  MERGED_CONFIG
}

/** zowe.config.json in JSONC format, parsed to a Kotlin class */
data class ZoweConfigJsonc(
  val schema: String? = null,
  var profiles: Map<String, Any>? = null,
  val defaults: Map<String, String>? = null
) {
  fun toJsonElement(): JsonElement = buildJsonObject {
    schema?.let { put($$"$schema", it) }
    profiles?.let { put("profiles", it.toJsonElement()) }
    defaults?.let { put("defaults", JsonObject(it.mapValues { (_, v) -> JsonPrimitive(v) })) }
  }

  companion object {
    fun fromJsonText(text: String): ZoweConfigJsonc {
      val obj = jsoncJson.parseToJsonElement(text).jsonObject
      return ZoweConfigJsonc(
        schema = obj[$$"$schema"]?.jsonPrimitive?.contentOrNull,
        profiles = obj["profiles"]?.toAny() as? Map<String, Any>,
        defaults = obj["defaults"]?.jsonObject?.mapValues { (_, v) -> v.jsonPrimitive.content }
      )
    }
  }
}

/**
 * Class used to represent a single config file.
 * Mainly it will have the following details:
 *    1. Type ("User Config" or "Team Config")
 *       -------
 *       User Configs override Team Configs.
 *       User Configs are used to have personalized config details
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
data class ZoweConfigFile(
  val type: ConfigType,
  val name: String,
  private var _location: String? = null,
  private var _profiles: Map<String, Any>? = null,
  private var _defaults: Map<String, String>? = null,
  private var _schemaPath: String? = null,
  private var _secureProps: Map<String, String>? = null,
  private var _jsonc: ZoweConfigJsonc? = null,
  private val missingSecureProps: MutableList<String> = mutableListOf()
) {
  private val logger = LoggerFactory.getLogger(javaClass)

  val fileName: String
    get() = when(type) {
      ConfigType.TEAM_CONFIG -> "$name.config.json"
      ConfigType.USER_CONFIG -> "$name.config.user.json"
      else -> name
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

  var secureProps: Map<String, String>?
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
  @Suppress("UNCHECKED_CAST")
  private fun loadSecureProperties() {
    secureProps = ZoweCredentialManager.secureProps[filePath ?: ""] ?: mapOf()
    for ((key, value) in secureProps ?: mapOf()) {
      val segments = key.split(".")
        .filterIndexed { index, _ -> index % 2 == 1 }
        .toMutableList()

      val propertyName = segments.removeLastOrNull() ?: continue

      // Move through each segment to reach the target nested profile
      val targetProfile = segments.fold(profiles as Any?) { profilesMap, profileName ->
        (profilesMap as? Map<*, *>)?.get(profileName)
      } as? MutableMap<String, Any> ?: continue

      val properties = targetProfile.getOrPut("properties") {
        mutableMapOf<String, Any>()
      } as MutableMap<String, Any>

      properties[propertyName] = value
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
        @Suppress("UNCHECKED_CAST")
        (profileDescription as? Map<String, Any>)
          ?.get("type")
          ?.let { currProfileType -> currProfileType == profileType }
          ?: run {
            if (!suppressConfigFileWarnings) {
              logger.warn("ZoweProfile '$profileName' has no attribute 'type'")
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
  @Suppress("UNCHECKED_CAST")
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
          logger.warn("ZoweProfile $currentProfileName not found")
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
  @Suppress("UNCHECKED_CAST")
  private fun extractSecureProperties(profilesObj: Map<String, Any>, jsonPath: String = "profiles"): MutableMap<String, String> {
    val secureProps = mutableMapOf<String, String>()

    for ((key, value) in profilesObj) {
      val valueMap = value as? MutableMap<String, Any> ?: continue
      val secureList = valueMap["secure"] as? List<String> ?: listOf()
      val properties = valueMap["properties"] as? MutableMap<String, Any> ?: mutableMapOf()

      for (propertyName in secureList) {
        properties.remove(propertyName)?.let {
          secureProps["$jsonPath.$key.properties.$propertyName"] = it.toString()
        }
      }

      valueMap["properties"] = properties

      (valueMap["profiles"] as? Map<String, Any>)?.let { nestedProfiles ->
        secureProps.putAll(
          extractSecureProperties(nestedProfiles, "$jsonPath.$key.profiles")
        )
      }
    }

    return secureProps
  }

  /**
   * Get the path of the profile from the given name
   * @param shortPath the partial path of a profile
   * @return the full profile path
   */
  private fun getProfilePathFromName(shortPath: String): String {
    return Regex("""(^|\.)""").replace(shortPath, "$1profiles.")
  }

  /**
   * Set or create a nested profile within the profiles structure
   * @param profileName the dot-separated path name of the profile to set or create
   * @param profileData the data to set in the specified profile
   */
  private fun setOrCreateNestedProfile(profileName: String, profileData: Map<String, Any>) {
    val path = getProfilePathFromName(profileName)
    val keys = path.split(".").drop(1)

    var currentMap = profiles as? MutableMap<String, Any> ?: return

    for (key in keys) {
      @Suppress("UNCHECKED_CAST")
      currentMap = currentMap.getOrPut(key) {
        mutableMapOf<String, Any>()
      } as MutableMap<String, Any>
    }

    currentMap.putAll(profileData)
  }

  /**
   * Determine if a property should be stored securely based on its presence in the secure list
   * @param jsonPath the JSON path of the property within the profiles structure
   * @param propertyName the name of the property to check for secure storage requirements
   * @return true if the property is listed to be stored securely, false otherwise
   */
  @Suppress("UNCHECKED_CAST")
  private fun isSecure(jsonPath: String, propertyName: String): Boolean {
    return (findProfile(jsonPath, profiles ?: mapOf())
      ?.get("secure") as? List<String>)
      ?.contains(propertyName)
      ?: false
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

    val configJsonc: ZoweConfigJsonc = ZoweConfigJsonc.fromJsonText(File(nonNullFilePath).readText(Charsets.UTF_8))

    profiles = configJsonc.profiles ?: emptyMap()
    schemaPath = configJsonc.schema
    defaults = configJsonc.defaults ?: emptyMap()
    jsonc = configJsonc

    if (shouldValidateSchema) {
      validateSchema()
    }

    ZoweCredentialManager.loadSecureProps()
    loadSecureProperties()
  }

  /**
   * Load the schema properties in a sorted order according to the priority
   * @param cwd current working directory
   * @return properties from schema
   */
  @Suppress("UNCHECKED_CAST")
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

    val schemaJson = jsoncJson.parseToJsonElement(schemaJsonAsText).toAny() as? Map<String, Any> ?: return listOf()
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
   * @return the requested [ZoweProfile]
   */
  fun getProfile(
    profileName: String? = null,
    profileType: String,
    shouldValidateSchema: Boolean = true
  ): ZoweProfile {
    if (profiles == null) {
      initFromFile(shouldValidateSchema)
    }

    val resolvedProfileName = profileName ?: getProfileNameFromProfileType(profileType)
    val resolvedProfileProps = loadProfileProperties(resolvedProfileName)

    return ZoweProfile(resolvedProfileProps, resolvedProfileName, missingSecureProps)
  }

  /**
   * Get the full name and type of all profiles in the config.
   * Names are dot-separated paths, e.g. "lpar1.zosmf", "lpar2.inner.base".
   * @param shouldValidateSchema true if validation is preferred
   * @return list of pairs where first is the full profile name and second is the profile type (null if not specified)
   */
  fun getProfilesNameAndType(shouldValidateSchema: Boolean = true): List<Pair<String, String?>> {
    if (profiles == null) {
      initFromFile(shouldValidateSchema)
    }
    return collectProfilesNameAndType(profiles ?: emptyMap(), prefix = "")
  }

  private fun collectProfilesNameAndType(
    profilesMap: Map<String, Any>,
    prefix: String
  ): List<Pair<String, String?>> {
    val result = mutableListOf<Pair<String, String?>>()
    for ((name, data) in profilesMap) {
      @Suppress("UNCHECKED_CAST")
      val profileData = data as? Map<String, Any> ?: continue
      val fullName = if (prefix.isEmpty()) name else "$prefix.$name"
      val type = profileData["type"] as? String
      result.add(fullName to type)
      @Suppress("UNCHECKED_CAST")
      val nested = profileData["profiles"] as? Map<String, Any>
      if (nested != null) {
        result.addAll(collectProfilesNameAndType(nested, fullName))
      }
    }
    return result
  }

  /**
   * Find a profile at a specified location from within a set of nested profiles
   * @param path the location to look for the profile (separated by dots)
   * @param profiles a dict of nested profiles
   * @return the profile object that was found, null otherwise
   */
  @Suppress("UNCHECKED_CAST")
  fun findProfile(path: String, profiles: Map<String, Any>): Map<String, Any>? {
    val segments = path.split(".").toMutableList()

    for ((profileName, profileData) in profiles) {
      if (profileData !is Map<*, *>) { // Ensure profileData is a Map
        if (!suppressConfigFileWarnings) {
          logger.warn("Invalid profile passed when schema validation is off")
        }
      } else if (segments[0] == profileName) {
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

    return null
  }

  /**
   * Get the name of the profile from the given path
   * @param path the location to look for the profile
   * @return the profile name
   */
  fun getProfileNameFromPath(path: String): String {
    val segments = path.split(".")
    return segments
      .filterIndexed { i, _ -> i % 2 == 1 && segments[i - 1] != "properties" }
      .joinToString(".")
  }

  /**
   * Set a property in the profile, storing it securely if necessary
   * @param jsonPath the JSON path of the property to set
   * @param value the value to set for the property
   * @param shouldBeSecure if true, the property will be stored securely,
   *                       default is null => will be calculated with isSecure function
   */
  @Suppress("UNCHECKED_CAST")
  fun setProperty(jsonPath: String, value: String, shouldBeSecure: Boolean? = null) {
    if (profiles == null) {
      initFromFile()
    }

    // Checking whether the property should be stored securely or in plain text
    val propertyName = jsonPath.split(".").last()
    val profileName = getProfileNameFromPath(jsonPath)

    // Check if the property is already secure
    val isPropertySecure = isSecure(profileName, propertyName)
    val isSecure = shouldBeSecure ?: isPropertySecure

    val profileMap = (findProfile(profileName, profiles ?: mapOf()) ?: mapOf()).toMutableMap()

    val currentProperties = (profileMap.getOrPut("properties") {
      mutableMapOf<String, Any>()
    } as MutableMap<String, Any>).toMutableMap()

    val currentSecure = (profileMap.getOrPut("secure") {
      mutableListOf<String>()
    } as List<String>).toMutableList()

    currentProperties[propertyName] = value

    when {
      isSecure && !isPropertySecure -> currentSecure.add(propertyName)
      !isSecure && isPropertySecure -> currentSecure.remove(propertyName)
    }

    profileMap["properties"] = currentProperties
    profileMap["secure"] = currentSecure
    setOrCreateNestedProfile(profileName, profileMap)
  }

  /**
   * Set a profile in the config file
   * @param profilePath the path of the profile to be set. eg: profiles.zosmf
   * @param profileData the data to be set for the profile
   */
  @Suppress("UNCHECKED_CAST")
  fun setProfile(profilePath: String, profileData: Map<String, Any>) {
    if (profiles == null) {
      initFromFile()
    }

    val profileName = getProfileNameFromPath(profilePath)
    val mutableProfileData = profileData.toMutableMap()

    if ("secure" in profileData) {
      // Checking if the profile has a 'secure' field with values
      val secureFields = profileData["secure"] as? List<String> ?: listOf()
      val currentProfile = findProfile(profileName, profiles ?: mapOf()) ?: mapOf()

      val existingSecureFields = currentProfile["secure"] as? List<String> ?: listOf()
      val newSecureFields = secureFields.filter { it !in existingSecureFields }

      // Updating the 'secure' field of the profile with the combined list of secure fields
      mutableProfileData["secure"] = existingSecureFields + newSecureFields

      // If a field is provided in the 'secure' list and its value exists in 'profile_data', remove it
      val currentProperties = currentProfile["properties"] as? Map<String, Any> ?: mapOf()
      val newProperties = profileData["properties"] as? Map<String, Any> ?: mapOf()

      mutableProfileData["properties"] = currentProperties + newProperties
    }

    setOrCreateNestedProfile(profileName, mutableProfileData)
  }

  /**
   * Save the config file to disk and secure props to vault
   * @param updateSecureProps If true, the secure properties will be stored in the vault. Default is true
   */
  fun save(updateSecureProps: Boolean = true) {
    val nonNullFilePath = filePath ?: throw Exception("File path is not set or invalid")

    // Updating the config file with any changes
    val nonNullProfiles = profiles ?: return
    if (nonNullProfiles.values.none { (it as? Map<*, *>)?.isNotEmpty() == true }) {
      return
    }

    val newProfiles = nonNullProfiles.toJsonElement().toAny() as MutableMap<String, Any>
    val secureProps = extractSecureProperties(newProfiles)

    ZoweCredentialManager.secureProps[nonNullFilePath] = secureProps

    jsonc?.profiles = newProfiles

    val nonNullJsonc = jsonc ?: throw Exception("Unable to find config JSON file")
    val jsonText = jsoncJson.encodeToString(JsonElement.serializer(), nonNullJsonc.toJsonElement())
    File(nonNullFilePath).writeText(jsonText)

    if (updateSecureProps) {
      ZoweCredentialManager.saveSecureProps()
    }
  }
}

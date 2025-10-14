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

import io.github.cdimascio.dotenv.dotenv
import org.slf4j.LoggerFactory
import java.io.FileNotFoundException

/**
 * Class used to manage profiles. ZoweProfile Manager contains the logic to merge the different properties of profiles
 * (from the Project Config and the Project User Config as well as the Global Config and Global User Config).
 * This class handles all the exceptions raised in the Config File to provide a smooth user experience
 * Based on https://github.com/zowe/zowe-client-python-sdk/blob/main/src/core/zowe/core_for_zowe_sdk/profile_manager.py
 * @property configAppName name of the app
 * @property showWarnings indicates whether warnings are shown
 */
class ZoweProfileManager(
  /** The name of the application as configured in the current instance */
  val configAppName: String = "zowe",
  var showWarnings: Boolean = true
) {
  companion object {
    // Collect Zowe environment variables from .env file
    val devEnv by lazy { dotenv { ignoreIfMissing = true } }

    /**
     * Map the env variables to the profile properties
     * @param zoweConfigFile a config file that contains the schema properties.
     * @param cwd path of current working directory.
     * @return a map containing profile properties from env variables (prop: value).
     */
    fun getEnv(zoweConfigFile: ZoweConfigFile, cwd: String? = null): Map<String, Any> {
      val props = zoweConfigFile.schemaList(cwd)
        .ifEmpty {
          // If the list is empty, return an empty map
          return mapOf()
        }
        .flatMap { it.entries }
        .associate { it.toPair() }

      // Collect .env variables
      val envVars: MutableMap<String, Any> = devEnv.entries()
        .associate { entry -> entry.key to (entry.value ?: "") }
        .toMutableMap()

      // Collect ZOWE_OPT environment variables
      val userEnv = System.getenv()
        .filterKeys { it.startsWith("ZOWE_OPT") }
        .mapKeys { (key, _) -> key.removePrefix("ZOWE_OPT_").lowercase() }

      for ((nextEnvVarName, nextEnvVarValue) in userEnv) {
        val words = nextEnvVarName.split("_")
        val propKey = if (words.size > 1) {
          words[0] + words[1].replaceFirstChar { it.uppercase() }
        } else {
          words[0]
        }

        if (propKey in props) {
          @Suppress("UNCHECKED_CAST")
          val propMap = props[propKey] as? Map<String, Any>
          val propType = propMap?.get("type") as? String

          envVars[propKey] = when (propType) {
            "number" -> nextEnvVarValue?.trimStart('-')?.toIntOrNull() ?: 0
            "boolean" -> nextEnvVarValue.lowercase() in listOf("true", "1", "yes")
            else -> nextEnvVarValue
          }
        }
      }

      return envVars
    }

    /**
     * Retrieve a profile from the configuration file, optionally validating the schema
     * @param zoweConfigFile the configuration file object which contains profiles
     * @param profileName the name of the profile to retrieve. If null, the method attempts to fetch the profile
     *                    based only on the type
     * @param profileType the type of the profile to retrieve
     * @param shouldValidateSchema whether to validate the profile against the schema present in the configuration file
     * @return [ZoweProfile] instance, containing the profile data, name, and any secure properties not found
     */
    fun getProfile(
      zoweConfigFile: ZoweConfigFile,
      profileName: String? = null,
      profileType: String,
      shouldValidateSchema: Boolean = true
    ): ZoweProfile {
      val logger = LoggerFactory.getLogger(this::class.java)

      return try {
        zoweConfigFile.getProfile(profileName, profileType, shouldValidateSchema)
      } catch (schemaNotResolvedException: SchemaNotResolvedException) {
        logger.error(schemaNotResolvedException.message, schemaNotResolvedException)
        throw schemaNotResolvedException
      } catch (jsonValidationException: JsonValidationException) {
        logger.error(jsonValidationException.message, jsonValidationException)
        throw jsonValidationException
      } catch (e: Exception) {
        logger.warn("ZoweProfile of type '$profileType' not found in file '${zoweConfigFile.fileName}'. Reason: ${e.message}. Returning empty profile instead")
        ZoweProfile()
      }
    }
  }

  private val logger = LoggerFactory.getLogger(javaClass)

  private val projectTeamConfig = ZoweConfigFile(ConfigType.TEAM_CONFIG, configAppName)
  private val projectUserConfig = ZoweConfigFile(ConfigType.USER_CONFIG, configAppName)
  private val globalTeamConfig = ZoweConfigFile(ConfigType.TEAM_CONFIG, GLOBAL_CONFIG_NAME)
  private val globalUserConfig = ZoweConfigFile(ConfigType.USER_CONFIG, GLOBAL_CONFIG_NAME)

  /**
   * The folder path where the Zowe z/OSMF Team Project Config files are stored.
   * On update, updates the Zowe z/OSMF User Project Config location as well
   */
  var teamConfigDir: String?
    get() = projectTeamConfig.location
    set(dirname) {
      projectTeamConfig.location = dirname
      projectUserConfig.location = dirname
    }

  /** The folder path where the Zowe z/OSMF User Project Config files are stored */
  var userConfigDir: String?
    get() = projectUserConfig.location
    set(dirname) {
      projectUserConfig.location = dirname
    }

  /** The filename for the Zowe z/OSMF Team Project Config */
  val teamConfigFileName: String = projectTeamConfig.name

  /** The full filepath for the Zowe z/OSMF Team Project Config file */
  val teamConfigFilePath: String? = projectTeamConfig.filePath

  init {
    try {
      globalTeamConfig.location = GLOBAL_CONFIG_LOCATION
    } catch (_: Exception) {
      logger.warn("Could not find Global Config Team Directory, please provide one")
    }

    try {
      globalUserConfig.location = GLOBAL_CONFIG_LOCATION
    } catch (_: Exception) {
      logger.warn("Could not find Global Config User Config Directory")
    }
  }

  /**
   * Perform a deep merge of two [String] to [Any] maps
   * @param base the base map to merge into
   * @param override the overriding map to merge
   * @return the resulting deep merged map
   */
  @Suppress("UNCHECKED_CAST")
  private fun deepMerge(base: Map<String, Any>, override: Map<String, Any>): MutableMap<String, Any> {
    val result = base.toMutableMap()
    for ((key, value) in override) {
      if (value is Map<*, *> && result[key] is Map<*, *>) {
        result[key] = deepMerge(
          result[key] as Map<String, Any>,
          value as Map<String, Any>
        )
      } else {
        result[key] = value
      }
    }
    return result
  }

  /**
   * Load connection details from a team config profile.
   * We will load properties from config files in the following order, from highest to lowest priority:
   * 1. Project User Config (./zowe.config.user.json)
   * 2. Project Config (./zowe.config.json)
   * 3. Global User Config (~/zowe.config.user.json)
   * 4. Global Config (~/zowe.config.json)
   * If [profileType] is not 'base', then we will load properties from both [profileType]
   * and base profiles and merge them together.
   * @param profileName the of the profile to load. If null, profiles are loaded based only on profile type
   * @param profileType the type of the profile to load, e.g. 'zosmf', 'zftp'.
   *                    If null, profiles are loaded based only on name
   * @param shouldCheckMissingProps flag to indicate whether to check for missing secure properties
   * @param shouldValidateSchema whether to validate the loaded profile against the schema defined in the configuration
   * @param shouldOverrideWithEnv if true, overrides profile properties with values from environment variables
   * @return a map containing the merged connection details from all relevant profiles
   */
  fun load(
    profileName: String? = null,
    profileType: String? = null,
    shouldCheckMissingProps: Boolean = true,
    shouldValidateSchema: Boolean = true,
    shouldOverrideWithEnv: Boolean = false
  ): Map<String, Any> {
    if (profileName == null && profileType == null) {
      val profileNameAndTypeNotProvidedMsg = "Failed to load a profile as both profile name and profile type are null"
      val profileNameAndTypeNotProvidedException = Exception(profileNameAndTypeNotProvidedMsg)
      logger.error(profileNameAndTypeNotProvidedMsg, profileNameAndTypeNotProvidedException)
      throw profileNameAndTypeNotProvidedException
    }

    val missingSecureProps = mutableListOf<String>() // track which secure props were not loaded

    val defaultsMerged = mutableMapOf<String, String>()
    var configNameNullable: String? = null
    var configSchema: String? = null
    var configSchemaDir: String? = null

    listOf(projectUserConfig, projectTeamConfig, globalUserConfig, globalTeamConfig)
      .forEach { configLayer ->
        if (configLayer.profiles == null) {
          try {
            configLayer.initFromFile(shouldValidateSchema)
          } catch (e: SecureProfileLoadException) {
            if (showWarnings) {
              logger.warn("Could not load secure properties for ${configLayer.filePath} due to exception: ${e.message}")
            }
          }
        }

        configLayer.defaults?.forEach { (name, value) ->
          defaultsMerged.putIfAbsent(name, value)
        }

        if (configNameNullable == null) {
          configNameNullable = configLayer.name
        }

        if (configSchema == null && configLayer.schemaPath != null) {
          configSchema = configLayer.schemaPath
          configSchemaDir = configLayer.location
        }
      }

    val configName = configNameNullable ?: throw Exception("Config name not resolved")

    val userProjectProfiles = projectUserConfig.profiles ?: mapOf()
    val teamProjectProfiles = projectTeamConfig.profiles ?: mapOf()
    val mergedProjectProfiles = deepMerge(teamProjectProfiles, userProjectProfiles)

    val userGlobalProfiles = globalUserConfig.profiles ?: mapOf()
    val teamGlobalProfiles = globalTeamConfig.profiles ?: mapOf()
    val mergedGlobalProfiles = deepMerge(teamGlobalProfiles, userGlobalProfiles)

    val mergedProfiles = mergedGlobalProfiles + mergedProjectProfiles

    val zoweConfigFile = ZoweConfigFile(
      type = ConfigType.MERGED_CONFIG,
      name = configName,
      _profiles = mergedProfiles,
      _defaults = defaultsMerged,
      _schemaPath = configSchema
    )

    var profileProps: Map<String, Any> = mapOf()

    getProfile(zoweConfigFile, profileName, profileType ?: "", shouldValidateSchema)
      .also { loadedProfile ->
        profileProps = loadedProfile.data
        missingSecureProps.addAll(loadedProfile.missingSecureProps)
      }

    if (profileType != BASE_PROFILE) {
      profileProps = load(profileType=BASE_PROFILE, shouldCheckMissingProps=false) + profileProps
    }

    if (shouldCheckMissingProps) {
      val missingProps = missingSecureProps.filter { it !in profileProps.keys }.toSet()
      if (missingProps.isNotEmpty()) {
        val secureValuesMissingMsg = "Failed to load secure values: $missingProps"
        val secureValuesMissingException = Exception(secureValuesMissingMsg)
        logger.error(secureValuesMissingMsg, secureValuesMissingException)
        throw secureValuesMissingException
      }
    }

    val envVar = if (shouldOverrideWithEnv) getEnv(zoweConfigFile, configSchemaDir) else mapOf()
    return profileProps + envVar
  }

  /** Save the layers (configuration files) to disk */
  fun save() {
    listOf(projectUserConfig, projectTeamConfig, globalUserConfig, globalTeamConfig)
      .forEach { it.save(false) }
    ZoweCredentialManager.saveSecureProps()
  }

  /**
   * Get the highest priority layer (configuration file) based on the given profile name
   * @param jsonPath the JSON path to look for the profile
   * @return the highest priority layer ([ZoweConfigFile]) that contains the specified profile,
   *         or throws an exception if the profile is not found in any layer
   */
  fun getHighestPriorityLayer(jsonPath: String): ZoweConfigFile {
    val layers = listOf(projectUserConfig, projectTeamConfig, globalUserConfig, globalTeamConfig)
    val originalName = layers[0].getProfileNameFromPath(jsonPath)

    var highestLayer: ZoweConfigFile? = null
    var longestMatch = ""

    for (layer in layers) {
      runCatching { layer.initFromFile() }
        .onFailure { if (it !is FileNotFoundException) throw it }

      val parts = originalName.split(".").toMutableList()

      while (parts.isNotEmpty()) {
        val currentName = parts.joinToString(".")
        val profile = layer.findProfile(currentName, layer.profiles ?: mapOf())

        if (profile != null && currentName.length > longestMatch.length) {
          highestLayer = layer
          longestMatch = currentName
        } else {
          parts.removeAt(parts.size - 1)
        }
      }

      if (originalName == longestMatch) break

      highestLayer = highestLayer ?: layer
    }

    return highestLayer
      ?: run {
        val validLayerNotFoundMsg = "Could not find a valid layer for $jsonPath"
        val validLayerNotFoundException = FileNotFoundException(validLayerNotFoundMsg)
        logger.error(validLayerNotFoundMsg, validLayerNotFoundException)
        throw validLayerNotFoundException
      }
  }

  /**
   * Set a property in the profile, storing it securely if necessary
   * @param jsonPath the JSON path of the property to set
   * @param value the value to set for the property
   * @param shouldBeSecure if true, the property will be stored securely, false is a default
   */
  fun setProperty(jsonPath: String, value: String, shouldBeSecure: Boolean = false) {
    getHighestPriorityLayer(jsonPath)
      .setProperty(jsonPath, value, shouldBeSecure)
  }

  /**
   * Set a profile in the highest priority layer (configuration file) based on the given profile name
   * @param profilePath the path of the profile to set, e.g.: 'profiles.zosmf'
   * @param profileData the data of the profile to set
   */
  fun setProfile(profilePath: String, profileData: Map<String, Any>) {
    getHighestPriorityLayer(profilePath)
      .setProfile(profilePath, profileData)
  }
}

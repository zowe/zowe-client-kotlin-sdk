/*
 * Copyright (c) 2020-2024 IBA Group.
 *
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   IBA Group
 *   Zowe Community
 *   Uladzislau Kalesnikau
 */

package org.zowe.kotlinsdk.zowe.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.zowe.kotlinsdk.zowe.client.sdk.core.ZOSConnection
import java.io.File
import java.util.*

/**
 * Represents an object model of zowe.config.json file.
 * @author Valiantsin Krus
 * @version 0.5
 * @since 2021-08-12
 */
data class ZoweConfig(
  @Expose
  @SerializedName("\$schema")
  private val schema: String,
  @Expose
  val profiles: Map<String, ZoweConfigProfile>,
  @Expose
  val defaults: Map<String, String>
) {

  companion object {
    const val ZOWE_SECURE_ACCOUNT = "secure_config_props"
    const val ZOWE_SERVICE_BASE = "Zowe"
    private val ZOWE_SERVICE_NAME = "$ZOWE_SERVICE_BASE/$ZOWE_SECURE_ACCOUNT"

    /**
     * Save secure object configCredentialsMap for provided file in credential object and save these changes to credential storage.
     * @see readZoweCredentialsFromStorage
     * @param origFilePath path of zowe.config.json file. Secure props will be saved
     *                     inside this property of connection object.
     * @param configCredentialsMap map with parameters and values to save in credential storage.
     *                             Ex. "profiles.base.properties.user" = username
     * @param keytar instance of [KeytarWrapper]. This param is needed for accessing credential storage.
     * @return Nothing.
     */
    fun saveNewSecureProperties(
      origFilePath: String,
      configCredentialsMap: MutableMap<String, Any?>,
      keytar: KeytarWrapper = DefaultKeytarWrapper()
    ) {
      val filePath = origFilePath.split("/").toTypedArray().joinToString(File.separator)
      val configCredentials = try {
        readZoweCredentialsFromStorage(keytar).toMutableMap()
      } catch (e: Exception) {
        mutableMapOf()
      }
      if (configCredentials.containsKey(filePath)) {
        @Suppress("UNCHECKED_CAST")
        configCredentialsMap.forEach {
          (configCredentials[filePath] as? MutableMap<String, Any?>)?.set(it.key, it.value)
        }
      } else {
        configCredentials[filePath] = configCredentialsMap
      }
      savePropertiesInKeyStore(configCredentials, keytar)
    }

    /**
     * Save secure object to OS credential storage
     * @param configCredentials map with parameters and values to save in credential storage.
     * @param keytar instance of [KeytarWrapper]. This param is needed for accessing credential storage.
     * @return Nothing.
     */
    private fun savePropertiesInKeyStore(
      configCredentials: MutableMap<Any?, Any?>,
      keytar: KeytarWrapper = DefaultKeytarWrapper()
    ) {
      val passwordToSave = Gson().toJson(configCredentials)
      val osName = System.getProperty("os.name")
      val encodedObjectToSave = passwordToSave.encodeToBase64()
      if (passwordToSave.length < WINDOWS_MAX_PASSWORD_LENGTH || !osName.contains("Windows")) {
        keytar.setPassword(ZOWE_SERVICE_BASE, ZOWE_SECURE_ACCOUNT, encodedObjectToSave)
      } else {
        keytar.deletePassword(ZOWE_SERVICE_BASE, ZOWE_SECURE_ACCOUNT)
        encodedObjectToSave.chunked(WINDOWS_MAX_PASSWORD_LENGTH).forEachIndexed { i, chunk ->
          keytar.setPassword(ZOWE_SERVICE_BASE, "$ZOWE_SECURE_ACCOUNT-${i + 1}", chunk)
        }
      }
    }

    /**
     * Extracts and decodes config object of all files from credential storage.
     * @see KeytarWrapper
     * @see DefaultKeytarWrapper
     * @param keytar instance of [KeytarWrapper]. This param is needed for accessing credential storage.
     * @return Map where key is config file path and value is map of secure properties.
     *         For example:
     *         {
     *           "/user/root/zowe.config.json": {
     *              "profiles.base.properties.user": "testUser",
     *              "profiles.base.properties.password": "testPassword",
     *           }
     *         }
     */
    private fun readZoweCredentialsFromStorage(keytar: KeytarWrapper = DefaultKeytarWrapper()): Map<*, *> {
      var configMap = keytar.getCredentials(ZOWE_SERVICE_BASE)
      if (configMap.isNotEmpty() && configMap.containsKey(ZOWE_SECURE_ACCOUNT)) {
        return Gson().fromJson(configMap[ZOWE_SECURE_ACCOUNT]?.decodeFromBase64(), Map::class.java)
      }
      var result = ""
      var configNumber = 1
      do {
        configMap = keytar.getCredentials("${ZOWE_SERVICE_NAME}-${configNumber}")
        val account = "${ZOWE_SECURE_ACCOUNT}-${configNumber++}"
        if (configMap.containsKey(account)) {
          result += configMap[account]
        }
      } while (configMap.isNotEmpty())
      return if (result.isEmpty()) {
        mapOf<Any, Any>()
      } else {
        Gson().fromJson(result.decodeFromBase64(), Map::class.java)
      }
    }
  }

  /**
   * Builder class for setting the sequence of profiles to search property by name.
   * @param propName property name to search.
   */
  inner class PropertyBuilder(val propName: String) {
    var profilesToSearchProp = mutableListOf<ZoweConfigProfile?>()

    /**
     * Sets the next profile to search for the property to zosmf.
     * @return Nothing.
     */
    fun zosmf() {
      profilesToSearchProp.add(zosmfProfile)
    }

    /**
     * Sets the next profile to search for the property to tso.
     * @return Nothing
     */
    fun tso() {
      profilesToSearchProp.add(tsoProfile)
    }


    /**
     * Sets the next profile to search for the property to ssh.
     * @return Nothing
     */
    fun ssh () {
      profilesToSearchProp.add(sshProfile)
    }

    /**
     * Sets the next profile to search for the property to base.
     * @return Nothing
     */
    fun base () {
      profilesToSearchProp.add(baseProfile)
    }
  }

  /**
   * Searches for a property by a given sequence of profiles.
   * @return Found property value or null.
   */
  private fun PropertyBuilder.search (): Any? {
    val profiles = profilesToSearchProp.filterNotNull()

    fun searchParentProfile(isBase: Boolean, isParent: Boolean) =
      getProfile(profiles, isBase, isParent)
        ?.properties
        ?.get(propName)

    fun searchProfile(isBase: Boolean) =
      searchParentProfile(isBase, false) ?: searchParentProfile(isBase, true)

    return searchProfile(false) ?: searchProfile(true)
  }

  /**
   * Searches where to set property value and sets it after searching by given profiles sequence.
   * For example if given profiles sequence is zosmf -> base then it checks that zosmf profile
   * or its parent contains property and if so, then method will set property in this profile else
   * method will try to find property in base profile and set it there etc. If profile with
   * corresponding property not found then nothing will happen.
   */
  fun PropertyBuilder.set(value: Any?) {
    val profiles = profilesToSearchProp.filterNotNull()

    fun updateParentProfile(isBase: Boolean, isParent: Boolean) =
      getProfile(profiles, isBase, isParent)
        ?.properties
        ?.takeIf { it.containsKey(propName) }
        ?.set(propName, value)

    fun updateProfile(isBase: Boolean) =
      updateParentProfile(isBase, false) ?: updateParentProfile(isBase, true)

    updateProfile(false) ?: updateProfile(true)
  }

  /**
   * Forcefully set a property, and it's value to the first provided profile in the profiles sequence
   * @param value the value to set
   */
  fun PropertyBuilder.setForcefully(value: Any?) {
    profilesToSearchProp.firstOrNull()?.properties?.set(propName, value)
  }

  /**
   * Returns required profile from profilesToSearchProp.
   * The function is required to get/set property values.
   * First, the zosmf profile and its parent should be checked
   * Then base and its parent
   */
  private fun getProfile(profiles: List<ZoweConfigProfile?>, isBase: Boolean, isParent: Boolean): ZoweConfigProfile? {
    val prof = profiles.find { if (isBase) it?.type == "base" else it?.type != "base" }
    return if (isParent) prof?.parentProfile else prof
  }

  /**
   * Searches for a property with creating profiles sequence to search.
   * @see search
   * @param propName property name to search.
   * @param block extension function for PropertyBuilder class. This parameter is needed for
   *              creating a sequence of profiles to search by invoking corresponding methods
   *              in the right order.
   * @return Property value.
   */
  private fun searchProperty(propName: String, block: PropertyBuilder.() -> Unit): Any? {
    return PropertyBuilder(propName).apply(block).search()
  }

  /**
   * Searches for a property and updates it in found profile with creating profiles sequence to search.
   * @see set
   * @param propName property name to search.
   * @param block extension function for PropertyBuilder class. This parameter is needed for
   *              creating a sequence of profiles to search by invoking corresponding methods
   *              in the right order.
   * @return Nothing.
   */
  private fun updateProperty(propName: String, propValue: Any?, block: PropertyBuilder.() -> Unit) {
    PropertyBuilder(propName).apply(block).set(propValue)
  }

  /**
   * Put a property to the first provided profile in the profiles sequence
   * @see setForcefully
   * @param propName the property to set
   * @param propValue the property value to set
   * @param block the function to initialize profiles sequence
   */
  private fun putProperty(propName: String, propValue: Any?, block: PropertyBuilder.() -> Unit) {
    PropertyBuilder(propName).apply(block).setForcefully(propValue)
  }

  /**
   * Extracts secure properties from secure store by zowe config file path in current instance.
   * @see readZoweCredentialsFromStorage
   * @param filePath path of zowe.config.json file. Secure props will be extracted by this parameter.
   * @param keytar instance of [KeytarWrapper]. This param is needed for accessing credential storage.
   * @return Nothing.
   */
  fun extractSecureProperties(filePath: String, keytar: KeytarWrapper = DefaultKeytarWrapper()) {
    val configCredentials = readZoweCredentialsFromStorage(keytar).toMutableMap()
    if (configCredentials.containsKey(filePath)) {
      @Suppress("UNCHECKED_CAST")
      val configCredentialsMap = configCredentials[filePath] as MutableMap<String, Any?>
      extractSecureProperties(configCredentialsMap, profiles)
    }
  }

  /**
   * Get secure property names. Will try to get the names:
   * 1. from the "secure" section of the profile
   * 2. from the "secure" section of any nearest parent profile
   * 3. from the "secure" section of the base profile
   * @param profile the profile to check the "secure" section in
   * @param parentSecurePropNames the parent profile secure property names (from the top of the tree)
   * @return secure property names of empty list
   */
  private fun getSecurePropNamesList(profile: ZoweConfigProfile, parentSecurePropNames: List<String>): List<String> {
    return (profile.secure ?: listOf())
      .ifEmpty { parentSecurePropNames }
      .ifEmpty { baseProfile?.secure ?: listOf() }
  }

  /**
   * Recursively extracts secure properties from secure store. The secure property is set if the property name is set
   * in the "secure" section of the profile or the parent profile.
   * The primary value will be:
   * - the secure property that is set in the "properties" section (like "<propName>": "<propValue>")
   * - the secure property that is set in the credentials store (like "profiles.<path-to-the-profile>.properties.<propName>": "<propValue>")
   * - the secure property that is set in the parent's "properties" section
   * - the secure property that is set in the credentials store for the parent's profile property
   * @param configCredentialsMap map for zosmf secure properties for the related config file path
   * @param profiles profiles to extract secure properties for
   */
  private fun extractSecureProperties(
    configCredentialsMap: MutableMap<String, Any?>,
    profiles: Map<String, ZoweConfigProfile>,
    parentSecureProps: List<Pair<String, Any?>> = listOf()
  ) {
    profiles.values.forEach { profile ->
      val secureProps = getSecurePropNamesList(profile, parentSecureProps.map { it.first })
        .mapNotNull { secureProfileProp ->
          val securePropValuePlain = profile.properties?.get(secureProfileProp)
          val securePropValueInStorage =
            configCredentialsMap
              .getOrDefault(
                "profiles.${buildCredPath(profile, ".profiles.")}.properties.$secureProfileProp",
                null
              )
          val securePropParentPlain = parentSecureProps
            .find { (securePropKey, _) -> securePropKey == secureProfileProp }
          val securePropParentInStorage = parentSecureProps
            .find { (securePropKey, _) -> securePropKey == "secure$secureProfileProp" }
          when {
            securePropValuePlain != null -> secureProfileProp to securePropValuePlain
            securePropValueInStorage != null -> "secure$secureProfileProp" to securePropValueInStorage
            securePropParentPlain != null -> securePropParentPlain
            else -> securePropParentInStorage
          }
        }
      secureProps.forEach { (securePropKey, securePropValue) ->
        profile.properties?.set(securePropKey, securePropValue)
      }
      if (profile.profiles != null) {
        extractSecureProperties(configCredentialsMap, profile.profiles, secureProps)
      }
    }
  }

  /**
   * Builds full path for profile
   * @param profile
   * @param separator between profile's names
   * @return full profile name  to extract secure properties from secure store or full profile name.
   */
  private fun buildCredPath(profile: ZoweConfigProfile?, separator: String): String {
    val currProfile = mutableListOf<String>()
    var v: ZoweConfigProfile? = profile
    while (v != null) {
      currProfile.add(v.name.toString())
      v = v.parentProfile
    }
    currProfile.reverse()
    return currProfile.fold("") { curr, next -> "$curr$separator$next" }.substring(separator.length)
  }

  /**
   * Updates secure object for provided file in credential object and save these changes to credential storage.
   * @see readZoweCredentialsFromStorage
   * @param filePath path of zowe.config.json file. Secure props will be saved
   *                 inside this property of connection object.
   * @param keytar instance of [KeytarWrapper]. This param is needed for accessing credential storage.
   * @return Nothing.
   */
  @Suppress("UNCHECKED_CAST")
  fun saveSecureProperties (filePath: String, keytar: KeytarWrapper = DefaultKeytarWrapper()) {
    val configCredentials = readZoweCredentialsFromStorage(keytar).toMutableMap()
    val zosmfConfigCredentialsMap = mutableMapOf<String, Any?>()
    val baseConfigCredentialsMap = mutableMapOf<String, Any?>()

    buildSecureProperties(this.profiles, zosmfConfigCredentialsMap, baseConfigCredentialsMap)
    if (configCredentials[filePath] != null) {
      if (zosmfConfigCredentialsMap.isNotEmpty()) zosmfConfigCredentialsMap.forEach { (k, v) ->
        (configCredentials[filePath] as MutableMap<String, Any?>)[k] = v
      }
      else baseConfigCredentialsMap.forEach { (k, v) ->
        (configCredentials[filePath] as MutableMap<String, Any?>)[k] = v
      }
    } else configCredentials[filePath] = zosmfConfigCredentialsMap.ifEmpty { baseConfigCredentialsMap }
    savePropertiesInKeyStore(configCredentials, keytar)
  }

  /**
   * Fills config credentials for zosmf and/or base profile.
   * Will set the secure properties basing on the "secure" section of the profile or the parent profile
   * @param profiles profiles
   * @param zosmfConfigCredentialsMap map for zosmf secure props
   * @param baseConfigCredentialsMap map for base secure props
   * @param parentSecurePropNames the secure property names of the parent profiles tree
   * @return Nothing.
   */
  private fun buildSecureProperties(
    profiles: Map<String, ZoweConfigProfile>,
    zosmfConfigCredentialsMap: MutableMap<String, Any?>,
    baseConfigCredentialsMap: MutableMap<String, Any?>,
    parentSecurePropNames: List<String> = listOf()
  ) {
    val zosmfProfileName = buildCredPath(zosmfProfile, ".profiles.")
    val baseProfileName = buildCredPath(baseProfile, ".profiles.")
    profiles.values.forEach { profile ->
      val securePropNames = getSecurePropNamesList(profile, parentSecurePropNames)
      val curr = buildCredPath(profile, ".profiles.")
      securePropNames.forEach { propName ->
        if (profile.properties?.containsKey("secure$propName") == true)
          if (curr == zosmfProfileName)
            zosmfConfigCredentialsMap["profiles.${curr}.properties.${propName}"] = profile.properties["secure$propName"]
          else if (curr == baseProfileName)
            baseConfigCredentialsMap["profiles.${curr}.properties.${propName}"] = profile.properties["secure$propName"]
      }
      if (profile.profiles != null) {
        buildSecureProperties(
          profile.profiles,
          zosmfConfigCredentialsMap,
          baseConfigCredentialsMap,
          securePropNames
        )
      }
    }
  }

  /**
   * Extracts secure properties from secure store by zowe config file path in current instance.
   * @see readZoweCredentialsFromStorage
   * @param filePathTokens path of zowe.config.json file split by delimiter.
   *                       Secure props will be extracted by this parameter.
   * @param keytar instance of [KeytarWrapper]. This param is needed for accessing credential storage.
   * @return Nothing.
   */
  fun extractSecureProperties(filePathTokens: Array<String>, keytar: KeytarWrapper = DefaultKeytarWrapper()) {
    extractSecureProperties(filePathTokens.joinToString(File.separator), keytar)
  }

  /**
   * Updates secure object for provided file in credential object and save these changes to credential storage.
   * @see readZoweCredentialsFromStorage
   * @param filePathTokens path of zowe.config.json file split by delimiter.
   *                       Secure props will be saved inside this property of connection object.
   * @param keytar instance of [KeytarWrapper]. This param is needed for accessing credential storage.
   * @return Nothing.
   */
  fun saveSecureProperties (filePathTokens: Array<String>, keytar: KeytarWrapper = DefaultKeytarWrapper()) {
    saveSecureProperties(filePathTokens.joinToString(File.separator), keytar)
  }

  /**
   * Deserializes current [ZoweConfig] instance to JSON string without secure properties.
   * @return String with deserialized object.
   */
  fun toJson(): String {
    val gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create()
    val zoweConfigCopy = parseConfigJson(gson.toJson(this))
    removeSecure(zoweConfigCopy.profiles)
    return gson.toJson(zoweConfigCopy, zoweConfigCopy::class.java)
  }

  /**
   * Recursively remove secure properties from profiles.
   */
  private fun removeSecure(ps: Map<String, ZoweConfigProfile>?, parentSecurePropNames: List<String> = listOf()) {
    ps?.forEach { (_, profile) ->
      getSecurePropNamesList(profile, parentSecurePropNames)
        .forEach { propName ->
          profile.properties?.remove("secure$propName")
        }
        removeSecure(profile.profiles, parentSecurePropNames)
    }
  }

  /**
   * Creates [ZOSConnection] based on zowe config or throws exception if data is not correct.
   * @return [ZOSConnection] instance
   */
  fun toZosConnection(): ZOSConnection {
    if (host?.isEmpty() != false || port == null || protocol.isEmpty()){
      throw IllegalStateException("Zowe config data is not valid for creating ZOSConnection")
    }
    return ZOSConnection(
      host ?: "",
      port.toString(),
      user ?: "",
      password ?: "",
      protocol,
      rejectUnauthorized,
      basePath,
      encoding,
      responseTimeout,
      buildCredPath(zosmfProfile, ".")
    )
  }

  /**
   * "user" property.
   * Is searched in z/OSMF profile first (up the parents hierarchy as well), in the base profile if not found.
   * Is updated in the actual profile, or the nearest parent where it is already set, or the base profile.
   * If the credentials are not yet set anywhere, they will be put in the current profile
   */
  var user: String?
    get() = searchProperty("user") { zosmf(); base() } as String?
      ?: searchProperty("secureuser") { zosmf(); base() } as String?
    set(el) {
      if (searchProperty("user") { zosmf(); base() } as String? != null)
        updateProperty("user", el ?: "") { zosmf(); base() }
      else if (searchProperty("secureuser") { zosmf(); base() } as String? != null)
        updateProperty("secureuser", el ?: "") { zosmf(); base() }
      else
        putProperty("secureuser", el ?: "") { zosmf() }
    }

  /**
   * "password" property.
   * Is searched in z/OSMF profile first (up the parents hierarchy as well), in the base profile if not found.
   * Is updated in the actual profile, or the nearest parent where it is already set, or the base profile.
   * If the credentials are not yet set anywhere, they will be put in the current profile
   */
  var password: String?
    get() = searchProperty("password") { zosmf(); base() } as String?
      ?: searchProperty("securepassword") { zosmf(); base() } as String?
    set(el) {
      if (searchProperty("password") { zosmf(); base() } as String? != null)
        updateProperty("password", el ?: "") { zosmf(); base() }
      if (searchProperty("securepassword") { zosmf(); base() } as String? != null)
        updateProperty("securepassword", el ?: "") { zosmf(); base() }
      else
        putProperty("securepassword", el ?: "") { zosmf() }
    }

  var host: String?
    get() = searchProperty("host") { zosmf(); base() } as String?
    set(el) { updateProperty("host", el) { zosmf(); base() } }

  var rejectUnauthorized: Boolean?
    get() = searchProperty("rejectUnauthorized") { zosmf(); base() } as Boolean?
    set(el) { updateProperty("rejectUnauthorized", el ?: true) { zosmf(); base() } }

  var port: Long?
    get() = searchProperty("port") { zosmf(); base() } as Long?
    set(el) { updateProperty("port", el) { zosmf(); base() } }

  var protocol: String
    get() = searchProperty("protocol") { zosmf(); base() } as String? ?: "https"
    set(el) { updateProperty("protocol", el) { zosmf(); base() } }

  var basePath: String
    get() = searchProperty("basePath") { zosmf(); base() } as String? ?: "/"
    set(el) { updateProperty("basePath", el) { zosmf(); base() } }

  var encoding: Long
    get() = searchProperty("encoding") { zosmf(); base() } as Long? ?: 1047
    set(el) { updateProperty("encoding", el) { zosmf(); base() } }

  var responseTimeout: Long
    get() = searchProperty("responseTimeout") { zosmf(); base() } as Long? ?: 600
    set(el) { updateProperty("responseTimeout", el) { zosmf(); base() } }

  /**
   * Searches profile by its path. For example if profile has path "gr1.example" then it will search
   * profile "example" in "gr1" group.
   * @param searchPath path to search profile
   * @return found profile or null if *searchPath* is not valid or no one profile exists by this path
   */
  fun profile(searchPath: String?): ZoweConfigProfile? {
    searchPath ?: return null
    val searchPaths = searchPath.split(".")
    searchPaths.isEmpty() && return null
    return searchPaths.drop(1).fold(profiles[searchPaths[0]]) { acc, s ->
      acc?.let { if (it.profiles?.containsKey(s) == true) it.profiles[s] else null }
    }
  }

  var zosmfProfile: ZoweConfigProfile? = profile(defaults["zosmf"])

  fun setProfile(prName: String) {
    zosmfProfile = profile(prName)
  }

  fun restoreProfile() {
    zosmfProfile = profile(defaults["zosmf"])
  }

  val tsoProfile: ZoweConfigProfile?
    get() = profile(defaults["tso"])

  val sshProfile: ZoweConfigProfile?
    get() = profile(defaults["ssh"])

  val baseProfile: ZoweConfigProfile?
    get() = profile(defaults["base"])

  /**
   * Searches ZOSConnection in zowe config file.
   * @return list of found ZOSConnection or empty list
   */
  fun getListOfZosmfConnections(): List<ZOSConnection> {
    val zosConnectionsList = getZosmfConnections(profiles)
    zosmfProfile = profile(defaults["zosmf"])
    return zosConnectionsList
  }

  /**
   * Get [ZOSConnection] instances from the provided Zowe config profile map (including nested profiles)
   * @param profiles current profile to transform to [ZOSConnection]
   * @return list of [ZOSConnection] instances, build from profiles map
   */
  private fun getZosmfConnections(profiles: Map<String, ZoweConfigProfile>): List<ZOSConnection> {
    return profiles.values.flatMap { profile ->
      val currZosConnection = if (profile.type == "zosmf") {
        zosmfProfile = profile(buildCredPath(profile, "."))
        listOf(toZosConnection())
      } else listOf()
      val nestedZosConnections = getZosmfConnections(profile.profiles ?: mapOf())
      currZosConnection + nestedZosConnections
    }
  }
}

data class ZoweConfigProfile(
  var name: String,
  @Expose
  val type: String,
  @Expose
  val properties: MutableMap<String, Any?>?,
  @Expose
  val secure: ArrayList<String>?,
  @Expose
  val profiles: Map<String, ZoweConfigProfile>?,
  var parentProfile: ZoweConfigProfile?
)

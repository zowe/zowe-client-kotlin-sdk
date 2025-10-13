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

import com.fasterxml.jackson.core.json.JsonReadFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion
import com.networknt.schema.ValidationMessage
import java.io.File
import java.net.URI

val jsoncMapper: JsonMapper by lazy {
  JsonMapper.builder()
    .enable(JsonReadFeature.ALLOW_JAVA_COMMENTS)
    .enable(JsonReadFeature.ALLOW_YAML_COMMENTS)
    .build()
}

/**
 * Exception to throw when zowe.schema.json is not resolved
 * @param reasonNotResolved the reason, describing why the schema is not resolved
 * @param originException the original [Exception] to appear during a resolve try
 */
class SchemaNotResolvedException(
  reasonNotResolved: String,
  originException: Exception
) : Exception("$reasonNotResolved: ${originException.message}", originException)

/**
 * Load schema JSON as [String]
 * @param pathSchemaJson path to zowe.schema.json
 * @param location the location to search for the schema in
 * @return loaded schema JSON as [String]
 */
fun retrieveSchemaJsonAsText(pathSchemaJson: String, location: String): String {
  return when {
    // checks if the path_schema_json point to an internet URI and download the schema using the URI
    pathSchemaJson.startsWith("https://") || pathSchemaJson.startsWith("http://") -> {
      try {
        URI(pathSchemaJson).toURL().readText(Charsets.UTF_8)
      } catch (e: Exception) {
        throw SchemaNotResolvedException("Invalid schema request", e)
      }
    }
    // checks if the path_schema_json is a file
    File(pathSchemaJson.replace("file://", "")).isFile || pathSchemaJson.startsWith("file://") -> {
      val schemaPathNoProtocol = pathSchemaJson.replace("file://", "")
      try {
        File(schemaPathNoProtocol).readText(Charsets.UTF_8)
      } catch (e: Exception) {
        throw SchemaNotResolvedException(
          "Invalid schema file '$schemaPathNoProtocol",
          e
        )
      }
    }
    // checks if the path_schema_json is absolute
    !File(pathSchemaJson).isAbsolute -> {
      val schemaPath = File(location, pathSchemaJson).path
      try {
        File(schemaPath).readText(Charsets.UTF_8)
      } catch (e: Exception) {
        throw SchemaNotResolvedException(
          "Invalid JSON in schema file '$schemaPath'",
          e
        )
      }
    }
    // if there is no valid pathSchemaJson it will return null
    else -> throw SchemaNotResolvedException(
      "Could not resolve JSON schema by the path: $pathSchemaJson",
      Exception()
    )
  }
}

/**
 * Validate that zowe.config.json file matches zowe.schema.json
 * @param pathConfigJson absolute path to zowe.config.json or Map with config data
 * @param pathSchemaJson absolute path to zowe.schema.json
 * @param cwd path of the current working directory
 * Based on https://github.com/zowe/zowe-client-python-sdk/blob/main/src/core/zowe/core_for_zowe_sdk/validators.py
 */
fun validateConfigJson(pathConfigJson: Any, pathSchemaJson: String, cwd: String) {
  // Load schema JSON as text
  val schemaJsonAsText = retrieveSchemaJsonAsText(pathSchemaJson, cwd)

  // Load config JSON
  val configJsonText = when (pathConfigJson) {
    is String -> File(pathConfigJson).readText(Charsets.UTF_8)
    is ZoweConfigJsonc -> jsoncMapper.writeValueAsString(pathConfigJson)
    else -> throw IllegalArgumentException("pathConfigJson must be String or ZoweConfigJsonc")
  }

  // Validate using json-schema-validator
  val schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7)
  val schemaJson = schemaFactory.getSchema(schemaJsonAsText)
  val jsonNode = jsoncMapper.readTree(configJsonText)
  val errors: Set<ValidationMessage> = schemaJson.validate(jsonNode)

  if (errors.isNotEmpty()) {
    val errorMessages = errors.joinToString("\n") { it.message }
    throw IllegalArgumentException("Schema validation failed:\n$errorMessages")
  }
}

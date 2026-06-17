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

import io.github.optimumcode.json.schema.JsonSchema
import io.github.optimumcode.json.schema.SchemaType
import kotlinx.serialization.json.*
import java.io.File
import java.net.URI

val jsoncJson: Json by lazy {
  Json {
    allowComments = true
    allowTrailingComma = true
    ignoreUnknownKeys = true
    isLenient = true
  }
}

fun JsonElement.toAny(): Any? = when (this) {
  is JsonNull -> null
  is JsonObject -> entries.associate { (k, v) -> k to v.toAny() }.toMutableMap()
  is JsonArray -> map { it.toAny() }.toMutableList()
  is JsonPrimitive -> when {
    isString -> content
    booleanOrNull != null -> boolean
    longOrNull != null -> long
    doubleOrNull != null -> double
    else -> content
  }
}

fun Any?.toJsonElement(): JsonElement = when (this) {
  null -> JsonNull
  is Map<*, *> -> JsonObject(entries.associate { (k, v) -> k.toString() to v.toJsonElement() })
  is List<*> -> JsonArray(map { it.toJsonElement() })
  is String -> JsonPrimitive(this)
  is Boolean -> JsonPrimitive(this)
  is Number -> JsonPrimitive(this)
  else -> JsonPrimitive(toString())
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
 * Exception to throw on JSON validation error
 * @param reasonNotValid the reason message the JSON is not valid
 */
class JsonValidationException(reasonNotValid: String) : Exception(reasonNotValid)

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
  val schemaJsonAsText = retrieveSchemaJsonAsText(pathSchemaJson, cwd)

  val configJsonText = when (pathConfigJson) {
    is String -> File(pathConfigJson).readText(Charsets.UTF_8)
    is ZoweConfigJsonc -> jsoncJson.encodeToString(JsonElement.serializer(), pathConfigJson.toJsonElement())
    else -> throw IllegalArgumentException("pathConfigJson must be String or ZoweConfigJsonc")
  }

  val schema = JsonSchema.fromDefinition(schemaJsonAsText, defaultType = SchemaType.DRAFT_7)
  val configElement = jsoncJson.parseToJsonElement(configJsonText)
  val errors = mutableListOf<String>()
  schema.validate(configElement) { error ->
    errors.add(error.message)
  }

  if (errors.isNotEmpty()) {
    val errorMessages = errors.joinToString("\n")
    throw JsonValidationException("Schema validation failed:\n$errorMessages")
  }
}
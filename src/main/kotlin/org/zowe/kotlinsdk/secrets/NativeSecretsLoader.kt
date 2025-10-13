/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.secrets

import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

// TODO: doc
internal object NativeSecretsLoader {
  private var isLoaded = false
  private val logger = LoggerFactory.getLogger(javaClass)
  private const val LIB_NAME = "keyring"

  var HAS_KEYRING = false

  init {
    try {
      loadNativeLibrary()
      HAS_KEYRING = true
    } catch (e: Exception) {
      logger.error("Failed to load native library", e)
      HAS_KEYRING = false
//      throw ZoweSecretsException("Failed to initialize native secrets library", e)
    }
  }

  private fun loadNativeLibrary() {
    if (isLoaded) {
      logger.debug("Native library already loaded")
      return
    }

    val osName = System.getProperty("os.name").lowercase()
    val osArch = System.getProperty("os.arch").lowercase()

    logger.info("Loading native library for OS: $osName, arch: $osArch")

    // Define architecture
    val arch = when {
      osArch.contains("aarch64") || osArch.contains("arm64") -> "aarch64"
      osArch.contains("amd64") || osArch.contains("x86_64") -> "x86_64"
      else -> throw ZoweSecretsException("Unsupported architecture: $osArch")
    }

    // Define OS and path to library
    val (resourcePath, libExtension) = when {
      osName.contains("linux") -> {
        "native/linux/$arch/lib$LIB_NAME.so" to ".so"
      }
      osName.contains("mac") || osName.contains("darwin") -> {
        "native/macos/$arch/lib$LIB_NAME.dylib" to ".dylib"
      }
      osName.contains("win") -> {
        "native/windows/$arch/$LIB_NAME.dll" to ".dll"
      }
      else -> {
        throw ZoweSecretsException("Unsupported operating system: $osName")
      }
    }

    logger.debug("Resource path: $resourcePath (arch: $arch)")

    // Load the library from resources
    val inputStream: InputStream = NativeSecretsLoader::class.java.classLoader
      .getResourceAsStream(resourcePath)
      ?: throw ZoweSecretsException(
        "Native library not found in resources: $resourcePath\n" +
        "OS: $osName, Arch: $osArch (normalized: $arch)\n" +
        "Make sure the library is built for your platform."
      )

    // Create a temporary file
    val tempFile = File.createTempFile("lib${LIB_NAME}_$arch", libExtension)
      .apply { deleteOnExit() }

    logger.debug("Extracting library to: ${tempFile.absolutePath}")

    // Copy the library to the temporary file
    try {
      FileOutputStream(tempFile).use { outputStream ->
        inputStream.copyTo(outputStream)
      }
    } catch (e: Exception) {
      throw ZoweSecretsException("Failed to extract native library", e)
    }

    // Load the library
    try {
      System.load(tempFile.absolutePath)
      isLoaded = true
      logger.info("Native library loaded successfully for $arch")
    } catch (e: UnsatisfiedLinkError) {
      throw ZoweSecretsException(
        "Failed to load native library for $osName/$arch: ${e.message}\n" +
        "Library path: ${tempFile.absolutePath}",
        e
      )
    }
  }

  @JvmStatic
  external fun setPassword(service: String, account: String, password: String)

  @JvmStatic
  external fun getPassword(service: String, account: String): String?

  @JvmStatic
  external fun deletePassword(service: String, account: String): Boolean

  @JvmStatic
  external fun findPassword(service: String): String?

  @JvmStatic
  external fun findCredentials(service: String): List<Pair<String, String>>
}
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

/**
 * Zowe profile datatype is used by ZoweConfigFile to return a Zowe profile data along with metadata,
 * such as the profile [name] and [missingSecureProps]
 * Based on https://github.com/zowe/zowe-client-python-sdk/blob/main/src/core/zowe/core_for_zowe_sdk/config_file.py
 */
data class ZoweProfile(
  val data: Map<String, Any> = mapOf(),
  val name: String = "",
  val missingSecureProps: List<String> = listOf()
)

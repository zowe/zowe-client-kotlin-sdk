/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.openssh.jes

/**
 * Extract the next job value from the provided string of SSH response.
 * The string must be in a format "=|||= <fieldName>: <the-value> =|||="
 * @param output the SSH response output to get the string from
 * @param fieldName the field name to get the value for
 * @return the found value as a string or empty string if the value is not found or is not defined for the field name
 */
fun extractJobValue(output: String, fieldName: String): String {
  val regex = """=\|\|\|= $fieldName: (.*?) =\|\|\|=""".toRegex()
  return regex.find(output)?.groupValues?.getOrNull(1) ?: ""
}

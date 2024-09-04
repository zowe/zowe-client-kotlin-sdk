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
 */

package common

import okhttp3.Credentials
import okhttp3.ResponseBody

val zosmfUrl = System.getenv("ZOSMF_TEST_URL") ?: ""
val zosmfUser = System.getenv("ZOSMF_TEST_USERNAME") ?: ""
val zosmfPassword = System.getenv("ZOSMF_TEST_PASSWORD") ?: ""

val basicCreds = Credentials.basic(zosmfUser, zosmfPassword)

fun errorBodyToList(errorBody: ResponseBody) : List<String> {
  return errorBody.charStream().readLines()
}

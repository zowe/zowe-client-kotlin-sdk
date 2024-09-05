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

package org.zowe.kotlinsdk

import retrofit2.Response

/**
 * Validates the response received from an API call.
 *
 * @param response The response object received from the API call.
 * @param defaultMessage The default message to throw if the response is not successful and there's no error body.
 * @throws Exception if the response is not successful
 */
fun validateResponse(response: Response<*>?, defaultMessage: String = "") {
    if (response?.isSuccessful != true) {
        val errorBody = response?.errorBody()?.string()
        errorBody?.let {
            if (errorBody.isBlank()) {
                throw Exception("HTTP code = ${response.code()}; Message: ${response.message()}")
            } else {
                throw Exception("${if (defaultMessage.isBlank()) "" else "$defaultMessage. "}${errorBody}")
            }
        }
    }
}

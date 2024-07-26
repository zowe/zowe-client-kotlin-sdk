// Copyright © 2020 IBA Group, a.s. All rights reserved. Use of this source code is governed by Eclipse Public License – v 2.0 that can be found at: https://www.eclipse.org/legal/epl-2.0/

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
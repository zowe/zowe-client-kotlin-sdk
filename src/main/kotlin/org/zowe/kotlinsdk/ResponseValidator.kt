// Copyright © 2020 IBA Group, a.s. All rights reserved. Use of this source code is governed by Eclipse Public License – v 2.0 that can be found at: https://www.eclipse.org/legal/epl-2.0/

package org.zowe.kotlinsdk

import retrofit2.Response

fun validateResponse(response: Response<*>?, defaultMessage: String = "") {
    if (response?.isSuccessful != true) {
        if (response?.errorBody()?.string().isNullOrBlank()) {
            throw Exception("code = ${response?.code()}; ${response?.message()}")
        } else {
            throw Exception("${if (defaultMessage.isBlank()) "" else "$defaultMessage. "}${response?.errorBody()}")
        }
    }
}
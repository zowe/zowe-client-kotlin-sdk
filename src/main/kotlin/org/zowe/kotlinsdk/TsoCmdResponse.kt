/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright IBA Group 2020
 */

package org.zowe.kotlinsdk

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TsoCmdResponse(
    @SerializedName("cmdResponse")
    @Expose
    var cmdResponse: List<TsoCmdResult> = emptyList(),

    @SerializedName("servletKey")
    @Expose
    var servletKey: String? = null,

    @SerializedName("tsoPromptReceived")
    @Expose
    var tsoPromptReceived: String? = null,

    @SerializedName("keywordDetected")
    @Expose
    var keywordDetected: String? = null
)

data class TsoCmdResult(
    @SerializedName("message")
    @Expose
    var message: String? = null,

    @SerializedName("returnCode")
    @Expose
    var returnCode: String? = null,

    @SerializedName("reason")
    @Expose
    var reason: String? = null
)

data class TsoCmdRequestBody(
    @SerializedName("tsoCmd")
    @Expose
    var tsoCmd: String,

    @SerializedName("system")
    @Expose
    var system: Int? = null,

    @SerializedName("maxWaitTime")
    @Expose
    var maxWaitTime: Int? = null,

    @SerializedName("cmdState")
    @Expose
    var cmdState: TsoCmdState? = TsoCmdState.STATELESS,

    @SerializedName("servletKey")
    @Expose
    var servletKey: String? = null,

    @SerializedName("keyword")
    @Expose
    var keyword: String? = null
)

enum class TsoCmdState(private val cmdState: String) {
    STATELESS("stateless"),
    STATEFUL("stateful");

    override fun toString(): String {
        return cmdState
    }
}
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

data class TsoResponse(
  @SerializedName("servletKey")
  @Expose
  var servletKey: String? = null,

  @SerializedName("ver")
  @Expose
  var ver: Int? = null,

  @SerializedName("queueID")
  @Expose
  var queueId: String? = null,

  @SerializedName("remoteSys")
  @Expose
  var remoteSys: String? = null,

  @SerializedName("tsoData")
  @Expose
  var tsoData: List<TsoData> = emptyList(),

  @SerializedName("appData")
  @Expose
  var appData: String? = null,

  @SerializedName("timeout")
  @Expose
  var timeout: Boolean? = null,

  @SerializedName("reused")
  @Expose
  var reused: Boolean? = null,

  @SerializedName("msgData")
  @Expose
  var msgData: List<MessageData> = emptyList(),

  @SerializedName("messages")
  @Expose
  var messages: String? = null,
)

data class TsoData(
  @SerializedName(value = "TSO MESSAGE")
  @Expose
  var tsoMessage: MessageType? = null,

  @SerializedName(value = "TSO PROMPT")
  @Expose
  var tsoPrompt: MessageType? = null,

  @SerializedName(value = "TSO RESPONSE")
  @Expose
  var tsoResponse: MessageType? = null,
)

data class MessageType(
  @SerializedName("VERSION")
  @Expose
  var version: String,

  @SerializedName(value = "DATA")
  @Expose
  var data: String? = null,

  @SerializedName(value = "HIDDEN")
  @Expose
  var hidden: String? = null,

  @SerializedName(value = "ACTION")
  @Expose
  var action: String? = null,
)

data class MessageData(
  @SerializedName("messageText")
  @Expose
  var messageText: String? = null,

  @SerializedName("messageId")
  @Expose
  var messageId: String? = null,
)

enum class TsoCodePage(val codePage: String) {
 IBM_1025("1025"),
 IBM_1047("1047");

  override fun toString(): String {
    return codePage
  }
}

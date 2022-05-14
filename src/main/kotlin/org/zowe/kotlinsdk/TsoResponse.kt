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
  @SerializedName("message-type")
  @Expose
  var messageType: MessageType,
)

data class MessageType(
  @SerializedName("VERSION")
  @Expose
  var version: String,

  @SerializedName("data-type")
  @Expose
  var dataType: String,
)

data class MessageData(
  @SerializedName("messageText")
  @Expose
  var messageText: String? = null,

  @SerializedName("messageId")
  @Expose
  var messageId: String? = null,
)

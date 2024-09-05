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

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ChangePasswordResponse(
  @SerializedName("returnCode")
  @Expose
  var returnCode: String? = null,

  @SerializedName("reasonCode")
  @Expose
  var reasonCode: String? = null,

  @SerializedName("message")
  @Expose
  var message: String? = null
)

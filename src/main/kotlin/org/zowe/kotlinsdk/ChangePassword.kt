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

data class ChangePassword(
  @SerializedName("userID")
  @Expose
  var userID: String? = null,

  @SerializedName("oldPwd")
  @Expose
  var oldPwd: String? = null,

  @SerializedName("newPwd")
  @Expose
  var newPwd: String? = null
)

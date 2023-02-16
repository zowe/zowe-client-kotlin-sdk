// Copyright © 2020 IBA Group, a.s. All rights reserved. Use of this source code is governed by Eclipse Public License – v 2.0 that can be found at: https://www.eclipse.org/legal/epl-2.0/

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
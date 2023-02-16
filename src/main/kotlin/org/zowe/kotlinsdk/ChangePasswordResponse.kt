// Copyright © 2020 IBA Group, a.s. All rights reserved. Use of this source code is governed by Eclipse Public License – v 2.0 that can be found at: https://www.eclipse.org/legal/epl-2.0/

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
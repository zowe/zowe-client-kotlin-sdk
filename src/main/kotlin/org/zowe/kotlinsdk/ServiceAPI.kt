// Copyright © 2020 IBA Group, a.s. All rights reserved. Use of this source code is governed by Eclipse Public License – v 2.0 that can be found at: https://www.eclipse.org/legal/epl-2.0/

package org.zowe.kotlinsdk

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import retrofit2.Call
import retrofit2.http.*

interface ServiceAPI {

  @AvailableSince(ZVersion.ZOS_2_5)
  @PUT("/zosmf/services/authenticate")
  fun changeUserPassword(
    @Body body: ChangePassword
  ): Call<ChangePasswordResponse>

}
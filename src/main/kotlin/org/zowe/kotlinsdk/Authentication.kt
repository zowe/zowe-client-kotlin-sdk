/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 *
 * Contributors:
 *   Zowe Community
 *   Dzianis Lisiankou
 */

package org.zowe.kotlinsdk

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Authentication(

  @SerializedName("userId")
  @Expose
  val userId: String? = null,

  @SerializedName("domain")
  @Expose
  val domain: String? = null,

  @SerializedName("creation")
  @Expose
  val creation: String? = null,

  @SerializedName("expiration")
  @Expose
  val expiration: String? = null
)

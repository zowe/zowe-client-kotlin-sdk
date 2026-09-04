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

@Deprecated(
  "Scheduled for removal since v1.0.0",
  ReplaceWith("ZosmfRecallDatasetRequestBody", "org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging")
)
data class HRecall(
  @SerializedName("request")
  @Expose
  private val request: String = "hrecall",


  @SerializedName("wait")
  @Expose
  var wait: Boolean? = null
)

@Deprecated(
  "Scheduled for removal since v1.0.0",
  ReplaceWith("ZosmfMigrateDatasetRequestBody", "org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging")
)
data class HMigrate(
  @SerializedName("request")
  @Expose
  private val request: String = "hmigrate",

  @SerializedName("wait")
  @Expose
  var wait: Boolean? = null
)

@Deprecated(
  "Scheduled for removal since v1.0.0",
  ReplaceWith("ZosmfDeleteDatasetBackupVersionRequestBody", "org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging")
)
data class HDelete(
  @SerializedName("request")
  @Expose
  private val request: String = "hdelete",

  @SerializedName("wait")
  @Expose
  var wait: Boolean? = null,

  @SerializedName("purge")
  @Expose
  var purge: Boolean? = null
)

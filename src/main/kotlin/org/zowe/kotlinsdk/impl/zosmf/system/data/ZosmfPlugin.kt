//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.zosmf.system.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion

/**
 * TODO: doc
 * For more info, please, refer to:
 * https://www.ibm.com/docs/en/zos/3.1.0?topic=service-retrieve-zosmf-information#GETMethodRetrieveZOSMFConfiguration__QueryServiceJSONobject__title__1
 */
data class ZosmfPlugin(
  /** pluginVersion response param */
  @SerializedName("pluginVersion")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val version: String? = null,

  /** pluginStatus response param */
  @SerializedName("pluginStatus")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val status: ZosmfPluginStatus? = null,

  /** pluginDefaultName response param */
  @SerializedName("pluginDefaultName")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val defaultName: String? = null
) {
  enum class PluginStatus {
    ACTIVE,
    INSTALLED,
    UNINSTALLED
  }

  interface PluginStatusValue {
    val value: PluginStatus
  }

  enum class ZosmfPluginStatus(override val value: PluginStatus) : PluginStatusValue {
    @SerializedName("ACTIVE")
    ACTIVE(PluginStatus.ACTIVE),

    @SerializedName("INSTALLED")
    INSTALLED(PluginStatus.INSTALLED),

    @SerializedName("UNINSTALLED")
    UNINSTALLED(PluginStatus.UNINSTALLED)
  }
}
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
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.system.data.GetSystemInfoResponse

/**
 * TODO: doc
 * For more info, please, refer to:
 * https://www.ibm.com/docs/en/zos/3.1.0?topic=service-retrieve-zosmf-information#GETMethodRetrieveZOSMFConfiguration__QueryServiceJSONobject__title__1
 */
class ZosmfGetSystemInfoResponse(
  /** zosmf_saf_realm response param */
  @SerializedName("zosmf_saf_realm")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val zosmfSafRealm: String? = null,

  /** zosmf_port response param */
  @SerializedName("zosmf_port")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val zosmfPort: String? = null,

  /** zosmf_full_version response param */
  @SerializedName("zosmf_full_version")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val zosmfFullVersion: String? = null,

  /** plugins response param */
  @SerializedName("plugins")
  @JsonAdapter(ZosmfPluginsAdapter::class)
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val plugins: List<ZosmfPlugin> = emptyList(),

  /** api_version response param */
  @SerializedName("api_version")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val apiVersion: String? = null,

  /** zos_version response param */
  @SerializedName("zos_version")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val zosVersion: String? = null,

  /** zosmf_version response param */
  @SerializedName("zosmf_version")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val zosmfVersion: String? = null,

  /** zosmf_hostname response param */
  @SerializedName("zosmf_hostname")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val zosmfHostname: String? = null
): GetSystemInfoResponse()
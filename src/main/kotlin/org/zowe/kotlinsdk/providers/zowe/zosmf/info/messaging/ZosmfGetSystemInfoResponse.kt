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
 *   Uladzislau Kalesnikau
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.info.messaging

import io.ktor.http.HttpStatusCode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.info.api.messaging.GetSystemInfoResponse
import org.zowe.kotlinsdk.providers.zowe.HttpResponse

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=service-retrieve-zosmf-information#GETMethodRetrieveZOSMFConfiguration__title__8">Retrieve z/OSMF information: Expected response</a> */
@Serializable
class ZosmfGetSystemInfoResponse(
  @Transient
  override var status: HttpStatusCode = HttpStatusCode.OK,

  /** zosmf_saf_realm response param */
  @SerialName("zosmf_saf_realm")
  @AvailableSince(ZVersion.ZOS_2_1) val zosmfSafRealm: String? = null,

  /** zosmf_port response param */
  @SerialName("zosmf_port")
  @AvailableSince(ZVersion.ZOS_2_1) val zosmfPort: String? = null,

  /** zosmf_full_version response param */
  @SerialName("zosmf_full_version")
  @AvailableSince(ZVersion.ZOS_2_1) val zosmfFullVersion: String? = null,

  /** plugins response param */
  @SerialName("plugins")
  @AvailableSince(ZVersion.ZOS_2_1) val plugins: List<ZosmfPlugin> = emptyList(),

  /** api_version response param */
  @SerialName("api_version")
  @AvailableSince(ZVersion.ZOS_2_1) val apiVersion: String? = null,

  /** zos_version response param */
  @SerialName("zos_version")
  @AvailableSince(ZVersion.ZOS_2_1) val zosVersion: String? = null,

  /** zosmf_version response param */
  @SerialName("zosmf_version")
  @AvailableSince(ZVersion.ZOS_2_1) val zosmfVersion: String? = null,

  /** zosmf_hostname response param */
  @SerialName("zosmf_hostname")
  @AvailableSince(ZVersion.ZOS_2_1) val zosmfHostname: String? = null
) : HttpResponse, GetSystemInfoResponse {

  @Serializable
  data class ZosmfPlugin(
    /** pluginVersion response param */
    @SerialName("pluginVersion")
    @AvailableSince(ZVersion.ZOS_2_1) val version: String? = null,

    /** pluginStatus response param */
    @SerialName("pluginStatus")
    @AvailableSince(ZVersion.ZOS_2_1) val status: ZosmfPluginStatus? = null,

    /** pluginDefaultName response param */
    @SerialName("pluginDefaultName")
    @AvailableSince(ZVersion.ZOS_2_1) val defaultName: String? = null
  ) {

    @Serializable
    enum class ZosmfPluginStatus {
      @SerialName("ACTIVE") ACTIVE,
      @SerialName("INSTALLED") INSTALLED,
      @SerialName("UNINSTALLED") UNINSTALLED
    }

  }

}
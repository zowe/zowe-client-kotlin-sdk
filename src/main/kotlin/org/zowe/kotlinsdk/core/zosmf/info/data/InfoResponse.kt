//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.core.zosmf.info.data

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion

/**
 * TODO: doc
 * For more info, please, refer to:
 * https://www.ibm.com/docs/en/zos/2.5.0?topic=service-retrieve-zosmf-information
 */
class InfoResponse(
  /** zosmf_saf_realm response param */
  @AvailableSince(ZVersion.ZOS_2_1) val zosmfSafRealm: String = "null",

  /** zosmf_port response param */
  @AvailableSince(ZVersion.ZOS_2_1) val zosmfPort: String = "null",

  /** zosmf_full_version response param */
  @AvailableSince(ZVersion.ZOS_2_1) val zosmfFullVersion: String = "null",

  /** plugins response param */
  @AvailableSince(ZVersion.ZOS_2_1) val plugins: List<ZosmfPluginItem> = emptyList(),

  /** api_version response param */
  @AvailableSince(ZVersion.ZOS_2_1) val apiVersion: String = "null",

  /** zos_version response param */
  @AvailableSince(ZVersion.ZOS_2_1) val zosVersion: String = "null",

  /** zosmf_version response param */
  @AvailableSince(ZVersion.ZOS_2_1) val zosmfVersion: String = "null",

  /** zosmf_hostname response param */
  @AvailableSince(ZVersion.ZOS_2_1) val zosmfHostname: String = "null"
)
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
class ZosmfPluginItem(
  /** pluginVersion response param */
  @AvailableSince(ZVersion.ZOS_2_1) val version: String? = null,

  /** pluginStatus response param */
  @AvailableSince(ZVersion.ZOS_2_1) val status: String? = null,

  /** pluginDefaultName response param */
  @AvailableSince(ZVersion.ZOS_2_1) val defaultName: String? = null
)
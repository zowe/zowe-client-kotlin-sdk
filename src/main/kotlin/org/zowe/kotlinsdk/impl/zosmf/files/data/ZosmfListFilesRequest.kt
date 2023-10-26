//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.zosmf.files.data

import org.zowe.kotlinsdk.annotations.AvailableOnly
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.files.data.ListFilesRequest

/**
 * TODO: doc
 * For more info, please, refer to:
 * https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-list-files-directories-unix-file-path
 */
class ZosmfListFilesRequest(
  /** path query param */
  @AvailableSince(ZVersion.ZOS_2_1) private val path: String,

  /** X-IBM-Async-Threshold default header */
  @AvailableSince(ZVersion.ZOS_2_1) val asyncThreshold: Int? = null,

  /** X-IBM-Response-Timeout default header */
  @AvailableSince(ZVersion.ZOS_2_1) val responseTimeout: Int? = null,

  /** X-IBM-Session-Limit-Wait default header */
  @AvailableOnly(ZVersion.ZOS_2_4) val sessionLimitWait: Int? = null,

  /** X-IBM-Target-System default header */
  @AvailableSince(ZVersion.ZOS_2_4) val targetSystem: String? = null,

  /** X-IBM-Request-Acctnum default header */
  @AvailableSince(ZVersion.ZOS_2_5) val requestAcctnum: String? = null,

  /** X-IBM-Request-Proc default header */
  @AvailableSince(ZVersion.ZOS_2_5) val requestProc: String? = null,

  /** X-IBM-Request-Region default header */
  @AvailableSince(ZVersion.ZOS_2_5) val requestRegion: String? = null,

  /** X-IBM-Max-Items custom header */
  @AvailableSince(ZVersion.ZOS_2_1) val maxItems: Int? = null,

  /** X-IBM-Lstat custom header */
  @AvailableSince(ZVersion.ZOS_2_1) val xIBMLstat: Boolean? = null,

  /** X-IBM-Target-System-User custom header */
  @AvailableSince(ZVersion.ZOS_2_4) val targetSystemUser: String? = null,

  /** X-IBM-Target-System-Password custom header */
  @AvailableSince(ZVersion.ZOS_2_4) val targetSystemPassword: String? = null,

  /** depth query param */
  @AvailableSince(ZVersion.ZOS_2_3) val depth: Int? = null,

  /** limit query param */
  @AvailableSince(ZVersion.ZOS_2_3) val limit: Int? = null,

  /** filesys query param */
  @AvailableSince(ZVersion.ZOS_2_3) val fileSystem: String? = null,

  /** symlinks query param */
  @AvailableSince(ZVersion.ZOS_2_3) val followSymlinks: SymlinkMode? = null,

  /** group query param */
  @AvailableSince(ZVersion.ZOS_2_3) val group: String? = null,

  /** mtime query param */
  @AvailableSince(ZVersion.ZOS_2_3) val mtime: String? = null,

  /** name query param */
  @AvailableSince(ZVersion.ZOS_2_3) val name: String? = null,

  /** size query param */
  @AvailableSince(ZVersion.ZOS_2_3) val size: String? = null,

  /** perm query param */
  @AvailableSince(ZVersion.ZOS_2_3) val perm: String? = null,

  /** type query param */
  @AvailableSince(ZVersion.ZOS_2_3) val type: String? = null,

  /** user query param */
  @AvailableSince(ZVersion.ZOS_2_3) val user: String? = null,
) : ListFilesRequest(filter = path)

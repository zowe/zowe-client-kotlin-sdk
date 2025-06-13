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

package org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.definitions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.datasets.data.MemberItem

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=zdsfri-json-document-specifications-zos-data-set-file-rest-interface-requests#RESTFILES_JSONDocumentSpecifications__pdskeyPairs">JSON document specifications for z/OS data set and file REST interface requests: PDS/PDSE member list with attributes document (RECFM=F or V)</a>
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=zdsfri-json-document-specifications-zos-data-set-file-rest-interface-requests#RESTFILES_JSONDocumentSpecifications__pdsUkeypairs">JSON document specifications for z/OS data set and file REST interface requests: PDS/PDSE member list with attributes document (RECFM=U)</a>
 */
@Serializable
class ZosmfMemberItem(
  /** member response param */
  @SerialName("member")
  @AvailableSince(ZVersion.ZOS_2_1) override val memberName: String,

  /** vers response param */
  @SerialName("vers")
  @AvailableSince(ZVersion.ZOS_2_1) val versionNumber: Int? = null,

  /** mod response param */
  @SerialName("mod")
  @AvailableSince(ZVersion.ZOS_2_1) val modificationLevel: Int? = null,

  /** c4date response param */
  @SerialName("c4date")
  @AvailableSince(ZVersion.ZOS_2_1) val creationDate: String? = null,

  /** m4date response param */
  @SerialName("m4date")
  @AvailableSince(ZVersion.ZOS_2_1) val modificationDate: String? = null,

  /** cnorc response param */
  @SerialName("cnorc")
  @AvailableSince(ZVersion.ZOS_2_1) val currentNumberOfRecords: Int? = null,

  /** inorc response param */
  @SerialName("inorc")
  @AvailableSince(ZVersion.ZOS_2_1) val beginningNumberOfRecords: Int? = null,

  /** mnorc response param */
  @SerialName("mnorc")
  @AvailableSince(ZVersion.ZOS_2_1) val numberOfChangedRecords: Int? = null,

  /** mtime response param */
  @SerialName("mtime")
  @AvailableSince(ZVersion.ZOS_2_1) val lastChangeTime: String? = null,

  /** msec response param */
  @SerialName("msec")
  @AvailableSince(ZVersion.ZOS_2_1) val secondsOfLastChangeTime: String? = null,

  /** user response param */
  @SerialName("user")
  @AvailableSince(ZVersion.ZOS_2_1) val user: String? = null,

  /** sclm response param */
  @SerialName("sclm")
  @AvailableSince(ZVersion.ZOS_2_1) val sclm: String? = null,

  /** ac response param */
  @SerialName("ac")
  @AvailableSince(ZVersion.ZOS_2_1) val authorizationCode: String? = null,

  /** alias-of response param */
  @SerialName("alias-of")
  @AvailableSince(ZVersion.ZOS_2_1) val aliasOf: String? = null,

  /** amode response param */
  @SerialName("amode")
  @AvailableSince(ZVersion.ZOS_2_1) val amode: String? = null,

  /** attr response param */
  @SerialName("attr")
  @AvailableSince(ZVersion.ZOS_2_1) val loadModuleAttributes: String? = null,

  /** rmode response param */
  @SerialName("rmode")
  @AvailableSince(ZVersion.ZOS_2_1) val rmode: String? = null,

  /** size response param */
  @SerialName("size")
  @AvailableSince(ZVersion.ZOS_2_1) val size: String? = null,

  /** ttr response param */
  @SerialName("ttr")
  @AvailableSince(ZVersion.ZOS_2_1) val ttr: String? = null,

  /** ssi response param */
  @SerialName("ssi")
  @AvailableSince(ZVersion.ZOS_2_1) val ssi: String? = null
) : MemberItem

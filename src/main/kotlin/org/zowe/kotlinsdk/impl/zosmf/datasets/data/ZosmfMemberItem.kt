//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.zosmf.datasets.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.datasets.data.MemberItem

/**
 * TODO: doc
 * For more info, please, refer to:
 * https://ibm.com/docs/en/zos/2.5.0?topic=zdsfri-json-document-specifications-zos-data-set-file-rest-interface-requests#RESTFILES_JSONDocumentSpecifications__dataSetListWithAttributes__title__1
 */
class ZosmfMemberItem(
  /** member response param */
  @SerializedName("member")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) override val memberName: String,

  /** vers response param */
  @SerializedName("vers")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val versionNumber: Int? = null,

  /** mod response param */
  @SerializedName("mod")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val modificationLevel: Int? = null,

  /** c4date response param */
  @SerializedName("c4date")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val creationDate: String? = null,

  /** m4date response param */
  @SerializedName("m4date")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val modificationDate: String? = null,

  /** cnorc response param */
  @SerializedName("cnorc")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val currentNumberOfRecords: Int? = null,

  /** inorc response param */
  @SerializedName("inorc")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val beginningNumberOfRecords: Int? = null,

  /** mnorc response param */
  @SerializedName("mnorc")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val numberOfChangedRecords: Int? = null,

  /** mtime response param */
  @SerializedName("mtime")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val lastChangeTime: String? = null,

  /** msec response param */
  @SerializedName("msec")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val secondsOfLastChangeTime: String? = null,

  /** user response param */
  @SerializedName("user")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val user: String? = null,

  /** sclm response param */
  @SerializedName("sclm")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val sclm: String? = null,

  /** ac response param */
  @SerializedName("ac")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val authorizationCode: String? = null,

  /** alias-of response param */
  @SerializedName("alias-of")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val aliasOf: String? = null,

  /** amode response param */
  @SerializedName("amode")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val amode: String? = null,

  /** attr response param */
  @SerializedName("attr")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val loadModuleAttributes: String? = null,

  /** rmode response param */
  @SerializedName("rmode")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val rmode: String? = null,

  /** size response param */
  @SerializedName("size")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val size: String? = null,

  /** ttr response param */
  @SerializedName("ttr")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val ttr: String? = null,

  /** ssi response param */
  @SerializedName("ssi")
  @Expose
  @AvailableSince(ZVersion.ZOS_2_1) val ssi: String? = null
) : MemberItem(memberName)

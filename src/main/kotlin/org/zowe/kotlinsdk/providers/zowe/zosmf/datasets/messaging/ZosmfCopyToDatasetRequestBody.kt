/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.messaging

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.datasets.data.FromEntity

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-zos-data-set-member-utilities#IZUHPINFO_API_PutDataSetMemberUtilities__title__6">z/OS data set and member utilities: Request body</a>
 * @property fromFile the file to copy
 * @property fromDataset the dataset to copy
 * @property enq enq request body param
 * @property replace when from-file specified, ignored unless from-file/type=text.
 *                   If true, members in the target data set are replaced.
 *                   If false(default), like named members are not copied and an error is returned.
 */
@Serializable
data class ZosmfCopyToDatasetRequestBody(
  @SerialName("from-file")
  @property:AvailableSince(ZVersion.ZOS_2_1) val fromFile: FromFile? = null,

  @SerialName("from-dataset")
  @property:AvailableSince(ZVersion.ZOS_2_1) val fromDataset: FromDataset? = null,

  @SerialName("enq")
  @property:AvailableSince(ZVersion.ZOS_2_1) val enq: Enq?,

  @SerialName("replace")
  @property:AvailableSince(ZVersion.ZOS_2_1) val replace: Boolean?
) {
  @SerialName("request")
  @property:AvailableSince(ZVersion.ZOS_2_1) val request: String = "copy"

  @Serializable
  enum class FileType {
    @SerialName("binary") BIN,
    @SerialName("text") TEXT,
    @SerialName("executable") EXE
  }

  /**
   * The file to copy
   * @property entityName the absolute source filename
   * @property type default is [FileType.TEXT]
   */
  @Serializable
  data class FromFile(
    @SerialName("filename")
    override val entityName: String,

    @SerialName("type")
    val type: FileType = FileType.TEXT
  ) : FromEntity

  /**
   * The data set to copy
   * @property entityName the source dataset
   * @property memberName used to specify a member; "*" means all members
   * @property volser may be specified if dsn is not cataloged
   * @property alias if true, aliases are copied along with main member;
   *                 if false(default), alias relationships are not maintained
   */
  @Serializable
  data class FromDataset(
    @SerialName("dsn")
    override val entityName: String,

    @SerialName("member")
    val memberName: String? = null,

    @SerialName("volser")
    val volser: String? = null,

    @SerialName("alias")
    val alias: Boolean? = null,
  ) : FromEntity

  @Serializable
  enum class Enq {
    @SerialName("SHR") SHR,
    @SerialName("SHRW") SHRW,
    @SerialName("EXCLU") EXCLU
  }
}

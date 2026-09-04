/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.openssh.datasets.definitions

import org.zowe.kotlinsdk.core.datasets.data.FromEntity

/**
 * The data set or the data set member to copy to a data set or a data set member from.
 * Note that the "volser" and the "alias" options, available in the z/OSMF provider, have no "cp" command
 * counterparts, so they are not represented here
 * @property entityName the name of the data set to copy from
 * @property memberName the name of the data set member to copy from. If not provided, the whole data set
 *                      is copied (all the members of a partitioned data set, respectively)
 * @see <a href="https://www.ibm.com/docs/en/zos/latest?topic=descriptions-cp-copy-file">cp - Copy a file</a>
 */
data class SshFromDataset(
  override val entityName: String,
  val memberName: String? = null
) : FromEntity {
  /** Build the source operand of the "cp" command for the data set or the data set member */
  fun buildCpSourceOperand(): String {
    val formedDsName = if (!memberName.isNullOrEmpty()) "$entityName($memberName)" else entityName
    return "\"//'$formedDsName'\""
  }
}

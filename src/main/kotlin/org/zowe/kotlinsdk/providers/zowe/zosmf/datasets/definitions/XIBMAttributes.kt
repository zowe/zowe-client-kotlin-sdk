/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.definitions

import org.zowe.kotlinsdk.core.datasets.AttributesLevel

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-list-zos-data-sets-system#ListDataSets__title__5">List the z/OS data sets on a system: X-IBM-Attributes</a>
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-list-members-zos-data-set#ListDataSetMembers__title__3">List the members of a z/OS data set: Custom headers</a>
 * @property attributesLevel data set level of attributes to return
 * @property isMemberList if true, the request is considered as the "return member names only" request,
 *                        if false, the attributes level is applied to the data set entries
 * @property isTotal if true, total number of entries on the remote is returned as a separate field ("totalRows")
 */
data class XIBMAttributes(
  private val attributesLevel: AttributesLevel = AttributesLevel.FULL,
  private val isMemberList: Boolean = false,
  private val isTotal: Boolean = false
) {
  private var attributesLevelStr: String = when (attributesLevel) {
    AttributesLevel.NAME -> if (isMemberList) "member" else "dsname"
    AttributesLevel.VOLSER -> if (isMemberList) "member" else "vol"
    AttributesLevel.FULL -> "base"
  }

  override fun toString(): String {
    return if(isTotal) "${attributesLevelStr},total" else attributesLevelStr
  }
}

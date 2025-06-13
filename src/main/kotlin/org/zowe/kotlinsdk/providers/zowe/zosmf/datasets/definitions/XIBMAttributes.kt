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

/**
 * Request type.
 * Setting the X-IBM-Attribute to base returns all the basic attributes for the data set / data set member
 * being queried.
 * Setting the X-IBM-Attribute to vol returns the volume where the data set resides.
 */
open class ReqType {
  companion object {
    const val BASE = "base"
    const val VOL = "vol"
  }
}

/** Requests that only data set names be returned. */
class DsReqType : ReqType() {
  companion object {
    const val DSNAME = "dsname"
  }
}

/** A request that only member names be returned. */
class MemReqType : ReqType() {
  companion object {
    const val MEMBER = "member"
  }
}

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-list-zos-data-sets-system#ListDataSets__title__5">List the z/OS data sets on a system: X-IBM-Attributes</a>
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-list-members-zos-data-set#ListDataSetMembers__title__3">List the members of a z/OS data set: Custom headers</a>
 */
data class XIBMAttributes(private var type: String = ReqType.BASE, private val isTotal: Boolean = false) {
  init {
    if (!(type == ReqType.BASE || type == ReqType.VOL || type == DsReqType.DSNAME || type == MemReqType.MEMBER)) {
      type = ReqType.BASE
    }
  }

  override fun toString(): String {
    return if(isTotal) "${type},total" else type
  }
}

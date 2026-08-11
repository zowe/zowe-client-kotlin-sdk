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

/** SSH-compatible data set type */
enum class SshDatasetType {
  LIBRARY,
  LIBRARY_1,
  LIBRARY_2,
  HSF,
  PDS,
  EXTREQ,
  EXTREQ_1,
  EXTREQ_2,
  EXTPREF,
  EXTPREF_1,
  EXTPREF_2,
  BASIC,
  LARGE;

  /** Build a string of the data set type for ALLOC TSO command */
  fun buildDsnTypeForAlloc(): String {
    return StringBuilder(" DSNTYPE(")
      .append(when (this) {
        LIBRARY_1 -> "LIBRARY,1"
        LIBRARY_2 -> "LIBRARY,2"
        EXTREQ_1 -> "EXTREQ,1"
        EXTREQ_2 -> "EXTREQ,2"
        EXTPREF_1 -> "EXTPREF,1"
        EXTPREF_2 -> "EXTPREF,2"
        else -> "$this"
      })
      .append(")")
      .toString()
  }
}
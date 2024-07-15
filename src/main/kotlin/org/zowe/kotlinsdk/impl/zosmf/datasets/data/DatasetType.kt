// Copyright (c) 2024 IBA Group.
//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Contributors:
//   IBA Group
//   Zowe Community

package org.zowe.kotlinsdk.impl.zosmf.datasets.data

/**
 * z/OS dataset types
 * @see <a href="https://www.ibm.com/docs/en/zos/2.5.0?topic=statement-dsntype-parameter">DSNTYPE parameter</a>
 * */
enum class DatasetType(private val type: String) {
  LIBRARY("LIBRARY"),
  HFS("HFS"),
  PDS("PDS"),
  EXTREQ("EXTREQ"),
  EXTPREF("EXTREF"),
  LARGE("LARGE"),
  BASIC("BASIC");

  override fun toString(): String {
    return this.type
  }
}

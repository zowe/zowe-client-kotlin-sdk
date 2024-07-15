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

// TODO: doc
enum class DatasetOrganization(private val type: String) {
  PO("PO"),
  POU("POU"),

  //TODO: not found
  POE("PO-E"),
  PS("PS"),
  PSU("PSU"),
  DA("DA"),
  DAU("DAU"),

  //TODO: not found
  VS("VS");

  override fun toString(): String {
    return this.type
  }
}

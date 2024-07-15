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
 * z/OS datasets record formats
 * @see <a href="https://www.ibm.com/docs/en/zos/2.5.0?topic=statement-recfm-parameter">RECFM parameter</a>
 * */
enum class RecordFormat(private val type: String) {

  D("D"),
  DA("DA"),
  DB("DB"),
  DBA("DBA"),
  DBS("DBS"),
  DBSA("DBSA"),
  DS("DS"),
  DSA("DSA"),
  F("F"),
  FA("FA"),
  FB("FB"),
  FBA("FBA"),
  FBM("FBM"),
  FBS("FBS"),
  FBSA("FBSA"),
  FBSM("FBSM"),
  FBT("FBT"),
  FBTA("FBTA"),
  FBTM("FBTM"),
  FM("FM"),
  FS("FS"),
  FSA("FSA"),
  FSM("FSM"),
  FT("FT"),
  FTA("FTA"),
  FTM("FTM"),
  U("U"),
  UA("UA"),
  UM("UM"),
  UT("UT"),
  UTA("UTA"),
  UTM("UTM"),
  V("V"),
  VA("VA"),
  VB("VB"),
  VBA("VBA"),
  VBM("VBM"),
  VBS("VBS"),
  VBSA("VBSA"),
  VBSM("VBSM"),
  VBST("VBST"),
  VBSTA("VBSTA"),
  VBSTM("VBSTM"),
  VBT("VBT"),
  VBTA("VBTA"),
  VBTM("VBTM"),
  VM("VM"),
  VS("VS"),
  VSA("VSA"),
  VSM("VSM"),
  VT("VT"),
  VTA("VTA"),
  VTM("VTM");

  override fun toString(): String {
    return this.type
  }
}

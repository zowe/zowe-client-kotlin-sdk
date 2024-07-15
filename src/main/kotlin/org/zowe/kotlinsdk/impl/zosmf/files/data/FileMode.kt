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

package org.zowe.kotlinsdk.impl.zosmf.files.data

class FileMode(
  var owner: Int,
  var group: Int = 0,
  var all: Int = 0,
  var prefix: String = ""
) {

  constructor(modeString: CharSequence, prefix: String = "") : this(
    stringToDigit(modeString.subSequence(1, 4)),
    stringToDigit(modeString.subSequence(4, 7)),
    stringToDigit(modeString.subSequence(7, 10)),
    prefix
  )

  constructor (
    owner: FileModeValue,
    group: FileModeValue = FileModeValue.NONE,
    all: FileModeValue = FileModeValue.NONE,
    prefix: String = ""
  ) : this(owner = owner.mode, group = group.mode, all = all.mode, prefix)


  override fun toString(): String {
    return prefix + digitToString(owner) + digitToString(group) + digitToString(all)
  }

  enum class FileModeValue(val mode: Int) {
    NONE(0),
    EXECUTE(1),
    WRITE(2),
    WRITE_EXECUTE(3),
    READ(4),
    READ_EXECUTE(5),
    READ_WRITE(6),
    READ_WRITE_EXECUTE(7)
  }
}

fun stringToDigit(modeString: CharSequence): Int {
  return when (modeString) {
    "---" -> 0
    "--x" -> 1
    "-w-" -> 2
    "-wx" -> 3
    "r--" -> 4
    "r-x" -> 5
    "rw-" -> 6
    "rwx" -> 7
    else -> 0
  }
}

fun digitToString(digit: Int): String {
  return when (digit) {
    0 -> "---"
    1 -> "--x"
    2 -> "-w-"
    3 -> "-wx"
    4 -> "r--"
    5 -> "r-x"
    6 -> "rw-"
    7 -> "rwx"
    else -> "---"
  }
}

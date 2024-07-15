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

package org.zowe.kotlinsdk.impl.zosmf.files.api.messaging

/**
 * Request body for create file or directory request
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-create-unix-file-directory#CreateUnixFile__Reqbod">Request body to create a UNIX file or directory</a>
 * */
data class ZosmfCreateFileBody(
  /**
   * The request type. This field supports the values: directory or dir to create a directory.
   * The value: file is supported to create a file
   * */
  var type: Type,

  /**
   * Specifies the file or directory permission bits to be used in creating the file or directory.
   * The characters used to describe permissions are:
   *
   * r: Permission to read the file
   *
   * w: Permission to write on the file
   *
   * x: Permission to execute the file
   *
   * -: No permission
   *
   * An example would be: rwxrwxrwx
   * */
  var mode: String
) {
  enum class Type(private val type: String) {
    DIRECTORY("directory"),
    DIR("dir"),
    FILE("file");

    override fun toString(): String {
      return this.type
    }
  }
}

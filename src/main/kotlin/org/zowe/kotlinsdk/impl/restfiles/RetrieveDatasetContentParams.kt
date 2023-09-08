//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.restfiles

// TODO: doc
class RetrieveDatasetContentParams(

  /**
   * The local file to download the data set contents to, e.g. "./path/to/file.txt"
   */
  val file: String ?= null,

  /**
   * The indicator to force return of ETag.
   * If set to 'true' it forces the response to include an "ETag" header, regardless of the size of the response data.
   * If it is not present, the default is to only send an Etag for data sets smaller than a system determined length,
   * which is at least 8 MB.
   */
  val returnEtag: Boolean ?= null,

  /**
   * The volume on which the data set is stored
   */
  val volume: String ?= null

)
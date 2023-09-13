//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.zosmf.restfiles.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.zowe.kotlinsdk.core.zosmf.restfiles.data.DataSetListDocument

// TODO: doc
internal class DataSetListDocumentGson(
  @SerializedName("items")
  @Expose
  val items: List<DatasetItemGson>,

  @SerializedName("returnedRows")
  @Expose
  val returnedRows: Int,

  @SerializedName("totalRows")
  @Expose
  val totalRows: Int?,

  @SerializedName("JSONversion")
  @Expose
  val jsonVersion: Int,

  @Transient
  val converted: DataSetListDocument = DataSetListDocument(
    items.map { it.converted },
    returnedRows,
    totalRows,
    jsonVersion
  )
)
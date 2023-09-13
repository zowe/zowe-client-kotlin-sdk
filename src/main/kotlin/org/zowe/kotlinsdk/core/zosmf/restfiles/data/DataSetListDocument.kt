//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.core.zosmf.restfiles.data

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion

/**
 * TODO: doc
 * For more info, please, refer to:
 * https://www.ibm.com/docs/en/zos/2.5.0?topic=zdsfri-json-document-specifications-zos-data-set-file-rest-interface-requests#RESTFILES_JSONDocumentSpecifications__RESTFILES_data_set_list__title__1
 */
class DataSetListDocument(
  /** items response param */
  @AvailableSince(ZVersion.ZOS_2_1) val items: List<DatasetItem> = emptyList(),

  /** returnedRows response param */
  @AvailableSince(ZVersion.ZOS_2_1) val returnedRows: Int = 0,

  /** totalRows response param */
  @AvailableSince(ZVersion.ZOS_2_1) val totalRows: Int? = null,

  /** JSONversion response param */
  @AvailableSince(ZVersion.ZOS_2_1) val jsonVersion: Int = 0
)
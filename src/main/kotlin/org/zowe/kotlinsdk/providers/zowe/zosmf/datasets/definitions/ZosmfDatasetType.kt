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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=statement-dsntype-parameter">DSNTYPE parameter</a> */
@Serializable
enum class ZosmfDatasetType {
  @SerialName("LIBRARY") LIBRARY,
  @SerialName("HFS") HFS,
  @SerialName("PDS") PDS,
  @SerialName("EXTREQ") EXTREQ,
  @SerialName("EXTPREF") EXTPREF,
  @SerialName("LARGE") LARGE,
  @SerialName("BASIC") BASIC
}

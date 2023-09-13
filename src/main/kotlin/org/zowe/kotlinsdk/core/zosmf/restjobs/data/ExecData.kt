//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.core.zosmf.restjobs.data

/**
 * TODO: doc
 * For more info, please, refer to:
 * https://www.ibm.com/docs/en/zos/2.5.0?topic=interface-obtain-status-job
 */
enum class ExecData(val value: String) {
  YES("Y"),
  NO("N");

  override fun toString(): String {
    return value
  }
}
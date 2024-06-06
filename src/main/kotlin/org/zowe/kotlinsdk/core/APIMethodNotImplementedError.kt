//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.core

// TODO: doc
class APIMethodNotImplementedError(methodName: String, apiType: String? = null) :
  Error("$methodName is not implemented for the ${apiType ?: "provided"} connection type")
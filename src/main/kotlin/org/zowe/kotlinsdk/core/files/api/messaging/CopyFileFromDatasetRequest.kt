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

package org.zowe.kotlinsdk.core.files.api.messaging

import org.zowe.kotlinsdk.core.HttpRequest

/**
 * Represents basic request to copy file or directory from dataset
 * @param fpName identifies the file or directory to be the target of the operation
 * */
abstract class CopyFileFromDatasetRequest(
  open val fpName: String
) : HttpRequest

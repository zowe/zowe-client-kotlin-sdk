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
 * Represents basic request to delete file or directory
 * @param fpName name of the file or directory, that should be deleted
 * */
abstract class DeleteFileRequest(
  open val fpName: String
) : HttpRequest

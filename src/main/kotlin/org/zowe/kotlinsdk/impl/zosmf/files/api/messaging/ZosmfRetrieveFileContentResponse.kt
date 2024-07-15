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

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.files.api.messaging.RetrieveFileContentResponse

/**
 * The response body for [ZosmfRetrieveFileContentRequest]
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=interface-retrieve-contents-zos-unix-file">Retrieve the contents of a z/OS UNIX file</a>
 * */
class ZosmfRetrieveFileContentResponse(
  /** text content as the response */
  @AvailableSince(ZVersion.ZOS_2_1) val fetchedText: String? = null,

  /** bytes content as the response */
  @AvailableSince(ZVersion.ZOS_2_1) val fetchedBytes: ByteArray? = null
) : RetrieveFileContentResponse()

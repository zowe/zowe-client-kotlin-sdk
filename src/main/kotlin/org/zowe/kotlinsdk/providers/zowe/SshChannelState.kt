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

package org.zowe.kotlinsdk.providers.zowe

/**
 * SSH channel state.
 * READY - read is not yet started, command is not yet executed
 * READ_STARTED - the command is already executed, channel read is possible
 * COMPLETE - the channel reading is completed and is not possible anymore
 * CLOSED - the channel is closed, SSH status object is formed, channel is not possible to operate anymore
 */
enum class SshChannelState {
  READY,
  READ_STARTED,
  COMPLETE,
  CLOSED
}

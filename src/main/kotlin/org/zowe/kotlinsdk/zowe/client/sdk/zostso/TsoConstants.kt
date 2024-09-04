/*
 * Copyright (c) 2020-2024 IBA Group.
 *
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   IBA Group
 *   Zowe Community
 */

package org.zowe.kotlinsdk.zowe.client.sdk.zostso

/**
 * Constants for various tso related info
 */
object TsoConstants {
  /**
   * Default character-set value
   */
  const val DEFAULT_CHSET = "697"

  /**
   * Default number of columns value
   */
  const val DEFAULT_COLS = "80"

  /**
   * Default code page value
   */
  const val DEFAULT_CPAGE = "1047"

  /**
   * Default logonProcedure value
   */
  const val DEFAULT_PROC = "IZUFPROC"

  /**
   * Default number of rows value
   */
  const val DEFAULT_ROWS = "24"

  /**
   * Default region-size value
   */
  const val DEFAULT_RSIZE = "4096"

  /**
   * z/OSMF unknown error
   */
  const val ZOSMF_UNKNOWN_ERROR = "zOSMF unknown error response"
}

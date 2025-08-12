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

package org.zowe.kotlinsdk.core.datasets

/**
 * The attributes level for data sets:
 *  - [DSNAME] - returned data sets list will contain data set names only
 *  - [VOLSER] - returned data sets list will contain data set names and volumes
 *  - [FULL] - returned data sets list will contain all data set base attributes
 */
sealed interface AttributesLevel {
  object DSNAME : AttributesLevel
  object VOLSER : AttributesLevel
  object FULL : AttributesLevel
}

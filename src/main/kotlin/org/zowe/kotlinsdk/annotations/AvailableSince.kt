/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright IBA Group 2020
 */

package org.zowe.kotlinsdk.annotations

import java.lang.annotation.Inherited

private const val osVersionPrefix = "z/OS"

enum class ZVersion(val version: String) {
  ZOS_2_1("$osVersionPrefix 2.1"),
  ZOS_2_2("$osVersionPrefix 2.2"),
  ZOS_2_3("$osVersionPrefix 2.3"),
  ZOS_2_4("$osVersionPrefix 2.4"),
  ZOS_2_5("$osVersionPrefix 2.5")
}

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
@Inherited
annotation class AvailableSince(val version: ZVersion)

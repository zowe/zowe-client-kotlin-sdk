/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe

/** Annotation class to restrict access to specified internal Zowe API classes */
@RequiresOptIn(message = "This class functionality provides interaction with internal Zowe APIs and needs an opt-in. Do the opt-in only in specified places to avoid incorrect functionality usage.")
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class ZoweInternalAPI

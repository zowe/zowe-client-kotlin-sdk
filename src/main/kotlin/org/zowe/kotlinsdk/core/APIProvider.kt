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

package org.zowe.kotlinsdk.core

/**
 * An API provider abstraction to provide a basic implementation of the API providing mechanism
 * @param initialAPIs the initial API instances to provide an appropriate functionality implementation
 */
abstract class APIProvider(vararg initialAPIs: Pair<Class<out API>, API>) {
  abstract val supportedProtocols: List<SupportedProtocol>

  private val apiInstances: MutableMap<Class<out API>, API> = mutableMapOf()

  init {
    for ((apiClass, instance) in initialAPIs) {
      apiInstances[apiClass] = instance
    }
  }

    /**
   * Get API of the provided API base class
   * @param T the type of the API to get
   * @param apiClass the API base class to get API instance by
   * @return the [API] implementation instance if the provider has it, [IllegalArgumentException] otherwise
   */
  fun <T : API> getApi(apiClass: Class<T>): T {
    @Suppress("UNCHECKED_CAST")
    apiInstances[apiClass]?.let { return it as T }
    throw IllegalArgumentException("API not found for class: ${apiClass.simpleName}")
  }
}

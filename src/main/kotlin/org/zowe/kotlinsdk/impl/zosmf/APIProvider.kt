//
// This program and the accompanying materials are made available under the terms of the
// Eclipse Public License v2.0 which accompanies this distribution, and is available at
// https://www.eclipse.org/legal/epl-v20.html
//
// SPDX-License-Identifier: EPL-2.0
//
// Copyright IBA Group 2020
//

package org.zowe.kotlinsdk.impl.zosmf

import org.zowe.kotlinsdk.core.API

/**
 * Carries and provides the needed API instances
 * @constructor accepts initial API instances to save on initialization of the provider
 */
abstract class APIProvider(vararg initialAPIs: Pair<Class<out API>, API>) {
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

  /**
   * Save the provided API instance in the API provider
   * @param T the type of the API to save
   * @param apiClass the API base class to save the API instance under
   */
  fun <T : API> saveApi(apiClass: Class<T>, instance: T) {
    apiInstances[apiClass] = instance
  }
}

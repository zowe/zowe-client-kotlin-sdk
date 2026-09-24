/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk

import org.junit.jupiter.api.Test
import org.zowe.kotlinsdk.zowe.client.sdk.core.ZOSConnection
import org.zowe.kotlinsdk.zowe.config.ZoweConfigProfile
import org.zowe.kotlinsdk.zowe.config.ZoweConnection

internal class ConnectionRedactionTest {

  private val secret = "s3cr3tPassw0rd"

  @Test
  fun zosConnectionDoesNotRenderThePassword() {
    val rendered = ZOSConnection("test.host.com", "10443", "user", secret).toString()
    assert(!rendered.contains(secret)) { "The password leaked into the rendered connection: $rendered" }
    assert(rendered.contains("password='***'")) { rendered }
    assert(rendered.contains("host='test.host.com'") && rendered.contains("user='user'")) { rendered }
  }

  @Test
  fun zosConnectionDoesNotRenderThePasswordWhenInterpolated() {
    assert(!"${ZOSConnection("test.host.com", "10443", "user", secret)}".contains(secret))
  }

  @Test
  fun zoweConnectionDoesNotRenderThePassword() {
    val rendered = ZoweConnection("test.host.com", 10443, "user", secret).toString()
    assert(!rendered.contains(secret)) { "The password leaked into the rendered connection: $rendered" }
    assert(rendered.contains("password=***")) { rendered }
    assert(rendered.contains("host=test.host.com") && rendered.contains("user=user")) { rendered }
  }

  @Test
  fun zoweConnectionDistinguishesAnAbsentPassword() {
    assert(ZoweConnection("test.host.com", 10443, "user", null).toString().contains("password=null"))
  }

  @Test
  fun zoweConfigProfileDoesNotRenderTheCredentials() {
    val profile = ZoweConfigProfile(
      name = "zosmf",
      type = "zosmf",
      properties = mutableMapOf(
        "host" to "test.host.com",
        "user" to "user",
        "password" to secret,
        "tokenValue" to secret,
        "customSecret" to secret
      ),
      secure = arrayListOf("customSecret"),
      profiles = null,
      parentProfile = null
    )
    val rendered = profile.toString()
    assert(!rendered.contains(secret)) { "A credential leaked into the rendered profile: $rendered" }
    assert(rendered.contains("host=test.host.com")) { rendered }
  }

  @Test
  fun zoweConfigProfileDoesNotRepeatTheParentProfile() {
    val parent = ZoweConfigProfile("base", "base", mutableMapOf("password" to secret), null, null, null)
    val child = ZoweConfigProfile("zosmf", "zosmf", mutableMapOf(), null, null, parent)
    val rendered = child.toString()
    assert(!rendered.contains(secret)) { rendered }
    assert(rendered.contains("parentProfile=base")) { rendered }
  }
}

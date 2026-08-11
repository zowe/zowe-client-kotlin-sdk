/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.files.definitions

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.zowe.kotlinsdk.core.files.data.FilePermissions
import org.zowe.kotlinsdk.core.files.data.PermissionSet

/** z/OSMF-specific USS file mode representation */
@Serializable(with = ZosmfFileModeSerializer::class)
class ZosmfFileMode(
  zosmfOwner: PermissionSet,
  zosmfGroup: PermissionSet,
  zosmfOthers: PermissionSet
) : FilePermissions(owner = zosmfOwner, group = zosmfGroup, others = zosmfOthers) {
  companion object {
    fun fromString(str: String): ZosmfFileMode {
      val filePermissions = FilePermissions.fromString(str)
      return ZosmfFileMode(
        zosmfOwner = filePermissions.owner,
        zosmfGroup = filePermissions.group,
        zosmfOthers = filePermissions.others
      )
    }
  }
}

object ZosmfFileModeSerializer : KSerializer<ZosmfFileMode> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ZosmfFileMode", PrimitiveKind.STRING)

  override fun deserialize(decoder: Decoder): ZosmfFileMode {
    val str = decoder.decodeString()
    return ZosmfFileMode.fromString(str)
  }

  override fun serialize(encoder: Encoder, value: ZosmfFileMode) {
    encoder.encodeString(value.toString())
  }
}

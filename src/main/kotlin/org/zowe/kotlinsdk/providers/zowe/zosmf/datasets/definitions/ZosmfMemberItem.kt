/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.zosmf.datasets.definitions

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.datasets.data.MemberItem
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=zdsfri-json-document-specifications-zos-data-set-file-rest-interface-requests#RESTFILES_JSONDocumentSpecifications__pdskeyPairs">JSON document specifications for z/OS data set and file REST interface requests: PDS/PDSE member list with attributes document (RECFM=F or V)</a>
 * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=zdsfri-json-document-specifications-zos-data-set-file-rest-interface-requests#RESTFILES_JSONDocumentSpecifications__pdsUkeypairs">JSON document specifications for z/OS data set and file REST interface requests: PDS/PDSE member list with attributes document (RECFM=U)</a>
 * @property memberName the member response param
 * @property versionNumber the vers response param
 * @property modificationLevel the mod response param
 * @property creationDate the c4date response param
 * @property modificationDate the m4date response param
 * @property currentNumberOfRecords the cnorc response param
 * @property beginningNumberOfRecords the inorc response param
 * @property numberOfChangedRecords the mnorc response param
 * @property lastChangeTime the mtime response param
 * @property secondsOfLastChangeTime the msec response param
 * @property user the user response param
 * @property modifiedIn the sclm response param
 * @property authorizationCode the ac response param (RECFM=U)
 * @property aliasOf the alias-of response param (RECFM=U)
 * @property amode the amode response param (RECFM=U)
 * @property loadModuleAttributes the attr response param (RECFM=U)
 * @property rmode the rmode response param (RECFM=U)
 * @property size the size response param (RECFM=U)
 * @property ttr the ttr response param (RECFM=U)
 * @property ssi the ssi response param (RECFM=U)
 */
@Serializable
class ZosmfMemberItem(
  @SerialName("member")
  @property:AvailableSince(ZVersion.ZOS_2_1) override val memberName: String,

  @SerialName("vers")
  @property:AvailableSince(ZVersion.ZOS_2_1) val versionNumber: Int? = null,

  @SerialName("mod")
  @property:AvailableSince(ZVersion.ZOS_2_1) val modificationLevel: Int? = null,

  @SerialName("c4date")
  @Serializable(with = LocalDateSerializer::class)
  @property:AvailableSince(ZVersion.ZOS_2_1) val creationDate: LocalDate? = null,

  @SerialName("m4date")
  @Serializable(with = LocalDateSerializer::class)
  @property:AvailableSince(ZVersion.ZOS_2_1) val modificationDate: LocalDate? = null,

  @SerialName("cnorc")
  @property:AvailableSince(ZVersion.ZOS_2_1) val currentNumberOfRecords: Int? = null,

  @SerialName("inorc")
  @property:AvailableSince(ZVersion.ZOS_2_1) val beginningNumberOfRecords: Int? = null,

  @SerialName("mnorc")
  @property:AvailableSince(ZVersion.ZOS_2_1) val numberOfChangedRecords: Int? = null,

  @SerialName("mtime")
  @property:AvailableSince(ZVersion.ZOS_2_1) val lastChangeTime: String? = null,

  @SerialName("msec")
  @property:AvailableSince(ZVersion.ZOS_2_1) val secondsOfLastChangeTime: Int? = null,

  @SerialName("user")
  @property:AvailableSince(ZVersion.ZOS_2_1) val user: String? = null,

  @SerialName("sclm")
  @Serializable(with = ModifiedInSerializer::class)
  @property:AvailableSince(ZVersion.ZOS_2_1) val modifiedIn: ModifiedIn? = null,

  @SerialName("ac")
  @property:AvailableSince(ZVersion.ZOS_2_1) val authorizationCode: String? = null,

  @SerialName("alias-of")
  @property:AvailableSince(ZVersion.ZOS_2_1) val aliasOf: String? = null,

  @SerialName("amode")
  @property:AvailableSince(ZVersion.ZOS_2_1) val amode: String? = null,

  @SerialName("attr")
  @property:AvailableSince(ZVersion.ZOS_2_1) val loadModuleAttributes: String? = null,

  @SerialName("rmode")
  @property:AvailableSince(ZVersion.ZOS_2_1) val rmode: String? = null,

  @SerialName("size")
  @property:AvailableSince(ZVersion.ZOS_2_1) val size: String? = null,

  @SerialName("ttr")
  @property:AvailableSince(ZVersion.ZOS_2_1) val ttr: String? = null,

  @SerialName("ssi")
  @property:AvailableSince(ZVersion.ZOS_2_1) val ssi: String? = null
) : MemberItem {
  /** Indicates whether the member was last modified by SCLM or ISPF, or by some other tools */
  enum class ModifiedIn {
    SCLM,
    ISPF,
    UNDEFINED
  }

  /** [ModifiedIn] enum serializer. Transforms "Y" into "SCLM", "N" into "ISPF", and null into "UNDEFINED" */
  class ModifiedInSerializer : KSerializer<ModifiedIn> {
    override val descriptor = PrimitiveSerialDescriptor("ModifiedIn", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ModifiedIn) {
      when (value) {
        ModifiedIn.SCLM -> encoder.encodeString("Y")
        ModifiedIn.ISPF -> encoder.encodeString("N")
        ModifiedIn.UNDEFINED -> encoder.encodeString("")
      }
    }

    override fun deserialize(decoder: Decoder): ModifiedIn {
      return try {
        when (decoder.decodeString()) {
          "Y" -> ModifiedIn.SCLM
          "N" -> ModifiedIn.ISPF
          else -> ModifiedIn.UNDEFINED
        }
      } catch (_: Exception) {
        ModifiedIn.UNDEFINED
      }
    }
  }

  /** [LocalDate] serializer. Transforms nullable string into nullable [LocalDate] and back */
  class LocalDateSerializer : KSerializer<LocalDate?> {
    private val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")

    override val descriptor = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDate?) {
      if (value == null) {
        encoder.encodeString("")
      } else {
        val javaDate = LocalDate.of(value.year, value.monthValue, value.dayOfMonth)
        encoder.encodeString(formatter.format(javaDate))
      }
    }

    override fun deserialize(decoder: Decoder): LocalDate? {
      return try {
        val dateString = decoder.decodeString()
        if (dateString.isBlank()) null else LocalDate.parse(dateString, formatter)
      } catch (_: Exception) {
        null
      }
    }
  }
}

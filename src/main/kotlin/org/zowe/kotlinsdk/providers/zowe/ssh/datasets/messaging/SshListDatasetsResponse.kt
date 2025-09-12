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

package org.zowe.kotlinsdk.providers.zowe.ssh.datasets.messaging

import org.zowe.kotlinsdk.core.datasets.AttributesLevel
import org.zowe.kotlinsdk.core.datasets.api.messaging.ListDatasetsResponse
import org.zowe.kotlinsdk.core.datasets.data.DatasetItem
import org.zowe.kotlinsdk.providers.zowe.SshResponse
import org.zowe.kotlinsdk.providers.zowe.SshStatus
import org.zowe.kotlinsdk.providers.zowe.ssh.datasets.definitions.SshDatasetItem
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * @see <a href="https://www.ibm.com/docs/en/zos/2.1.0?topic=lce-example-1-3">LISTDS command: Example 1</a>
 * Includes Format-1 DSCB and Format-3 DSCB processing when applicable
 */
class SshListDatasetsResponse(
  override val status: SshStatus = SshStatus.OK,
  private val originalMask: String,
  private val modifiedMask: String,
  private val attributesLevel: AttributesLevel
) : SshResponse, ListDatasetsResponse {
  override val dsItems: List<DatasetItem>

  /** Produce unsigned single-digit int from a byte */
  fun u1(b: Byte) = b.toInt() and 0xFF
  /** Produce unsigned double-digit int from two bytes */
  fun u2(b0: Byte, b1: Byte) = ((u1(b0) shl 8) or u1(b1))

  /**
   * Translate EBCDIC hex string to a byte array
   * @param hex the EBCDIC string to translate
   * @return the translated byte array
   */
  fun hexToBytes(hex: String): ByteArray {
    val clean = hex.replace(" ", "")
    require(clean.length % 2 == 0)
    return ByteArray(clean.length / 2) { i ->
      clean.substring(i*2, i*2+2).toInt(16).toByte()
    }
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=types-format-1-format-8-dscbs">Format-1 DSCB</a>
   * Not all parameters are specified or utilized due to redundancy.
   * Extents are grouped as a DS1EXT map, that consists of the 4-byte lower and upper limits
   */
  object F1 {
    val DS1DSFMT = 0x2C to 1  // F1 (name of the DSCB format)
    val DS1CREDT = 0x35 to 3  // Creation date ('YDD', means year = 1900 + Y byte, day = DD byte)
    val DS1EXPDT = 0x38 to 3  // Expiration date ('YDD', means year = 1900 + Y byte, day = DD byte)
    val DS1NOEPV = 0x3B to 1  // Number of extents on volume
    val DS1REFD  = 0x4B to 3  // Date last referenced ('YDD', means year = 1900 + Y byte, day = DD byte)
    val DS1SCAL1 = 0x5E to 1  // Flag byte
    val DS1EXT = mapOf(0x6B to 0x6F, 0x75 to 0x79, 0x7F to 0x83)
  }

  /**
   * Absolute track address in the volume
   * @property cylinder the number of a cylinder where the track is situated
   * @property head the track address on the cylinder
   */
  data class TrackAddress(val cylinder: Int, val head: Int) {
    companion object {
      /**
       * Parse the track address from a byte array
       * @param extBytes the extent bytes to parse
       * @return the parsed [TrackAddress]
       */
      fun parseFromBytes(extBytes: ByteArray): TrackAddress {
        val cylinder = ((extBytes[0].toInt() and 0xFF) shl 8) or (extBytes[1].toInt() and 0xFF)
        val head = extBytes[3].toInt() and 0x0F
        return TrackAddress(cylinder, head)
      }

      /**
       * Calculate the number of tracks on the extent as a difference between the upper and lower limits
       * @param bytes the bytes to parse track addresses from
       * @param lByteAddr the lower limit start
       * @param uByteAddr the upper limit start
       * @return the number of tracks
       */
      fun calculateTracks(bytes: ByteArray, lByteAddr: Int, uByteAddr: Int): Int {
        val extStart = parseFromBytes(bytes.copyOfRange(lByteAddr, lByteAddr + 4))
        val extEnd = parseFromBytes(bytes.copyOfRange(uByteAddr, uByteAddr + 4))
        return extEnd - extStart
      }
    }

    /**
     * Find the difference between two track addresses.
     * As the result, the number of tracks is returned
     * @param other the upper limit track address
     * @return the number of tracks (0 if the relative track addresses are 0)
     */
    operator fun minus(other: TrackAddress): Int {
      val tracksPerCyl = 15 // 3390
      val startRelTrack = other.cylinder * tracksPerCyl + other.head
      val endRelTrack = this.cylinder * tracksPerCyl + this.head
      val reservedTrack = if (endRelTrack == 0 && startRelTrack == 0) 0 else 1
      return endRelTrack - startRelTrack + reservedTrack
    }
  }

  /**
   * Get date as a UTC+0 string
   * @param bytes the bytes to parse the date from
   * @param firstPos the first byte to get data from
   * @param secondPos the last byte to get data to
   * @return a UTS+0 string
   */
  fun getDateFromBytes(bytes: ByteArray, firstPos: Int, secondPos: Int): String {
    val dateBytes = bytes.copyOfRange(firstPos, secondPos)
    val yearRaw = u1(dateBytes[0])
    val year = 1900 + yearRaw
    val day = u2(dateBytes[1], dateBytes[2])
    return if (year == 1900 && day == 0) "N/A"
      else LocalDate.ofYearDay(year, day).atStartOfDay(ZoneOffset.UTC).toString()
  }

  /**
   * Transform a byte to a binary representation,
   * and return 8 true/false values, depending on each transformed binary position value (1 - true, 0 - false)
   * @param byte the byte to transform
   * @return the list of true/false values
   */
  fun byteToBins(byte: Byte): List<Boolean> {
    return Integer.toBinaryString(byte.toInt() and 0xFF)
      .padStart(8, '0')
      .toCharArray()
      .map { it == '1' }
  }

  /**
   * Parse Format-1 DSCB.
   * Will return the data set creation, expiration, and last reference dates, allocated size in tracks, and space units
   * @param bytes the bytes to read as the Format-1 DSCB
   * @return the map of string to string values
   */
  fun parseDscbF1(bytes: ByteArray): MutableMap<String, String> {
    require(u1(bytes[F1.DS1DSFMT.first].toInt().toByte()) == 0xF1) { "Not a Format-1 DSCB" }

    val creationDate = getDateFromBytes(bytes, F1.DS1CREDT.first, F1.DS1CREDT.first + F1.DS1CREDT.second)
    val expirationDate = getDateFromBytes(bytes, F1.DS1EXPDT.first, F1.DS1EXPDT.first + F1.DS1EXPDT.second)
    val lastRefDate = getDateFromBytes(bytes, F1.DS1REFD.first, F1.DS1REFD.first + F1.DS1REFD.second)

    val numOfExts = u1(bytes[F1.DS1NOEPV.first].toInt().toByte())

    val binsDS1SCAL1 = byteToBins(bytes[F1.DS1SCAL1.first])

    val spaceUnits = when {
      binsDS1SCAL1[0] && binsDS1SCAL1[1] -> "CYL"
      binsDS1SCAL1[0] && !binsDS1SCAL1[1] -> "TRK"
      !binsDS1SCAL1[0] && binsDS1SCAL1[1] -> "BLK"
      else -> ""
    }

    val allocatedTracks = F1.DS1EXT
      .entries
      .sumOf { (lByteAddr, uByteAddr) -> TrackAddress.calculateTracks(bytes, lByteAddr, uByteAddr) }
      .let { "$it" }

    return mutableMapOf(
      "CREATED_AT" to creationDate,
      "EXPIRES_AT" to expirationDate,
      "LAST_REFERENCE" to lastRefDate,
      "SIZE_IN_TRACKS" to allocatedTracks,
      "SPACE_UNITS" to spaceUnits
    )
  }

  /**
   * @see <a href="https://www.ibm.com/docs/en/zos/3.1.0?topic=types-format-3-dscb">Format-3 DSCB</a>
   * Not all parameters are specified or utilized due to redundancy
   */
  object F3 {
    val DS3FMTID = 0x2C to 1  // Format identifier (0xF3)
    val DS3EXT = mapOf(
      0x06 to 0x0A, 0x10 to 0x14, 0x1A to 0x1E, 0x24 to 0x28, 0x2F to 0x33, 0x39 to 0x3D, 0x43 to 0x47, 0x4D to 0x51,
      0x57 to 0x5B, 0x61 to 0x65, 0x6B to 0x6F, 0x75 to 0x79, 0x7F to 0x83
    )
  }

  /**
   * Parse Format-3 DSCB
   * @param bytes the bytes to read as the Format-3 DSCB
   * @return the number of track for each of the extents, filled with values
   */
  fun parseDscbF3(bytes: ByteArray): Int {
    require(u1(bytes[F3.DS3FMTID.first].toInt().toByte()) == 0xF3) { "Not a Format-3 DSCB" }
    val tracksCount = F3.DS3EXT
      .entries
      .sumOf { (lByteAddr, uByteAddr) -> TrackAddress.calculateTracks(bytes, lByteAddr, uByteAddr) }
    return tracksCount
  }

  /**
   * Produce a regular [SshDatasetItem] from the raw strings, provided as the response of the SSH command
   * @param dsAttributes the raw dataset attributes strings list to process
   * @return composed [SshDatasetItem] with prefilled parameters from the provided attributes
   */
  private fun produceRegularSshDatasetItem(dsAttributes: List<String>): SshDatasetItem {
    /**
     * Handle raw dataset attributes header.
     * Will process dataset attributes header to produce a raw list of attribute titles
     * with the same number of characters for each attribute value.
     * It is needed to later define the actual attribute values by the number of characters for each value,
     * because the size of a title is equal to a value
     * Example header:
     *   --RECFM-ATTR1---ATTR2-VOLUMES---
     *  will produce the list:
     *   ["--RECFM-", "ATTR1---", "ATTR2-", "VOLUMES---"]
     * @param rawDsAttrsHeaderStr the raw line to handle
     * @return list of raw attribute titles
     */
    fun handleRawDsAttrsHeader(rawDsAttrsHeaderStr: String): List<String> {
      val rawDsAttrsHeaderList = mutableListOf<String>()

      var hasOtherHeaders = true
      var currIdx = 0
      var nextRawHeader = rawDsAttrsHeaderStr
      var currHeader: String

      while (hasOtherHeaders) {
        val headerNoFirstHyphens = nextRawHeader.replaceFirst(Regex("^-+"), "")
        currHeader = headerNoFirstHyphens.substringBefore("-")
        nextRawHeader = headerNoFirstHyphens.substringAfter(currHeader)
        val hyphensLengthAfterCurrHeader = nextRawHeader.takeWhile { it == '-' }.length
        hasOtherHeaders = nextRawHeader.any { it != '-' }
        currHeader += "-".repeat(hyphensLengthAfterCurrHeader)
        rawDsAttrsHeaderList.add(currIdx, currHeader)
        currIdx++
      }

      if (rawDsAttrsHeaderList.isNotEmpty()) {
        rawDsAttrsHeaderList[0] = "--${rawDsAttrsHeaderList[0]}"
      }

      return rawDsAttrsHeaderList
    }

    /**
     * Split attribute values by the number of characters of a respective attribute title
     * Example:
     *   we had a header and an attribute values line like this:
     *   --ATTR1---ATTR2-ATTR3---ATTR4--
     *     123     222   test    someval
     *   the resulting values must be:
     *   ["123", "222", "test", "someval"]
     * @param rawDsAttrsHeaderList the list of raw titles, handled previously
     * @param rawDsAttrsStr the line of attribute values to process
     * @return the list of recognized attribute values
     */
    fun handleRawDsAttrValues(rawDsAttrsHeaderList: List<String>, rawDsAttrsStr: String): List<String> {
      return rawDsAttrsHeaderList
        .fold(0 to mutableListOf<String>()) { (pointerIdx, rawDsAttrsList), nextRawHeader ->
          val nextPointerIdx = pointerIdx + nextRawHeader.length
          val lastCharIdx = if (nextPointerIdx > rawDsAttrsStr.length) rawDsAttrsStr.length else nextPointerIdx
          rawDsAttrsList.add(rawDsAttrsStr.substring(pointerIdx, lastCharIdx))
          nextPointerIdx to rawDsAttrsList
        }
        .second
        .map { it.trim() }
    }

    /**
     * Get a zipped map of attribute titles to attribute values
     * Example:
     *   we had a header and an attribute values line like this:
     *   --ATTR1---ATTR2-ATTR3---ATTR4--
     *     123     222   test    someval
     *   the result must be:
     *   { "ATTR1": "123", "ATTR2": "222", "ATTR3": "test", "ATTR4": "someval" }
     * @param rawDsAttrsHeaderStr the attributes header line to process
     * @param rawDsAttrValuesStr the attribute values line to process
     * @return a map of recognized attribute titles to recognized attribute values
     */
    fun getDsHeaderToValues(rawDsAttrsHeaderStr: String, rawDsAttrValuesStr: String): Map<String, String> {
      val dsAttrsHeaderRawList = handleRawDsAttrsHeader(rawDsAttrsHeaderStr)
      val dsAttrsHeaderList = dsAttrsHeaderRawList.map { it.replace("-", "") }
      val dsAttrValues = handleRawDsAttrValues(
        dsAttrsHeaderRawList,
        rawDsAttrValuesStr
      )
      return dsAttrsHeaderList.zip(dsAttrValues).toMap()
    }

    val dsName = dsAttributes[0].trim()

    val dsAttrsHeaderToValues1 = getDsHeaderToValues(
      dsAttributes[1],
      dsAttributes[2]
    )
    val dsAttrsHeaderToValues2 = getDsHeaderToValues(
      dsAttributes[3],
      dsAttributes[4]
    )

    var extendedDsAttrsHeaderToValues: MutableMap<String, String> = mutableMapOf("SIZE_IN_TRACKS" to "0")
    if (dsAttributes.size >= 9) {
      if (dsAttributes[5] == "--FORMAT 1 DSCB--") {
        val dscbF1Hex = "00".repeat(44) + dsAttributes[6] + dsAttributes[7] + dsAttributes[8]
        val dscbF1Bytes = hexToBytes(dscbF1Hex)
        extendedDsAttrsHeaderToValues = parseDscbF1(dscbF1Bytes)
      }
      var dscb3PointerIdx = 9
      while (dsAttributes.size > dscb3PointerIdx && dsAttributes[dscb3PointerIdx] == "--FORMAT 3 DSCB--") {
        val dscbF3Hex = dsAttributes[dscb3PointerIdx + 1] +
          dsAttributes[dscb3PointerIdx + 2] +
          dsAttributes[dscb3PointerIdx + 3] +
          dsAttributes[dscb3PointerIdx + 4] +
          dsAttributes[dscb3PointerIdx + 5]
        val dscbF3Bytes = hexToBytes(dscbF3Hex)
        val dscbF3 = parseDscbF3(dscbF3Bytes)
        extendedDsAttrsHeaderToValues["SIZE_IN_TRACKS"] =
          "${(extendedDsAttrsHeaderToValues["SIZE_IN_TRACKS"]?.toInt() ?: 0) + dscbF3}"
        dscb3PointerIdx += 6
      }
    }

    val dsAttrsHeaderToValues = mapOf("DSNAME" to dsName) +
      dsAttrsHeaderToValues1 +
      dsAttrsHeaderToValues2 +
      extendedDsAttrsHeaderToValues
    return SshDatasetItem.produceFromMap(dsAttrsHeaderToValues)
  }

  /**
   * Produce an [SshDatasetItem] with a data set name inside only
   * @param dsAttributes the list of raw data set attributes to parse the data set name from
   * @return the [SshDatasetItem] with a data set name inside only
   */
  private fun produceDsNameOnlySshDatasetItem(dsAttributes: List<String>): SshDatasetItem {
    val sshDatasetItem = produceRegularSshDatasetItem(dsAttributes)
    return SshDatasetItem(datasetName = sshDatasetItem.datasetName)
  }

  /**
   * Produce an [SshDatasetItem] with data set name and volume serial inside only
   * @param dsAttributes the list of raw data set attributes to parse the data set name from
   * @return the [SshDatasetItem] with data set name and volume serial inside only
   */
  private fun produceDsNameAndVolserSshDatasetItem(dsAttributes: List<String>): SshDatasetItem {
    val sshDatasetItem = produceRegularSshDatasetItem(dsAttributes)
    return SshDatasetItem(datasetName = sshDatasetItem.datasetName, volumeSerial = sshDatasetItem.volumeSerial)
  }

  // TODO: define error codes and handling strategy
  /**
   * Handle the uncommon dataset attributes raw list.
   * Normally the SSH response should contain 5 lines of data for each dataset.
   * If the number of lines is different, data should be processed respectively to the content, provided in the lines
   * @param dsAttributes the dataset raw attributes list to process
   * @return non-regular [SshDatasetItem] when it is possible to process such dataset, null otherwise
   */
  private fun produceNonregularSshDatasetItem(dsAttributes: List<String>): SshDatasetItem? {
    return null
//    return if (dsAttributes.any { it.startsWith("IKJ58507I")}) null
//      else throw Exception("Error fetching datasets list: ${dsAttributes.joinToString("\n")}")
  }

  /**
   * Split output to the dataset raw attributes and process them separately to produce [SshDatasetItem]s.
   * Also, filters out the datasets, whose names are not compatible with the originally provided mask
   * @return list of produced [SshDatasetItem]s with prefilled parameters (if they are recognized)
   */
  private fun produceDsListFromSshCmdOutput(): List<DatasetItem> {
    val collectedDatasetAttributesStrings = mutableListOf<MutableList<String>?>()
    var nextDatasetAttributesStrings: MutableList<String>? = null

    status
      .output
      .split("\n")
      .filter { it.isNotEmpty() }
      .forEach { sshNextLine ->
        if (sshNextLine.startsWith(modifiedMask)) {
          nextDatasetAttributesStrings = mutableListOf(sshNextLine)
          collectedDatasetAttributesStrings.add(nextDatasetAttributesStrings)
        } else {
          nextDatasetAttributesStrings?.add(sshNextLine)
        }
      }

    return collectedDatasetAttributesStrings
      .filterNotNull()
      .filter { it[0].trim().matches(Regex("^${originalMask.replace("**", "*")}")) }
      .mapNotNull {
        if (it.size < 5) {
          produceNonregularSshDatasetItem(it)
        } else when (attributesLevel) {
          AttributesLevel.DSNAME -> produceDsNameOnlySshDatasetItem(it)
          AttributesLevel.VOLSER -> produceDsNameAndVolserSshDatasetItem(it)
          AttributesLevel.FULL -> produceRegularSshDatasetItem(it)
        }
      }
  }

  init {
    dsItems = produceDsListFromSshCmdOutput()
  }
}

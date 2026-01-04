/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.openssh.jes.definitions

import org.zowe.kotlinsdk.providers.zowe.openssh.jes.extractJobValue
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatterBuilder
import java.util.Locale

/**
 * Job execution detailed data. Provides more info about the job run,
 * but a request consumes more info to produce it, if specified
 * @property execSystem the system the job is executed on
 * @property execMember the node the job is executed on
 * @property execSubmitted the [LocalDateTime] of the job submission date
 *                         (fetched from DATER and TIMER parameters of ISFEXEC ST, milliseconds precision)
 * @property execStarted the [LocalDateTime] of the job start date
 *                       (fetched from JESMSGLG, search happens for the line with message $HASP373, seconds precision)
 * @property execEnded the [LocalDateTime] of the job start date
 *                     (fetched from JESMSGLG, search happens for the line with message $HASP395, seconds precision)
 */
class SshJobExecData(sshCmdOutput: String) {
  /**
   * Parse submitted date and time. Expects the date in format "yyyy.DDD" and time in format "H:mm:ss.cc"
   * @param date the exec-submitted date string to parse
   * @param time the exec-submitted time string to parse
   * @return parsed exec-submitted as [LocalDateTime] instance, null if there is no date or time
   */
  private fun parseExecSubmitted(date: String, time: String): LocalDateTime? {
    if (date.isEmpty() || time.isEmpty()) return null

    val (year, dayOfYear) = date.split(".")
    val date = LocalDate.ofYearDay(year.toInt(), dayOfYear.toInt())

    val timeParts = time.split(":")
    val hour = timeParts[0].toInt()
    val minute = timeParts[1].toInt()
    val (seconds, centiseconds) = timeParts[2].split(".")
      .let { it[0].toInt() to (if (it.size > 1) it[1].toInt() else 0) }
    val nanos = centiseconds * 10_000_000

    return LocalDateTime.of(date, LocalTime.of(hour, minute, seconds, nanos))
  }

  /**
   * Parse started or ended date and time. Expects the date in format "d MMM yyyy" and time in format "HH.mm.ss".
   * Due to the nature of functionalities, triggered by SSH calls, it is impossible to fetch milliseconds of the
   * job start or end, thus the start or the end time milliseconds will be either the same as the previous time,
   * or "000" if the seconds of the next exec time are different from the previous time
   * @param date the exec-started or exec-ended date string to parse
   * @param time the exec-started or exec-ended time string to parse
   * @return parsed exec-started or exec-ended as [LocalDateTime] instance, null if there is no date or time
   */
  private fun parseExecStartedOrEnded(date: String, time: String, prevExecDateTime: LocalDateTime?): LocalDateTime? {
    if (date.isEmpty() || time.isEmpty()) return null

    val formatter = DateTimeFormatterBuilder()
      .parseCaseInsensitive()
      .appendPattern("d MMM yyyy")
      .toFormatter(Locale.ENGLISH)
    val parsedDate = LocalDate.parse(date, formatter)

    val (hour, minute, second) = time.split(".").map { it.toInt() }

    val parsedDateTime = LocalDateTime.of(parsedDate, LocalTime.of(hour, minute, second, 0))
    return if (prevExecDateTime != null) {
      when {
        parsedDateTime.second > prevExecDateTime.second -> parsedDateTime
        parsedDateTime.second == prevExecDateTime.second -> parsedDateTime.withNano(prevExecDateTime.nano)
        parsedDateTime.isBefore(prevExecDateTime) -> prevExecDateTime
        else -> parsedDateTime
      }
    } else parsedDateTime
  }

  private val execSubmittedDate = extractJobValue(sshCmdOutput, "exec-submitted-date")
  private val execSubmittedTime = extractJobValue(sshCmdOutput, "exec-submitted-time")
  private val execStartedDate = extractJobValue(sshCmdOutput, "exec-started-date")
  private val execStartedTime = extractJobValue(sshCmdOutput, "exec-started-time")
  private val execEndedDate = extractJobValue(sshCmdOutput, "exec-ended-date")
  private val execEndedTime = extractJobValue(sshCmdOutput, "exec-ended-time")

  val execSystem = extractJobValue(sshCmdOutput, "exec-system")
  val execMember = extractJobValue(sshCmdOutput, "exec-member")
  val execSubmitted = parseExecSubmitted(execSubmittedDate, execSubmittedTime)
  val execStarted = parseExecStartedOrEnded(execStartedDate, execStartedTime, execSubmitted)
  val execEnded = parseExecStartedOrEnded(execEndedDate, execEndedTime, execStarted)
}
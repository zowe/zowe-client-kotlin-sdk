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

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.text.toInt

/**
 * The step-data for the SSH job item response
 * @property isActive indicates if the step active (false by default)
 * @property stepNumber the positional number of the step
 * @property selectedTime the date and time the step is selected for execution
 * @property stepName the step name
 * @property endTime the date and time the step is finished its execution
 * @property procedureStepName the procedure name of the step
 * @property completion the final completion message of the step execution
 * @property abendReasonCode if the step is finished with abend, this value hold the reason as integer
 */
data class SshJobStepData(
  @property:AvailableSince(ZVersion.ZOS_2_2) val isActive: Boolean = false,
  @property:AvailableSince(ZVersion.ZOS_2_2) val stepNumber: Int,
  @property:AvailableSince(ZVersion.ZOS_2_2) val selectedTime: LocalDateTime? = null,
// TODO: decide whether it is reasonable to process JCL for it (e.g. //STEP1  EXEC PGM=HWTHXCB1,)
//  @property:AvailableSince(ZVersion.ZOS_2_2) val programName: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val stepName: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val endTime: LocalDateTime? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val procedureStepName: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val completion: String? = null,
  @property:AvailableSince(ZVersion.ZOS_2_2) val abendReasonCode: Int? = null
) {
  private data class JesmsglgParsedStep(val stepName: String, val procStep: String = "")

  private data class JesysmsgParsedStep(
    val stepNumber: Int,
    val stepName: String,
    val completion: String,
    private val abendReasonCodeStr: String?,
    private val startDateTimeStr: String,
    private val endDateTimeStr: String
  ) {
    /** Parse the provided date and time as string in format "YYYYDDD.HHM" to [LocalDateTime] */
    private fun parseJesysmsgDateTime(toParse: String): LocalDateTime {
      val parts = toParse.split(".")
      val datePart = parts[0]
      val timePart = parts[1]

      val year = datePart.take(4).toInt()
      val dayOfYear = datePart.substring(4).toInt()
      val date = LocalDate.ofYearDay(year, dayOfYear)

      val hour = timePart.take(2).toInt()
      val minute = timePart.substring(2, 4).toInt()

      return LocalDateTime.of(date, LocalTime.of(hour, minute, 0, 0))
    }

    val abendReasonCode = abendReasonCodeStr?.toIntOrNull(16)
    val startDateTime = parseJesysmsgDateTime(startDateTimeStr)
    val endDateTime = parseJesysmsgDateTime(endDateTimeStr)
  }

  companion object {
    /**
     * Parse the JESMSGLG spool file to get [JesmsglgParsedStep] objects list.
     * The messages are expected to contain any of the two formats of the steps result:
     *  1. STEPNAME PROCSTEP    RC
     *  v: ________ ________ _____
     *  2. JOBNAME  STEPNAME PROCSTEP    RC
     *  v: ________ ________ ________ _____
     * @param stepDataLines the list of respective spool file lines to build the objects from
     * @return the list of [JesmsglgParsedStep]'s if recognized.
     *         If any of the steps has an incorrect format, the list will be empty
     */
    private fun parseJESMSGLGSummary(stepDataLines: List<String>): List<JesmsglgParsedStep> {
      val jesmsglgSummaryStart = stepDataLines.indexOfFirst { it.contains("=/= JESMSGLG SUMMARY START =/=") }
      val jesmsglgSummaryEnd = stepDataLines.indexOfFirst { it.contains("=\\= JESMSGLG SUMMARY END =\\=") }
      val jesmsglgSummaryLines =
        if (jesmsglgSummaryStart != -1 && jesmsglgSummaryEnd != -1) {
          stepDataLines.subList(jesmsglgSummaryStart + 1, jesmsglgSummaryEnd)
            .map { it.drop(21) }
            .filter {
              !it.contains("-----TIMINGS (MINS.)------")
              && !it.matches(Regex("^.{8} ENDED\\..*TOTAL ELAPSED TIME.*"))
            }
        } else listOf()
      val lineSize = jesmsglgSummaryLines.firstOrNull()
        ?.take(8)
        ?.let {
          when {
            it.contains("JOBNAME") -> 32
            it.contains("STEPNAME") -> 23
            else -> null
          }
        }
        ?: return listOf()
      return jesmsglgSummaryLines.drop(1)
        .map { it.take(lineSize) }
        .fold(mutableListOf()) { result, nextLine ->
          var nextStartIdx = if (lineSize == 32) 9 else 0
          val stepName = nextLine.substring(nextStartIdx, nextStartIdx + 8).trim()
          nextStartIdx += 9
          val procStep = nextLine.substring(nextStartIdx, nextStartIdx + 8).trim()
          result.add(JesmsglgParsedStep(stepName, procStep))
          result
        }
    }

    /**
     * Parse the JESYSMSG spool file to get [JesysmsgParsedStep] objects list
     * @param stepDataLines the list of respective spool file lines to build the objects from
     * @return the list of [JesysmsgParsedStep]'s if recognized.
     *         If any of the steps has an incorrect format, the list will be empty
     */
    private fun parseJESYSMSGSummary(stepDataLines: List<String>): List<JesysmsgParsedStep> {
      val jesysmsgMessagesStart = stepDataLines.indexOfFirst { it.contains("=/= JESYSMSG MESSAGES START =/=") }
      val jesysmsgMessagesEnd = stepDataLines.indexOfFirst { it.contains("=\\= JESYSMSG MESSAGES END =\\=") }
      val jesysmsgMessagesLines =
        if (jesysmsgMessagesStart != -1 && jesysmsgMessagesEnd != -1)
          stepDataLines.subList(jesysmsgMessagesStart, jesysmsgMessagesEnd)
            .map { it.trim().uppercase() }
        else listOf()

      val stepsResults = mutableListOf<JesysmsgParsedStep>()
      var stepNumber = 1

      var i = 0
      while (i < jesysmsgMessagesLines.size) {
        val nextLine = jesysmsgMessagesLines[i]
        val lineMsg = nextLine.take(7)

        if (lineMsg == "IEF472I" || lineMsg == "IEF142I") {
          val completionInfoAndDetails = nextLine.split(" - ").drop(1)
          if (completionInfoAndDetails.size != 2) {
            // TODO: warning
            return listOf()
          }
          val completionInfo = completionInfoAndDetails.first()
          val completionDetails = completionInfoAndDetails.last()

          val isAbend = when (completionInfo) {
            "COMPLETION CODE" -> true
            "STEP WAS EXECUTED" -> false
            else -> {
              // TODO: warning
              return listOf()
            }
          }

          var completion: String
          var abendReasonCode: String? = null
          if (isAbend) {
            val systemCode = completionDetails.substringAfterLast("SYSTEM=")
              .substringBefore(" ")
            val userCode = completionDetails.substringAfterLast("USER=")
              .substringBefore(" ")
            abendReasonCode = completionDetails.substringAfterLast("REASON=")
            completion = if (systemCode.isEmpty() || userCode.isEmpty() || abendReasonCode.isEmpty()) {
              // TODO: warning
              return listOf()
            } else {
              when {
                systemCode.isNotEmpty() -> "ABEND S$systemCode"
                userCode.isNotEmpty() -> "ABENDU$userCode"
                else -> throw Exception("Abend code cannot be 0 both for system and user")
              }
            }
          } else {
            val completionCode = completionDetails.substringAfterLast("COND CODE ")
            if (completionCode.isEmpty()) {
              // TODO: warning
              return listOf()
            } else {
              completion = "CC $completionCode"
            }
          }

          var stepName = ""
          var startDateTimeStr = ""
          var endDateTimeStr = ""
          var j = i + 1
          while (j < jesysmsgMessagesLines.size) {
            val nextLine = jesysmsgMessagesLines[j]
            val lineMsg = nextLine.take(7)

            when (lineMsg) {
              "IEF373I" -> {
                stepName = nextLine.substringAfterLast("STEP/")
                  .substringBeforeLast("/START")
                  .trim()
                startDateTimeStr = nextLine.substringAfterLast("/START").trim()
              }
              "IEF032I" -> {
                endDateTimeStr = nextLine.substringAfterLast("/STOP").trim()
              }
              else -> break
            }

            j++
          }

          if (stepName.isEmpty() || startDateTimeStr.isEmpty() || endDateTimeStr.isEmpty()) {
            // TODO: warning
            return listOf()
          }

          stepsResults.add(JesysmsgParsedStep(stepNumber, stepName, completion, abendReasonCode, startDateTimeStr, endDateTimeStr))
          stepNumber++
          i = j
        } else {
          i++
        }
      }

      return stepsResults
    }

    /**
     * Produce the step-data instances from parsed JESMSGLG and JESYSMSG spool files
     * @param parsedJESMSGLG the list of [JesmsglgParsedStep]'s to get info from
     * @param parsedJESYSMSG the list of [JesysmsgParsedStep]'s to get info from
     * @return the list of [SshJobStepData] object, recognized from the spool files
     */
    private fun produceStepsDataFromParsedSpoolFiles(
      parsedJESMSGLG: List<JesmsglgParsedStep>,
      parsedJESYSMSG: List<JesysmsgParsedStep>
    ): List<SshJobStepData> {
      return parsedJESYSMSG.map { nextParsedJesysmsgStep ->
        SshJobStepData(
          stepNumber = nextParsedJesysmsgStep.stepNumber,
          stepName = nextParsedJesysmsgStep.stepName,
          procedureStepName = parsedJESMSGLG.find { it.stepName == nextParsedJesysmsgStep.stepName }?.procStep,
          selectedTime = nextParsedJesysmsgStep.startDateTime,
          endTime = nextParsedJesysmsgStep.endDateTime,
          completion = nextParsedJesysmsgStep.completion,
          abendReasonCode = nextParsedJesysmsgStep.abendReasonCode
        )
      }
    }

    /**
     * Parse the job step-data from the SSH command output.
     * The step-data is expected to be between messages "=///= STEP DATA START =///="
     * and "=\\\= STEP DATA END =\\\="
     * @param sshCmdOutput the SSH command output to parse
     * @return the list of recognized [SshJobStepData] instances if the info is recognized
     */
    fun parseJobStepData(sshCmdOutput: String): List<SshJobStepData> {
      val lines = sshCmdOutput.lines()

      val startIndex = lines.indexOfFirst { it.contains("=///= STEP DATA START =///=") }
      val endIndex = lines.indexOfFirst { it.contains("=\\\\\\= STEP DATA END =\\\\\\=") }
      if (startIndex == -1 || endIndex == -1) return listOf()

      val stepDataLines = lines.subList(startIndex + 1, endIndex)

      val parsedJESMSGLG = parseJESMSGLGSummary(stepDataLines)
      val parsedJESYSMSG = parseJESYSMSGSummary(stepDataLines)

      return produceStepsDataFromParsedSpoolFiles(parsedJESMSGLG, parsedJESYSMSG)
    }
  }
}

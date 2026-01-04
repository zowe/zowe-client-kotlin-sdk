/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.openssh.jes.messaging

import org.zowe.kotlinsdk.annotations.AvailableSince
import org.zowe.kotlinsdk.annotations.ZVersion
import org.zowe.kotlinsdk.core.connectivity.SshConnection
import org.zowe.kotlinsdk.core.jes.api.messaging.GetJobRequest
import org.zowe.kotlinsdk.providers.zowe.SshCmdResponse
import org.zowe.kotlinsdk.providers.zowe.SshRequest
import org.zowe.kotlinsdk.providers.zowe.openssh.jes.definitions.SshJobStepData
import org.zowe.kotlinsdk.providers.zowe.openssh.jes.definitions.SshJobExecData
import kotlin.random.Random

/**
 * Get job SSH request. Gets the job by the job name and job ID.
 * NOTE: functionality is tested for JES2 only.
 *       In theory, it should work with JES3 as well, if not - reach out to us with the examples.
  *      The exec-data and step-data functionality is designed to work with JES2 only
 * @property jobName the job name to search for
 * @property jobId the job ID to search for
 * @property isFetchStepData the parameter to fetch and parse [SshJobStepData] if true.
 *                           NOTE: the step-data is fetched correctly only for the jobs that are in the OUTPUT state
 * @property isFetchExecData the parameter to fetch and parse [SshJobExecData] if true.
 */
class SshGetJobRequest(
  override val connection: SshConnection,
  @property:AvailableSince(ZVersion.ZOS_2_2) val jobName: String,
  @property:AvailableSince(ZVersion.ZOS_2_2) val jobId: String,
  @property:AvailableSince(ZVersion.ZOS_2_2) val isFetchStepData: Boolean = false,
  @property:AvailableSince(ZVersion.ZOS_2_2) val isFetchExecData: Boolean = false
) : SshRequest, GetJobRequest {

  private val scriptPath: String =
    "/tmp/zowe_get_job_${System.currentTimeMillis()}_${Random.nextInt(10000)}.rexx"

  /** exec-data input parameters setup for the Rexx script */
  private fun produceExecDataInputs(): String {
    return """
      exec_system = ''
      exec_member = ''
      exec_started_date = ''
      exec_started_time = ''
      exec_ended_date = ''
      exec_ended_time = ''
      exec_submitted_date = ''
      exec_submitted_time = ''
      exec_data_last_date_line = ''
    """.trimIndent()
  }

  /** exec-data system and member parsing from the JESMSGLG for the Rexx script */
  private fun parseExecSystemAndMember(): String {
    return """
      if Pos('J E S 2  J O B  L O G', isfline.kx) > 0 then do
        parse_line = isfline.kx
        
        system_pos = Pos('S Y S T E M', parse_line)
        if system_pos > 0 then do
          after_system = Substr(parse_line, system_pos + Length('S Y S T E M'))
          no_pre_spaces = Strip(after_system, 'L')
          no_spaces = Space(no_pre_spaces, 0)
          parse var no_spaces exec_system '--' .
        end
        
        node_pos = Pos('N O D E', parse_line)
        if node_pos > 0 then do
          after_node = Substr(parse_line, node_pos + Length('N O D E'))
          no_pre_spaces = Strip(after_node, 'L')
          exec_member = Space(no_pre_spaces, 0)
        end
      end
    """.trimIndent()
  }

  /** exec-data submit, start, and end date and time parsing for the Rexx script */
  private fun parseExecDatesAndTimes(): String {
    return """
      if Pos('----', current_line) > 0 then do
        upper_line = Translate(current_line)
        if Pos('MONDAY', upper_line) > 0 | Pos('TUESDAY', upper_line) > 0 | Pos('WEDNESDAY', upper_line) > 0 | ,
           Pos('THURSDAY', upper_line) > 0 | Pos('FRIDAY', upper_line) > 0 | Pos('SATURDAY', upper_line) > 0 | ,
           Pos('SUNDAY', upper_line) > 0 then do
          exec_data_last_date_line = current_line
        end
      end
      
      if (Pos('${'$'}HASP373', current_line) > 0 & Pos('STARTED', current_line) > 0) | ,
         (Pos('${'$'}HASP395', current_line) > 0 & Pos('ENDED', current_line) > 0) then do
        parse var current_line exec_time jobid_skip rest
        
        if exec_data_last_date_line <> '' then do
          parse var exec_data_last_date_line . . '----' day_of_week ',' day month year '----' .
          day = Strip(day)
          month = Strip(month)
          year = Strip(year)
          exec_date = day month year
        end
        if Pos('STARTED', current_line) > 0 then do
          exec_started_date = exec_date
          exec_started_time = exec_time
        end
        if Pos('ENDED', current_line) > 0 then do
          exec_ended_date = exec_date
          exec_ended_time = exec_time
        end
      end
    """.trimIndent()
  }

  /** exec-data output for the Rexx script */
  private fun produceExecDataOutput(): String {
    return """
      if exec_system <> '' then
        Say "=|||= exec-system:" exec_system "=|||="
      if exec_member <> '' then
        Say "=|||= exec-member:" exec_member "=|||="
      if exec_submitted_date <> '' then
        Say "=|||= exec-submitted-date:" value("DATER."r_i) "=|||="
      if exec_submitted_time <> '' then
        Say "=|||= exec-submitted-time:" value("TIMER."r_i) "=|||="
      if exec_started_date <> '' then
        Say "=|||= exec-started-date:" exec_started_date "=|||="
      if exec_started_time <> '' then
        Say "=|||= exec-started-time:" exec_started_time "=|||="
      if exec_ended_date <> '' then
        Say "=|||= exec-ended-date:" exec_ended_date "=|||="
      if exec_ended_time <> '' then
        Say "=|||= exec-ended-time:" exec_ended_time "=|||="
    """.trimIndent()
  }

  /** step-data input parameters setup for the Rexx script */
  private fun produceStepDataInputs(): String {
    return """
      step_data_summary_lines. = ''
      step_data_summary_count = 0
      step_data_ief_messages. = ''
      step_data_ief_count = 0
      step_data_track_ids = 'IEF472I IEF142I IEF373I IEF032I'
    """.trimIndent()
  }

  /** step-data parameters parsing from JESMSGLG spool file for the Rexx script */
  private fun parseStepDataFromJESMSGLG(): String {
    return """
      /* Check for pattern ' nn.nn.nn J_IDxxxx  -' */
      if Length(current_line) >= 21 then do
        if Substr(current_line, 1, 1) = ' ' & ,
           Datatype(Substr(current_line, 2, 1), 'W') & ,
           Datatype(Substr(current_line, 3, 1), 'W') & ,
           Substr(current_line, 4, 1) = '.' & ,
           Datatype(Substr(current_line, 5, 1), 'W') & ,
           Datatype(Substr(current_line, 6, 1), 'W') & ,
           Substr(current_line, 7, 1) = '.' & ,
           Datatype(Substr(current_line, 8, 1), 'W') & ,
           Datatype(Substr(current_line, 9, 1), 'W') & ,
           Substr(current_line, 10, 1) = ' ' & ,
           Substr(current_line, 19, 1) = ' ' & ,
           Substr(current_line, 20, 1) = ' ' & ,
           Substr(current_line, 21, 1) = '-' then do

          jobid_in_line = Strip(Substr(current_line, 11, 8))
          if jobid_in_line = Strip(value("JOBID."r_i)) then do
            step_data_summary_count = step_data_summary_count + 1
            step_data_summary_lines.step_data_summary_count = current_line
          end
        end
      end
    """.trimIndent()
  }

  /** step-data parameters parsing from JESYSMSG spool file for the Rexx script */
  private fun parseStepDataFromJESYSMSG(): String {
    return """
      if jds_DDNAME.ds_i = "JESYSMSG" then do
        do until isfnextlinetoken='' | is_subsys_defined = 1
          Address SDSF "ISFBROWSE ST TOKEN('"jds_TOKEN.ds_i"')"
          do kx=1 to isfline.0
            current_line = isfline.kx

            do msg_idx = 1 to Words(step_data_track_ids)
              msg_id = Word(step_data_track_ids, msg_idx)
              if Pos(msg_id, current_line) > 0 then do
                step_data_ief_count = step_data_ief_count + 1
                step_data_ief_messages.step_data_ief_count = current_line
                leave
              end
            end
          end
          isfstartlinetoken = isfnextlinetoken
        end
      end
    """.trimIndent()
  }

  /** step-data output for the Rexx script */
  private fun produceStepDataOutput(): String {
    return """
      step_data_summary_lines.0 = step_data_summary_count
      step_data_ief_messages.0 = step_data_ief_count
      if step_data_summary_lines.0 > 0 then do
        Say "=///= STEP DATA START =///="
        Say "=/= JESMSGLG SUMMARY START =/="
        do i = 1 to step_data_summary_lines.0
          Say step_data_summary_lines.i
        end
        Say "=\= JESMSGLG SUMMARY END =\="
      end
      if step_data_ief_messages.0 > 0 then do
        Say "=/= JESYSMSG MESSAGES START =/="
        do i = 1 to step_data_ief_messages.0
          Say step_data_ief_messages.i
        end
        Say "=\= JESYSMSG MESSAGES END =\="
      end
      Say "=\\\= STEP DATA END =\\\="
    """.trimIndent()
  }

  /**
   * Produce a Rexx script to fetch the job parameters.
   * The script calls ISFEXEC ST and parses JESMSGLG + JESYSMSG for all the needed parameters
   */
  private fun produceRexxScript(): String {
    return """/* REXX */
      rc = isfcalls('ON')
      Address SDSF "ISFEXEC ST $jobName" /* Access the ST panel */
      ISFCOLS = 'JNAME JOBID OWNERID TOKEN RETCODE JTYPE PHASE PHASENAME JCLASS QUEUE ${if (isFetchExecData) "DATER TIMER" else ""}'
      if rc <> 0 then do
        Say "=== ISFEXEC ERROR, RC="rc" ==="
        Call isfcalls 'OFF'
        Exit rc
      end
      FIXED_FIELD = word(ISFCOLS, 1) /* Get fixed field name from first word of ISFCOLS special variable */
      if ISFROWS = 0 then do
        Say "=== JOB BY NAME $jobName IS NOT FOUND ==="
        Call isfcalls 'OFF'
        Exit 255
      end
      Say "=== JOB INFO OUTPUT START ==="
      do r_i = 1 to ISFROWS /* Process all rows */
        curr_j_id = value("JOBID."r_i)
        if curr_j_id = $jobId then do
          job_token = value("TOKEN".r_i)
          Address SDSF "ISFACT ST TOKEN('"job_token"') PARM(NP ?) ( prefix jds_" /* ? (JDS) action */
          lrc=rc
          call msgrtn
          if lrc <> 0 then
            Exit 20

          is_subsys_defined = 0
          subsystem = ''
          ${if (isFetchExecData) produceExecDataInputs() else ""}
          ${if (isFetchStepData) produceStepDataInputs() else ""}
          /* Find the JESMSGLG data set and read it using ISFBROWSE. */
          /* Use isflinelim to limit the number of REXX variables returned. */
          isflinelim=500
          do ds_i=1 to jds_DDNAME.0
            if jds_DDNAME.ds_i = "JESMSGLG" then do
              do until isfnextlinetoken='' | is_subsys_defined = 1
                Address SDSF "ISFBROWSE ST TOKEN('"jds_TOKEN.ds_i"')"
                do kx=1 to isfline.0
                  current_line = isfline.kx
                  
                  if Pos('J E S 2  J O B  L O G', current_line) > 0 | Pos('${'$'}HASP', current_line) > 0 then do
                    subsystem = "JES2"
                    ${if (isFetchExecData) parseExecSystemAndMember() else ""}
                  end
                  
                  ${if (isFetchExecData) parseExecDatesAndTimes() else ""}

                  ${if (isFetchStepData) parseStepDataFromJESMSGLG() else ""}
                end
                isfstartlinetoken = isfnextlinetoken
              end
            end
            ${if (isFetchStepData) parseStepDataFromJESYSMSG() else ""}
          end
          if subsystem <> '' then
            Say "=|||= Job subsystem:" subsystem "=|||="
          else
            Say "=|||= Job subsystem: JES3 =|||="
          Say "=|||= Job name:" value("JNAME."r_i) "=|||="
          Say "=|||= Job ID:" value("JOBID."r_i) "=|||="
          Say "=|||= Job owner:" value("OWNERID."r_i) "=|||="
          Say "=|||= Job RC:" value("RETCODE."r_i) "=|||="
          Say "=|||= Job type:" value("JTYPE."r_i) "=|||="
          Say "=|||= Job phase (num):" value("PHASE."r_i) "=|||="
          Say "=|||= Job phase name:" value("PHASENAME."r_i) "=|||="
          Say "=|||= Job class:" value("JCLASS."r_i) "=|||="
          Say "=|||= Job queue:" value("QUEUE."r_i) "=|||="
          ${if (isFetchExecData) produceExecDataOutput() else ""}
          ${if (isFetchStepData) produceStepDataOutput() else ""}
          leave
        end
      end
      Say "=== JOB INFO OUTPUT END ==="
      rc = isfcalls('OFF')
      Exit 0

      msgrtn: procedure expose isfmsg isfmsg2. /* Subroutine to list error messages */
        if isfmsg <> "" then /* The isfmsg variable contains a short message */
          Say "isfmsg is:" isfmsg
        do ix=1 to isfmsg2.0 /* The isfmsg2 stem contains additional descriptive error messages */
          Say "isfmsg2."ix "is:" isfmsg2.ix
        end
        return
      """.trimIndent()
  }

  /**
   * The SSH command to create a Rexx script under /tmp path.
   * Executes creation and removes the file no matter what RC is produced
   */
  override var sshCommand: String = ""
    get() {
      return """
        ### getJob cmd
        
        cleanup() {
          rm -rf $scriptPath
        }
        
        # Execute cleanup of the script on any exit
        trap cleanup EXIT INT TERM HUP
        
        # Produce the script
        cat > $scriptPath <<'REXX_EOF'
${produceRexxScript()}
REXX_EOF
        chmod +x $scriptPath
      
        # Run the script
        $scriptPath
      """.trimIndent()
    }

  override suspend fun produceResponseObject(clientResponse: Any): SshGetJobResponse {
    return SshGetJobResponse(clientResponse as SshCmdResponse)
  }

}

/*
 * This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Copyright Contributors to the Zowe Project.
 */

package org.zowe.kotlinsdk.providers.zowe.openssh.jes

import org.zowe.kotlinsdk.providers.zowe.openssh.jes.definitions.SshJobExecData
import org.zowe.kotlinsdk.providers.zowe.openssh.jes.definitions.SshJobItem
import org.zowe.kotlinsdk.providers.zowe.openssh.jes.definitions.SshJobStepData
import kotlin.random.Random

private val inputStatusQueues = listOf("CONVERSION", "SETUP")
private val activeStatusQueues = listOf("EXECUTION", "INPUT")
private val outputStatusQueues = listOf("PRINT", "OUTPUT", "XMITTER", "RECEIVE", "SPIN", "PURGE")

/**
 * Extract the next job value from the provided string of SSH response.
 * The string must be in a format "=|||= <fieldName>: <the-value> =|||="
 * @param output the SSH response output to get the string from
 * @param fieldName the field name to get the value for
 * @return the found value as a string or empty string if the value is not found or is not defined for the field name
 */
fun extractJobValue(output: String, fieldName: String): String {
  val regex = """=\|\|\|= $fieldName: (.*?) =\|\|\|=""".toRegex()
  return regex.find(output)?.groupValues?.getOrNull(1) ?: ""
}

/** exec-data input parameters setup for the Rexx script */
fun produceExecDataInput(): String {
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

/** exec-data output for the Rexx script */
fun produceExecDataOutput(): String {
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
fun produceStepDataInput(): String {
  return """
    step_data_summary_lines. = ''
    step_data_summary_count = 0
    step_data_ief_messages. = ''
    step_data_ief_count = 0
    step_data_track_ids = 'IEF472I IEF142I IEF373I IEF032I'
  """.trimIndent()
}

/** step-data output for the Rexx script */
fun produceStepDataOutput(): String {
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

/** exec-data system and member parsing from the JESMSGLG for the Rexx script */
fun parseExecSystemAndMember(): String {
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
fun parseExecDatesAndTimes(): String {
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

/** step-data parameters parsing from JESMSGLG spool file for the Rexx script */
fun parseStepDataFromJESMSGLG(): String {
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
fun parseStepDataFromJESYSMSG(): String {
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

/**
 * Produce a Rexx script to fetch the job parameters.
 * The script calls ISFEXEC ST and parses JESMSGLG + JESYSMSG for all the needed parameters
 * @param jobPrefix the job prefix to search jobs by
 * @param jobId the exact job ID to fetch the job by
 * @param jobOwner the job owner to search jobs by
 * @param isFetchExecData the flag to fetch exec-data for jobs if true
 * @param isFetchStepData the flag to fetch step-data for jobs if true
 * @param maxJobsCount the max amount of jobs to be returned by the script
 * @return the merged Rexx script to search for jobs with
 */
fun produceIsfexecStRexxScript(
  jobPrefix: String,
  jobId: String?,
  jobOwner: String?,
  isFetchExecData: Boolean,
  isFetchStepData: Boolean,
  maxJobsCount: Int = 0
): String {
  return """/* REXX */
    rc = isfcalls('ON')
    Address SDSF "ISFEXEC ST $jobPrefix" /* Access the ST panel */
    ISFCOLS = 'JNAME JOBID OWNERID TOKEN RETCODE JTYPE PHASE PHASENAME JCLASS QUEUE ${if (isFetchExecData) "DATER TIMER" else ""}'
    if rc <> 0 then do
      Say "=== ISFEXEC ERROR, RC="rc" ==="
      Call isfcalls 'OFF'
      Exit rc
    end
    FIXED_FIELD = word(ISFCOLS, 1) /* Get fixed field name from first word of ISFCOLS special variable */
    if ISFROWS = 0 then do
      Say "=== JOB BY NAME $jobPrefix IS NOT FOUND ==="
      Call isfcalls 'OFF'
      Exit 255
    end
    Say "=== JOBS INFO OUTPUT START ==="
    do r_i = 1 to ISFROWS /* Process all rows */
      curr_j_id = value("JOBID."r_i)
      ${if (jobId.isNullOrEmpty()) "if 1 then do" else "if curr_j_id = $jobId then do"}
      ${if (jobOwner.isNullOrEmpty()) "if 1 then do" else "if value(\"OWNERID.\"r_i) = $jobOwner then do"}
        job_token = value("TOKEN".r_i)
        Address SDSF "ISFACT ST TOKEN('"job_token"') PARM(NP ?) ( prefix jds_" /* ? (JDS) action */
        lrc=rc
        call msgrtn
        if lrc <> 0 then
          Exit 20

        match_count = 0
        is_subsys_defined = 0
        subsystem = ''
        ${if (isFetchExecData) produceExecDataInput() else ""}
        ${if (isFetchStepData) produceStepDataInput() else ""}
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
        Say "=== JOB" match_count "OUTPUT START ==="
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
        Say "=== JOB" match_count "OUTPUT END ==="
        ${if (!jobId.isNullOrEmpty()) "leave" else ""}
        match_count = match_count + 1
        ${if (maxJobsCount > 0) "if match_count >= $maxJobsCount then leave" else ""}
      end /* for job owner check */
      end /* for job ID check */
    end
    Say "=== JOBS INFO OUTPUT END ==="
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
 * Produce a multi-line .sh script to run using SSH
 * @param cmdName the command name to save a temporary .sh file, that will indicate the command to run by the script
 * @param scriptProducer the Rexx script producer to save as content of the .rexx file
 * @return a list of SSH commands that will create a .rexx file
 *         in /tmp folder on the USS side with the Rexx script inside
 */
fun produceRexxScriptRunFile(cmdName: String, scriptProducer: () -> String): String {
  val scriptPath = "/tmp/zowe_${cmdName}_${System.currentTimeMillis()}_${Random.nextInt(10000)}.rexx"
  return """
    ### $cmdName
    
    cleanup() {
      rm -rf $scriptPath
    }
    
    # Execute cleanup of the script on any exit
    trap cleanup EXIT INT TERM HUP
    
    # Produce the script
    cat > $scriptPath <<'REXX_EOF'
${scriptProducer()}
REXX_EOF
    chmod +x $scriptPath
  
    # Run the script
    $scriptPath
  """.trimIndent()
}

/**
 * Get the job status by the queue the job is placed in
 * @param queue the queue (from ISFEXEC ST) name the job is currently in
 * @return the [SshJobItem.SshJobStatus] or null if the queue is "UNKNOWN"
 */
private fun getJobStatusFromQueue(queue: String): SshJobItem.SshJobStatus? {
  val indepQueue = queue.replace(" (JES3)", "")
  return when {
    inputStatusQueues.contains(indepQueue) -> SshJobItem.SshJobStatus.INPUT
    activeStatusQueues.contains(indepQueue) -> SshJobItem.SshJobStatus.ACTIVE
    outputStatusQueues.contains(indepQueue) -> SshJobItem.SshJobStatus.OUTPUT
    else -> null // UNKNOWN and unparsed queue
  }
}

/**
 * Parse a single job block from the SSH output into an [SshJobItem].
 * Extracts all job fields using the =|||= delimited format.
 * @param jobOutputParts the raw text of one job block
 * @return the constructed [SshJobItem]
 */
fun parseSingleJob(jobOutputParts: String): SshJobItem {
  val jobId = extractJobValue(jobOutputParts, "Job ID")
  val jobName = extractJobValue(jobOutputParts, "Job name")
  val jobOwner = extractJobValue(jobOutputParts, "Job owner")
  val subsystem = extractJobValue(jobOutputParts, "Job subsystem")
  val jobQueue = extractJobValue(jobOutputParts, "Job queue")
  val jobStatus = getJobStatusFromQueue(jobQueue)
  val jobTypeStr = extractJobValue(jobOutputParts, "Job type")
  val jobType = SshJobItem.SshJobType.valueOf(jobTypeStr)
  val jobClass = if (jobType == SshJobItem.SshJobType.STC || jobType == SshJobItem.SshJobType.TSU) jobTypeStr
    else extractJobValue(jobOutputParts, "Job class")
  val jobRc = extractJobValue(jobOutputParts, "Job RC").ifEmpty { null }
  val phaseNum = extractJobValue(jobOutputParts, "Job phase \\(num\\)").toInt()
  val phaseName = extractJobValue(jobOutputParts, "Job phase name")
  val stepData = SshJobStepData.parseJobStepData(jobOutputParts)
  val execData = SshJobExecData(jobOutputParts)
  return SshJobItem(
    jobId,
    jobName,
    jobOwner,
    subsystem,
    jobStatus,
    jobType,
    jobClass,
    jobRc,
    phaseNum,
    phaseName,
    stepData,
    execData.execSystem,
    execData.execMember,
    execData.execSubmitted,
    execData.execStarted,
    execData.execEnded
  )
}

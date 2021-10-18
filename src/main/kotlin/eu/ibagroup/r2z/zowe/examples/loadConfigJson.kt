package eu.ibagroup.r2z.zowe.examples

import eu.ibagroup.r2z.zowe.config.getAuthEncoding
import eu.ibagroup.r2z.zowe.config.parseConfigJson
import eu.ibagroup.r2z.zowe.config.toBasicAuthToken

fun main() {
  val inputStream = object {}.javaClass.classLoader.getResourceAsStream("zowe.config.json")
  if (inputStream != null) {
    val zoweConfig = parseConfigJson(inputStream)
    println("url=\"${zoweConfig.protocol}://${zoweConfig.host}:${zoweConfig.port}\"; username=${zoweConfig.username}; password=${zoweConfig.password}")
    println(zoweConfig.getAuthEncoding().toBasicAuthToken())
  }
}

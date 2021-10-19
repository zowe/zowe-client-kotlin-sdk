package eu.ibagroup.r2z.zowe.examples

import eu.ibagroup.r2z.zowe.config.*

fun main() {
  val inputStream = object {}.javaClass.classLoader.getResourceAsStream("zowe.config.json")
  if (inputStream != null) {
    val zoweConfig = parseConfigJson(inputStream)
    println(zoweConfig.toJson())
  }
}

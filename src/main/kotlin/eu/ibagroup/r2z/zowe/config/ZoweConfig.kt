package eu.ibagroup.r2z.zowe.config

class ZoweConfig(
  val profiles: Map<String, ZoweConfigProfile>,
  val defaults: Map<String, String>
) {
  var username: String?
    get() = profiles["base"]?.secure?.get(0)
    set(el) { profiles["base"]?.secure?.set(0, el ?: "") }

  var password: String?
    get() = profiles["base"]?.secure?.get(1)
    set(el) { profiles["base"]?.secure?.set(1, el ?: "") }

  @Suppress("UNCHECKED_CAST")
  var host: String?
    get() = profiles["base"]?.properties?.get("host") as String?
    set(el) { profiles["base"]?.properties?.set("host", el) }

  @Suppress("UNCHECKED_CAST")
  var rejectUnauthorized: Boolean?
    get() = profiles["base"]?.properties?.get("rejectUnauthorized") as Boolean?
    set(el) { profiles["base"]?.properties?.set("rejectUnauthorized", el ?: true) }

  @Suppress("UNCHECKED_CAST")
  var port: Int?
    get() = (profiles["zosmf"]?.properties?.get("port") as Double?)?.toInt()
    set(el) { profiles["base"]?.properties?.set("port", el?.toDouble()) }

  @Suppress("UNCHECKED_CAST")
  var protocol: String
    get() = profiles["zosmf"]?.properties?.get("protocol") as String? ?: "http"
    set(el) { profiles["base"]?.properties?.set("protocol", el) }

  @Suppress("UNCHECKED_CAST")
  var basePath: String
    get() = profiles["zosmf"]?.properties?.get("basePath") as String? ?: "/"
    set(el) { profiles["base"]?.properties?.set("basePath", el) }

  @Suppress("UNCHECKED_CAST")
  var encoding: Int
    get() = (profiles["zosmf"]?.properties?.get("encoding") as Double?)?.toInt() ?: 1047
    set(el) { profiles["base"]?.properties?.set("encoding", el.toDouble()) }

  @Suppress("UNCHECKED_CAST")
  var responseTimeout: Int
    get() = (profiles["zosmf"]?.properties?.get("responseTimeout") as Double?)?.toInt() ?: 600
    set(el) { profiles["base"]?.properties?.set("responseTimeout", el.toDouble()) }
}

class ZoweConfigProfile(
  val type: String,
  val properties: MutableMap<String, Any?>,
  val secure: ArrayList<String>
)

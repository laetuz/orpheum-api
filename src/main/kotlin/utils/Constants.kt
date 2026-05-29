package id.neotica.utils

object Constants {
    val baseUrl = Utils.EnvLoader["NEOSERVER_URL"]
    const val AUTH_JWT = "auth-jwt"
    val WEED_URL = Utils.EnvLoader["WEED_URL"]
}
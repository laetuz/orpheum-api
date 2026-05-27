package id.neotica.utils

object Constants {
    val baseUrl = Utils.EnvLoader["NEOSERVER_URL"]
    const val AUTH_JWT = "auth-jwt"
}
package id.neotica.application

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*

fun Application.configureHTTP() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)
        allowHeader(HttpHeaders.Authorization)
        allowHeader("MyCustomHeader")
        allowHeader("username")
        allowHeader("password")
        allowHeader(HttpHeaders.ContentType)
        allowHost("neotica.id")
//        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }
}

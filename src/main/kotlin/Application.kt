package id.neotica

import id.neotica.application.configureAuthentication
import id.neotica.application.configureFrameworks
import id.neotica.application.configureMonitoring
import id.neotica.application.configureSerialization
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureAuthentication()
    configureSerialization()
    configureMonitoring()
    configureFrameworks()
    configureRouting()
}

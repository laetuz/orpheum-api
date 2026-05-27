package id.neotica.application

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import id.neotica.utils.Constants.AUTH_JWT
import id.neotica.utils.Constants.baseUrl
import id.neotica.utils.Utils
import io.ktor.http.auth.HttpAuthHeader
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureAuthentication() {
    authentication {
        jwt(AUTH_JWT) {
            realm = "access auth-jwt"
            authHeader { call ->
                call.request.parseAuthorizationHeader()
                    ?: call.request.cookies["auth_token"]?.let { HttpAuthHeader.Single("Bearer", it) }
            }
            verifier(
                JWT
                    .require(Algorithm.HMAC256(Utils.EnvLoader["HMAC_256_SECRET"]))
                    .withIssuer("${baseUrl}/")
                    .build()
            )
            validate {
                if (it.payload.getClaim("id").asString().isNotEmpty()) {
                    JWTPrincipal(it.payload)
                } else {
                    println("✨ jwt auth failed.")
                }
            }
        }
    }
}

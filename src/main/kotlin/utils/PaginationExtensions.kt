package id.neotica.utils

import io.ktor.server.application.ApplicationCall
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.jdbc.SizedIterable
import kotlin.math.ceil

@Serializable
data class PaginatedResponse<T>(
    val data: List<T>,
    val page: Int,
    val limit: Int,
    @SerialName("total_items")
    val totalItems: Long,
    @SerialName("total_pages")
    val totalPages: Int,
)

data class PaginationParams(val page: Int, val limit: Int, val offset: Long)

fun ApplicationCall.getPaginationParams(defaultLimit: Int = 10, maxLimit: Int = 50): PaginationParams {
    val page = request.queryParameters["page"]?.toIntOrNull()?.coerceAtLeast(1) ?: 1
    val limit = request.queryParameters["limit"]?.toIntOrNull()?.coerceIn(1, maxLimit) ?: defaultLimit
    val offset = ((page - 1) * limit).toLong()

    return PaginationParams(page, limit, offset)
}

fun <T: Entity<*>, R> SizedIterable<T>.paginate(
    params: PaginationParams,
    mapper: (T) -> R
): PaginatedResponse<R> {
    val totalItems = this.count()
    val totalPages = ceil(totalItems.toDouble() / params.limit).toInt()

    val pagedData = this.limit(params.limit).offset(params.offset).map(mapper)

    return PaginatedResponse(
        data = pagedData,
        page = params.page,
        limit = params.limit,
        totalItems = totalItems,
        totalPages = totalPages
    )
}
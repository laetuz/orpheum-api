package id.neotica.domain

interface NeoDatabase {
    suspend fun <T> dbQuery(block: () -> T): T
}
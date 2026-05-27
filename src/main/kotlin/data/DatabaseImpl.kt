package id.neotica.data

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import id.neotica.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import javax.sql.DataSource
import kotlin.coroutines.CoroutineContext

class DatabaseImpl(
    private val ioContext: CoroutineContext = Dispatchers.IO
) : NeoDatabase {

    init {
        val dataSource = hikari()
        runFlyway(dataSource)
        Database.connect(dataSource)
    }

    override suspend fun <T> dbQuery(block: () -> T): T = withContext(ioContext) { transaction { block() } }

    private fun hikari(): HikariDataSource = HikariDataSource(
        HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"

            // 🚨 CRITICAL ORPHEUM OVERRIDE 🚨
            // Assuming NEOSERVER_PG_BASE_URL is "jdbc:postgresql://127.0.0.1:5433"
            val baseUrl = Utils.EnvLoader["NEOSERVER_PG_BASE_URL"]
            jdbcUrl = "$baseUrl/db_orpheum"

            username = Utils.EnvLoader["NEOSERVER_PG_USER"]
            password = Utils.EnvLoader["NEOSERVER_PG_PASSWORD"]
            maximumPoolSize = 3
            isAutoCommit = true
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
    )

    private fun runFlyway(dataSource: DataSource) {
        Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:/db/migrations/")
            .load()
            .migrate()
    }
}

// The interface contract (usually in its own file or at the bottom)
interface NeoDatabase {
    suspend fun <T> dbQuery(block: () -> T): T
}
package id.neotica.di

import id.neotica.data.DatabaseImpl
import id.neotica.data.repository.CatalogRepositoryImpl
import id.neotica.data.repository.PlaylistRepositoryImpl
import id.neotica.domain.NeoDatabase
import id.neotica.domain.repository.CatalogRepository
import id.neotica.domain.repository.PlaylistRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val orpheumModule = module {
    single<NeoDatabase> { DatabaseImpl() }

    singleOf(::CatalogRepositoryImpl).bind(CatalogRepository::class)
    singleOf(::PlaylistRepositoryImpl).bind(PlaylistRepository::class)

    single {
        HttpClient(CIO) { expectSuccess = false }
    }
}
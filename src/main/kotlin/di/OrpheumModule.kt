package id.neotica.di

import id.neotica.data.DatabaseImpl
import id.neotica.data.repository.CatalogRepositoryImpl
import id.neotica.data.repository.PlaylistRepositoryImpl
import id.neotica.domain.NeoDatabase
import id.neotica.domain.repository.CatalogRepository
import id.neotica.domain.repository.PlaylistRepository
import id.neotica.route.AdminRoute
import id.neotica.route.CatalogRoute
import id.neotica.route.PlaylistRoute
import id.neotica.route.StreamRoute
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

    singleOf(::AdminRoute)
    singleOf(::CatalogRoute)
    singleOf(::PlaylistRoute)
    singleOf(::StreamRoute)
}
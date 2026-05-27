package id.neotica.di

import id.neotica.data.DatabaseImpl
import id.neotica.data.NeoDatabase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val orpheumModule = module {
    single<NeoDatabase> { DatabaseImpl() }
}
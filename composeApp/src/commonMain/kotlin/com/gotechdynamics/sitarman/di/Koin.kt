package com.gotechdynamics.sitarman.di

import com.russhwolf.settings.Settings
import com.gotechdynamics.sitarman.data.auth.*
import com.gotechdynamics.sitarman.data.user.*
import com.gotechdynamics.sitarman.screens.login.LoginViewModel
import com.gotechdynamics.sitarman.screens.user.UserViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

import org.koin.dsl.KoinAppDeclaration

val dataModule = module {
    single { Settings() }

    single {
        val json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            isLenient = true
        }
        HttpClient {
            install(ContentNegotiation) {
                json(json)
            }
        }
    }


    single { AuthApi(get()) }
    single { UserApi(get(), get()) }
    single { UserRepository(get()) }
}

val viewModelModule = module {
    factoryOf(::LoginViewModel)
    factoryOf( constructor = ::UserViewModel)
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(dataModule, viewModelModule)
    }
}
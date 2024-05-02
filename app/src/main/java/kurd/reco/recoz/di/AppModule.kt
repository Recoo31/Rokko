package kurd.reco.recoz.di

import org.koin.dsl.module

val appModule = module {
    includes(viewModelModule)
}

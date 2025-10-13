package com.sky31.gongmultiplatform.di

import com.sky31.gongmultiplatform.ui.viewModel.ConfigViewModel
import com.sky31.gongmultiplatform.ui.viewModel.MainViewModel
import org.koin.dsl.module

val viewModelModule = module {
    single<ConfigViewModel> { ConfigViewModel() }
    single<MainViewModel> { MainViewModel() }
}
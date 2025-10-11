package com.sky31.gongmultiplatform.di

import com.sky31.gongmultiplatform.ui.viewModel.ConfigViewModel
import org.koin.dsl.module

val viewModelModule = module {
    single { ConfigViewModel() }
}
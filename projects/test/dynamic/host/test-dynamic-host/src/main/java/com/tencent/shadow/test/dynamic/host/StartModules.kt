/**
 * StartModules.kt
 * @author blinkjiang
 * @date 2019-07-16
 */
package com.tencent.shadow.test.dynamic.host

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val startModules = module {

    viewModel { SplashViewModel(androidContext()) }

}

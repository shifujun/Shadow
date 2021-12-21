package com.tencent.cloudgamepluginbaseactivity2

import android.content.ComponentCallbacks
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ViewModelParameters
import org.koin.androidx.viewmodel.createViewModelProvider
import org.koin.androidx.viewmodel.getInstance
import org.koin.core.Koin
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass


inline fun <reified T : ViewModel> LifecycleOwner.viewModelFromHost(
        qualifier: Qualifier? = null,
        noinline parameters: ParametersDefinition? = null
): Lazy<T> = lazy { getViewModelFromHost<T>(qualifier, parameters) }

inline fun <reified T : ViewModel> LifecycleOwner.getViewModelFromHost(
        qualifier: Qualifier? = null,
        noinline parameters: ParametersDefinition? = null
): T {
    return getViewModelFromHost(T::class, qualifier, parameters)
}

fun <T : ViewModel> LifecycleOwner.getViewModelFromHost(
        clazz: KClass<T>,
        qualifier: Qualifier? = null,
        parameters: ParametersDefinition? = null
): T {
    return (this as ComponentCallbacks).getKoin().getViewModelFromHost(
            ViewModelParameters(
                    clazz,
                    this@getViewModelFromHost,
                    qualifier,
                    parameters = parameters
            )
    )
}

fun <T : ViewModel> Koin.getViewModelFromHost(parameters: ViewModelParameters<T>): T {
    val vmStore: ViewModelStore = parameters.owner.getViewModelStoreFromHost(parameters)
    val viewModelProvider = rootScope.createViewModelProvider(vmStore, parameters)
    return viewModelProvider.getInstance(parameters)
}

fun <T : ViewModel> LifecycleOwner.getViewModelStoreFromHost(
        parameters: ViewModelParameters<T>
): ViewModelStore =
        when {
            this is FragmentActivity -> this.viewModelStore
            this is Fragment -> this.viewModelStore
            else -> error("Can't getByClass ViewModel '${parameters.clazz}' on $this - Is not a FragmentActivity nor a Fragment neither a valid ViewModelStoreOwner")
        }
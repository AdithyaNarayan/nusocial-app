package com.teamnusocial.nusocial

import android.app.Application
import com.teamnusocial.nusocial.data.repository.AuthUserRepository
import com.teamnusocial.nusocial.ui.auth.AuthViewModel
import com.teamnusocial.nusocial.ui.auth.AuthViewModelFactory
import com.teamnusocial.nusocial.utils.FirebaseAuthUtils
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.singleton
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider

class DependencyInjectionApplication : Application(), KodeinAware {
    override val kodein: Kodein = Kodein.lazy {
        import(androidXModule(this@DependencyInjectionApplication))

        // Auth dependency tree
        bind() from singleton { FirebaseAuthUtils() }
        bind() from singleton { AuthUserRepository(instance()) }
        bind() from provider { AuthViewModelFactory(instance()) }
    }
}
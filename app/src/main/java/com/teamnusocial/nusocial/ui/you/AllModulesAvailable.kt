package com.teamnusocial.nusocial.ui.you

import com.teamnusocial.nusocial.data.model.Module
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Path

interface AllModulesAvailable {
    @GET("{acadYear}/moduleList.json")
    fun retrieveModules(@Path("acadYear") acadYear: String): Deferred<List<Module>>
}
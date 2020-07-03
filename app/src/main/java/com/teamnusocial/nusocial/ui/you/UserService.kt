package com.teamnusocial.nusocial.ui.you
import com.teamnusocial.nusocial.data.model.Module
import kotlinx.coroutines.Deferred
import retrofit2.http.*

public interface UserService {
    fun getReponse(): Deferred<String>
}
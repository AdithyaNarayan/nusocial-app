package com.teamnusocial.nusocial.utils

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.teamnusocial.nusocial.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        if (FirestoreUtils().getCurrentUser() != null) {
            addRegistrationToken(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("NOTIFICATION", message.data.toString())
    }

    companion object {
        fun addRegistrationToken(token: String) {
            val userRepository = UserRepository(FirestoreUtils())

            if (token.isEmpty()) {
                return
            }

            CoroutineScope(Dispatchers.IO).launch {
                userRepository.getRegistrationTokensAnd {
                    if (it.contains(token)) {
                        return@getRegistrationTokensAnd
                    }
                    it.add(token)
                    CoroutineScope(Dispatchers.IO).launch {
                        userRepository.setRegistrationTokens(it)
                    }
                }
            }
        }
    }
}
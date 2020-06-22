package com.teamnusocial.nusocial.utils

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.teamnusocial.nusocial.data.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class FirestoreUtils {
    private val firestoreInstance = FirebaseFirestore.getInstance()

    private val firebaseAuth = FirebaseAuth.getInstance()

    suspend fun getAllUsers() =
        coroutineScope {
            firestoreInstance.collection("users")
        }

    fun getCurrentUser() = firebaseAuth.currentUser

    suspend fun getCurrentUserAsDocument() = coroutineScope {
        return@coroutineScope firestoreInstance
            .collection("users")
            .document(getCurrentUser()!!.uid)
    }
    suspend fun getUserAsDocument(userID: String) = coroutineScope {
        return@coroutineScope firestoreInstance
            .collection("users")
            .document(userID)
    }

    suspend fun getMessagesOfUser(user: Pair<String, String>) = coroutineScope {
        return@coroutineScope firestoreInstance
            .collection("messagesChannel").whereArrayContains("recipients", user)
            .whereLessThan("invisible", user.second)
    }

    //posts, comments and communities
    suspend fun getAllPosts(communityID: String) = coroutineScope {
        return@coroutineScope firestoreInstance
            .collection("posts")
            .document(communityID)
            .collection("posts")
    }
    suspend fun getComments(communityID: String, postID: String) = coroutineScope {
        return@coroutineScope firestoreInstance
            .collection("comments")
            .document(communityID)
            .collection("posts")
            .document(postID)
            .collection("comments")
    }
    suspend fun getAllCommunities() =
        coroutineScope {
            firestoreInstance.collection("communities")
        }
    //
    fun getMessages(messageID: String) =
        firestoreInstance.collection("messagesChannel").document(messageID)

    suspend fun createChatWith(userID: String) = coroutineScope {
        getUserAsDocument(userID).get().addOnCompleteListener { otherUser ->
            CoroutineScope(Dispatchers.IO).launch {
                getCurrentUserAsDocument().get().addOnCompleteListener { currentUser ->
                    val messageID =
                        if (currentUser.result!!.id > otherUser.result!!.id) "${currentUser.result!!.id}_${otherUser.result!!.id}" else "${otherUser.result!!.id}_${currentUser.result!!.id}"
                    firestoreInstance.collection("messagesChannel")
                        .document(messageID).set(
                            MessageConfig(
                                listOf(
                                    Pair(
                                        otherUser.result!!["uid"] as String,
                                        otherUser.result!!["name"] as String
                                    ),
                                    Pair(
                                        currentUser.result!!["uid"] as String,
                                        currentUser.result!!["name"] as String
                                    )
                                ), ""
                            )
                        )
                }
            }
        }

    }
}
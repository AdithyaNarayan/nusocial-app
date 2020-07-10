package com.teamnusocial.nusocial.utils

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teamnusocial.nusocial.data.model.MessageConfig
import com.teamnusocial.nusocial.data.model.MyPair
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

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
    suspend fun getCommunity(communityID: String) = coroutineScope {
        return@coroutineScope firestoreInstance
            .collection("communities")
            .document(communityID)
    }

    suspend fun getAllPosts(communityID: String) = coroutineScope {
        return@coroutineScope firestoreInstance
            .collection("communities")
            .document(communityID)
            .collection("posts")
    }

    suspend fun getComments(communityID: String, postID: String) = coroutineScope {
        return@coroutineScope firestoreInstance
            .collection("communities")
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
                                mutableListOf(
                                    MyPair(
                                        otherUser.result!!["uid"] as String,
                                        otherUser.result!!["name"] as String
                                    ),
                                    MyPair(
                                        currentUser.result!!["uid"] as String,
                                        currentUser.result!!["name"] as String
                                    )
                                ), "", "", "", Timestamp.now(), messageID
                            )
                        )
                }
            }
        }

    }

    suspend fun createGroupChatWith(name: String, users: List<String>) = coroutineScope {

        val list = mutableListOf<MyPair>()

        users.forEach { userID ->
            getUserAsDocument(userID).get().addOnSuccessListener {
                list.add(MyPair(userID, it["name"] as String))
                notifyUpdateOfList(name, list, users.size)
            }
        }

    }

    private fun notifyUpdateOfList(name: String, list: List<MyPair>, size: Int) {
        if (list.size == size) {
            firestoreInstance.collection("messagesChannel")
                .add(MessageConfig(list.toMutableList(), "", name, "", Timestamp.now(), "")).addOnCompleteListener {
                    if (it.isSuccessful) {
                        it.result!!.update("id", it.result!!.id)
                    }
                }
        }
    }
}
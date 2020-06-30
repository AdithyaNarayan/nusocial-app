package com.teamnusocial.nusocial.data.repository

import android.location.Location
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.teamnusocial.nusocial.data.model.LocationLatLng
import com.teamnusocial.nusocial.data.model.Module
import com.teamnusocial.nusocial.data.model.TextMessage
import com.teamnusocial.nusocial.data.model.User
import com.teamnusocial.nusocial.utils.FirestoreUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserRepository(private val utils: FirestoreUtils) {
    suspend fun getUsers() = coroutineScope {
        val userList = listOf<User>().toMutableList()
        val currUser = getCurrentUserAsUser()
        utils.getAllUsers().get().addOnCompleteListener {
            if (it.isSuccessful) {
                it.result!!.documents.forEach { user ->
                    if(FirebaseAuth.getInstance().uid != user.id && !currUser.buddies.contains(user.id) && !currUser.seenAndMatch.contains(user.id)) {
                        val userItem = user.toObject(User::class.java)!!
                        userItem.uid = user.id
                        userList.add(userItem)
                    }
                }
            } else {
                Log.d("USER", it.exception!!.message.toString())
            }
        }.await()
        userList
    }

    suspend fun updateCurrentLocation(location: Location, cluster: String) = coroutineScope {
        utils
            .getAllUsers()
            .document(utils.getCurrentUser()!!.uid)
            .update(
                "location",
                LocationLatLng(
                    location.latitude,
                    location.longitude,
                    cluster
                )
            )
    }

    suspend fun updateCurrentBuddies(newBuddy: String) = coroutineScope {
        utils
            .getAllUsers().document(utils.getCurrentUser()!!.uid)
            .update(
                "buddies",
                FieldValue.arrayUnion(newBuddy)
            )
    }

    suspend fun updateCurrentMatches(newMatch: String) = coroutineScope {
        utils
            .getAllUsers().document(utils.getCurrentUser()!!.uid)
            .update(
                "seenAndMatch",
                FieldValue.arrayUnion(newMatch)
            )
    }

    suspend fun updateStringField(newValue: String, field: String) = coroutineScope {
        utils
            .getAllUsers().document(utils.getCurrentUser()!!.uid)
            .update(
                field,
                newValue
            )
    }

    suspend fun updateNumberField(newValue: Number, field: String) = coroutineScope {
        utils
            .getAllUsers().document(utils.getCurrentUser()!!.uid)
            .update(
                field,
                newValue
            )
    }
    suspend fun updateModules(newValue: MutableList<Module>) = coroutineScope {
        for(module in newValue) {
            utils
                .getAllUsers().document(utils.getCurrentUser()!!.uid)
                .update(
                    "modules",
                    FieldValue.arrayUnion(module)
                )
        }
    }

    suspend fun removeModule(module: Module, commID:String, userID: String) = coroutineScope {
        utils.getAllUsers().document(userID).update("modules", FieldValue.arrayRemove(module))
    }
    suspend fun removeCommFromUser(commID: String, userID: String) = coroutineScope {
        utils.getAllUsers().document(userID).update("communities", FieldValue.arrayRemove(commID))
    }
    suspend fun removeMemberFromComm(commID: String, userID: String) = coroutineScope {
        utils.getAllCommunities().document(commID).update("allMembersID", FieldValue.arrayRemove(userID))
    }


    suspend fun getCurrentUserAsDocument() = utils.getCurrentUserAsDocument()

    suspend fun updateLastBroadcasted(timestamp: Timestamp) {
        getCurrentUserAsDocument().update("lastBroadcasted", timestamp)
    }

    suspend fun getCurrentUserAsUser() = coroutineScope {
        var user = User()
        //utils.getCurrentUserAsDocument().get()
        utils.getCurrentUserAsDocument().get().addOnCompleteListener {
            if (it.isSuccessful) {
                user = it.result?.toObject(User::class.java)!!
            } else {
                Log.d("USER", it.exception!!.message.toString())
            }
        }.await()
        user
    }
    suspend fun getUser(userID: String) = coroutineScope {
        var user = User()
        utils.getUserAsDocument(userID).get().addOnCompleteListener {
            if (it.isSuccessful) {
                user = it.result?.toObject(User::class.java)!!
            } else {
                Log.d("USER", it.exception!!.message.toString())
            }
        }.await()
        user
    }

    suspend fun getUserAnd(userID: String, onComplete: (User) -> Unit) {
        utils.getUserAsDocument(userID).get().addOnCompleteListener {
            if (it.isSuccessful) {
                onComplete(it.result!!.toObject(User::class.java)!!)
            }
        }
    }

    suspend fun createChatWith(userID: String) = utils.createChatWith(userID)

    suspend fun sendMessage(messageID: String, messageText: String) = coroutineScope {
        getUserAnd(utils.getCurrentUser()!!.uid) {
            utils.getMessages(messageID).collection("messages")
                .add(
                    TextMessage(
                        messageText,
                        Timestamp.now(),
                        it.uid,
                        it.name,
                        getOtherUserInMessage(messageID, it.uid)
                    )
                )
        }
    }

    suspend fun makeInvisibleTo(userID: String, messageID: String) = coroutineScope {
        utils.getMessages(messageID).update("invisible", userID)
    }

    fun getMessageID(firstUser: User, secondUser: User) =
        if (firstUser.uid > secondUser.uid) "${firstUser.uid}_${secondUser.uid}" else "${secondUser.uid}_${firstUser.uid}"

    private fun getOtherUserInMessage(messageID: String, userID: String) =
        messageID.replace(userID, "").replace("_", "")


    suspend fun getRegistrationTokensAnd(onComplete: (tokens: MutableList<String>) -> Unit) {
        getUserAnd(utils.getCurrentUser()!!.uid) {
            onComplete(it.registrationToken)
        }
    }

    suspend fun setRegistrationTokens(tokens: MutableList<String>) {
        utils.getCurrentUserAsDocument().update("registrationToken", tokens)
    }
}


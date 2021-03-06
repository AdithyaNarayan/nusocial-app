package com.teamnusocial.nusocial.data.repository

import android.location.Location
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.storage.FirebaseStorage
import com.teamnusocial.nusocial.data.model.*
import com.teamnusocial.nusocial.utils.FirestoreUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SocialToolsRepository(val utils: FirestoreUtils) {
    suspend fun getAllCommunities() = coroutineScope {
        val commList = listOf<Community>().toMutableList()
        utils.getAllCommunities().get().addOnCompleteListener {
            if (it.isSuccessful) {
                it.result!!.documents.forEach { community ->
                    val communityAsObject = community.toObject(Community::class.java)!!
                    commList.add(communityAsObject)
                }
            } else {
                Log.d("COMM", it.exception!!.message.toString())
            }
        }.await()
        commList

    }
    suspend fun getCommunityAsObject(commID: String) = coroutineScope {
        var comm = Community()
        utils.getCommunity(commID).get().addOnSuccessListener {
            comm = it.toObject(Community::class.java)!!
        }.await()
        comm
    }
    suspend fun updateCommName(commID: String, newName: String) = coroutineScope {
        utils.getCommunity(commID).update("name", newName)
    }
    suspend fun updateCommAbout(commID: String, newAbout: String) = coroutineScope {
        utils.getCommunity(commID).update("about", newAbout)
    }
    suspend fun updateCommAdmin(commID: String, userID: String) = coroutineScope {
        utils.getCommunity(commID).update("allAdminsID", FieldValue.arrayUnion(userID))
    }
    suspend fun getPostsOfCommunity(commID: String) = coroutineScope {
        val postList = listOf<Post>().toMutableList()
        utils.getCommunity(commID)
            .collection("posts").get().addOnCompleteListener {
            if (it.isSuccessful) {
                it.result!!.documents.forEach { post ->
                    val postAsObject = post.toObject(Post::class.java)!!
                    postList.add(postAsObject)
                }
            } else {
                Log.d("COMM", it.exception!!.message.toString())
            }
        }.await()
        postList
    }
    suspend fun getNumberCommentsOfPost(postID: String, commID: String) = coroutineScope {
        var size = 0
        if(postID == "") return@coroutineScope 0
        utils.getComments(commID, postID).get().addOnSuccessListener {
            size = it.size()
        }.await()
        size
    }

    suspend fun addPost(value: Post, commID: String) = coroutineScope {
        var postID = ""
        utils
            .getAllPosts(commID)
            .add(value)
            .addOnSuccessListener { ref ->
                Log.d("POST_ADD", "At ${ref.id}")
                postID = ref.id
                ref.update("id", ref.id)
            }.await()
        postID
    }
    suspend fun editPost(value: Post, commID: String) = coroutineScope {
        utils
            .getAllPosts(commID)
            .document(value.id)
            .set(value)
            .addOnSuccessListener { ref ->
                Log.d("POST_EDIT", "SUCCESS")
            }
            .addOnFailureListener { e ->
                Log.d("POST_EDIT", "Error: ${e.message.toString()}")
            }.await()
    }

    suspend fun addComment(value: Comment, postID: String, commID: String) = coroutineScope {
        utils
            .getComments(commID, postID)
            .add(value)
            .addOnSuccessListener { ref ->
                Log.d("COMMENT_ADD", "At ${ref.id}")
                CoroutineScope(Dispatchers.IO).launch {
                    utils
                        .getComments(commID, postID)
                        .document(ref.id)
                        .update("id", ref.id)
                }
            }
            .addOnFailureListener { e ->
                Log.d("COMMENT_ADD", "Error: ${e.message.toString()}")
            }
    }
    suspend fun editComment(value: String,commentID:String, postID: String, commID: String) = coroutineScope {
        utils
            .getComments(commID, postID)
            .document(commentID)
            .update("textContent", value)
            .addOnSuccessListener { ref ->
                Log.d("COMMENT_EDIT", "SUCCESS")
            }
            .addOnFailureListener { e ->
                Log.d("COMMENT_EDIT", "Error: ${e.message.toString()}")
            }
    }
    suspend fun addCommunity(value: Community, userID: String) = coroutineScope {
        var refID = ""
        utils
            .getAllCommunities()
            .add(value)
            .addOnSuccessListener { ref ->
                refID = ref.id
                ref.update("id", refID)
                Log.d("COMMUNITY_ADD", "At ${ref.id}")
                CoroutineScope(Dispatchers.IO).launch {
                    utils
                        .getAllUsers()
                        .document(userID)
                        .update("communities", FieldValue.arrayUnion(refID))
                }
            }
            .addOnFailureListener { e ->
                Log.d("COMMUNITY_ADD", "Error: ${e.message.toString()}")
            }
        //Log.d("CHECK123", "ID is here ${refID}")
    }

    suspend fun addMemberToCommunity(userID: String, commID: String) = coroutineScope {
        utils
            .getAllCommunities()
            .document(commID)
            .update("allMembersID", FieldValue.arrayUnion(userID))
            .addOnSuccessListener {
                CoroutineScope(Dispatchers.IO).launch {
                    utils
                        .getAllUsers()
                        .document(userID)
                        .update("communities", FieldValue.arrayUnion(commID))
                }
            }
    }

    suspend fun likeUpdateAdd(commID: String, postID: String, userID: String) = coroutineScope {
        //Log.d("TEST_REAL", "here is ${postID} and ${userID}")
        utils.getAllPosts(commID).document(postID).update("userLikeList", FieldValue.arrayUnion(userID))
    }

    suspend fun likeUpdateRemove(commID: String, postID: String, userID: String) = coroutineScope {
        //Log.d("TEST_REAL", "here is ${postID} and ${userID}")
        utils.getAllPosts(commID).document(postID).update("userLikeList", FieldValue.arrayRemove(userID))
    }

    suspend fun commentUpdate(commID: String, postID: String, comment: Comment) = coroutineScope {
        var commentID = ""
        utils.getComments(commID, postID).add(comment).addOnSuccessListener {
            commentID = it.id
            Log.d("COMMENT_ADDED", "SUCCESS")
        }.addOnFailureListener {
            Log.d("COMMENT_ADDED", "FAILED")
        }.await()
        commentID
    }

    suspend fun deleteComment(commID: String, postID: String, commentID: String) = coroutineScope {
        utils.getComments(commID, postID).document(commentID).delete()
            .addOnSuccessListener {
                Log.d("DELETE_COMMENT", "SUCCESS")
                CoroutineScope(Dispatchers.IO).launch {
                    utils.getAllPosts(commID).document(postID).update("commentList", FieldValue.arrayRemove(commentID))
                }
            }
            .addOnFailureListener {
                Log.d("DELETE_COMMENT", "FAILED")
            }
    }
    fun deleteFilesOfPost(postID: String, images: MutableList<String>, files: MutableList<String>) {
        for(image in images) {
            FirebaseStorage.getInstance().getReference("post_images/${image}").delete()
        }
        for(file in files) {
            FirebaseStorage.getInstance().getReference("post_files/${postID}/${file}").delete()
        }
    }
    suspend fun deletePost(commID: String, postID: String, images: MutableList<String>, files: MutableList<String>) = coroutineScope {
        deleteFilesOfPost(postID,images, files)
        utils.getAllPosts(commID).document(postID).delete()
            .addOnSuccessListener {
                Log.d("DELETE_POST", "SUCCESS")
            }
            .addOnFailureListener {
                Log.d("DELETE_POST", "FAILED")
            }
    }
}
package com.teamnusocial.nusocial.data.repository

import android.location.Location
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.teamnusocial.nusocial.data.model.*
import com.teamnusocial.nusocial.utils.FirestoreUtils
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class SocialToolsRepository(val utils: FirestoreUtils) {
    suspend fun getAllCommunities() = coroutineScope {
        val commList = listOf<Community>().toMutableList()
        utils.getAllCommunities().get().addOnCompleteListener {
            if (it.isSuccessful) {
                it.result!!.documents.forEach { community ->
                    val communityAsObject = community.toObject(Community::class.java)!!
                    communityAsObject.id = community.id
                    commList.add(communityAsObject)
                }
            } else {
                Log.d("COMM", it.exception!!.message.toString())
            }
        }.await()
        commList

    }
    suspend fun getAllPostsOfCommunity(commID: String) = coroutineScope {
        val postList = listOf<Post>().toMutableList()
        utils.getAllPostsOfCommunity(commID).get().addOnCompleteListener {
            if(it.isSuccessful) {
                it.result!!.documents.forEach { post ->
                    val postAsObject = post.toObject(Post::class.java)!!
                    postAsObject.id = post.id
                    postList.add(postAsObject)
                }
            } else {
                Log.d("POST", it.exception!!.message.toString())
            }
        }.await()
        postList
    }
    suspend fun getAllCommentsOfPost(postID: String) = coroutineScope {
        val commentList = listOf<Comment>().toMutableList()
        utils.getAllCommentsOfPost(postID).get().addOnCompleteListener {
            if(it.isSuccessful) {
                it.result!!.documents.forEach { comment ->
                    val commentAsObject = comment.toObject(Comment::class.java)!!
                    commentAsObject.id = comment.id
                    commentList.add(commentAsObject)
                }
            } else {
                Log.d("COMMENT", it.exception!!.message.toString())
            }
        }.await()
        commentList
    }
    suspend fun addPost(value: Post) = coroutineScope {
        utils
            .getAllPosts()
            .add(value)
            .addOnSuccessListener { ref ->
                Log.d("POST_ADD", "At ${ref.id}")
            }
            .addOnFailureListener { e ->
                Log.d("POST_ADD", "Error: ${e.message.toString()}")
            }
    }
    suspend fun editPost(value: Post) = coroutineScope {
        utils
            .getAllPosts()
            .document(value.id)
            .set(value)
            .addOnSuccessListener { ref ->
                Log.d("POST_EDIT", "SUCCESS")
            }
            .addOnFailureListener { e ->
                Log.d("POST_EDIT", "Error: ${e.message.toString()}")
            }
    }
    suspend fun addComment(value: Comment) = coroutineScope {
        utils
            .getAllComments()
            .add(value)
            .addOnSuccessListener { ref ->
                Log.d("COMMENT_ADD", "At ${ref.id}")
            }
            .addOnFailureListener { e ->
                Log.d("COMMENT_ADD", "Error: ${e.message.toString()}")
            }
    }
    suspend fun editComment(value: Comment) = coroutineScope {
        utils
            .getAllComments()
            .document(value.id)
            .set(value)
            .addOnSuccessListener { ref ->
                Log.d("COMMENT_EDIT", "SUCCESS")
            }
            .addOnFailureListener { e ->
                Log.d("COMMENT_EDIT", "Error: ${e.message.toString()}")
            }
    }
    suspend fun addCommunity(value: Community) = coroutineScope {
        utils
            .getAllCommunities()
            .add(value)
            .addOnSuccessListener { ref ->
                Log.d("COMMUNITY_ADD", "At ${ref.id}")
            }
            .addOnFailureListener { e ->
                Log.d("COMMUNITY_ADD", "Error: ${e.message.toString()}")
            }
    }
}
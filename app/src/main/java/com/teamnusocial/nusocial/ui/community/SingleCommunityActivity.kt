package com.teamnusocial.nusocial.ui.community

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.Community
import com.teamnusocial.nusocial.data.model.Post
import com.teamnusocial.nusocial.data.model.TextMessage
import com.teamnusocial.nusocial.data.repository.SocialToolsRepository
import com.teamnusocial.nusocial.data.repository.UserRepository
import com.teamnusocial.nusocial.ui.messages.MessageHolder
import com.teamnusocial.nusocial.utils.FirestoreUtils
import kotlinx.android.synthetic.main.activity_single_community.*
import kotlinx.android.synthetic.main.post_create.*
import kotlinx.coroutines.*
import java.io.FileNotFoundException
import java.util.*


class SingleCommunityActivity : AppCompatActivity() {
    private lateinit var currCommData: Community
    var imageEncoded: String = ""
    var imagesEncodedList: MutableList<String> = mutableListOf()
    private lateinit var viewModel: CommunityViewModel
    var utils = SocialToolsRepository(FirestoreUtils())
    private lateinit var allPostAdapter: FirestoreRecyclerAdapter<Post, PostAdapter.PostHolder>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_community)
        currCommData = intent.getParcelableExtra("COMMUNITY_DATA")!!
        viewModel = ViewModelProvider(this).get(CommunityViewModel::class.java)
        viewModel.you = intent.getParcelableExtra("USER_DATA")!!
        val query =
            FirebaseFirestore.getInstance().collection("posts").document(currCommData.id).collection("posts").orderBy("timeStamp", Query.Direction.DESCENDING)
        allPostAdapter = PostAdapter(this,FirestoreRecyclerOptions.Builder<Post>()
            .setQuery(query, Post::class.java)
            .build(), viewModel.you, currCommData.id)
        updateUI()

    }
    fun updateUI() {
        Picasso.get().load(currCommData.coverImageUrl).into(community_cover_pic)
        //Log.d("TEST_PIC", currCommData.coverImageUrl)
        comm_name.text = currCommData.name
        setUpCreateNewPost()
        var layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        all_posts_single_comm.layoutManager = layoutManager
        all_posts_single_comm.adapter = allPostAdapter
    }
    fun setUpCreateNewPost() {
        Picasso.get().load(viewModel.you.profilePicturePath).into(profile_image)
        post_owner_name.text = viewModel.you.name

        val layoutManager_for_images = LinearLayoutManager(this)
        val layoutManager_for_videos = LinearLayoutManager(this)
        layoutManager_for_images.orientation = LinearLayoutManager.HORIZONTAL
        layoutManager_for_videos.orientation = LinearLayoutManager.HORIZONTAL

        images_slider.layoutManager = layoutManager_for_images
        video_slider.layoutManager = layoutManager_for_videos

        add_image_button.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 100)
            } else {
                addMultipleImages()
            }
        }

        add_videos_button.setOnClickListener {

        }

        publish_post_button.setOnClickListener {
            var postID = ""
            CoroutineScope(Dispatchers.IO).launch {
                postID = utils.addPost(Post("",currCommData.id,viewModel.you.uid, post_text_content_input.text.toString(),
                    mutableListOf(), mutableListOf(), Timestamp.now(), mutableListOf(),
                    mutableListOf()
                ), currCommData.id)
                if(!imageEncoded.equals("")) {
                    pushImagesToFirebase(imageEncoded.toUri(), 0, postID)
                } else if(imagesEncodedList.size > 0) {
                    for(index in 0 until imagesEncodedList.size) {
                        pushImagesToFirebase(imagesEncodedList[index].toUri(), index, postID)
                    }
                }
                viewModel.you = UserRepository(FirestoreUtils()).getCurrentUserAsUser()
                //viewModel.allSingleCommPost = utils.getAllPostsOfCommunity(currCommData.id)
                withContext(Dispatchers.Main) {
                    all_posts_single_comm.smoothScrollToPosition(0)
                    imageEncoded = ""
                    imagesEncodedList = mutableListOf()
                }
            }
            post_text_content_input.setText("")
            post_text_content_input.clearFocus()
            images_slider.adapter = PostImageAdapter(mutableListOf())
            //video_slider.adapter
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    )
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            100 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    addMultipleImages()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }

    fun addMultipleImages() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.type = "image/*"
        startActivityForResult(
            intent,
            1
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val images = mutableListOf<String>()
            if(data != null) {
                val clipData = data.clipData
                if(clipData != null) {
                    for (index in 0 until clipData.itemCount) {
                        val imgUri = clipData.getItemAt(index).uri
                        images.add(imgUri.toString())
                    }
                    imagesEncodedList = images
                } else {
                    val imgUri = data.data!!
                    imageEncoded = imgUri.toString()
                }
            } else {

            }
            if(imagesEncodedList.size > 0) {
                images_slider.adapter = PostImageAdapter(imagesEncodedList)
            } else if(!imageEncoded.equals("")) {
                images_slider.adapter = PostImageAdapter(mutableListOf(imageEncoded))
            } else {
                Log.d("ERROR", "NO IMAGES PICKED")
            }
        }
    }

    fun pushImagesToFirebase(imagePath: Uri, index: Int,postID: String) {
        val selectedImage = imagePath
        val storage = FirebaseStorage.getInstance()
        val fileName = postID + index.toString()
        Log.d("TEST_IMG_UPLOAD", "here is ${postID}")
        val filePath =
            storage.getReference("/post_images/${fileName}")
        filePath.putFile(selectedImage)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    filePath.downloadUrl.addOnCompleteListener { url ->
                        FirebaseFirestore.getInstance().collection("posts")
                            .document(postID)
                            .update("imageList", FieldValue.arrayUnion(url.result.toString()))
                    }
                }
            }
    }
    override fun onStart() {
        super.onStart()
        if (this::allPostAdapter.isInitialized) allPostAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        if (this::allPostAdapter.isInitialized) allPostAdapter.stopListening()
    }
}
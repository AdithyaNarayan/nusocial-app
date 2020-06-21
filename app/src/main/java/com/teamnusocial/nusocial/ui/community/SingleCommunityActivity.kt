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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.Community
import com.teamnusocial.nusocial.data.model.Post
import com.teamnusocial.nusocial.data.repository.UserRepository
import com.teamnusocial.nusocial.utils.FirestoreUtils
import kotlinx.android.synthetic.main.activity_single_community.*
import kotlinx.android.synthetic.main.post_create.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.util.*


class SingleCommunityActivity : AppCompatActivity() {
    private lateinit var currCommData: Community
    var imageEncoded: String = ""
    var imagesEncodedList: MutableList<String> = mutableListOf()
    private lateinit var viewModel: CommunityViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_community)
        currCommData = intent.getParcelableExtra("COMMUNITY_DATA")!!
        viewModel = ViewModelProvider(this).get(CommunityViewModel::class.java)
        lifecycleScope.launch {
            viewModel.you = UserRepository(FirestoreUtils()).getCurrentUserAsUser()
            updateUI()
        }
    }
    fun updateUI() {
        Picasso.get().load("https://i.pinimg.com/originals/28/35/be/2835be38b5274a4b20155999a7613542.jpg").into(community_cover_pic)
        setUpCreateNewPost()

        var allPostAdapter = PostAdapter(mutableListOf(Post(), Post(), Post(), Post()))
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

    fun pushImagesToFirebase(imagePath: Uri) {
        val selectedImage = imagePath
        val storage = FirebaseStorage.getInstance()
        val fileName = UUID.randomUUID()
        val filePath =
            storage.getReference("/post/images/${fileName}")
        filePath.putFile(selectedImage)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    filePath.downloadUrl.addOnCompleteListener { url ->
                        FirebaseFirestore.getInstance().collection("posts")
                            .document(viewModel.you.uid)
                            .update("imageList", FieldValue.arrayUnion(url.result.toString()))
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.you =
                                UserRepository(FirestoreUtils()).getCurrentUserAsUser()
                            withContext(Dispatchers.Main) { updateUI() }
                        }
                    }
                }
            }
    }
}
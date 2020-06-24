package com.teamnusocial.nusocial.ui.community

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.Post
import com.teamnusocial.nusocial.data.model.User
import com.teamnusocial.nusocial.data.repository.SocialToolsRepository
import com.teamnusocial.nusocial.utils.FirestoreUtils
import kotlinx.android.synthetic.main.activity_edit_post.*
import kotlinx.android.synthetic.main.post_create.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditPostActivity : AppCompatActivity() {
    private lateinit var postData: Post
    private lateinit var owner: User
    private lateinit var you: User
    private var imageEncoded: String = ""
    private var imagesEncodedList: MutableList<String> = mutableListOf<String>()
    private val storage = FirebaseStorage.getInstance()
    private val utils = SocialToolsRepository(FirestoreUtils())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_post)

        setSupportActionBar(toolbar_edit_post)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        postData = intent.getParcelableExtra("POST_DATA")
        owner = intent.getParcelableExtra("OWNER_DATA")
        you = intent.getParcelableExtra("USER_DATA")
        updateOldData()
    }
    fun updateOldData() {
        Picasso.get().load(owner.profilePicturePath).into(profile_image)
        post_owner_name.text = owner.name
        post_text_content_input.setText(postData.textContent)

        val layoutManager_for_new = LinearLayoutManager(this)
        val layoutManager_for_old = LinearLayoutManager(this)
        layoutManager_for_new.orientation = LinearLayoutManager.HORIZONTAL
        layoutManager_for_old.orientation = LinearLayoutManager.HORIZONTAL

        new_images_slider.layoutManager = layoutManager_for_new
        new_images_slider.adapter = PostImageEditAdapter(imagesEncodedList, true, "--blank--")

        old_images_slider.layoutManager = layoutManager_for_old
        old_images_slider.adapter = PostImageEditAdapter(postData.imageList, false, postData.id)


        add_image_button.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 100)
            } else {
                addMultipleImages()
            }
        }


        publish_post_button.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val newPost = Post(postData.id,postData.communityID,owner.uid, post_text_content_input.text.toString(),
                    postData.imageList, mutableListOf(), Timestamp.now(), postData.userLikeList,postData.numComment
                )
                utils.editPost(newPost, postData.communityID)
                if(!imageEncoded.equals("")) {
                    pushImagesToFirebase(imageEncoded.toUri(), 0, postData.id, postData.communityID)
                } else if(imagesEncodedList.size > 0) {
                    for(index in 0 until imagesEncodedList.size) {
                        pushImagesToFirebase(imagesEncodedList[index].toUri(), index, postData.id, postData.communityID)
                    }
                }
                //viewModel.allSingleCommPost = utils.getAllPostsOfCommunity(currCommData.id)
                withContext(Dispatchers.Main) {
                    setResult(
                        Activity.RESULT_OK,
                        Intent().putExtra("POST_DATA", newPost).putExtra("OWNER_DATA", owner).putExtra("USER_DATA", you)
                    )
                    finish()
                }
            }

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
                    imagesEncodedList.addAll(images)
                } else {
                    val imgUri = data.data!!
                    imageEncoded = imgUri.toString()
                }
            } else {

            }
            if(imagesEncodedList.size > 0) {
                new_images_slider.adapter = PostImageEditAdapter(imagesEncodedList, true, "--blank--")
            } else if(!imageEncoded.equals("")) {
                imagesEncodedList.add(imageEncoded)
                new_images_slider.adapter = PostImageEditAdapter(imagesEncodedList, true, "--blank--")
            } else {
                Log.d("ERROR", "NO IMAGES PICKED")
            }
        }
    }

    fun pushImagesToFirebase(imagePath: Uri, index: Int, postID: String, commID: String) {
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
                        FirebaseFirestore.getInstance().collection("communities").document(commID)
                            .collection("posts")
                            .document(postID)
                            .update("imageList", FieldValue.arrayUnion(url.result.toString()))
                        Log.d("TEST_IMG_HERE", "Here is ${url.result.toString()}")
                    }
                } else {
                    Log.d("TEST_IMG_HERE", "HOW THE FUCK")
                }
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
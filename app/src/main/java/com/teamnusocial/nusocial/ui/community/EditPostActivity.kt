package com.teamnusocial.nusocial.ui.community

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
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
import com.teamnusocial.nusocial.data.model.PostType
import com.teamnusocial.nusocial.data.model.User
import com.teamnusocial.nusocial.data.repository.SocialToolsRepository
import com.teamnusocial.nusocial.utils.FirestoreUtils
import kotlinx.android.synthetic.main.activity_edit_post.*
import kotlinx.android.synthetic.main.post_edit.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditPostActivity : AppCompatActivity() {
    companion object {
    private const val PICK_FILE_REQUEST = 72
    }


    private lateinit var postData: Post
    private lateinit var owner: User
    private lateinit var you: User
    private var imageEncoded = ""
    private var imagesEncodedList: MutableList<String> = mutableListOf<String>()
    private var filesList: MutableList<Uri> = mutableListOf()
    private var file: Uri? = null
    private val storage = FirebaseStorage.getInstance()
    var fileUriList = mutableListOf<Uri>()
    private val utils = SocialToolsRepository(FirestoreUtils())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_post)

        //setSupportActionBar(toolbar_edit_post)
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)
        back_button.setOnClickListener {
            finish()
        }
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
        val layoutManager_for_new_files = LinearLayoutManager(this)
        val layoutManager_for_old_files = LinearLayoutManager(this)
        layoutManager_for_new.orientation = LinearLayoutManager.HORIZONTAL
        layoutManager_for_old.orientation = LinearLayoutManager.HORIZONTAL
        layoutManager_for_new_files.orientation = LinearLayoutManager.HORIZONTAL
        layoutManager_for_old_files.orientation = LinearLayoutManager.HORIZONTAL

        new_images_slider.layoutManager = layoutManager_for_new
        new_images_slider.adapter = PostImageEditAdapter(imagesEncodedList, true, "--blank--")

        old_images_slider.layoutManager = layoutManager_for_old
        old_images_slider.adapter = PostImageEditAdapter(postData.imageList, false, postData.id)

        new_files_slider.layoutManager = layoutManager_for_new_files
        new_files_slider.adapter = PostFileEditAdapter(filesList, true, true,"--blank--", this)

        old_files_slider.layoutManager = layoutManager_for_old_files
        fileUriList = postData.videoList.map{item -> item.toUri()}.toMutableList()
        old_files_slider.adapter = PostFileEditAdapter(fileUriList, false, true, postData.id, this)



        add_image_button.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 100)
            } else {
                addMultipleImages()
            }
        }
        add_files_button.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 100)
            } else {
                addMultipleFiles()
            }
        }


        publish_post_button.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val newPost = Post(postData.id,postData.communityID,owner.uid, post_text_content_input.text.toString(),
                    postData.imageList, fileUriList.map{item -> item.toString()}.toMutableList(), Timestamp.now(), postData.userLikeList, postData.parentCommName, PostType.Question)
                utils.editPost(newPost, postData.communityID)
                if(!imageEncoded.equals("")) {
                    pushImagesToFirebase(imageEncoded.toUri(), 0, postData.id, postData.communityID)
                } else if(imagesEncodedList.size > 0) {
                    for(index in 0 until imagesEncodedList.size) {
                        pushImagesToFirebase(imagesEncodedList[index].toUri(), index, postData.id, postData.communityID)
                    }
                }
                if(file != null) {
                    pushFilesToFirebase(file!!, postData.id, postData.communityID)
                } else if(filesList.size > 0) {
                    for(index in 0 until filesList.size) {
                        pushFilesToFirebase(filesList[index], postData.id, postData.communityID)
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
    fun addMultipleFiles() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(
            Intent.createChooser(intent, "Select File"),
            PICK_FILE_REQUEST
        )
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
        } else  if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val files = mutableListOf<Uri>()
            if(data != null) {
                val clipData = data.clipData
                if(clipData != null) {
                    for (index in 0 until clipData.itemCount) {
                        val fileUri = clipData.getItemAt(index).uri
                        files.add(fileUri)
                    }
                    filesList = files
                } else {
                    val fileUri = data.data!!
                    file = fileUri
                }
            } else {
                Log.d("FILE_UPLOAD", "data is null")
            }
            if(filesList.size > 0) {
                new_files_slider.adapter = PostFileEditAdapter(filesList, true, true,"--blank--", this)
            } else if(file != null) {
                new_files_slider.adapter = PostFileEditAdapter(mutableListOf(file!!), true, true,"--blank--", this)
            } else {
                Log.d("ERROR", "NO FILES PICKED")
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
    fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.getScheme().equals("content")) {
            val cursor: Cursor = getContentResolver().query(uri, null, null, null, null)!!
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor.close()
            }
        }
        if (result == null) {
            result = uri.getPath()
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result!!.substring(cut + 1)
            }
        }
        return result
    }
    fun pushFilesToFirebase(filePath: Uri, postID: String, commID: String) {
        val selectedFile = filePath
        val storage = FirebaseStorage.getInstance()
        val filePath =
            storage.getReference("/post_files/${postID}/" + getFileName(filePath))
        filePath.putFile(selectedFile)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    filePath.downloadUrl.addOnCompleteListener { url ->
                        FirebaseFirestore.getInstance().collection("communities").document(commID).collection("posts")
                            .document(postID)
                            .update("videoList", FieldValue.arrayUnion(url.result.toString()))
                    }
                }
            }
    }
}
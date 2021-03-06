package com.teamnusocial.nusocial.ui.community

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.teamnusocial.nusocial.HomeActivity
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.Community
import com.teamnusocial.nusocial.data.model.Post
import com.teamnusocial.nusocial.data.model.PostType
import com.teamnusocial.nusocial.data.repository.SocialToolsRepository
import com.teamnusocial.nusocial.data.repository.UserRepository
import com.teamnusocial.nusocial.ui.you.CustomSpinner
import com.teamnusocial.nusocial.utils.FirestoreUtils
import com.teamnusocial.nusocial.utils.KeyboardToggleListener
import kotlinx.android.synthetic.main.activity_single_community.*
import kotlinx.android.synthetic.main.post_create.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SingleCommunityActivity : AppCompatActivity() {
    companion object {
        private const val PICK_FILE_REQUEST = 72
    }
    private lateinit var currCommData: Community
    //private lateinit var commJoinTime: Timestamp
    var imageEncoded: String = ""
    var imagesEncodedList: MutableList<String> = mutableListOf()
    private lateinit var listOfActions: Array<String>
    private lateinit var viewModel: CommunityViewModel
    private val utils = SocialToolsRepository(FirestoreUtils())
    private val userRepo = UserRepository(FirestoreUtils())
    private lateinit var allPostAdapter: FirestoreRecyclerAdapter<Post, PostAdapter.PostHolder>
    private var typeOfNewPost = PostType.Question
    private var file: Uri? = null
    private var listOfFiles: MutableList<Uri> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_community)

        /**data fetching**/
        currCommData = intent.getParcelableExtra("COMMUNITY_DATA")!!
        //commJoinTime = intent.getParcelableExtra("COMM_TIME")!!
        viewModel = ViewModelProvider(this).get(CommunityViewModel::class.java)
        viewModel.you = intent.getParcelableExtra("USER_DATA")!!

        /**top bar**/
       /* val toolBar: Toolbar = findViewById(R.id.tool_single_community)
        toolBar.title = currCommData.name
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)*/

        val query =
            FirebaseFirestore.getInstance().collection("communities").document(currCommData.id).collection("posts").orderBy("timeStamp", Query.Direction.DESCENDING)
        allPostAdapter = PostAdapter(this,FirestoreRecyclerOptions.Builder<Post>()
            .setQuery(query, Post::class.java)
            .build(), viewModel.you, currCommData.id, currCommData.allAdminsID)
        updateUI()

    }
    fun updateUI() {
        Picasso.get().load(currCommData.coverImageUrl).fit().centerCrop().error(R.drawable.nus).into(community_cover_pic)
        community_cover_pic.setOnClickListener {
            val photoPicker = Intent(Intent.ACTION_PICK)
            photoPicker.type = "image/*"
            startActivityForResult(photoPicker, 0)
        }
        //Log.d("TEST_PIC", currCommData.coverImageUrl)
        comm_name.text = currCommData.name
        back_button_single_comm.setOnClickListener {
            finish()
        }
        setUpDropDown()
        setUpCreateNewPost()
        var layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        all_posts_single_comm.layoutManager = layoutManager
        all_posts_single_comm.isNestedScrollingEnabled = false
        //allPostAdapter.setHasStableIds(true)
        all_posts_single_comm.adapter = allPostAdapter
    }
    fun setUpDropDown() {
        val listOfActionsForMembers = arrayOf("Choose an action","About","Leave")
        val listOfActionsForAdmins = arrayOf("Choose an action","About","Leave","Advanced")
        if(currCommData.allAdminsID.contains(viewModel.you.uid)) {
            listOfActions = listOfActionsForAdmins
        } else {
            listOfActions = listOfActionsForMembers
        }
        val arrayAdapter = object: ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listOfActions) {
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                var res =  super.getDropDownView(position, convertView, parent) as TextView
                //res.setTextColor(resources.getColor(R.color.black, resources.newTheme()))
                if(position == 0) {
                    res.setBackgroundResource(R.drawable.centre_background_rect)
                }
                return res
            }
        }
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        single_comm_dropdown.adapter = arrayAdapter

        val rotateAnim = RotateAnimation(0F, 180F, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotateAnim.duration = 300
        rotateAnim.setInterpolator(LinearInterpolator())
        single_comm_dropdown.setSpinnerEventsListener(object :
            CustomSpinner.OnSpinnerEventsListener {
            override fun onSpinnerOpened() {
                single_comm_dropdown.isSelected = true
                single_comm_dropdown.startAnimation(rotateAnim)
            }

            override fun onSpinnerClosed() {
                single_comm_dropdown.isSelected = false
                single_comm_dropdown.startAnimation(rotateAnim)
            }
        })
        single_comm_dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(listOfActions.get(position)) {
                    "Leave" -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            userRepo.removeCommFromUser(currCommData.id, viewModel.you.uid)
                            userRepo.removeMemberFromComm(currCommData.id, viewModel.you.uid)
                            if(currCommData.allAdminsID.contains(viewModel.you.uid)) {
                                userRepo.removeAdminFromComm(currCommData.id, viewModel.you.uid)
                            }
                            if(!currCommData.module.moduleCode.equals("")) {
                                userRepo.removeModule(currCommData.module, currCommData.id, viewModel.you.uid)
                            }
                            val intent = Intent(this@SingleCommunityActivity, HomeActivity::class.java)
                            intent.putExtra("FROM_UPDATE", "update")
                            startActivity(intent)
                        }
                    }
                    "About" -> {
                        val builder: AlertDialog.Builder = AlertDialog.Builder(this@SingleCommunityActivity)
                        val dialog_view = this@SingleCommunityActivity.layoutInflater.inflate(R.layout.custom_text_view_dialog, null)
                        builder.setView(dialog_view)
                        dialog_view.findViewById<TextView>(R.id.about_comm_content).text = currCommData.about
                        val dialog = builder.create()
                        dialog.show()
                        single_comm_dropdown.setSelection(0)
                    }
                    "Advanced" -> {
                        single_comm_dropdown.setSelection(0)
                        val intent = Intent(this@SingleCommunityActivity, EditCommunityActivity::class.java)
                        //intent.putExtra("COMM_TIME", commJoinTime)
                        intent.putExtra("COMM_DATA", currCommData)
                        intent.putExtra("USER_DATA", viewModel.you)
                        startActivityForResult(intent, 2)

                    }
                    else -> {

                    }
                }
            }
        }
    }
    fun setUpTypeOptions() {
        val listOfType = arrayOf("Question","Sharing","Discussion","Poll","Announcement")
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOfType)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        post_type_dropdown.adapter = arrayAdapter

        post_type_dropdown.setSpinnerEventsListener(object :
            CustomSpinner.OnSpinnerEventsListener {
            override fun onSpinnerOpened() {
                post_type_dropdown.isSelected = true
            }

            override fun onSpinnerClosed() {
                post_type_dropdown.isSelected = false
            }
        })
        post_type_dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(listOfType.get(position)) {
                    "Question" -> typeOfNewPost = PostType.Question
                    "Sharing" -> typeOfNewPost = PostType.Sharing
                    "Discussion" -> typeOfNewPost = PostType.Discussion
                    "Poll" -> typeOfNewPost = PostType.Poll
                    else -> typeOfNewPost = PostType.Announcement
                }
            }

        }
    }
    fun setUpCreateNewPost() {
        Picasso.get().load(viewModel.you.profilePicturePath).into(profile_image)
        post_owner_name.text = viewModel.you.name
        setUpTypeOptions()
        addKeyboardToggleListener { shown ->
            if (!shown) {
                post_text_content_input.clearFocus()
            }
        }

        val layoutManager_for_images = LinearLayoutManager(this)
        val layoutManager_for_videos = LinearLayoutManager(this)
        layoutManager_for_images.orientation = LinearLayoutManager.HORIZONTAL
        layoutManager_for_videos.orientation = LinearLayoutManager.HORIZONTAL

        old_images_slider_create.layoutManager = layoutManager_for_images
        new_images_slider_create.layoutManager = layoutManager_for_videos

        add_image_button.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    100
                )
            } else {
                addMultipleImages()
            }
        }

        add_videos_button.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    100
                )
            } else {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "*/*"
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                startActivityForResult(
                    Intent.createChooser(intent, "Select File"),
                    SingleCommunityActivity.PICK_FILE_REQUEST
                )
            }
        }

        publish_post_button.setOnClickListener {
            try {
                var postID = ""
                CoroutineScope(Dispatchers.IO).launch {
                    postID = utils.addPost(
                        Post(
                            "",
                            currCommData.id,
                            viewModel.you.uid,
                            post_text_content_input.text.toString(),
                            mutableListOf(),
                            mutableListOf(),
                            Timestamp.now(),
                            mutableListOf(),
                            currCommData.name,
                            typeOfNewPost
                        ), currCommData.id
                    )

                    if (!imageEncoded.equals("")) {
                        pushImagesToFirebase(imageEncoded.toUri(), 0, postID, currCommData.id)
                    } else if (imagesEncodedList.size > 0) {
                        for (index in 0 until imagesEncodedList.size) {
                            pushImagesToFirebase(
                                imagesEncodedList[index].toUri(),
                                index,
                                postID,
                                currCommData.id
                            )
                        }
                    }
                    if (file != null) {
                        pushFilesToFirebase(file!!, postID, currCommData.id)
                    } else if (listOfFiles.size > 0) {
                        for (index in 0 until listOfFiles.size) {
                            pushFilesToFirebase(listOfFiles[index], postID, currCommData.id)
                        }
                    }
                    viewModel.you = UserRepository(FirestoreUtils()).getCurrentUserAsUser()
                    //viewModel.allSingleCommPost = utils.getAllPostsOfCommunity(currCommData.id)
                    withContext(Dispatchers.Main) {
                        all_posts_single_comm.smoothScrollToPosition(0)
                        imageEncoded = ""
                        imagesEncodedList.clear()
                        if (old_images_slider_create.adapter != null)
                            old_images_slider_create.adapter!!.notifyDataSetChanged()

                        file = null
                        listOfFiles.clear()
                        if (new_images_slider_create.adapter != null)
                            new_images_slider_create.adapter!!.notifyDataSetChanged()

                    }
                }
                post_text_content_input.setText("")
                post_text_content_input.clearFocus()
                //old_images_slider.adapter = PostImageEditAdapter(mutableListOf(), true, "--blank--")
                //video_slider.adapter
            } catch (e: Exception) {
                var toast = Toast.makeText(this, "Error trying to upload files", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, 0)
                toast.show()
                Log.d("ERROR_PUBLISH", e.message.toString())
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
                    imagesEncodedList = images
                } else {
                    val imgUri = data.data!!
                    imageEncoded = imgUri.toString()
                }
            } else {
                Log.d("IMAGE_UPLOAD", "data is null")
            }
            if(imagesEncodedList.size > 0) {
                old_images_slider_create.adapter = PostImageEditAdapter(imagesEncodedList, true, "--blank--")
            } else if(!imageEncoded.equals("")) {
                imagesEncodedList.add(imageEncoded)
                old_images_slider_create.adapter = PostImageEditAdapter(imagesEncodedList, true, "--blank--")
            } else {
                Log.d("ERROR", "NO IMAGES PICKED")
            }
        } else if(requestCode == 0 && resultCode == Activity.RESULT_OK) {
            val selectedImage: Uri = data!!.data!!
            val filePath =
                FirebaseStorage.getInstance().getReference("/community_cover_pic/${currCommData.id}")
            filePath.putFile(selectedImage)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        filePath.downloadUrl.addOnCompleteListener { url ->
                            FirebaseFirestore.getInstance().collection("communities")
                                .document(currCommData.id)
                                .update("coverImageUrl", url.result.toString())
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.you =
                                    UserRepository(FirestoreUtils()).getCurrentUserAsUser()
                                withContext(Dispatchers.Main) {
                                    currCommData.coverImageUrl = url.result.toString()
                                    updateUI()
                                }
                            }
                        }


                    }
                }
        } else if(requestCode == 2 && resultCode == Activity.RESULT_OK) {
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.Main) {
                    spin_kit.visibility = View.VISIBLE
                    loading_cover.visibility = View.VISIBLE
                }
                currCommData = utils.getCommunityAsObject(currCommData.id)
                viewModel.you = UserRepository(FirestoreUtils()).getCurrentUserAsUser()
                withContext(Dispatchers.Main) {
                    updateUI()
                    spin_kit.visibility = View.GONE
                    loading_cover.visibility = View.GONE
                }
            }
        } else  if (requestCode == SingleCommunityActivity.PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            var files = mutableListOf<Uri>()
            if(data != null) {
                val clipData = data.clipData
                if(clipData != null) {
                    for (index in 0 until clipData.itemCount) {
                        val fileUri = clipData.getItemAt(index).uri
                        files.add(fileUri)
                    }
                    listOfFiles = files
                } else {
                    val fileUri = data.data!!
                    file = fileUri
                }
            } else {
                Log.d("FILE_UPLOAD", "data is null")
            }
            if(listOfFiles.size > 0) {
                new_images_slider_create.adapter = PostFileEditAdapter(listOfFiles, true, false,"--blank--", this)
            } else if(file != null) {
                listOfFiles.add(file!!)
                new_images_slider_create.adapter = PostFileEditAdapter(listOfFiles, true, false,"--blank--", this)
            } else {
                Log.d("ERROR", "NO FILES PICKED")
            }
        }
    }

    fun pushImagesToFirebase(imagePath: Uri, index: Int,postID: String, commID: String) {
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
                        FirebaseFirestore.getInstance().collection("communities").document(commID).collection("posts")
                            .document(postID)
                            .update("imageList", FieldValue.arrayUnion(url.result.toString()))
                    }
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
    override fun onStart() {
        super.onStart()
        if (this::allPostAdapter.isInitialized) allPostAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        if (this::allPostAdapter.isInitialized) allPostAdapter.stopListening()
    }

    fun addKeyboardToggleListener(onKeyboardToggleAction: (shown: Boolean) -> Unit): KeyboardToggleListener? {
        val root = findViewById<View>(android.R.id.content)
        val listener = KeyboardToggleListener(root, onKeyboardToggleAction)
        return root?.viewTreeObserver?.run {
            addOnGlobalLayoutListener(listener)
            listener
        }
    }
}
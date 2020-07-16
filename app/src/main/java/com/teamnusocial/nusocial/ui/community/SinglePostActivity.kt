package com.teamnusocial.nusocial.ui.community

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MarginLayoutParamsCompat
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.Comment
import com.teamnusocial.nusocial.data.model.Post
import com.teamnusocial.nusocial.data.model.User
import com.teamnusocial.nusocial.data.repository.SocialToolsRepository
import com.teamnusocial.nusocial.ui.you.CustomSpinner
import com.teamnusocial.nusocial.utils.FirestoreUtils
import com.teamnusocial.nusocial.utils.getTimeAgo
import kotlinx.android.synthetic.main.activity_single_post.*
import kotlinx.android.synthetic.main.post.*
import kotlinx.coroutines.*

class SinglePostActivity : AppCompatActivity() {
    private val viewPool = RecyclerView.RecycledViewPool()
    private lateinit var currPost: Post
    private lateinit var viewModel: SinglePostViewModel
    private lateinit var allCommentsAdapter: FirestoreRecyclerAdapter<Comment, CommentAdapter.CommentHolder>
    private val utils = SocialToolsRepository(FirestoreUtils())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_post)
        /**set up view model**/
        viewModel = ViewModelProvider(this).get(SinglePostViewModel::class.java)
        currPost = intent.getParcelableExtra<Post>("POST_DATA")
        viewModel.you = intent.getParcelableExtra<User>("USER_DATA")

        back_button.setOnClickListener {
            finish()
        }
        val query =
            FirebaseFirestore.getInstance().collection("communities").document(currPost.communityID)
                .collection("posts")
                .document(currPost.id)
                .collection("comments")
                .orderBy("timeStamp", Query.Direction.DESCENDING)
        allCommentsAdapter = CommentAdapter(this, FirestoreRecyclerOptions.Builder<Comment>()
            .setQuery(query, Comment::class.java)
            .build(), viewModel.you, currPost)
        viewModel.owner = intent.getParcelableExtra("OWNER_DATA")
        setUpPostFunctions(currPost)

    }
    fun setUpPostFunctions(currPost: Post) {
        /**basic stat**/
        var like_number = currPost.userLikeList.size

        like_stat.text = "${currPost.userLikeList.size} like(s)"
        CoroutineScope(Dispatchers.IO).launch {
            val numComment = utils.getNumberCommentsOfPost(currPost.id, currPost.communityID)
            withContext(Dispatchers.Main) {
                comment_stat.text = "${numComment} comments(s)"
            }
        }

        val postImageAdapter =
            PostImageAdapter(currPost.imageList)
        val childLayoutManager = LinearLayoutManager(
            this, RecyclerView.HORIZONTAL, false)
        old_images_slider.apply {
            layoutManager = childLayoutManager
            adapter = postImageAdapter
            setRecycledViewPool(viewPool)
        }

        comment_button.setOnClickListener {
            input_comment.requestFocus()
            showKeyboard(input_comment, this)
        }

        like_button.isChecked = currPost.userLikeList.contains(viewModel.owner.uid)

        like_button.setOnCheckedChangeListener { button, isChecked ->
            if(isChecked) {
                like_stat.text = "${++like_number} like(s)"
                currPost.userLikeList.add(viewModel.you.uid)
                CoroutineScope(Dispatchers.IO).launch {
                    utils.likeUpdateAdd(currPost.communityID, currPost.id, viewModel.you.uid)
                }
            } else {
                like_stat.text = "${--like_number} like(s)"
                currPost.userLikeList.remove(viewModel.you.uid)
                CoroutineScope(Dispatchers.IO).launch {
                    utils.likeUpdateRemove(currPost.communityID, currPost.id, viewModel.you.uid)
                }
            }
        }

        date_time_post.text = getTimeAgo(currPost.timeStamp.seconds)
        text_content.text = currPost.textContent
        post_owner_name.text = viewModel.owner.name
            Picasso.get().load(viewModel.owner.profilePicturePath)
            .into(profile_image)

        var allOptions = arrayListOf<String>("Choose an action","Edit","Delete")
        val arrayAdapter = object: ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, allOptions) {
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                var res =  super.getDropDownView(position, convertView, parent) as TextView
                if(position == 0) {
                    res.setBackgroundResource(R.drawable.centre_background_rect)
                }
                return res
            }
        }
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        post_options.adapter = arrayAdapter

        val rotateAnim = RotateAnimation(0F, 180F, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotateAnim.duration = 200
        rotateAnim.setInterpolator(LinearInterpolator())
        post_options.setSpinnerEventsListener(object :
            CustomSpinner.OnSpinnerEventsListener {
            override fun onSpinnerOpened() {
                post_options.isSelected = true
                post_options.startAnimation(rotateAnim)
            }

            override fun onSpinnerClosed() {
                post_options.isSelected = false
                post_options.startAnimation(rotateAnim)
            }
        })
        post_options.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(allOptions.get(position)) {
                    "Edit" -> {
                        val intent = Intent(this@SinglePostActivity, EditPostActivity::class.java)
                        intent.putExtra("POST_DATA", currPost)
                        intent.putExtra("OWNER_DATA", viewModel.owner)
                        intent.putExtra("USER_DATA", viewModel.you)
                        startActivityForResult(intent, 0)
                        post_options.setSelection(0)
                    }
                    "Delete" -> {
                        var images = currPost.imageList.map { image -> extractImageName(image) }
                        var files = currPost.videoList.map { file -> extractFileName(file) }
                        CoroutineScope(Dispatchers.IO).launch {
                            utils.deletePost(currPost.communityID, currPost.id,images.toMutableList(), files.toMutableList())
                            finish()
                        }
                    }
                    else -> {

                    }
                }
            }

        }
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        comments.layoutManager = layoutManager
        comments.adapter = allCommentsAdapter

        send_comment_button.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                utils.addComment(Comment("",viewModel.you.uid,viewModel.you.name,viewModel.you.profilePicturePath, Timestamp.now(), currPost.id, currPost.communityID, input_comment.text.toString(),
                    mutableListOf()), currPost.id, currPost.communityID)
                withContext(Dispatchers.Main) {
                    input_comment.setText("")
                    input_comment.clearFocus()
                    comment_stat.text = "${allCommentsAdapter.itemCount + 1} comments(s)"
                    comments.smoothScrollToPosition(0)
                }
            }
        }
    }
    fun showKeyboard(mEtSearch: EditText, context: Context) {
        mEtSearch.requestFocus()
        val imm: InputMethodManager =
            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }
    fun extractImageName(imagePath: String): String {
        var startIndex = 0
        var endIndex = 0
        for(index in 0 until imagePath.length) {
            if(imagePath[index] == '%') startIndex = index + 3
            else if(imagePath[index] == '?') {
                endIndex = index
                break
            }
        }
        return imagePath.substring(startIndex, endIndex)
    }
    fun extractFileName(url: String): String {
        var start_index = 0
        var end_index = 0
        var count = 0
        for (i in 0 until url.length) {
            if (url[i] == '%') {
                if (count == 0) count++;
                else if (count == 1) start_index = i + 3;
            } else if (url[i] == '?') {
                end_index = i;
                break;
            }
        }
        return url.substring(start_index, end_index)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 0 && resultCode == Activity.RESULT_OK) {
            currPost = data!!.getParcelableExtra<Post>("POST_DATA")
            viewModel.you = data!!.getParcelableExtra<User>("USER_DATA")
            viewModel.owner = data!!.getParcelableExtra("OWNER_DATA")
            setUpPostFunctions(currPost)
        }

    }
    override fun onBackPressed() {
        super.onBackPressed()
        input_comment.clearFocus()
    }
    override fun onStart() {
        super.onStart()
        if (this::allCommentsAdapter.isInitialized) allCommentsAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        if (this::allCommentsAdapter.isInitialized) allCommentsAdapter.stopListening()
    }

}
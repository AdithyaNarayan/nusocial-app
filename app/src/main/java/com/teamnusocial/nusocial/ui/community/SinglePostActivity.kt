package com.teamnusocial.nusocial.ui.community

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.squareup.picasso.Picasso
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.Comment
import com.teamnusocial.nusocial.data.model.Post
import com.teamnusocial.nusocial.ui.you.CustomSpinner
import com.teamnusocial.nusocial.utils.getTimeAgo
import kotlinx.android.synthetic.main.activity_single_post.*
import kotlinx.android.synthetic.main.post.*

class SinglePostActivity : AppCompatActivity() {
    private val viewPool = RecyclerView.RecycledViewPool()
    private lateinit var viewModel: SinglePostViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_post)
        /**top bar**/
        val toolBar: Toolbar = findViewById(R.id.tool_single_post)
        toolBar.title = "Post"
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        /**set up view model**/
        viewModel = ViewModelProvider(this).get(SinglePostViewModel::class.java)

        var currPost = intent.getParcelableExtra<Post>("POST_DATA")
        setUpPostFunctions(currPost)

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
    fun setUpPostFunctions(currPost: Post) {
        val postImageAdapter =
            PostImageAdapter(currPost.imageList)
        val snapHelper = LinearSnapHelper() //make the swiping snappy
        snapHelper.attachToRecyclerView(images_slider)
        val childLayoutManager = LinearLayoutManager(
            this, RecyclerView.HORIZONTAL, false)
        images_slider.apply {
            layoutManager = childLayoutManager
            adapter = postImageAdapter
            setRecycledViewPool(viewPool)
        }

        comment_button.setOnClickListener {
            input_comment.requestFocus()
            showKeyboard(input_comment, this)
        }

        date_time_post.text = getTimeAgo(currPost.timeStamp.seconds)
        text_content.text = currPost.textContent
        post_owner_name.text = "HIeu"
        Picasso.get().load("https://scontent-xsp1-2.xx.fbcdn.net/v/t1.0-9/19224907_1825340341115797_8328796348323933218_n.jpg?_nc_cat=107&_nc_sid=110474&_nc_ohc=4xKOhMdh9KoAX9wdXst&_nc_ht=scontent-xsp1-2.xx&oh=2108a955ba2194e976b112d5e83b90ab&oe=5F0E3856")
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
                    res.setBackgroundResource(R.drawable.centre_background)
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
                        Log.d("TEST40", "this is edit")
                    }
                    "Delete" -> {
                        Log.d("TEST40", "this is delete")
                    }
                    else -> {

                    }
                }
            }

        }

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        comments.layoutManager = layoutManager
        comments.adapter = CommentAdapter(viewModel.allComments)
    }
    fun showKeyboard(mEtSearch: EditText, context: Context) {
        mEtSearch.requestFocus()
        val imm: InputMethodManager =
            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        input_comment.clearFocus()
    }

}
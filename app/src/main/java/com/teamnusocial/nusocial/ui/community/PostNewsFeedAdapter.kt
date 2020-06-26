package com.teamnusocial.nusocial.ui.community

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.Post
import com.teamnusocial.nusocial.data.model.User
import com.teamnusocial.nusocial.data.repository.SocialToolsRepository
import com.teamnusocial.nusocial.data.repository.UserRepository
import com.teamnusocial.nusocial.ui.you.CustomSpinner
import com.teamnusocial.nusocial.ui.you.OtherUserActivity
import com.teamnusocial.nusocial.utils.FirestoreUtils
import com.teamnusocial.nusocial.utils.getTimeAgo
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PostNewsFeedAdapter(val context: Context, val you: User, val allPosts: MutableList<Post>): RecyclerView.Adapter<PostNewsFeedAdapter.PostHolder>() {
    private val viewPool = RecyclerView.RecycledViewPool()
    private val utils = SocialToolsRepository(FirestoreUtils())
    class PostHolder(val layoutView: ConstraintLayout): RecyclerView.ViewHolder(layoutView)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.post, parent, false) as ConstraintLayout
        return PostHolder(
            layoutView
        )
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val owner = UserRepository(FirestoreUtils()).getUser(allPosts[position].ownerUid)
            withContext(Dispatchers.Main) {
                updatePostFunctions(owner, holder, position, allPosts[position], position)
            }
        }
    }
    fun updatePostFunctions(owner: User, holder: PostHolder, position: Int, model: Post, model_position: Int) {
        val context_ = context
        val dateTimePost = holder.layoutView.findViewById<TextView>(R.id.date_time_post)
        val textContent = holder.layoutView.findViewById<TextView>(R.id.text_content)
        val postOwnerName = holder.layoutView.findViewById<TextView>(R.id.post_owner_name)
        val avatar = holder.layoutView.findViewById<CircleImageView>(R.id.profile_image)
        val imageSlider = holder.layoutView.findViewById<RecyclerView>(R.id.old_images_slider)
        val dropdown_options = holder.layoutView.findViewById<CustomSpinner>(R.id.post_options)
        val comment_button = holder.layoutView.findViewById<Button>(R.id.comment_button)
        val like_button = holder.layoutView.findViewById<CheckBox>(R.id.like_button)
        //val share_button = holder.layoutView.findViewById<Button>(R.id.share_button)
        val like_stat = holder.layoutView.findViewById<TextView>(R.id.like_stat)
        val comment_stat = holder.layoutView.findViewById<TextView>(R.id.comment_stat)
        val currPost = model

        /**basic stat**/
        var numLike = currPost.userLikeList.size
        like_stat.text = "${numLike} like(s)"
        CoroutineScope(Dispatchers.IO).launch {
            val numComment = utils.getNumberCommentsOfPost(model.id, model.communityID)
            withContext(Dispatchers.Main) {
                comment_stat.text = "${numComment} comments(s)"
            }
        }

        postOwnerName.setOnClickListener {
            navigateToYouPage(model.ownerUid)
        }

        /**set up image slider**/
        val postImageAdapter =
            PostImageAdapter(currPost.imageList)
        val childLayoutManager = LinearLayoutManager(
            context_, RecyclerView.HORIZONTAL, false)
        imageSlider.apply {
            layoutManager = childLayoutManager
            adapter = postImageAdapter
            setRecycledViewPool(viewPool)
        }
        like_button.isChecked = model.userLikeList.contains(you.uid)
        //Log.d("TEST_ID", "here is ${you.uid} ans ${like_button.isChecked}")

        like_button.setOnCheckedChangeListener { button, isChecked ->
            if(isChecked) {
                like_stat.text = "${++numLike} like(s)"
                CoroutineScope(Dispatchers.IO).launch {
                    utils.likeUpdateAdd(model.communityID, model.id, you.uid)
                }
            } else {
                like_stat.text = "${--numLike} like(s)"
                CoroutineScope(Dispatchers.IO).launch {
                    utils.likeUpdateRemove(model.communityID, model.id, you.uid)
                }
            }
        }

        comment_button.setOnClickListener {
            val intent = Intent(context_, SinglePostActivity::class.java)
            intent.putExtra("POST_DATA", currPost)
            intent.putExtra("USER_DATA", you)
            intent.putExtra("OWNER_DATA", owner)
            context_.startActivity(intent)
        }


        dateTimePost.text = getTimeAgo(currPost.timeStamp.seconds)
        textContent.text = currPost.textContent

        /**owner handling**/
        postOwnerName.text = owner.name
        Picasso.get().load(owner.profilePicturePath)
            .into(avatar)
        avatar.setOnClickListener {
            navigateToYouPage(model.ownerUid)
        }

        /**set up dropdown**/
        var allOptions = arrayListOf<String>("Choose an action","Edit","Delete")
        val arrayAdapter = object: ArrayAdapter<String>(context_, android.R.layout.simple_spinner_item, allOptions) {
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
        dropdown_options.adapter = arrayAdapter

        /**animation for dropdown**/
        val rotateAnim = RotateAnimation(0F, 180F, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotateAnim.duration = 200
        rotateAnim.setInterpolator(LinearInterpolator())
        dropdown_options.setSpinnerEventsListener(object :
            CustomSpinner.OnSpinnerEventsListener {
            override fun onSpinnerOpened() {
                dropdown_options.isSelected = true
                dropdown_options.startAnimation(rotateAnim)
            }

            override fun onSpinnerClosed() {
                dropdown_options.isSelected = false
                dropdown_options.startAnimation(rotateAnim)
            }
        })
        dropdown_options.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(allOptions.get(position)) {
                    "Edit" -> {
                        val intent = Intent(context, EditPostActivity::class.java)
                        intent.putExtra("POST_DATA", model)
                        intent.putExtra("OWNER_DATA", owner)
                        intent.putExtra("USER_DATA", you)
                        context.startActivity(intent)
                        dropdown_options.setSelection(0)
                    }
                    "Delete" -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            utils.deletePost(model.communityID, model.id)
                        }
                        allPosts.removeAt(model_position)
                        notifyItemRemoved(model_position)
                        notifyItemRangeChanged(model_position, allPosts.size)
                    }
                    else -> {}
                }
            }

        }
    }
    fun navigateToYouPage(userID: String) {
        val intent = Intent(context, OtherUserActivity::class.java)
        intent.putExtra("USER_ID", userID)
        context.startActivity(intent)
    }

    override fun getItemCount() = allPosts.size
}
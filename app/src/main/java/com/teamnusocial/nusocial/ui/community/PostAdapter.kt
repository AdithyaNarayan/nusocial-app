package com.teamnusocial.nusocial.ui.community

import android.content.Context
import android.content.Intent
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
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
import soup.neumorphism.NeumorphCardView


class PostAdapter(val context: Context, options: FirestoreRecyclerOptions<Post>, val you: User, val commID: String, val listOfAdminsID: MutableList<String>): FirestoreRecyclerAdapter<Post, PostAdapter.PostHolder>(options) {
    private val viewPool = RecyclerView.RecycledViewPool()
    private val utils = SocialToolsRepository(FirestoreUtils())
    class PostHolder(val layoutView: NeumorphCardView): RecyclerView.ViewHolder(layoutView)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.post, parent, false) as NeumorphCardView
        return PostHolder(
            layoutView
        )
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int, model: Post) {
        CoroutineScope(Dispatchers.IO).launch {
            val owner = UserRepository(FirestoreUtils()).getUser(model.ownerUid)
            withContext(Dispatchers.Main) {
                updatePostFunctions(owner, holder, position, model)
            }
        }
    }
    fun updatePostFunctions(owner: User, holder: PostHolder, position: Int, model: Post) {
        val context_ = context
        val dateTimePost = holder.layoutView.findViewById<TextView>(R.id.date_time_post)
        val textContent = holder.layoutView.findViewById<TextView>(R.id.text_content)
        val postOwnerName = holder.layoutView.findViewById<TextView>(R.id.post_owner_name)
        val avatar = holder.layoutView.findViewById<CircleImageView>(R.id.profile_image)
        val imageSlider = holder.layoutView.findViewById<RecyclerView>(R.id.old_images_slider)
        val fileSlider = holder.layoutView.findViewById<RecyclerView>(R.id.new_images_slider)
        val dropdown_options = holder.layoutView.findViewById<CustomSpinner>(R.id.post_options)
        val comment_button = holder.layoutView.findViewById<Button>(R.id.comment_button)
        val like_button = holder.layoutView.findViewById<CheckBox>(R.id.like_button)
        val type_of_post = holder.layoutView.findViewById<TextView>(R.id.type_of_post)
        val like_stat = holder.layoutView.findViewById<TextView>(R.id.like_stat)
        val comment_stat = holder.layoutView.findViewById<TextView>(R.id.comment_stat)
        val currPost = model

        /**basic stat**/
        like_stat.text = "${currPost.userLikeList.size}"
        CoroutineScope(Dispatchers.IO).launch {
            val numComment = utils.getNumberCommentsOfPost(model.id, model.communityID)
            withContext(Dispatchers.Main) {
                comment_stat.text = "${numComment}"
            }
        }
        try {
            type_of_post.text = currPost.postType.name
        } catch (e: Exception) {
            type_of_post.text = "Question"
        }

        /**set up image and file slider**/
        val postImageAdapter =
            PostImageAdapter(currPost.imageList)
        val childLayoutManager = LinearLayoutManager(
            context_, RecyclerView.HORIZONTAL, false)
        imageSlider.apply {
            layoutManager = childLayoutManager
            adapter = postImageAdapter
            //setRecycledViewPool(viewPool)
        }
        val postFileAdapter =
            PostFileEditAdapter(currPost.videoList.map{item -> item.toUri()}.toMutableList(),false,false, currPost.id,context_)
        val childLayoutManager_forFiles = LinearLayoutManager(
            context_, RecyclerView.HORIZONTAL, false)
        fileSlider.apply {
            layoutManager = childLayoutManager_forFiles
            adapter = postFileAdapter
            //setRecycledViewPool(viewPool)
        }

        like_button.isChecked = model.userLikeList.contains(you.uid)
        //Log.d("TEST_ID", "here is ${you.uid} ans ${like_button.isChecked}")

        like_button.setOnCheckedChangeListener { button, isChecked ->
            if(isChecked) {
                CoroutineScope(Dispatchers.IO).launch {
                    utils.likeUpdateAdd(commID, model.id, you.uid)
                }
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    utils.likeUpdateRemove(commID, model.id, you.uid)
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
        postOwnerName.movementMethod = ScrollingMovementMethod()
        Picasso.get().load(owner.profilePicturePath)
            .into(avatar)
        avatar.setOnClickListener {
            navigateToYouPage(currPost.ownerUid)
        }
        postOwnerName.setOnClickListener {
            navigateToYouPage(currPost.ownerUid)
        }


        /**set up dropdown**/
        var allOptions = arrayListOf<String>("Choose an action","Edit","Delete")
        if(you.uid.equals(currPost.ownerUid) || listOfAdminsID.contains(you.uid)) {
            val arrayAdapter = object :
                ArrayAdapter<String>(context_, android.R.layout.simple_spinner_item, allOptions) {
                override fun getDropDownView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
                ): View {
                    var res = super.getDropDownView(position, convertView, parent) as TextView
                    if (position == 0) {
                        res.setBackgroundResource(R.drawable.centre_background_rect)
                    }
                    return res
                }
            }
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dropdown_options.adapter = arrayAdapter

            /**animation for dropdown**/
            val rotateAnim = RotateAnimation(
                0F,
                180F,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
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

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    when (allOptions.get(position)) {
                        "Edit" -> {
                            val intent = Intent(context, EditPostActivity::class.java)
                            intent.putExtra("POST_DATA", model)
                            intent.putExtra("OWNER_DATA", owner)
                            intent.putExtra("USER_DATA", you)
                            context.startActivity(intent)
                            dropdown_options.setSelection(0)
                        }
                        "Delete" -> {
                            var images = model.imageList.map { image -> extractImageName(image) }
                            var files = model.videoList.map { file -> extractFileName(file) }
                            CoroutineScope(Dispatchers.IO).launch {
                                utils.deletePost(commID, model.id, images.toMutableList(), files.toMutableList())
                            }
                        }
                        else -> {
                        }
                    }
                }

            }
        } else dropdown_options.visibility = View.GONE
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
    fun navigateToYouPage(userID: String) {
        val intent = Intent(context, OtherUserActivity::class.java)
        intent.putExtra("USER_ID", userID)
        context.startActivity(intent)
    }
}
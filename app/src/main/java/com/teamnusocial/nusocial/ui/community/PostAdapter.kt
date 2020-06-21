package com.teamnusocial.nusocial.ui.community

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
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
import com.teamnusocial.nusocial.ui.you.CustomSpinner
import com.teamnusocial.nusocial.utils.getTimeAgo
import de.hdodenhof.circleimageview.CircleImageView


class PostAdapter(val allPosts: List<Post>): RecyclerView.Adapter<PostAdapter.PostHolder>() {
    private val viewPool = RecyclerView.RecycledViewPool()
    public var totalHeight: Int = 0
    class PostHolder(val layoutView: ConstraintLayout): RecyclerView.ViewHolder(layoutView)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.post, parent, false) as ConstraintLayout
        return PostHolder(
            layoutView
        )
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        var context_ = holder.layoutView.context
        var dateTimePost = holder.layoutView.findViewById<TextView>(R.id.date_time_post)
        var textContent = holder.layoutView.findViewById<TextView>(R.id.text_content)
        var postOwnerName = holder.layoutView.findViewById<TextView>(R.id.post_owner_name)
        var avatar = holder.layoutView.findViewById<CircleImageView>(R.id.profile_image)
        var imageSlider = holder.layoutView.findViewById<RecyclerView>(R.id.images_slider)
        var dropdown_options = holder.layoutView.findViewById<CustomSpinner>(R.id.post_options)
        var comment_button = holder.layoutView.findViewById<Button>(R.id.comment_button)
        var like_button = holder.layoutView.findViewById<CheckBox>(R.id.like_button)
        var share_button = holder.layoutView.findViewById<Button>(R.id.share_button)
        var like_stat = holder.layoutView.findViewById<TextView>(R.id.like_stat)
        var comment_stat = holder.layoutView.findViewById<TextView>(R.id.comment_stat)
        var share_stat = holder.layoutView.findViewById<TextView>(R.id.share_stat)


        val currPost = allPosts[position]

        /**basic stat**/
        like_stat.text = "${currPost.numLike} like(s)"
        comment_stat.text = "${currPost.numComment} comments(s)"


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

        comment_button.setOnClickListener {
            val intent = Intent(context_, SinglePostActivity::class.java)
            intent.putExtra("POST_DATA", currPost)
            context_.startActivity(intent)
        }
        dateTimePost.text = getTimeAgo(currPost.timeStamp.seconds)
        textContent.text = currPost.textContent
        postOwnerName.text = "HIeu"
        Picasso.get().load("https://scontent-xsp1-2.xx.fbcdn.net/v/t1.0-9/19224907_1825340341115797_8328796348323933218_n.jpg?_nc_cat=107&_nc_sid=110474&_nc_ohc=4xKOhMdh9KoAX9wdXst&_nc_ht=scontent-xsp1-2.xx&oh=2108a955ba2194e976b112d5e83b90ab&oe=5F0E3856")
            .into(avatar)

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
                        Log.d("TEST40", "this is edit")
                    }
                    "Delete" -> {
                        Log.d("TEST40", "this is delete")
                    }
                    else -> {}
                }
            }

        }

        like_button.setOnClickListener {
            like_stat.text = "${currPost.numLike} like(s)"
        }

        share_button.setOnClickListener {

        }
        totalHeight += holder.layoutView.measuredHeight + 10
        Log.d("TEST100","heigh in adapter ${totalHeight}")
    }



    override fun getItemCount() = allPosts.size

}
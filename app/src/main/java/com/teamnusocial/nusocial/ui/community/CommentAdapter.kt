package com.teamnusocial.nusocial.ui.community

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.squareup.picasso.Picasso
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.Comment
import com.teamnusocial.nusocial.data.model.Post
import com.teamnusocial.nusocial.data.model.User
import com.teamnusocial.nusocial.data.repository.SocialToolsRepository
import com.teamnusocial.nusocial.ui.you.CustomSpinner
import com.teamnusocial.nusocial.ui.you.OtherUserActivity
import com.teamnusocial.nusocial.utils.FirestoreUtils
import com.teamnusocial.nusocial.utils.getTimeAgo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CommentAdapter(val context_: Context, options: FirestoreRecyclerOptions<Comment>, val you: User, val parentPost: Post) : FirestoreRecyclerAdapter<Comment, CommentAdapter.CommentHolder>(options) {
    private val utils = SocialToolsRepository(FirestoreUtils())
    class CommentHolder(val commentLayout: ConstraintLayout): RecyclerView.ViewHolder(commentLayout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentHolder {
        val commentLayout = LayoutInflater.from(parent.context).inflate(R.layout.comment_layout, parent, false) as ConstraintLayout
        return CommentHolder(commentLayout)
    }

    override fun onBindViewHolder(holder: CommentHolder, position: Int, model: Comment) {
        val comment_stat = (context_ as Activity).findViewById<TextView>(R.id.comment_stat)
        val avatar = holder.commentLayout.findViewById<ImageView>(R.id.profile_image_comment)
        val name = holder.commentLayout.findViewById<TextView>(R.id.comment_owner_name)
        var comment = model
        holder.commentLayout.findViewById<TextView>(R.id.text_content_comment).text = comment.textContent
        name.text = comment.ownerName
        name.setOnClickListener {
            navigateToYouPage(model.ownerUid)
        }

        holder.commentLayout.findViewById<TextView>(R.id.date_time_comment).text = getTimeAgo(comment.timeStamp.seconds)
        Picasso.get().load(model.ownerProfileImageUrl).into(avatar)
        avatar.setOnClickListener {
            navigateToYouPage(model.ownerUid)
        }


        var dropdown_options = holder.commentLayout.findViewById<CustomSpinner>(R.id.comment_options)
        /**set up dropdown**/
        var toast = Toast.makeText(context_, "You cannot carry out this action", Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, 0)
        var allOptions: ArrayList<String> = arrayListOf()
        if(you.uid.equals(model.ownerUid)) allOptions = arrayListOf<String>("Choose an action","Edit","Delete")
        else if(you.uid.equals(parentPost.ownerUid)) allOptions = arrayListOf<String>("Choose an action","Delete")
        if(allOptions.size == 0) dropdown_options.visibility = View.GONE
        else {
            val arrayAdapter = object: ArrayAdapter<String>(context_, android.R.layout.simple_spinner_item, allOptions) {
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
                            val builder: AlertDialog.Builder = AlertDialog.Builder(context_)
                            val dialog_view = context_.layoutInflater.inflate(R.layout.custom_dialog, null)
                            builder.setView(dialog_view)
                            dialog_view.findViewById<TextView>(R.id.dialog_title).text = "Edit comment"
                            val input = dialog_view.findViewById<EditText>(R.id.edit_comment_input)
                            val button_confirm = dialog_view.findViewById<Button>(R.id.confirm_edit_comment_button)
                            val button_cancel = dialog_view.findViewById<Button>(R.id.cancel_edit_comment_button)
                            val dialog = builder.create()
                            button_confirm.setOnClickListener {
                                CoroutineScope(Dispatchers.IO).launch {
                                    SocialToolsRepository(FirestoreUtils()).editComment(
                                        input.text.toString(),
                                        model.id,
                                        model.parentPostID,
                                        model.parentCommunityID
                                    )
                                    withContext(Dispatchers.Main) {
                                        dialog.dismiss()
                                    }
                                }
                            }
                            button_cancel.setOnClickListener {
                                dialog.dismiss()
                            }
                            dialog.show()
                            dropdown_options.setSelection(0)
                        }
                        "Delete" -> {
                            CoroutineScope(Dispatchers.IO).launch {
                                utils.deleteComment(
                                    parentPost.communityID,
                                    model.parentPostID,
                                    model.id
                                )
                                withContext(Dispatchers.Main) {
                                    comment_stat.text = "${itemCount - 1} comment(s)"
                                }
                            }
                        }
                        else -> {
                        }
                    }
                }

            }
        }

    }
    fun navigateToYouPage(userID: String) {
        val intent = Intent(context_, OtherUserActivity::class.java)
        intent.putExtra("USER_ID", userID)
        context_.startActivity(intent)
    }
}
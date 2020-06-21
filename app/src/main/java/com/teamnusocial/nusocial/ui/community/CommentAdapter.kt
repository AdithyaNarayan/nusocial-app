package com.teamnusocial.nusocial.ui.community

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.Comment
import com.teamnusocial.nusocial.ui.you.CustomSpinner
class CommentAdapter(var allComments: List<Comment>) : RecyclerView.Adapter<CommentAdapter.CommentHolder>() {
    class CommentHolder(val commentLayout: ConstraintLayout): RecyclerView.ViewHolder(commentLayout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentHolder {
        val commentLayout = LayoutInflater.from(parent.context).inflate(R.layout.comment_layout, parent, false) as ConstraintLayout
        return CommentHolder(commentLayout)
    }

    override fun onBindViewHolder(holder: CommentHolder, position: Int) {
        var comment = allComments[position]
        holder.commentLayout.findViewById<TextView>(R.id.text_content_comment).text = comment.textContent
        holder.commentLayout.findViewById<TextView>(R.id.comment_owner_name).text = comment.ownerName

        var dropdown_options = holder.commentLayout.findViewById<CustomSpinner>(R.id.comment_options)
        /**set up dropdown**/
        var allOptions = arrayListOf<String>("Choose an action","Edit","Delete")
        val arrayAdapter = object: ArrayAdapter<String>(holder.commentLayout.context, android.R.layout.simple_spinner_item, allOptions) {
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

                    }
                    "Delete" -> {

                    }
                    else -> {}
                }
            }

        }

    }

    override fun getItemCount() = allComments.size
}
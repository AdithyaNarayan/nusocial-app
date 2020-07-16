package com.teamnusocial.nusocial.ui.buddymatch

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.squareup.picasso.Picasso
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.User
import jp.wasabeef.picasso.transformations.MaskTransformation


class BuddyMatchViewHolder(val context: Context, itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    private val card: MaterialCardView = itemView.findViewById(R.id.buddymatchCard)
    private val imageView: ImageView = itemView.findViewById(R.id.buddymatchImage)
    private val nameView: TextView = itemView.findViewById(R.id.name_buddymatch)
    private val courseView: TextView = itemView.findViewById(R.id.major_buddymatch)
    private val yearView: TextView = itemView.findViewById(R.id.curr_year_buddymatch)
    private val modulesView: RecyclerView = itemView.findViewById(R.id.matched_modules_buddymatch)

    val mainView: ConstraintLayout = itemView.findViewById(R.id.buddymatchParent)

    fun bindUser(user: User, currUser: User) {
        Picasso.get()
            .load(user.profilePicturePath)
            .resize(296, 200)
            .transform(MaskTransformation(context, R.drawable.buddymatch_image_transformation))
            .into(imageView)

        nameView.text = truncate(user.name)
        courseView.text = user.courseOfStudy
        yearView.text =
            if (user.yearOfStudy < 5) "Year " + user.yearOfStudy.toString() else "Graduate"

        val matchedModules = user.modules.filter { module ->
            for (module_you in currUser.modules) {
                if (module.moduleCode == module_you.moduleCode) return@filter true
            }
            false
        }.toMutableList()

        modulesView.layoutManager = GridLayoutManager(context, 3)
        val modulesAdapter = ModulesAdapter(matchedModules, context)
        modulesAdapter.setClickListener(object : ModulesAdapter.ItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                val toast = Toast.makeText(
                    context,
                    "Go to your personal page to access the community",
                    Toast.LENGTH_SHORT
                )
                toast.setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, 0)
                toast.show()
            }
        })
        modulesView.adapter = modulesAdapter
    }

    private fun truncate(string: String) =
        if (string.length > 15) string.substring(0, 12) + "..."
        else string

}
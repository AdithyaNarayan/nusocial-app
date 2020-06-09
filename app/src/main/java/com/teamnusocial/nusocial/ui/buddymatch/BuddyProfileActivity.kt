package com.teamnusocial.nusocial.ui.buddymatch

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.User
import kotlinx.android.synthetic.main.activity_buddy_profile.*
class BuddyProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buddy_profile)

        /**top bar**/
        val toolBar: Toolbar = findViewById(R.id.toolbarMoreInfo)
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        /**fetch data**/
        val userImg = intent.getStringExtra("USER_IMG")
        val userName = intent.getStringExtra("USER_NAME")
        val userAbout = intent.getStringExtra("USER_ABOUT")
        /****/
        Picasso
            .get()
            .load(userImg)
            .centerCrop()
            .transform(MaskTransformation(this, R.drawable.more_info_img_frame))
            .fit()
            .into(more_info_image)

        about_info_more_info.movementMethod = ScrollingMovementMethod()
        about_info_more_info.text = userAbout
        name_more_info.text = userName

        var cardView = findViewById<CardView>(R.id.interest_container_more_info)
            var linearLayoutHorizontal = LinearLayout(this)
            linearLayoutHorizontal.orientation = LinearLayout.HORIZONTAL
            for (x in 0..3) {
                val module: View = this.layoutInflater.inflate(R.layout.matched_module_child,null)
                linearLayoutHorizontal.addView(module)
            }
        cardView.addView(linearLayoutHorizontal)
    }
}

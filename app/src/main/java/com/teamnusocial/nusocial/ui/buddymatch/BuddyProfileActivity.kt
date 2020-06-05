package com.teamnusocial.nusocial.ui.buddymatch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.squareup.picasso.Picasso
import com.teamnusocial.nusocial.R
import kotlinx.android.synthetic.main.activity_buddy_profile.*

class BuddyProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buddy_profile)
        val toolBar: Toolbar = findViewById(R.id.toolbarMoreInfo)
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        Picasso
            .get()
            .load("https://scontent-xsp1-1.xx.fbcdn.net/v/t1.0-9/s960x960/78794853_2434835663499592_1312362149607112704_o.jpg?_nc_cat=110&_nc_sid=85a577&_nc_ohc=RV9gAt1ZQpkAX9xi11o&_nc_ht=scontent-xsp1-1.xx&_nc_tp=7&oh=881e036b42df80a44db5b16e4f50286f&oe=5EFC8264")
            .centerCrop()
            .transform(MaskTransformation(this, R.drawable.more_info_img_frame))
            .fit()
            .into(more_info_image)
    }
}

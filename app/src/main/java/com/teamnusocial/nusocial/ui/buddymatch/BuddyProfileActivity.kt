package com.teamnusocial.nusocial.ui.buddymatch

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
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
        /****/

        /****/
        val currUser: User = intent.getParcelableExtra<User>("USER")
        //val currImgUrl: String = currUser.
        /****/
        Picasso
            .get()
            .load("https://scontent-xsp1-1.xx.fbcdn.net/v/t1.0-9/s960x960/78794853_2434835663499592_1312362149607112704_o.jpg?_nc_cat=110&_nc_sid=85a577&_nc_ohc=RV9gAt1ZQpkAX9xi11o&_nc_ht=scontent-xsp1-1.xx&_nc_tp=7&oh=881e036b42df80a44db5b16e4f50286f&oe=5EFC8264")
            .centerCrop()
            .transform(MaskTransformation(this, R.drawable.more_info_img_frame))
            .fit()
            .into(more_info_image)
        about_info_more_info.movementMethod = ScrollingMovementMethod()
        var cardView = findViewById<CardView>(R.id.interest_container_more_info)
          // val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
          // params.setMargins(0,0,10,0)
            var linearLayoutHorizontal = LinearLayout(this)
            linearLayoutHorizontal.orientation = LinearLayout.HORIZONTAL
            for (x in 0..3) {
                val module: View = this.layoutInflater.inflate(R.layout.matched_module_child,null)
           //     module.layoutParams = params
                linearLayoutHorizontal.addView(module)
            }
        about_info_more_info.text = currUser.about
        cardView.addView(linearLayoutHorizontal)
    }
}

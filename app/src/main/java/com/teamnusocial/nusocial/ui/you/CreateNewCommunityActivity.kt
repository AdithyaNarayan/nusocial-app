package com.teamnusocial.nusocial.ui.you

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.gpfreetech.neumorphism.Neumorphism
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.Community
import com.teamnusocial.nusocial.data.model.Module
import com.teamnusocial.nusocial.data.model.User
import com.teamnusocial.nusocial.data.repository.SocialToolsRepository
import com.teamnusocial.nusocial.data.repository.UserRepository
import com.teamnusocial.nusocial.utils.FirestoreUtils
import kotlinx.android.synthetic.main.activity_create_new_community.*
import kotlinx.android.synthetic.main.activity_update_info.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateNewCommunityActivity : AppCompatActivity() {
    private val utils = SocialToolsRepository(FirestoreUtils())
    private lateinit var you: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_new_community)

        you = intent.getParcelableExtra("USER_DATA")!!
        back_button_create.setOnClickListener {
            finish()
        }
        create_button.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                utils.addCommunity(Community("", change_name_comm.text.toString(),
                    mutableListOf<String>(you.uid), Module("","", listOf()),"https://socialveo.co/frontend/assets/images/default-user-cover.png", mutableListOf(you.uid), about_input_comm.text.toString(),false),you.uid)
                    .addOnSuccessListener {
                        CoroutineScope(Dispatchers.IO).launch {
                            you = UserRepository(FirestoreUtils()).getCurrentUserAsUser()
                            setResult(Activity.RESULT_OK, Intent().putExtra("USER_DATA", you))
                            finish()
                        }
                    }

            }
        }

    }
}
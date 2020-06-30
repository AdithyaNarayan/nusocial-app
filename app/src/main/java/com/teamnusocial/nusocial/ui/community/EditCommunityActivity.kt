package com.teamnusocial.nusocial.ui.community

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.Community
import com.teamnusocial.nusocial.data.model.User
import com.teamnusocial.nusocial.data.repository.SocialToolsRepository
import com.teamnusocial.nusocial.data.repository.UserRepository
import com.teamnusocial.nusocial.utils.FirestoreUtils
import kotlinx.android.synthetic.main.activity_edit_community.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditCommunityActivity : AppCompatActivity() {
    private lateinit var commData: Community
    private lateinit var you: User
    private var socialRepo = SocialToolsRepository(FirestoreUtils())
    private var userRepo = UserRepository(FirestoreUtils())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_community)

        /**toolbar setup**/
        val toolBar: Toolbar = findViewById(R.id.toolbar_edit_comm)
        toolBar.title = "Update Community Info"
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        /**fetch data**/
        commData = intent.getParcelableExtra("COMM_DATA")
        you = intent.getParcelableExtra("USER_DATA")

        populateCurrInfo()
    }
    fun populateCurrInfo() {
        change_name_comm.setText(commData.name)
        about_input_comm.setText(commData.about)

        var unprocessed_add_id = ""
        var unprocessed_remove_id = ""
        var unprocessed_add_admin_id = ""
        confirm_edit_button.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                if(!change_name_comm.text.toString().equals(commData.name)) {
                        socialRepo.updateCommName(commData.id, change_name_comm.text.toString())
                }
                if(about_input_comm.text.toString() != commData.about) {
                        socialRepo.updateCommAbout(commData.id, about_input_comm.text.toString())
                }
                unprocessed_add_id = add_user_input.text.toString()
                unprocessed_remove_id = remove_user_input.text.toString()
                unprocessed_add_admin_id = add_admins_input.text.toString()

                var list_of_id_to_add = unprocessed_add_id.split(",")
                var list_of_id_to_remove = unprocessed_remove_id.split(",")
                var list_of_admin_to_add = unprocessed_add_admin_id.split(",")
                for(id in list_of_id_to_add) {
                    val good_id = id.replace("\\s".toRegex(), "")
                        val all_user = userRepo.getUsers()
                        for(user in all_user) {
                            if(user.uid.equals(good_id)) {
                                socialRepo.addMemberToCommunity(good_id, commData.id)
                                break
                            }
                        }
                }
                for(id in list_of_id_to_remove) {
                    val good_id = id.replace("\\s".toRegex(), "")
                    if(commData.allMembersID.contains(good_id)) {
                        userRepo.removeMemberFromComm(commData.id, good_id)
                        userRepo.removeCommFromUser(commData.id, good_id)
                    }
                }
                for(id in list_of_admin_to_add) {
                    val good_id = id.replace("\\s".toRegex(), "")
                    val all_user = userRepo.getUsers()
                    for(user in all_user) {
                        if(user.uid.equals(good_id)) {
                            socialRepo.updateCommAdmin(commData.id, good_id)
                            socialRepo.addMemberToCommunity(commData.id, good_id)
                            break
                        }
                    }
                }
                //withContext(Dispatchers.Main) {
                    setResult(Activity.RESULT_OK, Intent())
                    finish()

            }
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
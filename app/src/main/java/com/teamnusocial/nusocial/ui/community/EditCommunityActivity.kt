package com.teamnusocial.nusocial.ui.community

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.Community
import com.teamnusocial.nusocial.data.model.User
import com.teamnusocial.nusocial.data.repository.SocialToolsRepository
import com.teamnusocial.nusocial.data.repository.UserRepository
import com.teamnusocial.nusocial.ui.you.CommunityItemAdapter
import com.teamnusocial.nusocial.utils.FirestoreUtils
import com.toptoche.searchablespinnerlibrary.SearchableSpinner
import kotlinx.android.synthetic.main.activity_edit_community.*
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.fragment_you.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditCommunityActivity : AppCompatActivity() {
    private lateinit var commData: Community
    private lateinit var you: User
    private lateinit var allMembers: MutableList<User>
    private lateinit var allNonAdminMembers: MutableList<User>
    private lateinit var allUsers: MutableList<User>
    var usersToRemove: MutableList<User> = mutableListOf()
   // private lateinit var commJoinTime: Timestamp
    private var socialRepo = SocialToolsRepository(FirestoreUtils())
    private var userRepo = UserRepository(FirestoreUtils())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_community)

        /**toolbar setup**/
      /*  val toolBar: Toolbar = findViewById(R.id.toolbar_edit_comm)
        toolBar.title = "Update Community Info"
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)*/

        /**fetch data**/
        commData = intent.getParcelableExtra("COMM_DATA")
        you = intent.getParcelableExtra("USER_DATA")

        back_button.setOnClickListener {
            finish()
        }
        populateCurrInfo()
    }
    fun populateCurrInfo() {
        change_name_comm.setText(commData.name)
        about_input_comm.setText(commData.about)

        var unprocessed_add_id = ""
        var unprocessed_remove_id = ""
        var unprocessed_add_admin_id = ""
        confirm_edit_button.setOnClickListener {
          /*  CoroutineScope(Dispatchers.IO).launch {
                if(!change_name_comm.text.toString().equals(commData.name)) {
                        socialRepo.updateCommName(commData.id, change_name_comm.text.toString())
                }
                if(about_input_comm.text.toString() != commData.about) {
                        socialRepo.updateCommAbout(commData.id, about_input_comm.text.toString())
                }*/
              /*  unprocessed_add_id = add_user_input.text.toString()
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
                }*/
                //withContext(Dispatchers.Main) {
                   // setResult(Activity.RESULT_OK, Intent())
                    //finish()
           // }
            Log.d("TEST_REMOVE", usersToRemove.size.toString())
        }
        spin_kit.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            allUsers = userRepo.getUsers()
            allMembers = userRepo.getListOfUsers(commData.allMembersID.filter { user -> user != you.uid }.toMutableList())
            allNonAdminMembers = allMembers.filter { user -> !commData.allAdminsID.contains(user.uid) }.toMutableList()
            withContext(Dispatchers.Main) {
                setUpRemoveUserRecyclerViews()
                setUpSearchableSpinner()
                setUpSpinnerDropDown(add_user_input)
                spin_kit.visibility = View.GONE
            }
        }
    }
    fun setUpRemoveUserRecyclerViews() {
        var linearLayoutManager_for_remove = LinearLayoutManager(this)
        linearLayoutManager_for_remove.orientation = LinearLayoutManager.VERTICAL
        users_to_remove.layoutManager = linearLayoutManager_for_remove
        var remove_adapter = AddUserAdapter(this, allNonAdminMembers, usersToRemove)
        users_to_remove.adapter = remove_adapter
        /*remove_adapter.setClickListener(object : AddUserAdapter.ItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
               view!!.findViewById<ConstraintLayout>(R.id.user_item_wrapper).setBackgroundResource(R.drawable.background_home)
                view!!.findViewById<TextView>(R.id.user_item_name).setBackgroundColor(resources.getColor(R.color.whiteTextColor))
            }
        })*/
    }
    fun setUpSearchableSpinner() {
        add_user_input.setTitle("Select User");
        add_user_input.setPositiveButton("OK");
        add_admins_input.setTitle("Select User");
        add_admins_input.setPositiveButton("OK");

        var linearLayoutManager_for_members = LinearLayoutManager(this)
        linearLayoutManager_for_members.orientation = LinearLayoutManager.VERTICAL
        var linearLayoutManager_for_admins = LinearLayoutManager(this)
        linearLayoutManager_for_admins.orientation = LinearLayoutManager.VERTICAL

        users_to_add.layoutManager = linearLayoutManager_for_members
        admins_to_add.layoutManager = linearLayoutManager_for_admins

    }
    fun setUpSpinnerDropDown(dropdownSpinner: SearchableSpinner) {
        val listOfUsernames = allUsers.map { user -> user.name }
        Log.d("TEST_SPINNER", listOfUsernames.size.toString())
        val arrayAdapter = object: ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listOfUsernames) {
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                var res =  super.getDropDownView(position, convertView, parent) as TextView
                res.setTextColor(resources.getColor(R.color.black, resources.newTheme()))
                if(position == 0) {
                    res.setBackgroundResource(R.drawable.centre_background)
                }
                return res
            }
        }
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdownSpinner.adapter = arrayAdapter
    }
}
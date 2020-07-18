package com.teamnusocial.nusocial.ui.community

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.gpfreetech.neumorphism.Neumorphism
import com.gpfreetech.neumorphism.Neumorphism.CIRCULAR_SHAPE
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
import kotlinx.android.synthetic.main.activity_update_info.*
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
    var usersToAdd: MutableList<User> = mutableListOf()
    var adminsToAdd: MutableList<User> = mutableListOf()
    var usersToRemove: MutableList<User> = mutableListOf()
    var finalUsersToAdd: MutableList<User> = mutableListOf()
    var finalAdminsToAdd: MutableList<User> = mutableListOf()
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

        back_button_edit_comm.setOnClickListener {
            finish()
        }
        populateCurrInfo()
    }
    fun populateCurrInfo() {
        change_name_comm.setText(commData.name)
        about_input_comm.setText(commData.about)
        basic_info_update_button.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                if (!change_name_comm.text.toString().equals(commData.name)) {
                    socialRepo.updateCommName(commData.id, change_name_comm.text.toString())
                }
                if (about_input_comm.text.toString() != commData.about) {
                    socialRepo.updateCommAbout(commData.id, about_input_comm.text.toString())
                }
                withContext(Dispatchers.Main) {
                    setResult(Activity.RESULT_OK, Intent())
                    finish()
                }
            }
        }
        confirm_edit_button.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                for (user in usersToAdd) {
                    socialRepo.addMemberToCommunity(user.uid, commData.id)
                }
                for (user in usersToRemove) {
                    userRepo.removeMemberFromComm(commData.id, user.uid)
                    userRepo.removeCommFromUser(commData.id, user.uid)
                }
                for (user in adminsToAdd) {
                    socialRepo.updateCommAdmin(commData.id, user.uid)
                    socialRepo.addMemberToCommunity(commData.id, user.uid)
                }
                withContext(Dispatchers.Main) {
                    setResult(Activity.RESULT_OK, Intent())
                    finish()
                }
            }
        }
        spin_kit.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            allUsers = userRepo.getAllUsersToAdd(commData.allMembersID)
            allMembers = userRepo.getListOfUsers(commData.allMembersID.filter { user -> user != you.uid }.toMutableList())
            allNonAdminMembers = allMembers.filter { user -> !commData.allAdminsID.contains(user.uid) }.toMutableList()
            withContext(Dispatchers.Main) {
                setUpRemoveUserRecyclerViews()
                setUpSearchableSpinner()
                setUpSpinnerDropDown(add_user_input, "member")
                setUpSpinnerDropDown(add_admins_input, "admin")
                spin_kit.visibility = View.GONE
            }
        }
    }
    fun setUpRemoveUserRecyclerViews() {
        var linearLayoutManager_for_remove = LinearLayoutManager(this)
        linearLayoutManager_for_remove.orientation = LinearLayoutManager.VERTICAL
        users_to_remove.layoutManager = linearLayoutManager_for_remove
        var remove_adapter = AddUserAdapter(this, allNonAdminMembers)
        remove_adapter.setClickListener(object : AddUserAdapter.ItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                usersToRemove.add(allNonAdminMembers[position])
                allNonAdminMembers.removeAt(position)
                remove_adapter.notifyDataSetChanged()
            }
        })
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

        setUpAdapterForAdding(users_to_add, "member")
        setUpAdapterForAdding(admins_to_add, "admin")
    }
    fun setUpAdapterForAdding(recyclerView: RecyclerView, type: String) {
        var list = mutableListOf<User>()
        //var finalList = mutableListOf<User>()
        if(type.equals("member")) {
            list = usersToAdd
            //finalList = finalUsersToAdd
        } else {
            list = adminsToAdd
            //finalList = finalAdminsToAdd
        }
        var adapter = AddUserAdapter(this, list)
        adapter.setClickListener(object : AddUserAdapter.ItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                //finalList.add(list[position])
                allUsers.add(list[position])
                list.removeAt(position)
                adapter.notifyDataSetChanged()
            }
        })
        recyclerView.adapter = adapter
    }
    fun setUpSpinnerDropDown(dropdownSpinner: SearchableSpinner, type: String) {
        var listOfUsernames = mutableListOf<String>("Choose a user")
        listOfUsernames.addAll(allUsers.map { user -> user.name })
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
        dropdownSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(listOfUsernames.get(position)) {
                    "Choose a user" -> {}
                    else -> {
                        if(type.equals("member")) {
                            usersToAdd.add(allUsers[position - 1])
                            allUsers.removeAt(position-1)
                            users_to_add.adapter!!.notifyDataSetChanged()
                            arrayAdapter.remove(arrayAdapter.getItem(position))
                            dropdownSpinner.setSelection(0)
                        } else {
                            adminsToAdd.add(allUsers[position - 1])
                            allUsers.removeAt(position-1)
                            admins_to_add.adapter!!.notifyDataSetChanged()
                            arrayAdapter.remove(arrayAdapter.getItem(position))
                            dropdownSpinner.setSelection(0)
                        }
                    }
                }
            }

        }
    }
}
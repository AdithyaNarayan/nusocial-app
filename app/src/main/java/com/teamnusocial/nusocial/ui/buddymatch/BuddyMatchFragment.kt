package com.teamnusocial.nusocial.ui.buddymatch

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.Module
import com.teamnusocial.nusocial.data.model.User
import com.teamnusocial.nusocial.data.repository.UserRepository
import com.teamnusocial.nusocial.utils.FirestoreUtils
import kotlinx.android.synthetic.main.fragment_buddymatch.*
import kotlinx.android.synthetic.main.matched_module_child.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class BuddyMatchFragment : Fragment() {
    private lateinit var swipeView: RecyclerView
    private lateinit var buddyMatchViewModel: BuddyMatchViewModel
    private lateinit var snapHelper: LinearSnapHelper
    private lateinit var root: View
    private lateinit var linearLayoutVertical: LinearLayout
    private lateinit var you: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TEST10", "Init Here C")
        buddyMatchViewModel =
            ViewModelProvider(requireActivity()).get(BuddyMatchViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lifecycleScope.launch {
            buddyMatchViewModel.updateMatchedUsers(UserRepository(FirestoreUtils()).getUsers())
            buddyMatchViewModel.updateYou(UserRepository(FirestoreUtils()).getCurrentUserAsUser())
            populateMatchedUsers(buddyMatchViewModel.matchedUsers.value!!, inflater, container)

        }
        buddyMatchViewModel.you.observe(viewLifecycleOwner, Observer {
            you = buddyMatchViewModel.you.value!!
        })
        buddyMatchViewModel.matchedUsers.observe(viewLifecycleOwner, Observer {
            if (it.size > 0) {
                Log.d("TEST8", "Init Here")
            }
        })
        root = inflater.inflate(R.layout.fragment_buddymatch, container, false)
        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeView = match_swipe as RecyclerView
        snapHelper = LinearSnapHelper() //make the swiping snappy
        snapHelper.attachToRecyclerView(swipeView)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        swipeView.layoutManager = layoutManager
        val offset = resources.getDimension(R.dimen.offset_image)
        swipeView.addItemDecoration(OffsetHelper(offset.toInt()))
    }

    fun onScrollListener(
        currPos: Int,
        allUsers: MutableList<User>,
        inflater: LayoutInflater,
        container: ViewGroup?
    ) {
        val currUser = allUsers[currPos % allUsers.size]
        var cardView = matched_modules_buddymatch
        if (this::linearLayoutVertical.isInitialized) {
            cardView.removeAllViews()
        }
        linearLayoutVertical = LinearLayout(context)
        linearLayoutVertical.removeAllViewsInLayout()

        getMatchedModules(currPos, currUser, inflater, container, linearLayoutVertical)

        setUpButton(currUser, allUsers, currPos)
    }
    fun setUpButton(currUser: User, allUsers: MutableList<User>, currPos: Int) {
        if(allUsers.size == 0) {
            more_info_buddymatch.isEnabled = false
            match_button.isEnabled = false
        }
        else {
            more_info_buddymatch.isEnabled = true
            match_button.isEnabled = true
        }
        more_info_buddymatch.setOnClickListener {
            val intent = Intent(context, BuddyProfileActivity::class.java)
            intent.putExtra("USER_IMG", currUser.profilePicturePath)
            intent.putExtra("USER_NAME", currUser.name)
            intent.putExtra("USER_ABOUT", currUser.about)
            startActivity(intent)
        }
        match_button.setOnClickListener {
            allUsers.remove(currUser)
            buddyMatchViewModel.images.removeAt(currPos)
            if(currPos == allUsers.size) {
                swipeView.scrollToPosition(currPos - 1)
            }

            if(buddyMatchViewModel.images.size == 0) {
                buddyMatchViewModel.images.add("https://i7.pngflow.com/pngimage/455/105/png-anonymity-computer-icons-anonymous-user-anonymous-purple-violet-logo-smiley-clipart.png")
                name_buddymatch.text = "--Blank--"
                major_buddymatch.text = ""
                curr_year_buddymatch.text = ""
                matched_modules_buddymatch.removeAllViews()
            }
            (swipeView.adapter as Adapter).notifyDataSetChanged()
            var toast = Toast.makeText(context, "Buddy added !", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, 0)
            toast.show()
            matchBuddy(currUser)
        }
    }
    fun isMatched(module: Module): Boolean {
        for(module_you in you.modules) {
            if(module.moduleCode == module_you.moduleCode) return true
        }
        return false
    }
    fun getMatchedModules(currPos: Int, currUser: User, inflater: LayoutInflater, container: ViewGroup?, linearLayoutVertical: LinearLayout) {
        name_buddymatch.text = currUser.name
        major_buddymatch.text = currUser.courseOfStudy
        curr_year_buddymatch.text = "Year " + currUser.yearOfStudy.toString()
        var matchedModules = currUser.modules.filter { module -> isMatched(module) }.toList()
        val numberOfMatchedModules = matchedModules.size


        var numberOfRows = numberOfMatchedModules / 4
        numberOfRows += if (numberOfMatchedModules % 4 > 0) 1 else 0
        /****/

        /**matched modules**/
        var cardView = matched_modules_buddymatch
        var id: Int = 0

        linearLayoutVertical.orientation = LinearLayout.VERTICAL
        if (numberOfRows > 1 || numberOfMatchedModules == 4) {
            for (y in 1..numberOfRows) {
                var linearLayoutHorizontal = LinearLayout(context)
                linearLayoutHorizontal.orientation = LinearLayout.HORIZONTAL
                for (x in 0..3) {
                    val module: View =
                        inflater.inflate(R.layout.matched_module_child, container, false)
                    module.module_name.text = matchedModules[id++].moduleCode
                    linearLayoutHorizontal.addView(module)
                }
                linearLayoutVertical.addView(linearLayoutHorizontal)
            }
            var linearLayoutHorizontal = LinearLayout(context)
            linearLayoutHorizontal.orientation = LinearLayout.HORIZONTAL
            for (x in 1..numberOfMatchedModules % 4) {
                val module: View = inflater.inflate(R.layout.matched_module_child, container, false)
                module.module_name.text = matchedModules[id++].moduleCode
                linearLayoutHorizontal.addView(module)
            }
            linearLayoutVertical.addView(linearLayoutHorizontal)
        } else if (numberOfMatchedModules < 4) {
            var linearLayoutHorizontal = LinearLayout(context)
            linearLayoutHorizontal.orientation = LinearLayout.HORIZONTAL
            for (x in 1..numberOfMatchedModules) {
                val module: View = inflater.inflate(R.layout.matched_module_child, container, false)
                module.module_name.text = matchedModules[id++].moduleCode
                linearLayoutHorizontal.addView(module)
            }
            linearLayoutVertical.addView(linearLayoutHorizontal)
        }
        cardView.addView(linearLayoutVertical)
        /****/


    }
    fun populateMatchedUsers(
        allUsers: MutableList<User>,
        inflater: LayoutInflater,
        container: ViewGroup?
    ) {
        /**image swipe view**/
        val filteredAllUsers = MatchingUsers(allUsers, you).filterUsers()
        buddyMatchViewModel.images.clear()
        for (user in filteredAllUsers) {
            buddyMatchViewModel.images.add(user.profilePicturePath)
            Log.d("TEST6", user.name)
        }
        if(filteredAllUsers.size == 0) {
            buddyMatchViewModel.images.add("https://i7.pngflow.com/pngimage/455/105/png-anonymity-computer-icons-anonymous-user-anonymous-purple-violet-logo-smiley-clipart.png")
        }
        var adapter = Adapter(buddyMatchViewModel.images)
        swipeView.adapter = adapter
        /****/
        swipeView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val snapView = snapHelper.findSnapView(swipeView.layoutManager)
                if(filteredAllUsers.size > 0) {
                    onScrollListener(
                        swipeView.getChildAdapterPosition(snapView!!),
                        filteredAllUsers,
                        inflater,
                        container
                    )
                }
            }
        })
    }
    fun matchBuddy(currUser: User) {
        if(currUser.seenAndMatch.contains(you.uid)) {
            CoroutineScope(Dispatchers.IO).launch {
                UserRepository(FirestoreUtils()).updateCurrentBuddies(currUser.uid)
            }
        }
        else {
            CoroutineScope(Dispatchers.IO).launch {
                UserRepository(FirestoreUtils()).updateCurrentMatches(currUser.uid)
            }
        }
    }
}

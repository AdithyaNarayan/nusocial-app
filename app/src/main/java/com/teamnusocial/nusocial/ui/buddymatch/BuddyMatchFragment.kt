package com.teamnusocial.nusocial.ui.buddymatch

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.github.ybq.android.spinkit.SpinKitView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.Module
import com.teamnusocial.nusocial.data.model.User
import com.teamnusocial.nusocial.data.repository.UserRepository
import com.teamnusocial.nusocial.ui.community.SingleCommunityActivity
import com.teamnusocial.nusocial.ui.you.CommunityItemAdapter
import com.teamnusocial.nusocial.utils.FirestoreUtils
import kotlinx.android.synthetic.main.fragment_buddymatch.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BuddyMatchFragment : Fragment() {
    private lateinit var swipeView: RecyclerView
    private lateinit var buddyMatchViewModel: BuddyMatchViewModel
    private lateinit var snapHelper: LinearSnapHelper
    private lateinit var root: View
    private lateinit var you: User
    private lateinit var inflater: LayoutInflater
    private lateinit var container: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buddyMatchViewModel =
            ViewModelProvider(requireActivity()).get(BuddyMatchViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.inflater = inflater
        this.container = container!!
        root = inflater.inflate(R.layout.fragment_buddymatch, container, false)
        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val spin_kit = activity?.findViewById<SpinKitView>(R.id.spin_kit)!!
        val bg_cover = activity?.findViewById<CardView>(R.id.loading_cover)!!
            lifecycleScope.launch {
                spin_kit.visibility = View.VISIBLE
                bg_cover.visibility = View.VISIBLE
                buddyMatchViewModel.updateMatchedUsers(UserRepository(FirestoreUtils()).getUsers())
                buddyMatchViewModel.updateYou(UserRepository(FirestoreUtils()).getCurrentUserAsUser())
                spin_kit.visibility = View.GONE
                bg_cover.visibility = View.GONE
                if (buddyMatchViewModel.you.value!!.courseOfStudy != "--blank--") {
                    populateMatchedUsers(
                        buddyMatchViewModel.matchedUsers.value!!,
                        inflater,
                        container
                    )
                }
            }

        buddyMatchViewModel.you.observe(viewLifecycleOwner, Observer {
            you = buddyMatchViewModel.you.value!!
        })
        swipeView = match_swipe as RecyclerView
        snapHelper = LinearSnapHelper() //make the swiping snappy
        snapHelper.attachToRecyclerView(swipeView)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        swipeView.layoutManager = layoutManager
        val offset = resources.getDimension(R.dimen.offset_image)
        swipeView.addItemDecoration(OffsetHelperHorizontal(offset.toInt()))
    }

    fun onScrollListener(
        currPos: Int,
        allUsers: MutableList<User>,
        inflater: LayoutInflater,
        container: ViewGroup?
    ) {
        val currUser = allUsers[currPos % allUsers.size]
        var tableView = matched_modules_buddymatch
        tableView.removeAllViews()

        getMatchedModules(currPos, currUser, inflater, container)

        setUpButton(currUser, allUsers, currPos)
    }
    fun setUpButtonAfterAnimation(currUser: User, allUsers: MutableList<User>, currPos: Int) {
        allUsers.remove(currUser)
        buddyMatchViewModel.images.removeAt(currPos)
        if(currPos == allUsers.size) {
            swipeView.scrollToPosition(currPos - 1)
        }
        swipeView.scrollBy(-1,0)
        swipeView.scrollBy(1,0)
        swipeView.scrollBy(1,0)
        swipeView.scrollBy(-1,0)

        if(buddyMatchViewModel.images.size == 0) {
            match_button.isEnabled = false
            more_info_buddymatch.isEnabled = false
            buddyMatchViewModel.images.add("https://i7.pngflow.com/pngimage/455/105/png-anonymity-computer-icons-anonymous-user-anonymous-purple-violet-logo-smiley-clipart.png")
            name_buddymatch.text = "--Blank--"
            major_buddymatch.text = ""
            curr_year_buddymatch.text = ""
        }
        (swipeView.adapter as Adapter).notifyDataSetChanged()
        var toast = Toast.makeText(context, "Buddy added !", Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, 0)
        toast.show()
        matchBuddy(currUser)
    }
    fun setUpButton(currUser: User, allUsers: MutableList<User>, currPos: Int) {
        val alphaAni = AlphaAnimation(1F, 0.8F)
        val animFadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out)
        val animFadeOut2 = AnimationUtils.loadAnimation(context, R.anim.fade_out)
        animFadeOut.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation?) {
                setUpButtonAfterAnimation(currUser, allUsers, currPos)
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationStart(animation: Animation?) {
            }
        })
        if(allUsers.size == 0) {
            more_info_buddymatch.isEnabled = false
            match_button.isEnabled = false
        }
        else {
            more_info_buddymatch.isEnabled = true
            match_button.isEnabled = true
        }
        match_button.setOnClickListener {
            match_button.startAnimation(alphaAni)
            card_buddy_match.startAnimation(animFadeOut2)
            swipeView.findViewHolderForAdapterPosition(currPos)?.itemView?.startAnimation(animFadeOut)
        }
        more_info_buddymatch.setOnClickListener {
            if(allUsers.size > 0) {
                val intent = Intent(context, BuddyProfileActivity::class.java)
                intent.putExtra("USER_IMG", currUser.profilePicturePath)
                intent.putExtra("USER_NAME", currUser.name)
                intent.putExtra("USER_ABOUT", currUser.about)
                more_info_buddymatch.startAnimation(alphaAni)
                startActivity(intent)
            }
        }
    }
    fun isMatched(module: Module): Boolean {
        for(module_you in you.modules) {
            if(module.moduleCode.equals(module_you.moduleCode)) return true
        }
        return false
    }
    fun getMatchedModules(currPos: Int, currUser: User, inflater: LayoutInflater, container: ViewGroup?) {
        name_buddymatch.text = currUser.name
        major_buddymatch.text = currUser.courseOfStudy
        curr_year_buddymatch.text = if(currUser.yearOfStudy < 5) "Year " + currUser.yearOfStudy.toString() else "Graduate"
        var matchedModules = currUser.modules.filter { module -> isMatched(module) }.toList()

        /****/

        /**matched modules**/
        matched_modules_buddymatch.layoutManager = GridLayoutManager(context, 3)
        var modulesAdapter = ModulesAdapter(matchedModules.toTypedArray(), context)
        modulesAdapter.setClickListener(object : ModulesAdapter.ItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                val intent = Intent(requireContext(), SingleCommunityActivity::class.java)
                startActivity(intent)
            }
        })
        matched_modules_buddymatch.adapter = modulesAdapter

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

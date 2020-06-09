package com.teamnusocial.nusocial.ui.buddymatch

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.User
import com.teamnusocial.nusocial.data.repository.UserRepository
import com.teamnusocial.nusocial.utils.FirestoreUtils
import kotlinx.android.synthetic.main.fragment_buddymatch.*
import kotlinx.android.synthetic.main.matched_module_child.view.*
import kotlinx.coroutines.*


class BuddyMatchFragment : Fragment() {
    private lateinit var swipeView: RecyclerView
    private lateinit var buddyMatchViewModel: BuddyMatchViewModel
    private lateinit var snapHelper: LinearSnapHelper
    private lateinit var root: View
    private lateinit var linearLayoutVertical: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buddyMatchViewModel =
            ViewModelProvider(requireActivity()).get(BuddyMatchViewModel::class.java)
        lifecycleScope.launch {
            buddyMatchViewModel.updateMatchedUsers(UserRepository(FirestoreUtils()).getUsers())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        buddyMatchViewModel.matchedUsers.observe(viewLifecycleOwner, Observer {
            if (it.size > 0) {
                populateMatchedUsers(it, inflater, container)
            }
        })
        root = inflater.inflate(R.layout.fragment_buddymatch, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeView = match_swipe as RecyclerView
        snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(swipeView)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        layoutManager.scrollToPositionWithOffset(Int.MAX_VALUE / 2, swipeView.width / 2 + 185)
        swipeView.layoutManager = layoutManager
    }

    fun onScrollListener(
        currPos: Int,
        allUsers: MutableList<User>,
        inflater: LayoutInflater,
        container: ViewGroup?
    ) {
        val currUser = allUsers[currPos]
        name_buddymatch.text = currUser.name
        major_buddymatch.text = currUser.courseOfStudy
        curr_year_buddymatch.text = "Year " + currUser.yearOfStudy.toString()
        val numberOfMatchedModules = currUser.modules.size
        var numberOfRows = numberOfMatchedModules / 4
        numberOfRows += if (numberOfMatchedModules % 4 > 0) 1 else 0
        /****/

        /**matched modules**/

        var cardView = root.findViewById<CardView>(R.id.matched_modules_buddymatch)
        if (this::linearLayoutVertical.isInitialized) {
            cardView.removeAllViews()
        }
        linearLayoutVertical = LinearLayout(context)
        linearLayoutVertical.removeAllViewsInLayout()
        var id: Int = 0
        linearLayoutVertical.orientation = LinearLayout.VERTICAL
        if (numberOfRows > 1 || numberOfMatchedModules == 4) {
            for (y in 1..numberOfRows) {
                var linearLayoutHorizontal = LinearLayout(context)
                linearLayoutHorizontal.orientation = LinearLayout.HORIZONTAL
                for (x in 0..3) {
                    val module: View =
                        inflater.inflate(R.layout.matched_module_child, container, false)
                    module.module_name.text = currUser.modules[id++].moduleCode
                    linearLayoutHorizontal.addView(module)
                }
                linearLayoutVertical.addView(linearLayoutHorizontal)
            }

            var linearLayoutHorizontal = LinearLayout(context)
            linearLayoutHorizontal.orientation = LinearLayout.HORIZONTAL
            for (x in 1..numberOfMatchedModules % 4) {
                val module: View = inflater.inflate(R.layout.matched_module_child, container, false)
                module.module_name.text = currUser.modules[id++].moduleCode
                linearLayoutHorizontal.addView(module)
            }
            linearLayoutVertical.addView(linearLayoutHorizontal)
        } else if (numberOfMatchedModules < 4) {
            var linearLayoutHorizontal = LinearLayout(context)
            linearLayoutHorizontal.orientation = LinearLayout.HORIZONTAL
            for (x in 1..numberOfMatchedModules) {
                val module: View = inflater.inflate(R.layout.matched_module_child, container, false)
                module.module_name.text = currUser.modules[id++].moduleCode
                linearLayoutHorizontal.addView(module)
            }
            linearLayoutVertical.addView(linearLayoutHorizontal)
        }
        cardView.addView(linearLayoutVertical)
        /****/
        more_info_buddymatch.setOnClickListener {
            val intent = Intent(context, BuddyProfileActivity::class.java)
            intent.putExtra("USER_IMG", currUser.profilePicturePath)
            intent.putExtra("USER_NAME", currUser.name)
            intent.putExtra("USER_ABOUT", currUser.about)
            startActivity(intent)
        }
    }

    fun populateMatchedUsers(
        allUsers: MutableList<User>,
        inflater: LayoutInflater,
        container: ViewGroup?
    ) {
        /**image swipe view**/
        for (user in allUsers) {
            buddyMatchViewModel.images.add(user.profilePicturePath)
        }
        var adapter = Adapter(buddyMatchViewModel.images)
        swipeView.adapter = adapter
        /****/

        /**starting match**/
        var currPos = (Int.MAX_VALUE / 2) % allUsers.size
        var currUser = allUsers[currPos]
        name_buddymatch.text = currUser.name
        major_buddymatch.text = currUser.courseOfStudy
        curr_year_buddymatch.text = "Year " + currUser.yearOfStudy.toString()
        val numberOfMatchedModules = currUser.modules.size
        Log.d("Test", "Here " + numberOfMatchedModules)
        var numberOfRows = numberOfMatchedModules / 4
        numberOfRows += if (numberOfMatchedModules % 4 > 0) 1 else 0
        /****/

        /**matched modules**/
        var cardView = root.findViewById<CardView>(R.id.matched_modules_buddymatch)
        var linearLayoutVertical = LinearLayout(context)
        var id: Int = 0
        linearLayoutVertical.orientation = LinearLayout.VERTICAL
        if (numberOfRows > 1 || numberOfMatchedModules == 4) {
            for (y in 1..numberOfRows) {
                var linearLayoutHorizontal = LinearLayout(context)
                linearLayoutHorizontal.orientation = LinearLayout.HORIZONTAL
                for (x in 0..3) {
                    val module: View =
                        inflater.inflate(R.layout.matched_module_child, container, false)
                    module.module_name.text = currUser.modules[id++].moduleCode
                    linearLayoutHorizontal.addView(module)
                }
                linearLayoutVertical.addView(linearLayoutHorizontal)
            }

            var linearLayoutHorizontal = LinearLayout(context)
            linearLayoutHorizontal.orientation = LinearLayout.HORIZONTAL
            for (x in 1..numberOfMatchedModules % 4) {
                val module: View = inflater.inflate(R.layout.matched_module_child, container, false)
                module.module_name.text = currUser.modules[id++].moduleCode
                linearLayoutHorizontal.addView(module)
            }
            linearLayoutVertical.addView(linearLayoutHorizontal)
        } else if (numberOfMatchedModules < 4) {
            var linearLayoutHorizontal = LinearLayout(context)
            linearLayoutHorizontal.orientation = LinearLayout.HORIZONTAL
            for (x in 1..numberOfMatchedModules) {
                val module: View = inflater.inflate(R.layout.matched_module_child, container, false)
                module.module_name.text = currUser.modules[id++].moduleCode
                linearLayoutHorizontal.addView(module)
            }
            linearLayoutVertical.addView(linearLayoutHorizontal)
        }
        cardView.addView(linearLayoutVertical)
        /****/

        /**go to profile**/
        more_info_buddymatch.setOnClickListener {
            val intent = Intent(context, BuddyProfileActivity::class.java)
            intent.putExtra("USER_IMG", currUser.profilePicturePath)
            intent.putExtra("USER_NAME", currUser.name)
            intent.putExtra("USER_ABOUT", currUser.about)
            startActivity(intent)
        }
        /****/

        swipeView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val snapView = snapHelper.findSnapView(swipeView.layoutManager)
                onScrollListener(
                    swipeView.layoutManager!!.getPosition(snapView!!) % allUsers.size,
                    allUsers,
                    inflater,
                    container
                )
            }
        })
    }
}

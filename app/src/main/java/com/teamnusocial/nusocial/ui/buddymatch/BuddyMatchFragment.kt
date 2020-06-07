<<<<<<< HEAD
package com.teamnusocial.nusocial.ui.buddymatch

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
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
import java.lang.Math.abs


class BuddyMatchFragment : Fragment() {
    private lateinit var swipeView: RecyclerView
    private lateinit var buddyMatchViewModel: BuddyMatchViewModel
    private lateinit var snapHelper: LinearSnapHelper
    private lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        buddyMatchViewModel =
            ViewModelProvider(requireActivity()).get(BuddyMatchViewModel::class.java)
        lifecycleScope.launch {
            buddyMatchViewModel.updateMatchedUsers(UserRepository(FirestoreUtils()).getUsers())
        }

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
        var adapter = Adapter(buddyMatchViewModel.images.value!!)
        buddyMatchViewModel.images.observe(viewLifecycleOwner, Observer<ArrayList<String>> { urls ->
            adapter = Adapter(urls)
        })
        swipeView = match_swipe as RecyclerView
        snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(swipeView)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        layoutManager.scrollToPositionWithOffset(Int.MAX_VALUE / 2, swipeView.width / 2 + 185)
        swipeView.layoutManager = layoutManager
        swipeView.adapter = adapter
    }
    fun onScrollListener(currPos: Int, allUsers: MutableList<User>) {
        val currUser = allUsers[currPos]
        name_buddymatch.text = currUser.name
        major_buddymatch.text = currUser.courseOfStudy
        curr_year_buddymatch.text = "Year " + currUser.yearOfStudy.toString()
        more_info_buddymatch.setOnClickListener {
            val intent = Intent(context, BuddyProfileActivity::class.java)
            intent.putExtra("USER", currUser)
            startActivity(intent)
        }
    }

    fun populateMatchedUsers(allUsers: MutableList<User>, inflater: LayoutInflater, container: ViewGroup?) {
        /**starting match**/
        var currPos = (Int.MAX_VALUE / 2) % allUsers.size
        var currUser = allUsers[currPos]
        name_buddymatch.text = currUser.name
        major_buddymatch.text = currUser.courseOfStudy
        curr_year_buddymatch.text = "Year " + currUser.yearOfStudy.toString()
        val numberOfMatchedModules = currUser.modules.size
        val numberOfRows = numberOfMatchedModules / 4 + 1
        /****/

        /**matched modules**/
        var cardView = root.findViewById<CardView>(R.id.matched_modules_buddymatch)
        var linearLayoutVertical = LinearLayout(context)
        var id: Int = 0
        linearLayoutVertical.orientation = LinearLayout.VERTICAL
        for (y in 1..numberOfRows) {
            var linearLayoutHorizontal = LinearLayout(context)
            linearLayoutHorizontal.orientation = LinearLayout.HORIZONTAL
            for (x in 0..3) {
                val module: View = inflater.inflate(R.layout.matched_module_child, container, false)
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
        cardView.addView(linearLayoutVertical)
        /****/

        /**go to profile**/
        more_info_buddymatch.setOnClickListener {
            val intent = Intent(context, BuddyProfileActivity::class.java)
            intent.putExtra("USER", currUser)
            startActivity(intent)
        }
        /****/

        swipeView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val snapView = snapHelper.findSnapView(swipeView.layoutManager)
                onScrollListener(swipeView.layoutManager!!.getPosition(snapView!!) % allUsers.size, allUsers)
            }
        })
    }
}
=======
package com.teamnusocial.nusocial.ui.buddymatch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.LocationRequest
import com.teamnusocial.nusocial.R

class BuddyMatchFragment : Fragment() {

    private lateinit var buddyMatchViewModel: BuddyMatchViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        buddyMatchViewModel =
            ViewModelProvider(this).get(BuddyMatchViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_buddymatch, container, false)
        val textView: TextView = root.findViewById(R.id.text_buddymatch)
        buddyMatchViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }


}
>>>>>>> 686ba756b24167f624bedf129553d273f75b5eca

package com.teamnusocial.nusocial.ui.buddymatch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.github.ybq.android.spinkit.SpinKitView
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.User
import com.teamnusocial.nusocial.data.repository.UserRepository
import com.teamnusocial.nusocial.utils.FirestoreUtils
import kotlinx.android.synthetic.main.fragment_buddymatch.*
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

    fun populateMatchedUsers(
        allUsers: MutableList<User>,
        inflater: LayoutInflater,
        container: ViewGroup?
    ) {
        /**image swipe view**/
        val filteredAllUsers = MatchingUsers(allUsers, you).filterUsers()
        if(filteredAllUsers.size == 0) {
            filteredAllUsers.add(User())
        }
        var adapter = Adapter(filteredAllUsers, you, requireContext())
        swipeView.adapter = adapter

        /****/
    }
}

package com.teamnusocial.nusocial.ui.community

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.ybq.android.spinkit.SpinKitView
import com.google.firebase.Timestamp
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.Post
import com.teamnusocial.nusocial.data.repository.SocialToolsRepository
import com.teamnusocial.nusocial.data.repository.UserRepository
import com.teamnusocial.nusocial.ui.buddymatch.OffsetHelperHorizontal
import com.teamnusocial.nusocial.ui.buddymatch.OffsetHelperVertical
import com.teamnusocial.nusocial.utils.FirestoreUtils
import kotlinx.android.synthetic.main.fragment_community.*
import kotlinx.coroutines.launch

class CommunityFragment : Fragment() {
    private lateinit var viewModel: CommunityViewModel
    private val utils = SocialToolsRepository(FirestoreUtils())
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity()).get(CommunityViewModel::class.java)
        return inflater.inflate(R.layout.fragment_community, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spin_kit = activity?.findViewById<SpinKitView>(R.id.spin_kit)!!
        val bg_cover = activity?.findViewById<CardView>(R.id.loading_cover)!!
        lifecycleScope.launch {
            spin_kit.visibility = View.VISIBLE
            bg_cover.visibility = View.VISIBLE
            viewModel.you = UserRepository(FirestoreUtils()).getCurrentUserAsUser()
            for(commID in viewModel.you.communities) {
                viewModel.allPosts.addAll(utils.getPostsOfCommunity(commID))
            }
            updateUI()
            spin_kit.visibility = View.GONE
            bg_cover.visibility = View.GONE
        }
    }
    fun updateUI() {
        val allPosts = viewModel.allPosts.sortedBy { it.timeStamp }
        val postAdapter =
            PostNewsFeedAdapter(requireContext(),viewModel.you, allPosts.toMutableList())
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        personal_posts.layoutManager = layoutManager
        personal_posts.addItemDecoration(OffsetHelperVertical())
        personal_posts.adapter = postAdapter

    }

}
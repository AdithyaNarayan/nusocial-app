package com.teamnusocial.nusocial.ui.community

import android.app.Activity
import android.content.Intent
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
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.github.ybq.android.spinkit.SpinKitView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.gpfreetech.neumorphism.Neumorphism
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.Post
import com.teamnusocial.nusocial.data.model.User
import com.teamnusocial.nusocial.data.repository.SocialToolsRepository
import com.teamnusocial.nusocial.data.repository.UserRepository
import com.teamnusocial.nusocial.ui.buddymatch.OffsetHelperHorizontal
import com.teamnusocial.nusocial.ui.buddymatch.OffsetHelperVertical
import com.teamnusocial.nusocial.utils.FirestoreUtils
import com.teamnusocial.nusocial.utils.KeyboardToggleListener
import kotlinx.android.synthetic.main.fragment_community.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
            viewModel.allPosts.clear()
            for(commID in viewModel.you.communities) {
                viewModel.allPosts.addAll(utils.getPostsOfCommunity(commID))
            }
            updateUI()
        }

        search_button.setOnClickListener {
            val intent = Intent(context, SearchActivity::class.java)
            intent.putExtra("searchTerm", search_term.text.toString().toLowerCase())
            startActivity(intent)
        }
    }

    private fun updateUI() {
        val allPosts = viewModel.allPosts.sortedWith(PostComparator).toMutableList()
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        personal_posts.layoutManager = layoutManager
        var userTable = HashMap<String, User>()
        val spin_kit = activity?.findViewById<SpinKitView>(R.id.spin_kit)!!
        val bg_cover = activity?.findViewById<CardView>(R.id.loading_cover)!!
        CoroutineScope(Dispatchers.IO).launch {
            for(post in allPosts) {
                if(!userTable.containsKey(post.ownerUid)) {
                    val owner = UserRepository(FirestoreUtils()).getUser(post.ownerUid)
                    userTable.put(post.ownerUid, owner)
                }
            }
            withContext(Dispatchers.Main) {
                val postAdapter =
                    PostNewsFeedAdapter(requireContext(), viewModel.you, allPosts, userTable)
                val offset = resources.getDimension(R.dimen.offset_vertical_newsfeed)
                personal_posts.addItemDecoration(OffsetHelperVertical(offset.toInt()))
                personal_posts.adapter = postAdapter
                personal_posts.isNestedScrollingEnabled = false
                spin_kit.visibility = View.GONE
                bg_cover.visibility = View.GONE
            }
        }
        addKeyboardToggleListener { shown ->
            if(!shown) {
                if(search_term != null) {
                    search_term.clearFocus()
                }
            }
        }
    }
    fun addKeyboardToggleListener(onKeyboardToggleAction: (shown: Boolean) -> Unit): KeyboardToggleListener? {
        val root = activity?.findViewById<View>(android.R.id.content)
        val listener = KeyboardToggleListener(root, onKeyboardToggleAction)
        return root?.viewTreeObserver?.run {
            addOnGlobalLayoutListener(listener)
            listener
        }
    }
}
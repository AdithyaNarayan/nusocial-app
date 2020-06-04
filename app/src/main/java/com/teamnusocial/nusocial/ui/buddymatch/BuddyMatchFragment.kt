package com.teamnusocial.nusocial.ui.buddymatch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.teamnusocial.nusocial.R

class BuddyMatchFragment : Fragment() {
    private lateinit var swipeView: RecyclerView
    private lateinit var buddyMatchViewModel: BuddyMatchViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        buddyMatchViewModel = ViewModelProvider(this).get(BuddyMatchViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_buddymatch, container, false)
        var adapter = Adapter(buddyMatchViewModel.images.value!!)
        buddyMatchViewModel.images.observe(viewLifecycleOwner, Observer<ArrayList<String>> {urls ->
            adapter = Adapter(urls)
        })
        swipeView = root.findViewById(R.id.match_swipe) as RecyclerView
        LinearSnapHelper().attachToRecyclerView(swipeView)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        layoutManager.scrollToPositionWithOffset(Int.MAX_VALUE/2, swipeView.width/2 + 185)
        swipeView.layoutManager = layoutManager
        swipeView.adapter = adapter
        return root
    }
}

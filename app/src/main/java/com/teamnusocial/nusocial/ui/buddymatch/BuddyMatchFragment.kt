package com.teamnusocial.nusocial.ui.buddymatch

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.utils.KnuthShuffle


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

        var cardView = root.findViewById<CardView>(R.id.matched_modules_buddymatch)
        var linearLayoutVertical = LinearLayout(context)
        linearLayoutVertical.orientation = LinearLayout.VERTICAL
        //var shuffleColor = KnuthShuffle(10).shuffle()
        for(y in 0..2) {
            var linearLayoutHorizontal = LinearLayout(context)
            linearLayoutHorizontal.orientation = LinearLayout.HORIZONTAL
            for (x in 0..3) {
                val module: View = inflater.inflate(R.layout.matched_module_child, container, false)
                linearLayoutHorizontal.addView(module)
            }
            linearLayoutVertical.addView(linearLayoutHorizontal)
        }
        cardView.addView(linearLayoutVertical)
        val button = root.findViewById<Button>(R.id.more_info_buddymatch)
        button.setOnClickListener {
            val intent = Intent(context, BuddyProfileActivity::class.java)
            startActivity(intent)
        }
        return root
    }

}

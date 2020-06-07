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
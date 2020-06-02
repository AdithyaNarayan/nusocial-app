package com.teamnusocial.nusocial.ui.broadcast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.teamnusocial.nusocial.R

class BroadcastFragment : Fragment() {

    private lateinit var broadcastViewModel: BroadcastViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        broadcastViewModel =
            ViewModelProvider(this).get(BroadcastViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_broadcast, container, false)
        val textView: TextView = root.findViewById(R.id.text_broadcast)
        broadcastViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}
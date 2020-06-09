package com.teamnusocial.nusocial.ui.messages

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teamnusocial.nusocial.R
import kotlinx.android.synthetic.main.fragment_messages.*

class MessagesFragment : Fragment() {

    companion object {
        fun newInstance() = MessagesFragment()
    }

    private lateinit var viewModel: MessagesViewModel
    private lateinit var adapter: MessagesRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_messages, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MessagesViewModel::class.java)
        // TODO: Use the ViewModel
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userNames: ArrayList<String> =
            arrayListOf("Adithya", "Hieu", "n00b1", "n00b2", "n00b3", "n00b4", "n00b5", "n00b6")

        val recyclerView: RecyclerView = messageUsersRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = MessagesRecyclerViewAdapter(requireContext(), userNames)
        recyclerView.adapter = adapter
        adapter.setClickListener(object : MessagesRecyclerViewAdapter.ItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                Log.d("Messages", position.toString())
            }
        })
    }

}
package com.teamnusocial.nusocial.ui.messages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.User
import com.teamnusocial.nusocial.data.repository.UserRepository
import com.teamnusocial.nusocial.utils.FirestoreUtils
import kotlinx.android.synthetic.main.fragment_messages.*
import kotlinx.coroutines.*

class MessagesFragment : Fragment() {

    companion object {
        fun newInstance() = MessagesFragment()
    }

    private lateinit var viewModel: MessagesViewModel
    private lateinit var adapter: MessagesRecyclerViewAdapter
    private var messageIDList = mutableListOf<String>()
    private val list: MutableList<Pair<String, String>> = mutableListOf()

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
        CoroutineScope(Dispatchers.Main).launch {
            FirestoreUtils().getUserAsDocument(FirestoreUtils().getCurrentUser()!!.uid).get()
                .addOnCompleteListener {
                    CoroutineScope(Dispatchers.Main).launch {
                        updateMessages(
                            Pair(
                                it.result!!["uid"] as String,
                                it.result!!["name"] as String
                            )
                        )

                    }
                }
        }
    }

    private fun updateRecyclerView(list: List<Pair<String, String>>) {
        val recyclerView: RecyclerView = messageUsersRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = MessagesRecyclerViewAdapter(requireContext(), list)
        recyclerView.adapter = adapter
        adapter.setClickListener(object : MessagesRecyclerViewAdapter.ItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                val intent = Intent(requireContext(), MessageChatActivity::class.java)
                intent.putExtra("messageID", messageIDList[position])
                intent.putExtra("messageName", list[position].second)
                startActivity(intent)
            }
        })
    }

    private suspend fun updateMessages(pair: Pair<String, String>) = coroutineScope {
        FirestoreUtils().getMessagesOfUser(pair).get()
            .addOnCompleteListener {
                it.result!!.documents.forEach { document ->
                    messageIDList.add(document.id)
                    val listRecipients = document["recipients"] as List<*>
                    var pair: Pair<String, String> = Pair("", "")
                    listRecipients.forEach { recipient ->
                        if ((recipient as HashMap<String, String>)["first"] != FirestoreUtils().getCurrentUser()!!.uid) {
                            pair = Pair(recipient["first"]!!, recipient["second"]!!)
                        }
                    }
                    list.add(pair)
                }
                Log.d("MESSAGES", list.toString())
                updateRecyclerView(list)
            }
    }
}




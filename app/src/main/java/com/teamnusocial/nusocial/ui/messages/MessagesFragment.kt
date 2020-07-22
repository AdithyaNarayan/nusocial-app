package com.teamnusocial.nusocial.ui.messages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybq.android.spinkit.SpinKitView
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.MessageConfig
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
    private val list: MutableList<MessageConfig> = mutableListOf()

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
        val spin_kit = activity?.findViewById<SpinKitView>(R.id.spin_kit)!!
        val bg_cover = activity?.findViewById<CardView>(R.id.loading_cover)!!
        spin_kit.visibility = View.VISIBLE
        bg_cover.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.Main).launch {
            FirestoreUtils().getUserAsDocument(FirestoreUtils().getCurrentUser()!!.uid).get()
                .addOnCompleteListener {
                    CoroutineScope( Dispatchers.Main).launch {
                        updateMessages(
                            Pair(
                                it.result!!["uid"] as String,
                                it.result!!["name"] as String
                            )
                        )
                        withContext(Dispatchers.Main) {
                            spin_kit.visibility = View.GONE
                            bg_cover.visibility = View.GONE
                        }
                    }
                }
        }

        addChatButton.setOnClickListener {
            startActivity(Intent(requireContext(), GroupChatSelectorActivity::class.java))
        }
    }

    private fun updateRecyclerView(list: List<MessageConfig>) {
        if (messageUsersRecyclerView == null) {
            return
        }
        val recyclerView: RecyclerView = messageUsersRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = MessagesRecyclerViewAdapter(requireContext(), list)
        recyclerView.adapter = adapter
        adapter.setClickListener(object : MessagesRecyclerViewAdapter.ItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                val intent = Intent(requireContext(), MessageChatActivity::class.java)
                intent.putExtra("messageID", messageIDList[position])
                if (list[position].recipients.size < 2) {
                    intent.putExtra("messageName", list[position].recipients[0].second)
                } else {
                    intent.putExtra("messageName", list[position].name)
                }
                startActivity(intent)
            }
        })
    }

    private suspend fun updateMessages(pair: Pair<String, String>) = coroutineScope {
        list.clear()
        FirestoreUtils().getMessagesOfUser(pair).get()
            .addOnCompleteListener {
                it.result!!.documents.forEach { document ->
                    messageIDList.add(document.id)
                    val messageConfig = document.toObject(MessageConfig::class.java)!!

                    messageConfig.recipients = messageConfig.recipients.filter { recipient ->
                        recipient.first != FirestoreUtils().getCurrentUser()!!.uid
                    }.toMutableList()

                    list.add(messageConfig)
                    return@forEach
                }
                Log.d("MESSAGES", list.toString())
                list.sortWith(Comparator { obj1, obj2 -> obj2.latestTime.compareTo(obj1.latestTime) })
                updateRecyclerView(list)
            }
    }
}



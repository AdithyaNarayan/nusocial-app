package com.teamnusocial.nusocial.ui.messages

import android.os.Bundle
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.User
import com.teamnusocial.nusocial.data.repository.UserRepository
import com.teamnusocial.nusocial.utils.FirestoreUtils
import kotlinx.android.synthetic.main.activity_group_chat_selector.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import soup.neumorphism.NeumorphCardView
import soup.neumorphism.ShapeType

class GroupChatSelectorActivity : AppCompatActivity() {
    val data = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat_selector)

        val repository = UserRepository(FirestoreUtils())

        CoroutineScope(Dispatchers.IO).launch {
            val list = repository.getCurrentUserAsUser().buddies
            list.forEach {
                data.add(repository.getUser(it))
                withContext(Dispatchers.Main) {
                    notifyUpdateList(data, list.size)
                }
            }
        }
        createChatButton.setOnClickListener {
            val groupName = groupNameEditText.text.toString()
            CoroutineScope(Dispatchers.IO).launch {
                repository.createGroupChatWith(groupName, getSelectedList())
            }
            this.onBackPressed()
        }
    }

    private fun notifyUpdateList(list: List<User>, size: Int) {
        if (list.size == size) {
            val adapter = UserCheckableAdapter(list)
            userSelectorRecyclerView.adapter = adapter
            userSelectorRecyclerView.layoutManager =
                LinearLayoutManager(this@GroupChatSelectorActivity)
        }
    }

    private fun getSelectedList(): MutableList<String> {
        val list = mutableListOf(FirestoreUtils().getCurrentUser()!!.uid)
        (0 until data.size).forEach {
            if (userSelectorRecyclerView.layoutManager!!.findViewByPosition(it)!!
                    .findViewById<NeumorphCardView>(R.id.userCardCheckable).getShapeType() == ShapeType.PRESSED
            ) {
                list.add(data[it].uid)
            }
        }
        return list
    }
}
package com.teamnusocial.nusocial.ui.community

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
//import com.algolia.instantsearch.helper.searcher.SearcherSingleIndex
//import com.algolia.search.client.ClientSearch
//import com.algolia.search.model.APIKey
//import com.algolia.search.model.ApplicationID
//import com.algolia.search.model.IndexName
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.Post
import com.teamnusocial.nusocial.data.model.TextMessage
import com.teamnusocial.nusocial.data.repository.UserRepository
import com.teamnusocial.nusocial.ui.buddymatch.OffsetHelperVertical
import com.teamnusocial.nusocial.ui.messages.MessageChatRecyclerViewAdapter
import com.teamnusocial.nusocial.utils.FirestoreUtils
// import io.ktor.client.features.logging.LogLevel
import kotlinx.android.synthetic.main.activity_message_chat.*
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.fragment_community.*
import kotlinx.coroutines.*

class SearchActivity : AppCompatActivity() {
    val data = mutableListOf<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val searchTerm = intent.getStringExtra("searchTerm")
        toolBarSearch.title = "Results for $searchTerm"

//        val client = ClientSearch(
//            ApplicationID("VOLF6JWSNB"),
//            APIKey("f0ea1557a824276d2ef384f2398a5637"),
//            LogLevel.ALL
//        )
//        val index = client.initIndex(IndexName("posts_search"))
//        val searcher = SearcherSingleIndex(index)
        CoroutineScope(Dispatchers.IO).launch {
            UserRepository(FirestoreUtils()).getUserAnd(FirestoreUtils().getCurrentUser()!!.uid) { user ->
                Log.d("SEARCH", 1.toString())
                FirebaseFirestore.getInstance().collectionGroup("posts")
                    .get().addOnCompleteListener {
                        if (it.isSuccessful) {
                            Log.d("SEARCH", it.result!!.documents.toString())
                            it.result!!.documents.filter { post ->
                                post["communityID"] in user.communities
                            }.filter { post ->
                                searchTerm!! in post["textContent"].toString()
                            }.forEach { post ->
                                data.add(post.toObject(Post::class.java)!!)
                            }
                            CoroutineScope(Dispatchers.Main).launch {
                                Log.d("SEARCH", 3.toString())
                                updateUI()
                            }
                        }
                    }
            }
        }
    }

    private suspend fun updateUI() = coroutineScope {
        UserRepository(FirestoreUtils()).getUserAnd(FirestoreUtils().getCurrentUser()!!.uid) {
            Log.d("SEARCH", data.toString())
            val postAdapter =
                PostNewsFeedAdapter(this@SearchActivity, it, data)
            postsRecyclerView.adapter = postAdapter
            val layoutManager = LinearLayoutManager(this@SearchActivity)
            layoutManager.orientation = LinearLayoutManager.VERTICAL
            postsRecyclerView.layoutManager = layoutManager
            postsRecyclerView.addItemDecoration(OffsetHelperVertical())
        }
    }
}
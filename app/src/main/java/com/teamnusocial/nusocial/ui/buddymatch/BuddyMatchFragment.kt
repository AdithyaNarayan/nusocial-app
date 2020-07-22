package com.teamnusocial.nusocial.ui.buddymatch

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.github.ybq.android.spinkit.SpinKitView
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.User
import com.teamnusocial.nusocial.ui.broadcast.BuddyMatchViewModelFactory
import kotlinx.android.synthetic.main.fragment_buddymatch.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class BuddyMatchFragment : Fragment(), KodeinAware {
    override val kodein by closestKodein()
    private val factory by instance<BuddyMatchViewModelFactory>()
    private lateinit var swipeView: RecyclerView
    private lateinit var buddyMatchViewModel: BuddyMatchViewModel
    private lateinit var snapHelper: LinearSnapHelper
    private lateinit var root: View
    private lateinit var you: User

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        buddyMatchViewModel =
            ViewModelProvider(this, factory).get(BuddyMatchViewModel::class.java)
        root = inflater.inflate(R.layout.fragment_buddymatch, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val spin_kit = activity?.findViewById<SpinKitView>(R.id.spin_kit)!!
        val bg_cover = activity?.findViewById<CardView>(R.id.loading_cover)!!
        spin_kit.visibility = View.VISIBLE
        bg_cover.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            buddyMatchViewModel.updateMatchedUsers()
            buddyMatchViewModel.updateYou()
            //if (buddyMatchViewModel.you.value!!.courseOfStudy != "--blank--") {
            withContext(Dispatchers.Main) {
                populateMatchedUsers(buddyMatchViewModel.matchedUsers.value!!)
                spin_kit.visibility = View.GONE
                bg_cover.visibility = View.GONE
            }
            //}
        }

        buddyMatchViewModel.you.observe(viewLifecycleOwner, Observer {
            you = buddyMatchViewModel.you.value!!
        })
        swipeView = match_swipe as RecyclerView
        snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(swipeView)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        swipeView.layoutManager = layoutManager
        val offset = resources.getDimension(R.dimen.offset_image)
        swipeView.addItemDecoration(OffsetHelperHorizontal(offset.toInt()))
//        val offset = resources.getDimension(R.dimen.offset_image)
//        swipeView.addItemDecoration(OffsetHelperHorizontal(offset.toInt()))
//        swipeView.addItemDecoration(object : RecyclerView.ItemDecoration() {
//            override fun getItemOffsets(
//                outRect: Rect,
//                view: View,
//                parent: RecyclerView,
//                state: RecyclerView.State
//            ) {
//                super.getItemOffsets(outRect, view, parent, state)
//                outRect.left = -80
//                outRect.right = -80
//            }
//        })
        swipeView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                val position = layoutManager.getPosition(snapHelper.findSnapView(layoutManager)!!)
                val card =
                    swipeView.findViewHolderForAdapterPosition(position)?.itemView?.findViewById<CardView>(
                        R.id.card_wrapper
                    )!!

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    card.animate().setDuration(300).scaleX(0.95F).scaleY(0.95F)
                        .setInterpolator(AccelerateInterpolator()).start()
                } else {
                    card.animate().setDuration(300).scaleX(0.8F).scaleY(0.8F)
                        .setInterpolator(AccelerateInterpolator()).start()
                }
            }
        })
    }
    fun populateMatchedUsers(
        allUsers: MutableList<User>
    ) {
        if(!this::you.isInitialized) you = User()
        /**image swipe view**/
        val filteredAllUsers = MatchingUsers(allUsers, you).filterUsers()
        try {
            val adapter = Adapter(filteredAllUsers, you, requireContext())
            swipeView.adapter = adapter
            if (filteredAllUsers.size > 0) {
                Handler().postDelayed({
                    try {
                        val card =
                            swipeView.findViewHolderForAdapterPosition(0)?.itemView?.findViewById<CardView>(
                                R.id.card_wrapper
                            )!!
                        card.animate().setDuration(300).scaleX(0.95F).scaleY(0.95F)
                            .setInterpolator(AccelerateInterpolator()).start()
                    } catch (e: Exception) {
                        Log.d("ERROR", e.message.toString())
                    }
                }, 1000)
            }
        } catch (e: Exception) {
            Log.d("ERROR", e.message.toString())
        }
//        swipeView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                val snapView = snapHelper.findSnapView(swipeView.layoutManager)
//                if(filteredAllUsers.size > 0) {
//                    onScrollListener(
//                        swipeView.getChildAdapterPosition(snapView!!),
//                        filteredAllUsers,
//                        inflater,
//                        container
//                    )
//                }
//            }
//        })
    }
}

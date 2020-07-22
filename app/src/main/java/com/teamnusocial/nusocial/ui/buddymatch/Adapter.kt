package com.teamnusocial.nusocial.ui.buddymatch

import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.Module
import com.teamnusocial.nusocial.data.model.User
import com.teamnusocial.nusocial.data.repository.UserRepository
import com.teamnusocial.nusocial.ui.you.OtherUserActivity
import com.teamnusocial.nusocial.utils.FirestoreUtils
import jp.wasabeef.picasso.transformations.MaskTransformation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Adapter(private val matches: MutableList<User>, val you: User, val context: Context) : RecyclerView.Adapter<Adapter.MatchHolder>() {
    class MatchHolder(val layoutView: ConstraintLayout): RecyclerView.ViewHolder(layoutView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchHolder {
        val layoutView = LayoutInflater.from(parent.context).inflate(R.layout.buddymatch_item, parent, false) as ConstraintLayout
        return MatchHolder(layoutView)
    }
    override fun onBindViewHolder(holder: MatchHolder, position: Int) {
        val avatar = holder.layoutView.findViewById<ImageView>(R.id.buddy_img)
        val name = holder.layoutView.findViewById<TextView>(R.id.name_buddymatch)
        val course = holder.layoutView.findViewById<TextView>(R.id.major_buddymatch)
        val year = holder.layoutView.findViewById<TextView>(R.id.curr_year_buddymatch)
        val matched_modules = holder.layoutView.findViewById<RecyclerView>(R.id.matched_modules_buddymatch)
        val common_friends = holder.layoutView.findViewById<RecyclerView>(R.id.common_friends_buddymatch)
        val more_info_buddymatch = holder.layoutView.findViewById<Button>(R.id.more_info_buddymatch)
        val buddy_match_button = holder.layoutView.findViewById<Button>(R.id.match_button)
        //val wrapper = holder.layoutView.findViewById<ConstraintLayout>(R.id.match_item_wrapper)
        val common_friends_title = holder.layoutView.findViewById<TextView>(R.id.common_friends_title)
        val curr_match = matches[position]
        Picasso.get().load(curr_match.profilePicturePath).centerCrop().transform(
            MaskTransformation(
            context,
            R.drawable.buddymatch_image_transformation
        )).fit().into(avatar)
        name.text = curr_match.name
        course.text = curr_match.courseOfStudy
        year.text = if(curr_match.yearOfStudy < 5) "Year " + curr_match.yearOfStudy.toString() else "Graduate"

        getMatchedModules(curr_match, matched_modules, holder.layoutView.findViewById<TextView>(R.id.matched_modules_title))
        getCommonFriends(curr_match,common_friends_title, common_friends)
        setUpButton(curr_match,position,more_info_buddymatch,buddy_match_button, holder.layoutView.findViewById<ConstraintLayout>(R.id.match_item_wrapper))
    }
    fun getCommonFriends(currUser: User, common_friends_title: TextView, common_friends_buddymatch: RecyclerView) {
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        common_friends_buddymatch.layoutManager = linearLayoutManager
        var listOfCommonFriendsID = mutableListOf<String>()
        for(friend in you.buddies) {
            if(currUser.buddies.contains(friend)) {
                listOfCommonFriendsID.add(friend)
            }
        }
        if(listOfCommonFriendsID.size == 0) {
            common_friends_title.visibility = View.GONE
            common_friends_buddymatch.visibility = View.GONE
            return
        } else {
            common_friends_title.visibility = View.VISIBLE
            common_friends_buddymatch.visibility = View.VISIBLE
        }
        CoroutineScope(Dispatchers.IO).launch {
            var commonFriends = UserRepository(FirestoreUtils()).getListOfUsers(listOfCommonFriendsID)
            withContext(Dispatchers.Main) {
                var commonFriendAdapter = CommonFriendAdapter(commonFriends, context)
                commonFriendAdapter.setClickListener(object : CommonFriendAdapter.ItemClickListener {
                    override fun onItemClick(view: View?, position: Int) {
                        val intent = Intent(context, OtherUserActivity::class.java)
                        intent.putExtra("USER_ID", commonFriends[position].uid)
                        context!!.startActivity(intent)
                    }
                })
                common_friends_buddymatch.adapter = commonFriendAdapter
            }
        }
    }
    fun getMatchedModules(currUser: User, matched_modules_buddymatch: RecyclerView, title: TextView) {
        var matchedModules = currUser.modules.filter { module -> isMatched(module) }.toMutableList()
        /**matched modules**/
        if(matchedModules.size == 0) {
            title.visibility = View.GONE
            return
        }
        var linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        matched_modules_buddymatch.layoutManager = linearLayoutManager
        var modulesAdapter = ModulesAdapter(matchedModules, context)
        modulesAdapter.setClickListener(object : ModulesAdapter.ItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                var toast = Toast.makeText(context, "Go to your personal page to access the community", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, 0)
                toast.show()
            }
        })
        matched_modules_buddymatch.adapter = modulesAdapter

        /****/
    }
    fun isMatched(module: Module): Boolean {
        for(module_you in you.modules) {
            if(module.moduleCode.equals(module_you.moduleCode)) return true
        }
        return false
    }
    fun setUpButton(currUser: User, currPos: Int, more_info_buddymatch: Button, match_button: Button, wrapper: ConstraintLayout) {
        //val alphaAni = AlphaAnimation(0.5F, 0.8F)
        val animFadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out)
        //val animFadeOut2 = AnimationUtils.loadAnimation(context, R.anim.fade_out)
        animFadeOut.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation?) {
                setUpButtonAfterAnimation(currUser, currPos)
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationStart(animation: Animation?) {
            }
        })
        match_button.setOnClickListener {
            //match_button.startAnimation(alphaAni)
            //more_info_buddymatch.startAnimation(alphaAni)
            wrapper.startAnimation(animFadeOut)

        }
        more_info_buddymatch.setOnClickListener {
            if(matches.size > 0) {
                val intent = Intent(context, BuddyProfileActivity::class.java)
                intent.putExtra("USER_IMG", currUser.profilePicturePath)
                intent.putExtra("USER_NAME", currUser.name)
                intent.putExtra("USER_ABOUT", currUser.about)
                //more_info_buddymatch.startAnimation(alphaAni)
                context.startActivity(intent)
            }
        }
    }
    fun setUpButtonAfterAnimation(currUser: User, currPos: Int) {
        matches.removeAt(currPos)
        //notifyItemRemoved(currPos)
        notifyDataSetChanged()
        //notifyItemRangeChanged(currPos, matches.size)
        var toast = Toast.makeText(context, "Buddy added !", Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, 0)
        toast.show()
        matchBuddy(currUser)
    }
    private fun matchBuddy(currUser: User) {
        if(currUser.seenAndMatch.contains(you.uid)) {
            CoroutineScope(Dispatchers.IO).launch {
                UserRepository(FirestoreUtils()).updateCurrentBuddies(currUser.uid).addOnSuccessListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        val userRepo = UserRepository(FirestoreUtils())
                        userRepo.createChatWith(currUser.uid)
                        userRepo.sendAdminMessage(
                            userRepo.getMessageID(you, currUser),
                            "Matched through BuddyMatch!"
                        )
                    }
                }
            }
        }
        else {
            CoroutineScope(Dispatchers.IO).launch {
                UserRepository(FirestoreUtils()).updateCurrentMatches(currUser.uid)
            }
        }
        /* CoroutineScope(Dispatchers.IO).launch {
             val userRepo = UserRepository(FirestoreUtils())
             userRepo.createChatWith(currUser.uid)
             userRepo.sendAdminMessage(
                 userRepo.getMessageID(you, currUser),
                 "Matched through BuddyMatch!"
             )
         }*/
    }


    override fun getItemCount() = matches.size

}
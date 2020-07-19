package com.teamnusocial.nusocial.ui.you

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.ybq.android.spinkit.SpinKitView
import com.squareup.picasso.Picasso
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.User
import com.teamnusocial.nusocial.data.repository.SocialToolsRepository
import com.teamnusocial.nusocial.data.repository.UserRepository
import com.teamnusocial.nusocial.ui.buddymatch.MaskTransformation
import com.teamnusocial.nusocial.ui.buddymatch.ModulesAdapter
import com.teamnusocial.nusocial.utils.FirestoreUtils
import kotlinx.android.synthetic.main.activity_other_user.*
import kotlinx.android.synthetic.main.fragment_you.*
import kotlinx.android.synthetic.main.fragment_you.communities_in
import kotlinx.android.synthetic.main.fragment_you.course_you
import kotlinx.android.synthetic.main.fragment_you.modules_taking
import kotlinx.android.synthetic.main.fragment_you.number_of_buddies_you
import kotlinx.android.synthetic.main.fragment_you.year_you
import kotlinx.android.synthetic.main.fragment_you.you_image
import kotlinx.android.synthetic.main.fragment_you.you_name
import kotlinx.coroutines.launch

class OtherUserActivity : AppCompatActivity() {
    private lateinit var viewModel: YouViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_user)

        /**fetch data**/
        viewModel = ViewModelProvider(this).get(YouViewModel::class.java)
        val userID = intent.getStringExtra("USER_ID")
        back_button_other_user.setOnClickListener {
            finish()
        }
        /**loading components**/
        val spin_kit_you = findViewById<SpinKitView>(R.id.spin_kit)!!
        val bg_cover = findViewById<CardView>(R.id.loading_cover)!!
        /****/
        lifecycleScope.launch {
            spin_kit_you.visibility = View.VISIBLE
            bg_cover.visibility = View.VISIBLE
            viewModel.you = UserRepository(FirestoreUtils()).getUser(userID)
            viewModel.allCommunitites = SocialToolsRepository(FirestoreUtils()).getAllCommunities()
            viewModel.allYourCommunities = viewModel.allCommunitites.filter { comm -> viewModel.you.communities.contains(comm.id) }.toMutableList()
            viewModel.moduleCommunities.clear()
            viewModel.otherCommunities.clear()
            for(comm in viewModel.allYourCommunities) {
                if(comm.module.moduleCode.equals("")) {
                    viewModel.otherCommunities.add(comm)
                } else {
                    viewModel.moduleCommunities.add(comm)
                }
            }
            updateUI()
            spin_kit_you.visibility = View.GONE
            bg_cover.visibility = View.GONE
        }
    }

    fun updateUI() {
        Picasso
            .get()
            .load(viewModel.you.profilePicturePath)
            //.centerCrop()
            //.transform(MaskTransformation(this, R.drawable.more_info_img_frame))
            //.fit()
            .into(you_image)
        you_name.text = viewModel.you.name
        course_you.text = viewModel.you.courseOfStudy
        if(viewModel.you.yearOfStudy == 5) year_you.text = "Graduate"
        else year_you.text = "Year ${viewModel.you.yearOfStudy}"
        number_of_buddies_you.text = "${viewModel.you.buddies.size} buddies"

        /**set up recycler views**/
        val layoutManager_for_mods = LinearLayoutManager(this)
        val layoutManager_for_comms = LinearLayoutManager(this)
        layoutManager_for_comms.orientation = LinearLayoutManager.HORIZONTAL
        layoutManager_for_mods.orientation = LinearLayoutManager.HORIZONTAL
        modules_taking.layoutManager = layoutManager_for_mods
        communities_in.layoutManager = layoutManager_for_comms

        var modulesAdapter = ModulesAdapter(viewModel.you.modules, this)
        modulesAdapter.setClickListener(object : ModulesAdapter.ItemClickListener {
            override fun onItemClick(view: View?, position: Int) {

            }
        })
        modules_taking.adapter = modulesAdapter

        /**All other communities**/
        var communityAdapter = CommunityItemAdapter(viewModel.otherCommunities, this, User())
        communities_in.adapter = communityAdapter
        /****/
    }
}
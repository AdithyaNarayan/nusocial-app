package com.teamnusocial.nusocial.ui.you

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.github.ybq.android.spinkit.SpinKitView
import com.squareup.picasso.Picasso
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.User
import com.teamnusocial.nusocial.data.repository.SocialToolsRepository
import com.teamnusocial.nusocial.data.repository.UserRepository
import com.teamnusocial.nusocial.ui.buddymatch.MaskTransformation
import com.teamnusocial.nusocial.ui.buddymatch.ModulesAdapter
import com.teamnusocial.nusocial.ui.community.SingleCommunityActivity
import com.teamnusocial.nusocial.utils.FirestoreUtils
import kotlinx.android.synthetic.main.fragment_you.*
import kotlinx.coroutines.launch

class OtherUserActivity : AppCompatActivity() {
    private lateinit var viewModel: YouViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_user)

        /**fetch data**/
        viewModel = ViewModelProvider(this).get(YouViewModel::class.java)
        val userID = intent.getStringExtra("USER_ID")
        /**set up toolbar**/
        val toolBar: Toolbar = findViewById(R.id.toolbar_other_user)
        toolBar.title = "User"
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
            .centerCrop()
            .transform(MaskTransformation(this, R.drawable.more_info_img_frame))
            .fit()
            .into(you_image)
        you_name.text = viewModel.you.name
        course_you.text = viewModel.you.courseOfStudy
        if(viewModel.you.yearOfStudy == 5) year_you.text = "Graduate"
        else year_you.text = "Year ${viewModel.you.yearOfStudy}"
        number_of_buddies_you.text = "${viewModel.you.buddies.size} buddies"

        /**set up recycler views**/
        modules_taking.layoutManager = GridLayoutManager(this, 4)
        communities_in.layoutManager = GridLayoutManager(this, 1)

        var modulesAdapter = ModulesAdapter(viewModel.you.modules, this)
        modulesAdapter.setClickListener(object : ModulesAdapter.ItemClickListener {
            override fun onItemClick(view: View?, position: Int) {

            }
        })
        modules_taking.adapter = modulesAdapter

        /**All other communities**/
        var communityAdapter = CommunityItemAdapter(viewModel.otherCommunities, this)
        communityAdapter.setClickListener(object : CommunityItemAdapter.ItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
            }
        })
        communities_in.adapter = communityAdapter
        /****/
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
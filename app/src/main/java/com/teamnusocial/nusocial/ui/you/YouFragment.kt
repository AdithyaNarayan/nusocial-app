package com.teamnusocial.nusocial.ui.you

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.lifecycle.lifecycleScope
import com.google.firestore.v1.FirestoreGrpc
import com.squareup.picasso.Picasso
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.User
import com.teamnusocial.nusocial.data.repository.UserRepository
import com.teamnusocial.nusocial.ui.auth.SignInActivity
import com.teamnusocial.nusocial.ui.buddymatch.BuddyMatchViewModel
import com.teamnusocial.nusocial.ui.buddymatch.MaskTransformation
import com.teamnusocial.nusocial.utils.FirebaseAuthUtils
import com.teamnusocial.nusocial.utils.FirestoreUtils
import kotlinx.android.synthetic.main.activity_buddy_profile.*
import kotlinx.android.synthetic.main.fragment_buddymatch.*
import kotlinx.android.synthetic.main.fragment_you.*
import kotlinx.android.synthetic.main.matched_module_child.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class YouFragment : Fragment() {

    companion object {
        fun newInstance() = YouFragment()
    }

    private lateinit var viewModel: YouViewModel
    private lateinit var inflater: LayoutInflater
    private lateinit var container: ViewGroup
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_you, container, false)
        this.container = container!!
        this.inflater = inflater
        viewModel = ViewModelProvider(requireActivity()).get(YouViewModel::class.java)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.you = UserRepository(FirestoreUtils()).getCurrentUserAsUser()
            updateUI()
        }
    }

    private fun updateUI() {
        val updateInfoButton = update_info_button
        val logOutButton = log_out_button

        logOutButton.setOnClickListener {
            FirebaseAuthUtils().logOut()
            var intent = Intent(context, SignInActivity::class.java)
            intent.putExtra("USER_NAME", viewModel.you.name)
            intent.putExtra("USER_YEAR", viewModel.you.yearOfStudy)
            intent.putExtra("USER_COURSE", viewModel.you.courseOfStudy)
            startActivity(intent)

        }
        updateInfoButton.setOnClickListener {
            var intent = Intent(context, UpdateInfoActivity::class.java)
            startActivity(intent)
        }

        val image = you_img
        Picasso
            .get()
            .load(viewModel.you.profilePicturePath)
            .fit()
            .into(image)


        /**all modules**/
        var cardView = modules_taking
        var id: Int = 0
        var linearLayoutVertical = LinearLayout(context)
        val numberOfModules = viewModel.you.modules.size
        var numberOfRows = numberOfModules / 4
        numberOfRows += if (numberOfModules % 4 > 0) 1 else 0
        linearLayoutVertical.orientation = LinearLayout.VERTICAL
        if (numberOfRows > 1 || numberOfModules == 4) {
            for (y in 1..numberOfRows) {
                var linearLayoutHorizontal = LinearLayout(context)
                linearLayoutHorizontal.orientation = LinearLayout.HORIZONTAL
                for (x in 0..3) {
                    val module: View =
                        inflater.inflate(R.layout.matched_module_child, container , false)
                    module.module_name.text = viewModel.you.modules[id++].moduleCode
                    linearLayoutHorizontal.addView(module)
                }
                linearLayoutVertical.addView(linearLayoutHorizontal)
            }
            var linearLayoutHorizontal = LinearLayout(context)
            linearLayoutHorizontal.orientation = LinearLayout.HORIZONTAL
            for (x in 1..numberOfModules % 4) {
                val module: View = inflater.inflate(R.layout.matched_module_child, container, false)
                module.module_name.text = viewModel.you.modules[id++].moduleCode
                linearLayoutHorizontal.addView(module)
            }
            linearLayoutVertical.addView(linearLayoutHorizontal)
        } else if (numberOfModules < 4) {
            var linearLayoutHorizontal = LinearLayout(context)
            linearLayoutHorizontal.orientation = LinearLayout.HORIZONTAL
            for (x in 1..numberOfModules) {
                val module: View = inflater.inflate(R.layout.matched_module_child, container, false)
                module.module_name.text = viewModel.you.modules[id++].moduleCode
                linearLayoutHorizontal.addView(module)
            }
            linearLayoutVertical.addView(linearLayoutHorizontal)
        }
        cardView.addView(linearLayoutVertical)
        /****/

    }
}
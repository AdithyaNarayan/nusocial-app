package com.teamnusocial.nusocial.ui.you

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.github.ybq.android.spinkit.SpinKitView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.Community
import com.teamnusocial.nusocial.data.model.Module
import com.teamnusocial.nusocial.data.model.Post
import com.teamnusocial.nusocial.data.repository.UserRepository
import com.teamnusocial.nusocial.ui.auth.SignInActivity
import com.teamnusocial.nusocial.ui.buddymatch.MaskTransformation
import com.teamnusocial.nusocial.ui.buddymatch.ModulesAdapter
import com.teamnusocial.nusocial.ui.community.SingleCommunityActivity
import com.teamnusocial.nusocial.ui.messages.MessageChatActivity
import com.teamnusocial.nusocial.ui.messages.MessagesRecyclerViewAdapter
import com.teamnusocial.nusocial.utils.FirebaseAuthUtils
import com.teamnusocial.nusocial.utils.FirestoreUtils
import com.teamnusocial.nusocial.utils.getTimeAgo
import kotlinx.android.synthetic.main.fragment_buddymatch.*
import kotlinx.android.synthetic.main.fragment_you.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Collections.rotate


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
        modules_taking.layoutManager = GridLayoutManager(context, 4)
        communities_in.layoutManager = GridLayoutManager(context, 2)
        val spin_kit_you = activity?.findViewById<SpinKitView>(R.id.spin_kit)!!
        lifecycleScope.launch {
            spin_kit_you.visibility = View.VISIBLE
            viewModel.you = UserRepository(FirestoreUtils()).getCurrentUserAsUser()
            updateUI()
            spin_kit_you.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) if (resultCode == Activity.RESULT_OK) {
            val selectedImage: Uri = data!!.data!!
            val filePath =
                FirebaseStorage.getInstance().getReference("/images/${viewModel.you.uid}")
            filePath.putFile(selectedImage)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        filePath.downloadUrl.addOnCompleteListener { url ->
                            FirebaseFirestore.getInstance().collection("users")
                                .document(viewModel.you.uid)
                                .update("profilePicturePath", url.result.toString())
                            CoroutineScope(Dispatchers.IO).launch {

                                viewModel.you =
                                    UserRepository(FirestoreUtils()).getCurrentUserAsUser()
                                withContext(Dispatchers.Main) { updateUI() }
                            }
                        }


                    }
                }

        }
    }

    private fun updateUI() {

        you_name.text = viewModel.you.name

        course_you.text = viewModel.you.courseOfStudy

        if(viewModel.you.yearOfStudy == 5) year_you.text = "Graduate"
        else year_you.text = "Year ${viewModel.you.yearOfStudy}"

        number_of_buddies_you.text = "${viewModel.you.buddies.size} buddies"
        you_image.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, 1)
        }

        val updateInfoButton = update_info_button
        var m_Text = ""
        val listOfActions = arrayOf("Choose an action","Log out")
        val arrayAdapter = object: ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, listOfActions) {
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                var res =  super.getDropDownView(position, convertView, parent) as TextView
                res.setTextColor(resources.getColor(R.color.black, resources.newTheme()))
                if(position == 0) {
                    res.setBackgroundResource(R.drawable.centre_background)
                }
                return res
            }
        }
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        general_dropdown.adapter = arrayAdapter

        val rotateAnim = RotateAnimation(0F, 180F, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotateAnim.duration = 300
        rotateAnim.setInterpolator(LinearInterpolator())
        general_dropdown.setSpinnerEventsListener(object :
            CustomSpinner.OnSpinnerEventsListener {
            override fun onSpinnerOpened() {
                general_dropdown.isSelected = true
                general_dropdown.startAnimation(rotateAnim)
            }

            override fun onSpinnerClosed() {
                general_dropdown.isSelected = false
                general_dropdown.startAnimation(rotateAnim)
            }
        })
        general_dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(listOfActions.get(position)) {
                    "Log out" -> {
                            FirebaseAuthUtils().logOut()
                            var intent = Intent(context, SignInActivity::class.java)
                            startActivity(intent)
                    }
                    else -> {}
                }
            }

        }
        add_module_button.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle("Add Modules")
            val input = EditText(context)
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)
            builder.setPositiveButton(
                "OK"
            ) { dialog, which ->
                m_Text = input.text.toString()
                var result = m_Text.split(",")
                var realRes = mutableListOf<Module>()
                for (string in result) {
                    val moduleCode = string.replace("\\s".toRegex(), "")
                    realRes.add(Module(moduleCode, "", listOf()))
                }
                updateModules(realRes)


            }
            builder.setNegativeButton("Cancel",
                DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })
            builder.show()
        }
        updateInfoButton.setOnClickListener {
            var intent = Intent(context, UpdateInfoActivity::class.java)
            intent.putExtra("USER_NAME", viewModel.you.name)
            intent.putExtra("USER_YEAR", viewModel.you.yearOfStudy)
            intent.putExtra("USER_COURSE", viewModel.you.courseOfStudy)
            intent.putExtra("USER_ABOUT", viewModel.you.about)
            startActivity(intent)
        }

        Picasso
            .get()
            .load(viewModel.you.profilePicturePath)
            .centerCrop()
            .transform(MaskTransformation(requireContext(), R.drawable.more_info_img_frame))
            .fit()
            .into(you_image)


        /**all modules**/
        var modulesAdapter = ModulesAdapter(viewModel.you.modules.toTypedArray(), requireContext())
        modulesAdapter.setClickListener(object : ModulesAdapter.ItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                val intent = Intent(requireContext(), SingleCommunityActivity::class.java)
                var exist = false
                for(community in viewModel.you.communities) {
                    if(community.module.moduleCode.equals(viewModel.you.modules[position].moduleCode)) {
                        intent.putExtra("COMMUNITY_DATA", community)
                        exist = true
                        break
                    }
                }
                if(!exist) {
                    var toast = Toast.makeText(context, "No such community !", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, 0)
                    toast.show()
                } else {
                    startActivity(intent)
                }
            }
        })
        modules_taking.adapter = modulesAdapter
        viewModel.you.communities.add(Community("","CS1010", mutableListOf(), mutableListOf(),
            Module("CS1010","Prog", listOf()),"https://www.comp.nus.edu.sg/~cs1010/labs/2017s1/lab6/img/gas_stations.jpg",
            mutableListOf()
        ))
        var communityAdapter = CommunityItemAdapter(viewModel.you.communities.toTypedArray(), requireContext())
        communityAdapter.setClickListener(object : CommunityItemAdapter.ItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                val intent = Intent(requireContext(), SingleCommunityActivity::class.java)
                intent.putExtra("COMMUNITY_DATA", viewModel.you.communities[position])
                startActivity(intent)
            }
        })
        communities_in.adapter = communityAdapter
        /****/
    }

    private fun updateModules(value: MutableList<Module>) {
        CoroutineScope(Dispatchers.IO).launch {
            UserRepository(FirestoreUtils()).updateModules(value)
            viewModel.you = UserRepository(FirestoreUtils()).getCurrentUserAsUser()
            withContext(Dispatchers.Main) {
                modules_taking.adapter = ModulesAdapter(viewModel.you.modules.toTypedArray(), requireContext())
            }
        }
    }
}
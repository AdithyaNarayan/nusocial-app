package com.teamnusocial.nusocial.ui.you

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.github.ybq.android.spinkit.SpinKitView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.Community
import com.teamnusocial.nusocial.data.model.Module
import com.teamnusocial.nusocial.data.repository.SocialToolsRepository
import com.teamnusocial.nusocial.data.repository.UserRepository
import com.teamnusocial.nusocial.ui.auth.SignInActivity
import com.teamnusocial.nusocial.ui.buddymatch.MaskTransformation
import com.teamnusocial.nusocial.ui.buddymatch.ModulesAdapter
import com.teamnusocial.nusocial.ui.community.SingleCommunityActivity
import com.teamnusocial.nusocial.utils.CustomTextViewDialog
import com.teamnusocial.nusocial.utils.FirebaseAuthUtils
import com.teamnusocial.nusocial.utils.FirestoreUtils
import kotlinx.android.synthetic.main.fragment_you.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await


class YouFragment : Fragment() {

    companion object {
        fun newInstance() = YouFragment()
    }

    private lateinit var viewModel: YouViewModel
    private lateinit var inflater: LayoutInflater
    private lateinit var container: ViewGroup
    private var socialToolRepo = SocialToolsRepository(FirestoreUtils())
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
        communities_in.layoutManager = GridLayoutManager(context, 1)
        updateInfo()
    }

    fun updateInfo() {
        val spin_kit_you = activity?.findViewById<SpinKitView>(R.id.spin_kit)!!
        val bg_cover = activity?.findViewById<CardView>(R.id.loading_cover)!!
        lifecycleScope.launch {
            spin_kit_you.visibility = View.VISIBLE
            bg_cover.visibility = View.VISIBLE
           // viewModel.allModulesAvailable = viewModel.allModulesOffered()
            viewModel.you = UserRepository(FirestoreUtils()).getCurrentUserAsUser()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
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
        } else if(requestCode == 0 && resultCode == Activity.RESULT_OK) {
            viewModel.you = data!!.getParcelableExtra("USER_DATA")
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.allCommunitites = SocialToolsRepository(FirestoreUtils()).getAllCommunities()
                viewModel.allYourCommunities = viewModel.allCommunitites.filter { comm -> viewModel.you.communities.contains(comm.id) }.toMutableList()
                viewModel.moduleCommunities.clear()
                viewModel.otherCommunities.clear()
                for(comm in viewModel.allYourCommunities) {
                    if(comm.module.moduleCode.equals("")) {
                        //Log.d("TEST_MOD","here is ${comm.module.moduleCode} inside others")
                        viewModel.otherCommunities.add(comm)
                    } else {
                        //Log.d("TEST_MOD","here is ${comm.module.moduleCode} inside modules")
                        viewModel.moduleCommunities.add(comm)
                    }
                }
                withContext(Dispatchers.Main) {
                    updateUI()
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
        setUpDropDown()

        add_module_button.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            val dialog_view = inflater.inflate(R.layout.custom_dialog, null)
            builder.setView(dialog_view)
            dialog_view.findViewById<TextView>(R.id.dialog_title).text = "Add modules"
            val input = dialog_view.findViewById<EditText>(R.id.edit_comment_input)
            val button_confirm = dialog_view.findViewById<Button>(R.id.confirm_edit_comment_button)
            val button_cancel = dialog_view.findViewById<Button>(R.id.cancel_edit_comment_button)
            val dialog = builder.create()
            if(dialog.window != null) {
                dialog.window!!.attributes.windowAnimations = R.style.dialog_animation_fade
            }
            button_confirm.setOnClickListener {
                m_Text = input.text.toString()
                var result = m_Text.split(",")
                var realRes = mutableListOf<Module>()
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.allModulesAvailable = viewModel.allModulesOffered()
                    for (string in result) {
                        var isValid = false
                        val moduleCode = string.replace("\\s".toRegex(), "").toUpperCase()
                        for(module in viewModel.allModulesAvailable) {
                            if(moduleCode.equals(module.moduleCode)) {
                                isValid = true
                                realRes.add(module)
                                break
                            }
                        }
                        if(!isValid) {
                            withContext(Dispatchers.Main) {
                                var toast = Toast.makeText(
                                    context,
                                    "Some of the input modules were invalid",
                                    Toast.LENGTH_SHORT
                                )
                                toast.setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, 0)
                                toast.show()
                            }
                        }
                    }
                    if(realRes.size > 0) {
                        updateModules(realRes)
                        updateCommunity(realRes)
                    }
                    withContext(Dispatchers.Main) {
                        dialog.dismiss()
                    }
                }
            }
            button_cancel.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
        updateInfoButton.setOnClickListener {
            var intent = Intent(context, UpdateInfoActivity::class.java)
            intent.putExtra("USER_DATA", viewModel.you)
            startActivity(intent)
        }

        create_new_community.setOnClickListener {
            val intent = Intent(context, CreateNewCommunityActivity::class.java)
            intent.putExtra("USER_DATA", viewModel.you)
            startActivityForResult(intent, 0)
        }

        Picasso
            .get()
            .load(viewModel.you.profilePicturePath)
            .centerCrop()
            .transform(MaskTransformation(requireContext(), R.drawable.more_info_img_frame))
            .fit()
            .into(you_image)


        /**all modules**/
        var modulesAdapter = ModulesAdapter(viewModel.you.modules, requireContext())
        modulesAdapter.setClickListener(object : ModulesAdapter.ItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                val intent = Intent(requireContext(), SingleCommunityActivity::class.java)
                var exist = false
                for(community in viewModel.moduleCommunities) {
                    if(community.module.moduleCode.equals(viewModel.you.modules[position].moduleCode)) {
                        //intent.putExtra("COMM_TIME", getTimeToDelete(community.id))
                        intent.putExtra("COMMUNITY_DATA", community)
                        intent.putExtra("USER_DATA", viewModel.you)
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

        /**All other communities**/
        var communityAdapter = CommunityItemAdapter(viewModel.otherCommunities, requireContext())
        communityAdapter.setClickListener(object : CommunityItemAdapter.ItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                val intent = Intent(requireContext(), SingleCommunityActivity::class.java)
                //intent.putExtra("COMM_TIME", getTimeToDelete(viewModel.otherCommunities[position].id))
                intent.putExtra("COMMUNITY_DATA", viewModel.otherCommunities[position])
                intent.putExtra("USER_DATA", viewModel.you)
                startActivity(intent)
            }
        })
        communities_in.adapter = communityAdapter
        /****/
    }

    private fun updateModules(value: MutableList<Module>) {
        CoroutineScope(Dispatchers.IO).launch {
            UserRepository(FirestoreUtils()).updateModules(value)
        }
    }
    private fun updateCommunity(value: MutableList<Module>) {
        val spin_kit_you = activity?.findViewById<SpinKitView>(R.id.spin_kit)!!
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                spin_kit_you.visibility = View.VISIBLE
            }
            for(module in value) {
                if(!communityExits(module.moduleCode)) {
                    socialToolRepo.addCommunity(
                        Community(
                            "", module.moduleCode,
                            mutableListOf(viewModel.you.uid),module,"https://miro.medium.com/max/2580/1*4B6JM5TCU8hckm4WKYxPLQ.jpeg",
                            mutableListOf(),"",false
                        ),
                        viewModel.you.uid
                    ).await()
                } else {
                    var comm = viewModel.allCommunitites.find { comm -> comm.module.moduleCode.equals(module.moduleCode) }
                    if(comm != null) {
                        socialToolRepo.addMemberToCommunity(viewModel.you.uid, comm.id).await()
                    } else {
                        Log.d("ERROR_ADD_MOD","COMM DOES NOT EXIST")
                    }
                }
            }
            viewModel.you = UserRepository(FirestoreUtils()).getCurrentUserAsUser()
            viewModel.allCommunitites = SocialToolsRepository(FirestoreUtils()).getAllCommunities()
            viewModel.allYourCommunities = viewModel.allCommunitites.filter { comm -> viewModel.you.communities.contains(comm.id) }.toMutableList()
            //Log.d("TEST_MOD", "SIze of allCommm ${viewModel.allCommunitites.size} and size of allYourComm ${viewModel.allYourCommunities.size}")
            viewModel.otherCommunities.clear()
            viewModel.moduleCommunities.clear()
            for(comm in viewModel.allYourCommunities) {
                if(comm.module.moduleCode.equals("")) {
                    //Log.d("TEST_MOD","here is ${comm.module.moduleCode} inside others")
                    viewModel.otherCommunities.add(comm)
                } else {
                    //Log.d("TEST_MOD","here is ${comm.module.moduleCode} inside modules")
                    viewModel.moduleCommunities.add(comm)
                }
            }
            withContext(Dispatchers.Main) {
                modules_taking.adapter = ModulesAdapter(viewModel.you.modules, requireContext())
                updateUI()
                spin_kit_you.visibility = View.GONE
            }
        }
    }
    fun setUpDropDown() {
        val listOfActions = arrayOf("Choose an action","Log out", "Show ID")
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
                    "Show ID" -> {
                        val textViewDialog = CustomTextViewDialog(requireContext(), viewModel.you.uid, "Your ID")
                        textViewDialog.show()
                    }
                    else -> {

                    }
                }
            }

        }
    }
    fun communityExits(moduleCode: String): Boolean {
        for(community in viewModel.allCommunitites) {
            if(community.module.moduleCode.equals(moduleCode)) return true
        }
        return false
    }
}
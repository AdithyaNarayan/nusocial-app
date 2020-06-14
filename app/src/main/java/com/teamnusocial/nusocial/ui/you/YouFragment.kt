package com.teamnusocial.nusocial.ui.you

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.Module
import com.teamnusocial.nusocial.data.repository.UserRepository
import com.teamnusocial.nusocial.ui.auth.SignInActivity
import com.teamnusocial.nusocial.utils.FirebaseAuthUtils
import com.teamnusocial.nusocial.utils.FirestoreUtils
import kotlinx.android.synthetic.main.fragment_you.*
import kotlinx.android.synthetic.main.matched_module_child.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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
        you_img.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, 1)
        }
        youName.text = viewModel.you.name
        val updateInfoButton = update_info_button
        val logOutButton = log_out_button
        var m_Text = ""
        logOutButton.setOnClickListener {
            FirebaseAuthUtils().logOut()
            var intent = Intent(context, SignInActivity::class.java)
            intent.putExtra("USER_NAME", viewModel.you.name)
            intent.putExtra("USER_YEAR", viewModel.you.yearOfStudy)
            intent.putExtra("USER_COURSE", viewModel.you.courseOfStudy)
            startActivity(intent)

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
        var numberOfRows = numberOfModules / 3
        numberOfRows += if (numberOfModules % 3 > 0) 1 else 0
        linearLayoutVertical.orientation = LinearLayout.VERTICAL
        if (numberOfRows > 1 || numberOfModules % 3 == 0) {
            for (y in 1 until numberOfRows) {
                val linearLayoutHorizontal = LinearLayout(context)
                linearLayoutHorizontal.orientation = LinearLayout.HORIZONTAL
                for (x in 0..2) {
                    val module: View =
                        inflater.inflate(R.layout.matched_module_child, container, false)
                    module.module_name.text = viewModel.you.modules[id++].moduleCode
                    linearLayoutHorizontal.addView(module)
                }
                linearLayoutVertical.addView(linearLayoutHorizontal)
            }
            var linearLayoutHorizontal = LinearLayout(context)
            linearLayoutHorizontal.orientation = LinearLayout.HORIZONTAL
            var m = if (numberOfModules % 3 == 0) 3 else numberOfModules % 3
            if (numberOfModules == 0) m = 0
            for (x in 1..m) {
                val module: View = inflater.inflate(R.layout.matched_module_child, container, false)
                module.module_name.text = viewModel.you.modules[id++].moduleCode
                linearLayoutHorizontal.addView(module)
            }
            linearLayoutVertical.addView(linearLayoutHorizontal)
        } else if (numberOfModules < 3) {
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

    private fun updateModules(value: MutableList<Module>) {
        CoroutineScope(Dispatchers.IO).launch {
            UserRepository(FirestoreUtils()).updateModules(value)
            viewModel.you = UserRepository(FirestoreUtils()).getCurrentUserAsUser()
            withContext(Dispatchers.Main) {
                var cardView = modules_taking
                var id: Int = 0
                var linearLayoutVertical = LinearLayout(context)
                val numberOfModules = viewModel.you.modules.size
                var numberOfRows = numberOfModules / 3
                numberOfRows += if (numberOfModules % 3 > 0) 1 else 0
                linearLayoutVertical.orientation = LinearLayout.VERTICAL
                if (numberOfRows > 1 || numberOfModules % 3 == 0) {
                    for (y in 1 until numberOfRows) {
                        val linearLayoutHorizontal = LinearLayout(context)
                        linearLayoutHorizontal.orientation = LinearLayout.HORIZONTAL
                        for (x in 0..2) {
                            val module: View =
                                inflater.inflate(R.layout.matched_module_child, container, false)
                            module.module_name.text = viewModel.you.modules[id++].moduleCode
                            linearLayoutHorizontal.addView(module)
                        }
                        linearLayoutVertical.addView(linearLayoutHorizontal)
                    }
                    var linearLayoutHorizontal = LinearLayout(context)
                    linearLayoutHorizontal.orientation = LinearLayout.HORIZONTAL
                    val m = if (numberOfModules % 3 == 0) 3 else numberOfModules % 3
                    for (x in 1..m) {
                        val module: View =
                            inflater.inflate(R.layout.matched_module_child, container, false)
                        module.module_name.text = viewModel.you.modules[id++].moduleCode
                        linearLayoutHorizontal.addView(module)
                    }
                    linearLayoutVertical.addView(linearLayoutHorizontal)
                } else if (numberOfModules < 3) {
                    var linearLayoutHorizontal = LinearLayout(context)
                    linearLayoutHorizontal.orientation = LinearLayout.HORIZONTAL
                    for (x in 1..numberOfModules) {
                        val module: View =
                            inflater.inflate(R.layout.matched_module_child, container, false)
                        module.module_name.text = viewModel.you.modules[id++].moduleCode
                        linearLayoutHorizontal.addView(module)
                    }
                    linearLayoutVertical.addView(linearLayoutHorizontal)
                }
                cardView.addView(linearLayoutVertical)
            }
        }
    }
}
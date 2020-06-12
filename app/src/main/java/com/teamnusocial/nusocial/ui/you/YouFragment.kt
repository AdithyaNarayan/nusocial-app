package com.teamnusocial.nusocial.ui.you

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
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
        var m_Text = ""
        logOutButton.setOnClickListener {
            FirebaseAuthUtils().logOut()
            var intent = Intent(context, SignInActivity::class.java)
            intent.putExtra("USER_NAME", viewModel.you.name)
            intent.putExtra("USER_YEAR", viewModel.you.yearOfStudy)
            intent.putExtra("USER_COURSE", viewModel.you.courseOfStudy)
            startActivity(intent)

        }
        add_module_button.setOnClickListener{
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle("Add Modules")
            val input = EditText(context)
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)
            builder.setPositiveButton("OK",
                DialogInterface.OnClickListener { dialog, which ->
                    m_Text = input.text.toString()
                    var result = m_Text.split(",")
                    var realRes = mutableListOf<Module>()
                    for(string in result) {
                        val moduleCode = string.replace("\\s".toRegex(), "")
                        realRes.add(Module(moduleCode, "", listOf()))
                    }
                    updateModules(realRes)
                })
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
    fun updateModules(value: MutableList<Module>) {
        CoroutineScope(Dispatchers.IO).launch {
            UserRepository(FirestoreUtils()).updateModules(value)
        }
    }
}
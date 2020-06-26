package com.teamnusocial.nusocial.ui.you

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.teamnusocial.nusocial.HomeActivity
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.repository.UserRepository
import com.teamnusocial.nusocial.ui.you.CustomSpinner.OnSpinnerEventsListener
import com.teamnusocial.nusocial.utils.FirestoreUtils
import kotlinx.android.synthetic.main.activity_update_info.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class UpdateInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_info)

        /**top bar**/
        val toolBar: Toolbar = findViewById(R.id.toolbarUpdateInfo)
        toolBar.title = "Update personal info"
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val curr_name = intent.getStringExtra("USER_NAME")
        val curr_course = intent.getStringExtra("USER_COURSE")
        val curr_year = intent.getIntExtra("USER_YEAR", 0)
        val curr_about = intent.getStringExtra("USER_ABOUT")
        if(curr_name != "" && curr_name != null) {
            changeName.hint = curr_name
        }
        if(curr_course != "" && curr_course != null) {
            changeCourse.hint = curr_course
        }
        if(curr_about != "" && curr_about != null) {
            about_input.hint = curr_about
        }
        var new_year: Int = 0


        val listOfYear = arrayOf("1","2","3","4","Graduate")
        val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listOfYear)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        change_year_dropdown.adapter = arrayAdapter

        change_year_dropdown.setSpinnerEventsListener(object : OnSpinnerEventsListener {
            override fun onSpinnerOpened() {
                change_year_dropdown.isSelected = true
            }

            override fun onSpinnerClosed() {
                change_year_dropdown.isSelected = false
            }
        })
        change_year_dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(listOfYear.get(position)) {
                    "1" -> new_year = 1
                    "2" -> new_year = 2
                    "3" -> new_year = 3
                    "4" -> new_year = 4
                    else -> new_year = 5
                }
            }

        }
        update_button.setOnClickListener {
            if (changeName.text.toString() != curr_name && changeName.text.toString() != "") {
                updateStringField("name", changeName.text.toString())
            }
            if (changeCourse.text.toString() != curr_course && changeCourse.text.toString() != "") {
                updateStringField("courseOfStudy", changeCourse.text.toString())
            }
            if (new_year != 0 && new_year != curr_year) {
                updateNumberField(
                    "yearOfStudy",
                    new_year
                )
            }
            if(about_input.text.toString() != curr_about) {
                updateStringField("about", about_input.text.toString())
            }
            val intent = Intent(this,HomeActivity::class.java)
            intent.putExtra("FROM_UPDATE", "update")
            startActivity(intent)
        }
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

    fun updateStringField(field: String, value: String) {
        CoroutineScope(Dispatchers.IO).launch {
            UserRepository(FirestoreUtils()).updateStringField(value, field)
        }
    }

    fun updateNumberField(field: String, value: Number) {
        CoroutineScope(Dispatchers.IO).launch {
            UserRepository(FirestoreUtils()).updateNumberField(value, field)
        }
    }
}


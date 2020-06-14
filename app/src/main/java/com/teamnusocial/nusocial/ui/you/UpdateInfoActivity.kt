package com.teamnusocial.nusocial.ui.you

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.repository.UserRepository
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
        setSupportActionBar(toolbarUpdateInfo)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val curr_name = intent.getStringExtra("USER_NAME")
        val curr_course = intent.getStringExtra("USER_COURSE")
        val curr_year = intent.getIntExtra("USER_YEAR", -1)

        changeName.hint = curr_name
        changeCourse.hint = curr_course
        changeYearOfStudy.hint = curr_year.toString()

        update_button.setOnClickListener {
            if (changeName.text.toString() != curr_name || changeName.text.toString() != "") {
                updateStringField("name", changeName.text.toString())
            }
            if (changeCourse.text.toString() != curr_course || changeCourse.text.toString() != "") {
                updateStringField("courseOfStudy", changeCourse.text.toString())
            }
            /*if (changeYearOfStudy.text.toString() != curr_year.toString() || changeYearOfStudy.text.toString() != "-1") {
                updateNumberField(
                    "yearOfStudy",
                    changeYearOfStudy.text.toString()[0].toInt() % 5 + 1
                )
            }*/
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


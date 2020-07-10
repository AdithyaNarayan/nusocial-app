package com.teamnusocial.nusocial.ui.community

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.teamnusocial.nusocial.R
import com.teamnusocial.nusocial.data.model.Community
import com.teamnusocial.nusocial.data.model.User
import kotlinx.android.synthetic.main.activity_new_comm.*

class NewCommActivity : AppCompatActivity() {
    private lateinit var commData: Community
    private lateinit var you: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_comm)
        /**fetch data**/
        commData = intent.getParcelableExtra("COMM_DATA")
        you = intent.getParcelableExtra("USER_DATA")
        /**top bar**/
        val toolBar: Toolbar = findViewById(R.id.tool_new_community)
        toolBar.title = commData.name
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if(commData.isPublic) {
            join_or_ask_to_join_button.text = "Join"
            join_or_ask_to_join_button.setOnClickListener {
                joinPublicComm()
                val intent = Intent(this@NewCommActivity, SingleCommunityActivity::class.java)
                intent.putExtra("COMM_DATA", commData)
                intent.putExtra("USER_DATA", you)
                startActivity(intent)
            }
        } else {
            join_or_ask_to_join_button.text = "Ask to join"
            join_or_ask_to_join_button.setOnClickListener {
                joinPrivateComm()
                var toast = Toast.makeText(this@NewCommActivity, "Request sent", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, 0)
                toast.show()
                this@NewCommActivity.finish()
            }
        }

    }
    fun joinPublicComm() {
        
    }
    fun joinPrivateComm() {

    }
}
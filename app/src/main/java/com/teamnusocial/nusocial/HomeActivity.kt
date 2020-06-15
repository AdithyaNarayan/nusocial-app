package com.teamnusocial.nusocial

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.teamnusocial.nusocial.ui.you.YouFragment
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_buddymatch,
                R.id.navigation_broadcast,
                R.id.navigation_community,
                R.id.navigation_messages,
                R.id.navigation_you
            )
        )
        setupWithNavController(topBar, navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        val isFromUpdate = intent.getStringExtra("FROM_UPDATE")
        if(isFromUpdate !=null && isFromUpdate.equals("update")) {
            navController.navigate(R.id.navigation_you)
        }
        if (savedInstanceState == null) {
        }

    }

}
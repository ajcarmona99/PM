package com.example.pm_sfp

import com.example.pm_sfp.interfaces.AudioScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.pm_sfp.interfaces.AppNav
import com.example.pm_sfp.interfaces.CameraScreen
import com.example.pm_sfp.interfaces.HomeScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            AppNav(navController)
        }
    }
}


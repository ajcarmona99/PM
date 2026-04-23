package com.example.pm_sfp.interfaces

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable


object Routes{
    const val HOME = "home"
    const val AUDIO = "audio"
    const val CAMERA = "camera"
    const val IMAGE = "image"
    const val VIDEO = "video"
}

@Composable
fun AppNav(navController: NavHostController){
    NavHost(navController = navController, startDestination = Routes.HOME){
        composable(Routes.HOME) {HomeScreen(navController)}
        composable(Routes.AUDIO) {AudioScreen()}
        composable(Routes.CAMERA) {CameraScreen()}
        composable(Routes.IMAGE) {ImageScreen()}
        composable(Routes.VIDEO) {VideoScreen()}
    }
}
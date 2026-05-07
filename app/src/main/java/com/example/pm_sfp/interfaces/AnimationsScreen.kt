package com.example.pm_sfp.interfaces

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp


//APIS de animaciones
@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)


@Composable

fun AnimationsScreen(){
    //Booleano que si es false = caja pequeña, azul, sin rotacion
    //True caja grande roja rotada y con escala

    var expanded by remember { mutableStateOf(false) }

    //Controla si el texto animado aparecede o desaparece
    var visible by remember { mutableStateOf(false) }

    //Animaciones que nunca paran
    val infiniteTransition = rememberInfiniteTransition()

    //Crea una animacion entre 0.8 y 1.2 segundos, que haga 0.8-1-1.2
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        )
    )

    //Rojo o azul
    val color by animateColorAsState(
        if (expanded) Color.Red else Color.Blue,
        label = ""
    )

    //variable tamaño
    val size by animateDpAsState(
        if (expanded) 150.dp else 80.dp,
        label = ""
    )

    //variable transicion

    val transition = updateTransition(
        targetState = expanded,
        label = ""
    )

    val rotation by transition.animateFloat(label = "") {
        if (it) 360f else 0f
    }

    val scale by transition.animateFloat(label = "") {
        if (it) 1.5f else 1f
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Animaciones")
                }
            )
        }
    ) {padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Button(onClick = {
                expanded = !expanded
            }) {Text("Animar la caja") }

            Box(
                modifier = Modifier.size(size)
                    .graphicsLayer{
                        rotationZ = rotation
                        scaleX = scale
                        scaleY = scale
                    }
                    .background(color)
            )

            Button(
                onClick = {
                    visible = !visible
                }
            ) { Text("Mostrar/Ocultar")}

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Text(
                    "Hola"
                )
            }

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .scale(pulse)
                    .background(
                        Color.Magenta,
                        shape = CircleShape
                    )
            )
        }
    }
}
package com.s2b.hayagriva

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HayagrivaApp()
        }
    }
}

@Composable
fun HayagrivaApp() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            HayagrivaThinkingScreen()
        }
    }
}

@Composable
fun HayagrivaThinkingScreen() {

    val infiniteTransition = rememberInfiniteTransition(
        label = "hayagriva-thinking"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse-alpha"
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        /*
         * Temporary AI thinking indicator.
         *
         * Later this area can be replaced with the
         * Hayagriva animated visual/avatar supplied by the project.
         */

        Box(
            modifier = Modifier
                .size(180.dp)
                .alpha(pulseAlpha)
                .background(
                    color = Color(0xFF101820),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "H",
                color = Color(0xFFFFC107),
                style = MaterialTheme.typography.displayLarge
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "S2B HAYAGRIVA",
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Listening...",
            color = Color.LightGray,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

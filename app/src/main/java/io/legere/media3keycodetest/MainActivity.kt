package io.legere.media3keycodetest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.legere.media3keycodetest.ui.theme.Media3KeyCodeTestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Media3KeyCodeTestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Text(
                        text = "The App doesn't do anything, run the tests",
                        modifier = Modifier.fillMaxSize().padding(innerPadding).padding(
                            horizontal = 32.dp,
                            vertical = 64.dp
                        )
                    )
                }
            }
        }
    }
}

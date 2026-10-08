package com.example.viewfinder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.viewfinder.ui.composable.ViewFinder
import com.example.viewfinder.ui.theme.ViewFinderTheme

class MainActivity : ComponentActivity() {

    private val permission = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if camera permission is granted
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request camera permission
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA), permission
            )
        }

        enableEdgeToEdge()
        setContent {
            ViewFinderTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ViewFinder(
                        modifier = Modifier.padding(innerPadding),
                        lifecycleOwner = this
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ViewFinderTheme {
        ViewFinder(lifecycleOwner = LocalLifecycleOwner.current)
    }
}
package com.example.viewfinder.ui.composable

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.viewfinder.R
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.systemuicontroller.rememberSystemUiController

/*
@Composable
fun Greeting(
    name: String,
    modifier: Modifier = Modifier,
    lifecycleOwner: LifecycleOwner
    ) {
    var showCamera by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        showCamera = isGranted
    }

// Trigger the permission request
    LaunchedEffect(key1 = Unit) {
        launcher.launch(Manifest.permission.CAMERA)
    }
    if (showCamera) {
        CameraScreen(lifecycleOwner)

    }
    Text1(
        text = "Hello $name!",
        modifier = modifier
    )
}
*/

@Composable
fun ViewFinder(
    modifier: Modifier = Modifier, lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    var showCamera by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        showCamera = isGranted
    }

    val systemUiController = rememberSystemUiController()

    LaunchedEffect(key1 = systemUiController) {
        systemUiController.setStatusBarColor(
            color = Color.Transparent, // Set status bar color to transparent
            darkIcons = true // Set status bar icons to dark (optional)
        )
        systemUiController.isStatusBarVisible = false // Hide the status bar
    }

    // Trigger the permission request
    LaunchedEffect(key1 = Unit) {
        launcher.launch(Manifest.permission.CAMERA)
    }

    if (showCamera) CameraScreen(lifecycleOwner)
}

@Composable
fun CameraScreen(lifecycleOwner: LifecycleOwner) {

    @OptIn(ExperimentalPagerApi::class) Box(
        modifier = Modifier.fillMaxSize() // Occupy the entire screen
    ) {
        AndroidView(factory = { context ->
            val previewView = PreviewView(context).apply {
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener(
                {
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.surfaceProvider = previewView.surfaceProvider
                    }
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner, // Provide your LifecycleOwner here
                            cameraSelector, preview
                        )
                    } catch (exc: Exception) {
                        Log.e("CameraPreview", "Use case binding failed", exc)
                    }
                }, ContextCompat.getMainExecutor(context)
            )
            previewView
        })

        var painter1 = painterResource(id = R.drawable.fibonacci_spiral)
        var painter2 = painterResource(id = R.drawable.photo_3x3)
        var painter3 = painterResource(id = R.drawable.horizontal_symmetry_line)
        var painter4 = painterResource(id = R.drawable.vertical_symmetry_line)
        var painter5 = painterResource(id = R.drawable.plus_sign)
        var painter6 = painterResource(id = R.drawable.leading_curved_and_straight_arrows)
        var painter7 = painterResource(id = R.drawable.leading_inner_outer_curved_arrows)
        var painter8 = painterResource(id = R.drawable.leading_inner_outer_curved_arrows_1)
        var painter9 = painterResource(id = R.drawable.diagonal_types)

        // Load images into imageList
        val imageList = remember {
            mutableStateListOf<Painter>().also { list ->
                list.addAll(
                    listOf(
                        painter1,
                        painter2,
                        painter3,
                        painter4,
                        painter5,
                        painter6,
                        painter7,
                        painter8,
                        painter9
                    )
                )
            }
        }

        // Image carousel
        val pagerState = rememberPagerState()
        HorizontalPager(
            count = Int.MAX_VALUE, // imageList.size, // Replace imageList with your list of images
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .zIndex(1f) // Place above camera preview
        ) { page ->

            val actualImageIndex = page % imageList.size // Map to actual image index

            Image(
                painter = imageList[actualImageIndex], // Replace with your image loading logic
                contentDescription = "Viewfinder Guideline ${actualImageIndex + 1}",
                modifier = Modifier
                    .fillMaxSize(fraction = 0.775f)
                    .aspectRatio(1f) // Maintain aspect ratio
                    .alpha(0.5f) // Adjust transparency as needed
            )
        }
        Button(
            onClick = {
                takePhoto() // Implement your camera capture logic here
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) { }
        /*
        // Buttons centered horizontally and vertically
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(2f)
                .padding(16.dp)
                .align(Alignment.TopCenter) // Center within the Box
            ,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            IconButton(
                onClick = { /* Left button action */ },
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.LightGray) // Background for left button
            ) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(24.dp) // Increase icon size
                )
            }

            IconButton(
                onClick = { /* Right button action */ },
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.LightGray) // Background for right button

            ) {
                Icon(
                    Icons.Filled.ArrowForward,
                    contentDescription = "Forward",
                    modifier = Modifier.size(24.dp) // Increase icon size
                )
            }
        }
        */
    }
}
package com.example.viewfinder.ui.composable

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
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
import java.io.File

@Composable
fun ViewFinder(
    modifier: Modifier = Modifier, lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    var showCamera by remember { mutableStateOf(false) }

    val permissionsToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        arrayOf(Manifest.permission.CAMERA)
    } else {
        arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        showCamera = results[Manifest.permission.CAMERA] == true
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
        launcher.launch(permissionsToRequest)
    }

    if (showCamera) CameraScreen(lifecycleOwner)
}

@Composable
fun CameraScreen(lifecycleOwner: LifecycleOwner) {
    val context = LocalContext.current
    val imageCapture = remember { ImageCapture.Builder().build() }

    @OptIn(ExperimentalPagerApi::class) Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
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
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageCapture
                            )
                        } catch (exc: Exception) {
                            Log.e("CameraPreview", "Use case binding failed", exc)
                        }
                    }, ContextCompat.getMainExecutor(ctx)
                )
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        val painter1 = painterResource(id = R.drawable.fibonacci_spiral)
        val painter2 = painterResource(id = R.drawable.photo_3x3)
        val painter3 = painterResource(id = R.drawable.horizontal_symmetry_line)
        val painter4 = painterResource(id = R.drawable.vertical_symmetry_line)
        val painter5 = painterResource(id = R.drawable.plus_sign)
        val painter6 = painterResource(id = R.drawable.leading_curved_and_straight_arrows)
        val painter7 = painterResource(id = R.drawable.leading_inner_outer_curved_arrows)
        val painter8 = painterResource(id = R.drawable.leading_inner_outer_curved_arrows_1)
        val painter9 = painterResource(id = R.drawable.diagonal_types)

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

        val pagerState = rememberPagerState()
        HorizontalPager(
            count = Int.MAX_VALUE,
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .zIndex(1f)
        ) { page ->

            val actualImageIndex = page % imageList.size

            Image(
                painter = imageList[actualImageIndex],
                contentDescription = "Viewfinder Guideline ${actualImageIndex + 1}",
                modifier = Modifier
                    .fillMaxSize(fraction = 0.775f)
                    .aspectRatio(1f)
                    .alpha(0.5f)
            )
        }
        Button(
            onClick = { takePhoto(context, imageCapture) },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .size(72.dp)
                .clip(CircleShape)
                .zIndex(2f),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White,
                contentColor = Color.White
            ),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}

private fun takePhoto(context: Context, imageCapture: ImageCapture) {
    val contentResolver = context.contentResolver
    val name = "VF_${System.currentTimeMillis()}.jpg"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // API 29+: MediaStore with RELATIVE_PATH — no storage permission needed,
        // photo appears in Gallery automatically.
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/ViewFinder")
        }
        val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            contentResolver, collection, contentValues
        ).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    Log.d("Camera", "Photo saved to Gallery: ${output.savedUri}")
                    Toast.makeText(context, "Photo saved to Gallery", Toast.LENGTH_SHORT).show()
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("Camera", "Photo capture failed: ${exception.message}", exception)
                    Toast.makeText(context, "Capture failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    } else {
        // API 21–28: write to public Pictures, then scan into MediaStore.
        // Requires WRITE_EXTERNAL_STORAGE (requested at runtime above).
        val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val photoFile = File(picturesDir, name)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    MediaScannerConnection.scanFile(
                        context,
                        arrayOf(photoFile.absolutePath),
                        arrayOf("image/jpeg"),
                        null
                    )
                    Log.d("Camera", "Photo saved to Gallery: ${photoFile.absolutePath}")
                    Toast.makeText(context, "Photo saved to Gallery", Toast.LENGTH_SHORT).show()
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("Camera", "Photo capture failed: ${exception.message}", exception)
                    Toast.makeText(context, "Capture failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}
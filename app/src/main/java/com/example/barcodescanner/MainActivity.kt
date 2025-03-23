/**
 * Richard Luu [387861]
 * ADEV3007 (261251)
 * Co-op 2 - Barcode Scanner
 * This application uses CameraX and Google's ML Kit to scan QR codes and barcodes.
 * It requests camera permissions, displays a live camera feed, and detects barcodes in real-time.
 * Once a barcode is detected, the value is displayed and can be copied to the clipboard.
 * 03/23/2025
 * 
 * Resources:
 * - https://proandroiddev.com/integrating-google-ml-kit-for-barcode-scanning-in-jetpack-compose-android-apps-5deda28377c9 * - https://developer.android.com/training/camerax
 * - https://developer.android.com/training/camerax
 * - https://developers.google.com/ml-kit/vision/barcode-scanning
 **/

package com.example.barcodescanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.barcodescanner.ui.theme.BarcodeScannerTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.ui.unit.sp

/*
 * Purpose: Initializes main activity for the Barcode Scanner app.
 * This activity handles the permission request for the camera, and
 * manages the UI layout.
 * args: None
 * returns: None
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BarcodeScannerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

/*
 * Purpose: Displays the main UI of the app to start scanning and shows
 * the scanned barcode value.
 * args: modifier - Modifier used for layout.
 * returns: None.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    var scannedBarcode by rememberSaveable { mutableStateOf("No code scanned") }
    var isScanning by remember {mutableStateOf(false) }
    val permissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Copies barcode value to clipboard when clicked
        SelectionContainer {
            Text(
                text = scannedBarcode,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        copyToClipboard(context, scannedBarcode)
                    }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Button to request permission and start scanning if granted
        Button(
            onClick = {
                if (permissionState.status.isGranted) {
                    isScanning = true
                } else {
                    permissionState.launchPermissionRequest()
                }
            }
        ) {
            Text(
                text = "Scan Barcode",
                fontSize = 20.sp,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Show barcode scanner only when scanning is active
        if (isScanning) {
            ScanCode(
                onQrCodeDetected = { code ->
                    // Update scanned barcode value and stop scanning
                    scannedBarcode = code
                    isScanning = false // Hide scanner after detection
                }
            )
        }
    }
}

/*
 * Purpose: Copies the specified text to the clipboard and shows a Toast message confirming the action.
 * args: context - The context used to interact with the clipboard and show Toast.
 *       text - The text to be copied to the clipboard.
 * returns: None.
 */
fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Scanned Barcode", text)
    clipboard.setPrimaryClip(clip)

    Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
}
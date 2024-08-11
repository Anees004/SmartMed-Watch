package com.medbytes.smartwatch.presentation

import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.medbytes.smartwatch.presentation.theme.SmartwatchTheme
import com.medbytes.smartwatch.presentation.viewmodels.HealthViewModel
import com.medbytes.smartwatch.presentation.views.LoginScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.medbytes.smartwatch.presentation.viewmodels.AuthViewModel
import com.medbytes.smartwatch.presentation.viewmodels.DummyHealthViewModel
import com.medbytes.smartwatch.presentation.views.HomeScreen


class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val healthViewModel: DummyHealthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            SmartwatchTheme {
                MainApp(healthViewModel)
//                val user = authViewModel.user.observeAsState()
//                if (user.value != null) {
//                    HomeScreen(healthViewModel)
//                } else {
//                    LoginScreen(authViewModel)
//                }
            }
        }
    }
}
@Composable
fun MainApp(healthViewModel: DummyHealthViewModel) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(authViewModel, navController)
        }
        composable("home") {
             HomeScreen(healthViewModel)
            // Define and implement your HomeScreen composable here
        }
    }
}

//package com.medbytes.smartwatch.presentation
//
//import android.Manifest
//import android.content.pm.PackageManager
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.core.content.ContextCompat
//import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.tooling.preview.Devices
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.lifecycleScope
//import androidx.wear.compose.material.MaterialTheme
//import androidx.wear.compose.material.TimeText
//import com.medbytes.smartwatch.R
//import com.medbytes.smartwatch.presentation.theme.SmartwatchTheme
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.ktx.Firebase
//import kotlinx.coroutines.launch
//import java.util.UUID
//import android.hardware.Sensor
//import android.hardware.SensorEvent
//import android.hardware.SensorEventListener
//import android.hardware.SensorManager
//import androidx.compose.ui.res.stringResource
//import androidx.wear.compose.material.Button
//import androidx.wear.compose.material.CircularProgressIndicator
//import androidx.wear.compose.material.Text
//
//class MainActivity : ComponentActivity(), SensorEventListener {
//
//    private lateinit var sensorManager: SensorManager
//    private var heartRateSensor: Sensor? = null
//    private var temperatureSensor: Sensor? = null
//
//    private var heartRate: Double? = null
//    private var temperature: Double? = null
//
//    private val requestPermissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestMultiplePermissions()
//    ) { permissions ->
//        if (permissions[Manifest.permission.BODY_SENSORS] == true) {
//            startSensorDataCollection()
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        installSplashScreen()
//
//        super.onCreate(savedInstanceState)
//
//        setTheme(android.R.style.Theme_DeviceDefault)
//
//        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
//        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
//        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
//
//        setContent {
//            var isLoading by remember { mutableStateOf(false) }
//
//            WearApp(
//                isLoading = isLoading,
//                onButtonClick = {
//                    if (hasPermissions()) {
//                        isLoading = true
//                        startSensorDataCollection()
//                    } else {
//                        requestPermissions()
//                    }
//                }
//            )
//        }
//    }
//
//    private fun hasPermissions(): Boolean {
//        return ContextCompat.checkSelfPermission(
//            this,
//            Manifest.permission.BODY_SENSORS
//        ) == PackageManager.PERMISSION_GRANTED
//    }
//
//    private fun requestPermissions() {
//        requestPermissionLauncher.launch(arrayOf(Manifest.permission.BODY_SENSORS))
//    }
//
//    private fun startSensorDataCollection() {
//        sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL)
//        sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL)
//    }
//
//    override fun onSensorChanged(event: SensorEvent) {
//        when (event.sensor.type) {
//            Sensor.TYPE_HEART_RATE -> heartRate = event.values[0].toDouble()
//            Sensor.TYPE_AMBIENT_TEMPERATURE -> temperature = event.values[0].toDouble()
//        }
//
//        if (heartRate != null && temperature != null) {
//            lifecycleScope.launch {
//                uploadDataToFirebase()
//            }
//        }
//    }
//
//    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
//        // Implement if needed
//    }
//
//    private fun uploadDataToFirebase() {
//        val db = Firebase.firestore
//        val recordId = UUID.randomUUID().toString()
//        val data = hashMapOf(
//            "heart_rate" to heartRate,
//            "temperature" to temperature,
//            "timestamp" to System.currentTimeMillis()
//        )
//
//        db.collection("smartwatch")
//            .document(recordId)
//            .set(data)
//            .addOnSuccessListener {
//                println("Sensor data uploaded successfully")
//            }
//            .addOnFailureListener { e ->
//                println("Error uploading sensor data: $e")
//            }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        sensorManager.unregisterListener(this)
//    }
//}
//
//@Composable
//fun WearApp(isLoading: Boolean, onButtonClick: () -> Unit) {
//    SmartwatchTheme {
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(MaterialTheme.colors.background),
//            contentAlignment = Alignment.Center
//        ) {
//            TimeText()
//            if (isLoading) {
//                CircularProgressIndicator()
//            } else {
//                Greeting("Android")
//                UploadButton(onButtonClick)
//            }
//        }
//    }
//}
//
//@Composable
//fun Greeting(greetingName: String) {
//    Text(
//        modifier = Modifier.fillMaxWidth(),
//        textAlign = TextAlign.Center,
//        color = MaterialTheme.colors.primary,
//        text = stringResource(R.string.hello_world, greetingName)
//    )
//}
//
//@Composable
//fun UploadButton(onButtonClick: () -> Unit) {
//    Button(
//        onClick = onButtonClick,
//        modifier = Modifier.padding(top = 200.dp)
//    ) {
//        Text(text = "Start Sensors and Upload Data")
//    }
//}
//
//@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
//@Composable
//fun DefaultPreview() {
//    WearApp(isLoading = false, onButtonClick = {})
//}


//package com.medbytes.smartwatch.presentation
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.tooling.preview.Devices
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
//import androidx.lifecycle.lifecycleScope
//import androidx.wear.compose.material.Button
//import androidx.wear.compose.material.MaterialTheme
//import androidx.wear.compose.material.Text
//import androidx.wear.compose.material.TimeText
//import com.medbytes.smartwatch.R
//import com.medbytes.smartwatch.presentation.theme.SmartwatchTheme
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.ktx.Firebase
//import kotlinx.coroutines.launch
//import java.util.UUID
//
//class MainActivity : ComponentActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        installSplashScreen()
//
//        super.onCreate(savedInstanceState)
//
//        setTheme(android.R.style.Theme_DeviceDefault)
//
//        setContent {
//            WearApp {
//                lifecycleScope.launch {
//                    uploadDummyDataToFirebase()
//                }
//            }
//        }
//    }
//
//    private fun uploadDummyDataToFirebase() {
//        val db = Firebase.firestore
//        val recordId = UUID.randomUUID().toString()
//        val data = hashMapOf(
//            "type" to "dummy_data",
//            "value" to 123.45,
//            "timestamp" to System.currentTimeMillis()
//        )
//
//        db.collection("smartwatch")
//            .document(recordId)
//            .set(data)
//            .addOnSuccessListener {
//                println("Dummy data uploaded successfully")
//            }
//            .addOnFailureListener { e ->
//                println("Error uploading dummy data: $e")
//            }
//    }
//}
//
//@Composable
//fun WearApp(onButtonClick: () -> Unit) {
//    SmartwatchTheme {
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(MaterialTheme.colors.background),
//            contentAlignment = Alignment.Center
//        ) {
//            TimeText()
//            Greeting("Android")
//            UploadButton(onButtonClick)
//        }
//    }
//}
//
//@Composable
//fun Greeting(greetingName: String) {
//    Text(
//        modifier = Modifier.fillMaxWidth(),
//        textAlign = TextAlign.Center,
//        color = MaterialTheme.colors.primary,
//        text = stringResource(R.string.hello_world, greetingName)
//    )
//}
//
//@Composable
//fun UploadButton(onButtonClick: () -> Unit) {
//    Button(
//        onClick = onButtonClick,
//        modifier = Modifier.padding(top = 200.dp)
//    ) {
//        Text(text = "Upload Dummy Data")
//    }
//}
//
//@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
//@Composable
//fun DefaultPreview() {
//    WearApp {}
//}

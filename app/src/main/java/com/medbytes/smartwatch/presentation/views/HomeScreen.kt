package com.medbytes.smartwatch.presentation.views

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.random.Random

// --- Colors ---
val DarkBackground = Color(0xFF1B1B29)
val CardBackground = Color(0xFF2B2B3D)
val TextWhite = Color(0xFFEEEEEE)
val TextGrey = Color(0xFFAAAAAA)
val HeartColor = Color(0xFFE54D5F)
val TempColor = Color(0xFFF5A623)
val StepColor = Color(0xFF4CAF50)
val BloodPressureColor = Color(0xFF2196F3)
val LogoutRed = Color(0xFFD32F2F) // Red color for logout

val ButtonGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF6366F1), Color(0xFFA855F7))
)

@Composable
fun HomeScreen(context: Context, navController: NavController) {
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    // --- Sensor Data States ---
    var heartRate by remember { mutableStateOf<Float?>(null) }
    var temperature by remember { mutableStateOf<Float?>(null) }

    // --- Dummy Data Initialization ---
    var steps by remember {
        mutableStateOf<Float?>(Random.nextInt(1500, 5000).toFloat())
    }
    var bloodPressure by remember {
        mutableStateOf("${Random.nextInt(110, 135)}/${Random.nextInt(70, 85)}")
    }

    // --- UI States ---
    var isLoading by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    // Check permissions
    var hasPermissions by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.BODY_SENSORS) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val bodySensorsGranted = permissions[Manifest.permission.BODY_SENSORS] ?: false
        val activityGranted = permissions[Manifest.permission.ACTIVITY_RECOGNITION] ?: false

        if (bodySensorsGranted && activityGranted) {
            hasPermissions = true
        } else {
            Toast.makeText(context, "Permissions denied. Using dummy data.", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (!hasPermissions) {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.BODY_SENSORS, Manifest.permission.ACTIVITY_RECOGNITION)
            )
        }
    }

    // --- Sensor Listener ---
    val sensorEventListener = rememberUpdatedState(
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return
                val value = event.values[0]

                when (event.sensor.type) {
                    Sensor.TYPE_HEART_RATE -> {
                        if (value > 0) heartRate = value
                    }
                    Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                        temperature = value
                    }
                    Sensor.TYPE_STEP_COUNTER -> {
                        steps = value
                    }
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    )

    // Register Sensors
    DisposableEffect(hasPermissions) {
        if (hasPermissions) {
            val hrSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
            val tempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
            val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

            hrSensor?.let { sensorManager.registerListener(sensorEventListener.value, it, SensorManager.SENSOR_DELAY_UI) }
            tempSensor?.let { sensorManager.registerListener(sensorEventListener.value, tempSensor, SensorManager.SENSOR_DELAY_UI) }
            stepSensor?.let { sensorManager.registerListener(sensorEventListener.value, stepSensor, SensorManager.SENSOR_DELAY_UI) }
        }

        onDispose {
            sensorManager.unregisterListener(sensorEventListener.value)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Header (Uncommented slightly to show status if needed)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp)
            ) {
//                Icon(Icons.Default.c, contentDescription = "BT", tint = TextGrey, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("CONNECTED", color = TextGrey, fontSize = 10.sp, letterSpacing = 1.sp)
            }

            // Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 40.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Heart Rate
                item {
                    CompactSensorCard(
                        value = heartRate?.toInt()?.toString() ?: "--",
                        unit = "BPM",
                        icon = Icons.Default.Favorite,
                        iconColor = HeartColor
                    )
                }

                // Temperature
                item {
                    CompactSensorCard(
                        value = if (temperature != null) String.format("%.1f", temperature) else "--",
                        unit = "°C",
                        icon = Icons.Default.DeviceThermostat,
                        iconColor = TempColor
                    )
                }

                // Steps
                item {
                    CompactSensorCard(
                        value = steps?.toInt()?.toString() ?: "0",
                        unit = "Steps",
                        icon = Icons.Default.DirectionsWalk,
                        iconColor = StepColor
                    )
                }

                // Blood Pressure
                item {
                    CompactSensorCard(
                        value = bloodPressure,
                        unit = "mmHg",
                        icon = Icons.Default.WaterDrop,
                        iconColor = BloodPressureColor
                    )
                }

                // --- Upload Button ---
                item(span = { GridItemSpan(2) }) {
                    if (isLoading) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(10.dp)) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        }
                    } else {
                        Button(
                            onClick = {
                                isLoading = true
                                // Refresh Dummy Data
                                bloodPressure = "${Random.nextInt(110, 135)}/${Random.nextInt(70, 85)}"
                                val currentSteps = steps ?: 2000f
                                steps = currentSteps + Random.nextInt(10, 100).toFloat()

                                uploadDataToFirebase(heartRate?.toInt(), temperature, steps?.toInt(), bloodPressure, context) {
                                    isLoading = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .padding(top = 8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(ButtonGradient, shape = RoundedCornerShape(24.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Upload Data", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // --- NEW LOGOUT BUTTON (Bottom) ---
                item(span = { GridItemSpan(2) }) {
                    Button(
                        onClick = {
                            Firebase.auth.signOut()
                            Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                            navController.navigate("login") {
                                popUpTo("home") { inclusive = true }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(top = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LogoutRed),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Logout", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Top Menu (Still kept as backup, but Logout button is now in grid)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 4.dp, end = 4.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            Box {
                IconButton(onClick = { showMenu = !showMenu }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = TextGrey)
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(CardBackground)
                ) {
                    DropdownMenuItem(
                        text = { Text("Logout", color = Color.Red) },
                        onClick = {
                            showMenu = false
                            Firebase.auth.signOut()
                            Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                            navController.navigate("login") { popUpTo("home") { inclusive = true } }
                        }
                    )
                }
            }
        }
    }
}

// Compact Card
@Composable
fun CompactSensorCard(
    value: String,
    unit: String,
    icon: ImageVector,
    iconColor: Color
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        modifier = Modifier
            .aspectRatio(1.1f)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = unit, color = TextGrey, fontSize = 10.sp)
        }
    }
}

private fun uploadDataToFirebase(
    heartRate: Int?,
    temperature: Float?,
    steps: Int?,
    bloodPressure: String,
    context: Context,
    onComplete: () -> Unit
) {
    val user = Firebase.auth.currentUser
    if (user != null) {
        val userId = user.uid
        val db = Firebase.firestore
        val recordId = UUID.randomUUID().toString()
        val data = hashMapOf(
            "heart_rate" to (heartRate ?: 0),
            "temperature" to (temperature ?: 0.0f),
            "steps" to (steps ?: 0),
            "blood_pressure" to bloodPressure,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("users")
            .document(userId)
            .collection("smartwatch")
            .document(recordId)
            .set(data)
            .addOnSuccessListener {
                Toast.makeText(context, "Data uploaded", Toast.LENGTH_SHORT).show()
                onComplete()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                onComplete()
            }
    } else {
        Toast.makeText(context, "Not logged in", Toast.LENGTH_SHORT).show()
        onComplete()
    }
}
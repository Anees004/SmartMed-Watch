package com.medbytes.smartwatch.presentation.views

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.medbytes.smartwatch.R
import com.medbytes.smartwatch.presentation.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

@Composable
fun HomeScreen(context: Context,navController: NavController) {
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    var heartRate by remember { mutableStateOf<Float?>(null) }
    var temperature by remember { mutableStateOf<Float?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    val user = Firebase.auth.currentUser

    val heartRateSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
    val temperatureSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)

    val sensorEventListener = rememberUpdatedState(
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return
                when (event.sensor.type) {
                    Sensor.TYPE_HEART_RATE -> heartRate = event.values[0]
                    Sensor.TYPE_AMBIENT_TEMPERATURE -> temperature = event.values[0]
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    )

    DisposableEffect(Unit) {
        heartRateSensor?.also {
            sensorManager.registerListener(sensorEventListener.value, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        temperatureSensor?.also {
            sensorManager.registerListener(sensorEventListener.value, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        onDispose {
            sensorManager.unregisterListener(sensorEventListener.value)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Top menu icon
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                IconButton(onClick = { showMenu = !showMenu }) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Menu")
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Profile: ${user?.email ?: "No email available"}") },
                        onClick = {
                            showMenu = false
                            // Navigate to Profile if needed
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Logout") },
                        onClick = {
                            showMenu = false
                            Firebase.auth.signOut()
                            Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
                            navController.navigate("login") {
                                // Clear the back stack and prevent going back to the home screen
                                popUpTo("home") { inclusive = true }
                            }

                            // Handle logout (e.g., navigate to login screen)
                        }

                    )
                }
            }

            if (isLoading) {
                CircularProgressIndicator(color = Color.Black)
                Text("Uploading...", color = Color.Black, modifier = Modifier.padding(top = 8.dp))
            } else {
                Text("Heart Rate: ${heartRate?.toInt() ?: "78"}", color = Color.Black)
                Text(
                    "Temperature: ${temperature?.toInt() ?: "37"}",
                    modifier = Modifier.padding(top = 8.dp),
                    color = Color.Black
                )

                Button(
                    onClick = {
                        isLoading = true
                            isLoading = false
                        uploadDataToFirebase(heartRate?.toInt(), temperature, context) {
                        }
//                        uploadDummyData(context)
                        isLoading = false

                    },
                    modifier = Modifier.padding(top = 16.dp)
                )
                {
                    Text("Upload Health Data")
                }
            }
        }
    }

    // Create notification
    fun sendNotification(context: Context, title: String, messageBody: String) {
        val notificationManager = NotificationManagerCompat.from(context)
        val channelId = "default_channel"

        // Create notification
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel Title",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(context, "Notification permission not granted", Toast.LENGTH_SHORT).show()
            Log.e("Notification", "Permission not granted")
            return
        }

        try {
            notificationManager.notify(0, notificationBuilder.build())
            Toast.makeText(context, "Notification sent successfully", Toast.LENGTH_SHORT).show()
            Log.d("Notification", "Notification sent successfully")
        } catch (e: Exception) {
            Toast.makeText(context, "Error sending notification", Toast.LENGTH_SHORT).show()
            Log.e("Notification", "Error sending notification", e)
        }
    }

    fun sendNotificationToDevice(fcmToken: String, title: String, message: String) {
        val url = URL("https://fcm.googleapis.com/fcm/send")
        val connection = url.openConnection() as HttpURLConnection

        connection.doOutput = true
        connection.requestMethod = "POST"
        connection.setRequestProperty("Authorization", "key=<YOUR_SERVER_KEY>")
        connection.setRequestProperty("Content-Type", "application/json")

        val payload = """
        {
           "to": "$fcmToken",
           "notification": {
             "title": "$title",
             "body": "$message"
           }
        }
    """.trimIndent()

        val outputStream = connection.outputStream
        outputStream.write(payload.toByteArray())
        outputStream.flush()
        outputStream.close()

        val responseCode = connection.responseCode
        Log.d("FCM", "Response Code: $responseCode")

        if (responseCode == HttpURLConnection.HTTP_OK) {
            // Log success
            Log.d("FCM", "Notification sent successfully")
        } else {
            // Log failure
            Log.d("FCM", "Failed to send notification")
        }
    }
    // Function to send notification asynchronously
    fun sendNotificationAsync(context: Context, token: String, title: String, message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Call the function to send notification on a background thread
                sendNotificationToDevice(token, title, message)

                // Once successful, you can switch back to the Main thread to show a success message
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Notification sent successfully", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                // Log the error on a background thread
                Log.e("NotificationError", "Failed to send notification: ${e.message}", e)

                // Switch back to the Main thread to show a failure message
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed to send notification: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Sample button to trigger the notification
//    Button(
//        onClick = {
//            try {
////                sendNotificationToDevice("fpGph4O6Q3WqZEmVK3l7-7:APA91bEi7cc4Uj7c7A7NohfoQ9Zm1WlDWAzxf5r4QMqWqXdaRB3OfsgR6kzubcSe1QF5ahnhlobEkA34QT0_JlD7ckGbFdtb9SP6YZIYGFuD9TQwF8JDxJYvpJpKGgOav7FB5rfRoMQT", "Test Title", "This is a test notification.")
//                sendNotificationAsync(
//                    context,
//                    "fpGph4O6Q3WqZEmVK3l7-7:APA91bEi7cc4Uj7c7A7NohfoQ9Zm1WlDWAzxf5r4QMqWqXdaRB3OfsgR6kzubcSe1QF5ahnhlobEkA34QT0_JlD7ckGbFdtb9SP6YZIYGFuD9TQwF8JDxJYvpJpKGgOav7FB5rfRoMQT",
//                    "Test Title",
//                    "This is a test notification."
//                )
//
//            } catch (e: Exception) {
//                // Log the error for debugging
//                Log.e("NotificationError", "Failed to send notification: ${e.message}", e)
//
//                // Optionally, show a toast to the user indicating the failure
//                Toast.makeText(context, "Failed to send notification: ${e.message}", Toast.LENGTH_LONG).show()
//            }
////            sendNotification(context, "Test Title", "This is a test notification.")
//        }
//    ) {
//        Text("Test Notification")
//    }

}

private fun uploadDataToFirebase(
    heartRate: Int?,
    temperature: Float?,
    context: Context,
    onComplete: () -> Unit
) {
    val user = Firebase.auth.currentUser
    if (user != null) {
        val userId = user.uid
        val db = Firebase.firestore
        val recordId = UUID.randomUUID().toString()
        val data = hashMapOf(
            "blood_pressure" to "120/80",
            "heart_rate" to heartRate,
            "temperature" to temperature,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("users")
            .document(userId)
            .collection("smartwatch")
            .document(recordId)
            .set(data)
            .addOnSuccessListener {
                Toast.makeText(context, "Data uploaded successfully", Toast.LENGTH_SHORT).show()
                onComplete()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error uploading data: $e", Toast.LENGTH_SHORT).show()
                onComplete()
            }
    } else {
        Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
        onComplete()
    }
}


private fun uploadDummyData(context: Context) {
    val user = Firebase.auth.currentUser
    if (user != null) {
        val userId = user.uid
        val db = Firebase.firestore
        val currentTime = System.currentTimeMillis()
        val intervalInMillis = 12 * 60 * 1000 // 12 minutes in milliseconds

        // Generate dummy data
        for (i in 0 until 50) { // 5 entries per hour * 10 hours = 50 entries
            val timestamp = currentTime - (i * intervalInMillis)
            val heartRate = (60..100).random() // Random heart rate between 60 and 100
            val temperature = String.format("%.2f", (20..30).random().toDouble()) // Random temperature with 2 decimal places

            // Generate random blood pressure
            val systolic = (110..140).random() // Random systolic pressure
            val diastolic = (70..90).random() // Random diastolic pressure
            val bloodPressure = "$systolic/$diastolic"

            val recordId = UUID.randomUUID().toString()
            val data = hashMapOf(
                "heart_rate" to heartRate,
                "temperature" to temperature.toDouble(), // Ensure temperature is a double
                "blood_pressure" to bloodPressure,
                "timestamp" to timestamp
            )

            db.collection("users")
                .document(userId)
                .collection("smartwatch")
                .document(recordId)
                .set(data)
                .addOnSuccessListener {
                    Toast.makeText(context, "Dummy data uploaded successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error uploading dummy data: $e", Toast.LENGTH_SHORT).show()
                }
        }
    } else {
        Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
    }
}


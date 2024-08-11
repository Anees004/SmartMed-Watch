package com.medbytes.smartwatch.presentation.views

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Text
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.medbytes.smartwatch.presentation.viewmodels.DummyHealthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun HomeScreen(healthViewModel: DummyHealthViewModel) {
    val heartRate by healthViewModel.heartRate.observeAsState()
    val temperature by healthViewModel.temperature.observeAsState()
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Red),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(color = Color.White)
                Text("Loading...", color = Color.White, modifier = Modifier.padding(top = 8.dp))
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Heart Rate: ${heartRate ?: "N/A"}", color = Color.White)
                Text(
                    "Temperature: ${temperature ?: "N/A"}",
                    modifier = Modifier.padding(top = 8.dp),
                    color = Color.White
                )

                Button(
                    onClick = {
                        isLoading = true
                        healthViewModel.generateRandomData()
                        healthViewModel.heartRate.observeForever {
                            isLoading = false
//                            uploadDataToFirebase(heartRate, temperature)
                        }
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Record Health Data")
                }
            }
        }
    }
}
//package com.medbytes.smartwatch.presentation.views
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.runtime.*
//import androidx.compose.runtime.livedata.observeAsState
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import androidx.wear.compose.material.Button
//import androidx.wear.compose.material.Text
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.ktx.Firebase
//import com.medbytes.smartwatch.presentation.viewmodels.HealthViewModel
//import java.util.UUID
//
//@Composable
//fun HomeScreen(healthViewModel: HealthViewModel) {
//    val heartRate by healthViewModel.heartRate.observeAsState()
//    val temperature by healthViewModel.temperature.observeAsState()
//
//    Column(
//        modifier = Modifier.fillMaxSize().padding(16.dp).background(Color.Red),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text("Heart Rate: ${heartRate ?: "N/A"}")
//        Text("Temperature: ${temperature ?: "N/A"}", modifier = Modifier.padding(top = 8.dp))
//
//        Button(
//            onClick = {
////                healthViewModel.startSensorDataCollection()
//                uploadDataToFirebase();
//
//            },
//            modifier = Modifier.padding(top = 16.dp)
//        ) {
//            Text("Record Health Data")
//        }
//    }
//    fun uploadDataToFirebase() {
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
//}

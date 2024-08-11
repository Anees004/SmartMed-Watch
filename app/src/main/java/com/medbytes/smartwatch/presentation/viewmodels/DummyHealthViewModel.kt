package com.medbytes.smartwatch.presentation.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import java.util.UUID

class DummyHealthViewModel(application: Application) : AndroidViewModel(application) {
    private val _heartRate = MutableLiveData<Int>()
    val heartRate: LiveData<Int> = _heartRate

    private val _temperature = MutableLiveData<Float>()
    val temperature: LiveData<Float> = _temperature

    private val _bloodPressure = MutableLiveData<Pair<Int, Int>>()
    val bloodPressure: LiveData<Pair<Int, Int>> = _bloodPressure

    private val viewModelScope = CoroutineScope(Dispatchers.Main + Job())

    fun generateRandomData() {
        viewModelScope.launch {
            delay(5000) // Simulate 5-second loading period
            _heartRate.value = (60..100).random()
            _temperature.value = (36..38).random() + (0..9).random() / 10f
            _bloodPressure.value = Pair((90..120).random(), (60..80).random())

            // Log the generated values to ensure they are being set
            println("Generated heart rate: ${_heartRate.value}")
            println("Generated temperature: ${_temperature.value}")
            println("Generated blood pressure: ${_bloodPressure.value}")

            uploadDataToFirebase(_heartRate.value, _temperature.value, _bloodPressure.value)
        }
    }

    fun uploadDataToFirebase(heartRate: Int?, temperature: Float?, bloodPressure: Pair<Int, Int>?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("Firebase 123", "Step 1: Start uploadDataToFirebase")

                val user = Firebase.auth.currentUser
                if (user != null) {
                    val userId = user.uid
                    val db = Firebase.firestore
                    val recordId = UUID.randomUUID().toString()
                    Log.d("Firebase 123", "Step 2: Firestore instance and record ID generated")

                    val data = hashMapOf(
                        "heart_rate" to heartRate,
                        "temperature" to (temperature ?: 0f),
                        "blood_pressure" to "${bloodPressure?.first}/${bloodPressure?.second}",
                        "timestamp" to System.currentTimeMillis()
                    )
                    Log.d("Firebase 123", "Step 3: Data hashmap created: $data")

                    db.collection("users")
                        .document(userId)
                        .collection("smartwatch")
                        .document(recordId)
                        .set(data)
                        .addOnSuccessListener {
                            Log.d("Firebase 123", "Step 4: Sensor data uploaded successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.d("Firebase 123", "Step 4: Error uploading sensor data: $e")
                        }
                } else {
                    Log.e("Firebase 123", "Error: No authenticated user found")
                }
            } catch (e: Exception) {
                Log.e("Firebase 123", "Exception during upload: ${e.message}")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}

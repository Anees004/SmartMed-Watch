package com.medbytes.smartwatch.presentation.viewmodels

import android.app.Application
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.UUID

class HealthViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {
    private val sensorManager: SensorManager = application.getSystemService(SENSOR_SERVICE) as SensorManager
    private val heartRateSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
    private val temperatureSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)

    private val _heartRate = MutableLiveData<Double>()
    val heartRate: LiveData<Double> = _heartRate

    private val _temperature = MutableLiveData<Double>()
    val temperature: LiveData<Double> = _temperature

    fun startSensorDataCollection() {
        heartRateSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        temperatureSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stopSensorDataCollection() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_HEART_RATE -> _heartRate.value = event.values[0].toDouble()
            Sensor.TYPE_AMBIENT_TEMPERATURE -> _temperature.value = event.values[0].toDouble()
        }
        uploadDataToFirebase()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Implement if needed
    }

    private fun uploadDataToFirebase() {
        val db = Firebase.firestore
        val recordId = UUID.randomUUID().toString()
        val data = hashMapOf(
            "heart_rate" to heartRate.value,
            "temperature" to temperature.value,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("smartwatch")
            .document(recordId)
            .set(data)
            .addOnSuccessListener {
                println("Sensor data uploaded successfully")
            }
            .addOnFailureListener { e ->
                println("Error uploading sensor data: $e")
            }
    }

    override fun onCleared() {
        super.onCleared()
        stopSensorDataCollection()
    }
}

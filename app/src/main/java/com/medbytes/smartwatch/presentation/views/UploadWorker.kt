package com.medbytes.smartwatch.presentation.views

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class UploadWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val user = Firebase.auth.currentUser
        if (user != null) {
            val userId = user.uid
            val db = Firebase.firestore
            val currentTime = System.currentTimeMillis()
            val heartRate = (60..100).random() // Replace with actual sensor data if available
            val temperature = String.format("%.2f", (20..30).random().toDouble())

            val systolic = (110..140).random()
            val diastolic = (70..90).random()
            val bloodPressure = "$systolic/$diastolic"

            val recordId = UUID.randomUUID().toString()
            Log.d("Use ID ","my record id is "+recordId)
            val data = hashMapOf(
                "heart_rate" to heartRate,
                "temperature" to temperature.toDouble(),
                "blood_pressure" to bloodPressure,
                "timestamp" to currentTime
            )

            return try {
                db.collection("users")
                    .document(userId)
                    .collection("smartwatch")
                    .document(recordId)
                    .set(data)
                    .addOnSuccessListener {
                        // Log success
                    }
                    .addOnFailureListener { e ->
                        // Log failure
                    }
                Result.success()
            } catch (e: Exception) {
                Result.failure()
            }
        } else {
            return Result.failure()
        }
    }
}

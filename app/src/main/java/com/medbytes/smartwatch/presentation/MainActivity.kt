package com.medbytes.smartwatch.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.medbytes.smartwatch.presentation.theme.SmartwatchTheme
import com.medbytes.smartwatch.presentation.viewmodels.HealthViewModel
import com.medbytes.smartwatch.presentation.views.LoginScreen
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.medbytes.smartwatch.presentation.viewmodels.AuthViewModel
import com.medbytes.smartwatch.presentation.viewmodels.DummyHealthViewModel
import com.medbytes.smartwatch.presentation.views.HomeScreen
import com.medbytes.smartwatch.presentation.views.UploadWorker
import java.util.concurrent.TimeUnit


class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val healthViewModel: DummyHealthViewModel by viewModels()
    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted
            Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
        } else {
            // Permission denied
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                when {
                    ContextCompat.checkSelfPermission(
                        this, Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        // Permission already granted
                    }

                    else -> {
                        requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
                SmartwatchTheme {
                    MainApp(healthViewModel, this)
//                val user = authViewModel.user.observeAsState()
//                if (user.value != null) {
//                    HomeScreen(healthViewModel)
//                } else {
//                    LoginScreen(authViewModel)
//                }
                }
            }
            // Schedule periodic work to upload data every 12 minutes
//        val uploadWorkRequest = PeriodicWorkRequestBuilder<UploadWorker>(12, TimeUnit.MINUTES)
//            .build()
//        WorkManager.getInstance(this).enqueue(uploadWorkRequest)
        }
    }

    @Composable
    fun MainApp(healthViewModel: DummyHealthViewModel, context: Context) {
        val navController = rememberNavController()
        val authViewModel: AuthViewModel = viewModel()

        NavHost(navController = navController, startDestination = "login") {
            composable("login") {
                LoginScreen(authViewModel, navController)
            }
            composable("home") {
                HomeScreen(context = context, navController)
                // Define and implement your HomeScreen composable here
            }
        }
    }
}

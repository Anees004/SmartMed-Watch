package com.medbytes.smartwatch.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.wear.compose.material.Text
import com.medbytes.smartwatch.R
import com.medbytes.smartwatch.presentation.viewmodels.AuthViewModel
import com.medbytes.smartwatch.presentation.views.widgets.CustomTextField

@Composable
fun LoginScreen(authViewModel: AuthViewModel, navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loading by authViewModel.loading.observeAsState(false)
    val user by authViewModel.user.observeAsState()
    if (user != null) {
        LaunchedEffect(user) {
            user?.let {
                // Navigate to the home screen if the user is not null
                navController.navigate("home")
            }
        }
    }
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .background(Color.Gray)
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 24.sp),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        if (loading) {
            CircularProgressIndicator()
        } else {
            CustomTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                fontSize = 16,
                boxHeight = 56,
                labelHeight = 20,
            )
            CustomTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                fontSize = 16,
                boxHeight = 56,
                labelHeight = 20,
                isPassword = true,
            )
            Button(
                onClick = { authViewModel.signInWithEmail(email, password) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Other Sign Up Options", style = MaterialTheme.typography.bodySmall)
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                IconButton(onClick = { /* Add Google Sign-In logic here */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.google_icn),
                        contentDescription = "Google Sign-In"
                    )
                }
                IconButton(onClick = { /* Add Facebook Sign-In logic here */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.facebook_icn),
                        contentDescription = "Facebook Sign-In"
                    )
                }
            }
        }
    }
}

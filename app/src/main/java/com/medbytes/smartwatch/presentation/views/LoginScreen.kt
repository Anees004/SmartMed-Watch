package com.medbytes.smartwatch.presentation.views
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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
fun LoginScreen(
    authViewModel: AuthViewModel,
    navController: NavController
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val loading by authViewModel.loading.observeAsState(false)
    val user by authViewModel.user.observeAsState()

    LaunchedEffect(user) {
        if (user != null) {
            navController.navigate("home")
        }
    }

    // Background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0F2027),
                        Color.Black
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        // Circular Card
        Box(
            modifier = Modifier
                .size(260.dp)
                .background(Color.Black, shape = CircleShape)
                .padding(16.dp)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(8.dp))

                // 🔴 Heart Icon with circle background
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = Color(0xFF1C1C1C),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Heart",
                        tint = Color(0xFFE74C3C),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "SmartMed",
                    fontSize = 16.sp,
                    color = Color.White
                )

                Text(
                    text = "SECURE ACCESS",
                    fontSize = 10.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Email
                CustomTextField(
                    value = email,
                    onValueChange = { email = it },
                    hint = "Email"
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Password
                CustomTextField(
                    value = password,
                    onValueChange = { password = it },
                    hint = "Password",
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (loading) {
                    CircularProgressIndicator(
                        color = Color(0xFF2ECC71),
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Button(
                        onClick = {
                            authViewModel.signInWithEmail(email, password)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF27AE60),
                                        Color(0xFF2ECC71)
                                    )
                                ),
                                shape = RoundedCornerShape(50)
                            )
                    ) {
                        Text(
                            text = "Sign In",
                            color = Color.Black,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(52.dp))
            }
        }
    }
}

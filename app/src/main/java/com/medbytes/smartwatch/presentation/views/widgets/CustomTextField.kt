package com.medbytes.smartwatch.presentation.views.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.wear.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import com.medbytes.smartwatch.R
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    isPassword: Boolean = false,
    modifier: Modifier = Modifier
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(
                color = Color(0xFF1C1C1C),
                shape = RoundedCornerShape(50)
            )
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Leading Icon
            Icon(
                imageVector = if (isPassword) Icons.Default.Lock else Icons.Default.Email,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 12.sp
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                visualTransformation =
                    if (isPassword && !passwordVisible)
                        PasswordVisualTransformation()
                    else
                        VisualTransformation.None,
                modifier = Modifier
                    .weight(1f),
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            text = hint,
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                    innerTextField()
                }
            )

            // Password toggle
            if (isPassword) {
                IconButton(
                    onClick = { passwordVisible = !passwordVisible },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector =
                            if (passwordVisible)
                                Icons.Default.VisibilityOff
                            else
                                Icons.Default.Visibility,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

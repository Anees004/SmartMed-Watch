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
import com.medbytes.smartwatch.R

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    fontSize: Int,
    boxHeight: Int,
    labelHeight: Int,
    isPassword: Boolean = false
) {
    var focused by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .height(boxHeight.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = label,
            style = TextStyle(fontSize = 12.sp, color = Color.White),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.White, shape = RoundedCornerShape(16.dp))
                .padding(start = 8.dp, end = if(!isPassword) 8.dp else 2.dp)
                .height((boxHeight - labelHeight).dp)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(fontSize = fontSize.sp, color = Color.White),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        focused = focusState.isFocused
                    }
                    .background(Color.Transparent).align(Alignment.Center),
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None
            )
            if (isPassword) {
                IconButton(
                    onClick = { passwordVisible = !passwordVisible },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        painter = painterResource(id = if (passwordVisible) R.drawable.hide else R.drawable.show),
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = Color.White
                    )

                }
            }
        }
    }
}

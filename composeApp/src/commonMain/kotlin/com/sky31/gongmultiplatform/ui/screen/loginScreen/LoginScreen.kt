package com.sky31.gongmultiplatform.ui.screen.loginScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sky31.gongmultiplatform.ui.component.LoadingButton
import com.sky31.gongmultiplatform.ui.viewModel.AuthViewModel
import com.sky31.gongmultiplatform.util.AuthState
import gongmultiplatform.composeapp.generated.resources.Res
import gongmultiplatform.composeapp.generated.resources.login_logo
import gongmultiplatform.composeapp.generated.resources.password_invisible
import gongmultiplatform.composeapp.generated.resources.password_visible
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

@Composable
fun LoginScreen(
    navController: NavController
) {
    val authViewModel: AuthViewModel = viewModel { AuthViewModel() }

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var enabled by remember { mutableStateOf(true) }
    var alertVisible by remember { mutableStateOf(false) }
    var alertText by remember { mutableStateOf("") }

    val animatedColor by animateColorAsState(
        targetValue = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        animationSpec = tween(200),
        label = "color")

    val authState by authViewModel.authState.collectAsState()

    val passwordVisible = remember { mutableStateOf(false) }
    val passwordIconResource = remember(passwordVisible.value) {
        if (passwordVisible.value) Res.drawable.password_visible else Res.drawable.password_invisible
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(authState) {
        when(authState) {
            is AuthState.Authenticated -> {
                navController.navigate("main")
            }
            is AuthState.Loading -> {

            }
            is AuthState.Error -> {
                alertText = (authState as AuthState.Error).message
                alertVisible = true
                delay(2500)
                alertVisible = false
                authViewModel.resetAuthState()
            }
            else -> {}
        }
    }

    LaunchedEffect(username, password) {
        enabled = !(username == "" || password == "")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp)
    ) {
        Box(
            modifier = Modifier
                .height(40.dp)
                .fillMaxWidth()
        ) {
            this@Column.AnimatedVisibility(
                visible = alertVisible,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .height(30.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(top = 6.dp, bottom = 6.dp, start = 25.dp, end = 25.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = alertText,
                            color = Color.White
                            )
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier
                .height(40.dp)
        )

        Image(
            painter = painterResource(Res.drawable.login_logo),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(200.dp),
            contentDescription = "Login Logo",
        )

        Spacer(
            modifier = Modifier
                .height(40.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(.7f)
                .height(45.dp)
                .align(Alignment.CenterHorizontally)
                .border(
                    width = 1.dp,
                    color = Color(0xFF969696),
                    shape = RoundedCornerShape(30.dp)
                )
                .padding(start = 5.dp, end = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp),
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 16.sp,
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                decorationBox = { innerTextField ->
                    Box {
                        if (username.isEmpty()) {
                            Text("用户名", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        innerTextField()
                    }
                },
                value = username,
                onValueChange = {
                    username = it
                },
            )
        }

        Spacer(
            modifier = Modifier
                .height(15.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(.7f)
                .height(45.dp)
                .align(Alignment.CenterHorizontally)
                .border(
                    width = 1.dp,
                    color = Color(0xFF969696),
                    shape = RoundedCornerShape(30.dp)
                )
                .padding(start = 5.dp, end = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp),
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 16.sp,
                ),
                visualTransformation = if(passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Password
                ),
                decorationBox = { innerTextField ->
                    Box {
                        if (password.isEmpty()) {
                            Text("密码", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        innerTextField()
                    }
                },
                value = password,
                onValueChange = {
                    password = it
                },
            )

            Box(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .clickable {
                        passwordVisible.value = !passwordVisible.value
                    },
            ) {
                Image(
                    painter = painterResource(passwordIconResource),
                    contentDescription = "Login Logo",
                )
            }
        }

        Spacer(modifier = Modifier
            .height(80.dp)
        )

        LoadingButton(
            modifier = Modifier
                .width(170.dp)
                .height(40.dp)
                .clip(RoundedCornerShape(50))
                .background(animatedColor)
                .align(Alignment.CenterHorizontally),
            call = {
                keyboardController?.hide()
                authViewModel.login(username, password)
            },
            text = "登录"
        )
    }
}
package dev.sudhanshu.taskmanager.presentation.view.component

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import app.inscribe.presentation.viewmodel.LoginViewModel
import dev.sudhanshu.taskmanager.R
import dev.sudhanshu.taskmanager.presentation.view.Home
import dev.sudhanshu.taskmanager.presentation.ui.theme.TaskManagerTheme
import dev.sudhanshu.taskmanager.presentation.ui.theme.Typography

@Composable
fun LoginScreen(onGoogleSignInClick: () -> Unit, viewModel: LoginViewModel = hiltViewModel(), onSuccessLogin : (String) -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        when (uiState) {
            is LoginViewModel.UiState.LoggedOut -> {


            }
            is LoginViewModel.UiState.LoggedIn -> {
                (uiState as LoginViewModel.UiState.LoggedIn).user?.let { onSuccessLogin(it.uid) }
            }
            else -> {


            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize().background(color = MaterialTheme.colors.background)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Image(
            painter = painterResource(id = R.drawable.login_png),
            contentDescription = null,
            modifier = Modifier
                .height(400.dp)
                .fillMaxWidth()
                .padding(40.dp, 50.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground),
            contentScale = ContentScale.Fit
        )
        Text(
            text = "Welcome to TaskManager!",
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp, 0.dp),
            fontSize = 24.sp,
            style = Typography.h2
        )
        Text(
            text = "Welcome to our Task Manager app! Create, prioritize. Set deadlines, categorize tasks by project or priority, and track progress with ease",
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier.fillMaxWidth().padding(30.dp, 20.dp),
            style = Typography.h4,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
        MyCard(onGoogleSignInClick)

    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    TaskManagerTheme {
        LoginScreen(onGoogleSignInClick = {
        }, onSuccessLogin = {})
    }
}

@Composable
fun MyCard(onGoogleSignInClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(30.dp, 20.dp)
            .fillMaxWidth()
            .clickable(onClick = onGoogleSignInClick),
        shape = RoundedCornerShape(50.dp),
        backgroundColor = Color.Black
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.google),
                contentDescription = null,
                modifier = Modifier
                    .height(30.dp)
                    .width(30.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Sign in with Google",
                fontSize = 16.sp,
                color = Color.White,
                style = MaterialTheme.typography.subtitle1
            )
        }
    }
}


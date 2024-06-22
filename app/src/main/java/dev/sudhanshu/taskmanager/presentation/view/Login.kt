package dev.sudhanshu.taskmanager.presentation.view



import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import app.inscribe.presentation.viewmodel.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import dev.sudhanshu.taskmanager.R
import dev.sudhanshu.taskmanager.presentation.ui.theme.TaskManagerTheme
import dev.sudhanshu.taskmanager.presentation.view.component.LoginScreen
import dev.sudhanshu.taskmanager.util.LocationPreferences

@AndroidEntryPoint
class Login : ComponentActivity() {

    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationPreferences: LocationPreferences

    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        handleSignInResult(task)
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            getLastKnownLocation()
        } else {
            // Handle permission denied
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskManagerTheme {
                LoginScreen(onGoogleSignInClick = {
                    triggerGoogleSignIn()
                }, viewModel = loginViewModel, onSuccessLogin = {
                    locationPreferences.saveUserId(it)
                    val intent = Intent(this, Home::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                })
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationPreferences = LocationPreferences(this)
        checkLocationPermission()
    }

    private fun triggerGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)!!
            loginViewModel.signInWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            // Handle error
        }
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                getLastKnownLocation()
            }
            else -> {
                requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    handleLocationUpdate(location)
                } else {
                    requestLocationUpdates()
                }
            }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setMinUpdateIntervalMillis(5000)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                if (location != null) {
                    handleLocationUpdate(location)
                }
            }
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun handleLocationUpdate(location: Location) {
        locationPreferences.saveLocation(location)
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    TaskManagerTheme {

    }
}



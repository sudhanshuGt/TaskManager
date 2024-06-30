package dev.sudhanshu.taskmanager.presentation.view.component

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.GeoPoint
import dev.sudhanshu.taskmanager.presentation.ui.theme.Typography

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MapScreen(onLocationSelected: (GeoPoint) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var mapView: MapView? by remember { mutableStateOf(null) }
    var googleMap: GoogleMap? = null
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                googleMap?.isMyLocationEnabled = true
            }
        }
    )

    DisposableEffect(Unit) {
        mapView = MapView(context)
        mapView?.let { map ->
            map.onCreate(null)
            map.onResume()
            map.getMapAsync { map ->
                googleMap = map
                googleMap?.setOnMapClickListener { latLng ->
                    selectedLocation = latLng
                    googleMap?.clear()
                    googleMap?.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title("Selected Location")
                            .snippet("Lat: ${latLng.latitude}, Lng: ${latLng.longitude}")
                    )
                    googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                }

                val permissionCheck = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    googleMap?.isMyLocationEnabled = true
                } else {
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }
        }
        onDispose {
            mapView?.onDestroy()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clipToBounds()
            .background(color = MaterialTheme.colors.background)
    ) {
        Text(
            text = "Select Location",
            style = Typography.h1,
            fontSize = 16.sp,
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier
                .padding(20.dp)
                .align(Alignment.CenterHorizontally)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .background(color = MaterialTheme.colors.onBackground)
        ) {
            mapView?.let { map ->
                AndroidView(
                    factory = { map },
                    modifier = Modifier.fillMaxSize()
                )
            }
            Button(
                onClick = { selectedLocation?.let { onLocationSelected(GeoPoint(it.latitude, it.longitude)) } },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .height(56.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFE5FF7F)),
                elevation = ButtonDefaults.elevation(4.dp)
            ) {
                Text(
                    text = "Confirm location",
                    style = Typography.h2,
                    color = Color.Black,
                    fontSize = 14.sp
                )
            }
        }
    }
}

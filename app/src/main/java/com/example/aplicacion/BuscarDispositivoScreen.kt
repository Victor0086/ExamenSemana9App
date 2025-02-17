import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

@Composable
fun BuscarDispositivoScreen() {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var locationText by remember { mutableStateOf("Ubicación no obtenida") }
    var permisoDenegado by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted || coarseLocationGranted) {
            obtenerUbicacion(fusedLocationClient) { location ->
                locationText = "Lat: ${location.latitude}, Lng: ${location.longitude}"
            }
        } else {
            permisoDenegado = true
            locationText = "Permiso denegado. Actívalo en Configuración."
        }
    }

    // Forzar solicitud de permisos al abrir la pantalla
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = locationText)
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = {
            locationPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }) {
            Text(text = "Solicitar Permiso")
        }
        Button(onClick = {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacion(fusedLocationClient) { location ->
                    locationText = "Lat: ${location.latitude}, Lng: ${location.longitude}"
                }
            } else {
                locationText = "Primero otorga los permisos"
            }
        }) {
            Text(text = "Obtener ubicación")
        }
        if (permisoDenegado) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "Activa los permisos en la configuración del sistema.", color = androidx.compose.ui.graphics.Color.Red)
        }
    }
}

@SuppressLint("MissingPermission")
fun obtenerUbicacion(fusedLocationClient: FusedLocationProviderClient, onLocationReceived: (Location) -> Unit) {
    try {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                onLocationReceived(location)
            } else {
                Log.e("BuscarDispositivo", "No se pudo obtener la ubicación.")
            }
        }
    } catch (e: SecurityException) {
        Log.e("BuscarDispositivo", "Error de permisos: ${e.message}")
    }
}

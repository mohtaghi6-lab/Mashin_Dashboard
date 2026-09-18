package peugeot.platform.android.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import peugeot.platform.android.vehicle.VehicleData

@Composable
fun DashboardView(
    vehicleData: VehicleData = VehicleData.demo()
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "PEUGEOT VEHICLE OS",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "${vehicleData.speedKmh}",
            style = MaterialTheme.typography.displayLarge
        )

        Text(
            text = "km/h",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "RPM : ${vehicleData.rpm}"
        )

        Text(
            text = "Engine Temp : ${vehicleData.engineTempC} °C"
        )

        Text(
            text = "Battery : ${vehicleData.batteryVoltage} V"
        )

        Text(
            text = if (vehicleData.canConnected)
                "CAN Connected"
            else
                "CAN Waiting"
        )

        Text(
            text = "ECU Errors : ${vehicleData.ecuErrorCount}"
        )
    }
}

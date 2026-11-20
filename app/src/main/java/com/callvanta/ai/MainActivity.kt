package com.callvanta.ai

import android.app.role.RoleManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private enum class ScreeningRoleState {
    UNAVAILABLE,
    NOT_ENABLED,
    GRANTED,
    DENIED,
    REVOKED
}

class MainActivity : ComponentActivity() {
    private var roleState by mutableStateOf(ScreeningRoleState.NOT_ENABLED)
    private var requestAttempted = false
    private var roleWasGranted = false

    private val roleRequest = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        refreshRoleState()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        refreshRoleState()
        setContent {
            MaterialTheme {
                CallvantaHome(
                    roleState = roleState,
                    onEnableScreening = ::requestScreeningRole
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshRoleState()
    }

    private fun requestScreeningRole() {
        val roleManager = getSystemService(RoleManager::class.java)
        if (!roleManager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING)) {
            roleState = ScreeningRoleState.UNAVAILABLE
            return
        }
        if (roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)) {
            roleState = ScreeningRoleState.GRANTED
            roleWasGranted = true
            return
        }

        requestAttempted = true
        roleRequest.launch(roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING))
    }

    private fun refreshRoleState() {
        val roleManager = getSystemService(RoleManager::class.java)
        roleState = when {
            !roleManager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING) ->
                ScreeningRoleState.UNAVAILABLE
            roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING) -> {
                roleWasGranted = true
                ScreeningRoleState.GRANTED
            }
            roleWasGranted -> ScreeningRoleState.REVOKED
            requestAttempted -> ScreeningRoleState.DENIED
            else -> ScreeningRoleState.NOT_ENABLED
        }
    }
}

@androidx.compose.runtime.Composable
private fun CallvantaHome(
    roleState: ScreeningRoleState,
    onEnableScreening: () -> Unit
) {
    val (statusTitle, statusDescription) = when (roleState) {
        ScreeningRoleState.UNAVAILABLE -> "Unavailable" to
            "This Android device does not currently offer the call-screening role to Callvanta."
        ScreeningRoleState.NOT_ENABLED -> "Setup required" to
            "Call screening is not enabled. Android requires you to grant Callvanta the call-screening role before it can screen incoming calls."
        ScreeningRoleState.GRANTED -> "Call screening enabled" to
            "Android has granted Callvanta the call-screening role. Screening rules are not configured yet."
        ScreeningRoleState.DENIED -> "Permission not granted" to
            "The call-screening role was not granted. You can try again whenever you are ready."
        ScreeningRoleState.REVOKED -> "Access revoked" to
            "Callvanta no longer holds the call-screening role. Re-enable it below if you want to use call screening."
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(PaddingValues(horizontal = 20.dp, vertical = 28.dp)),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Callvanta",
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    text = "Your cellular call assistant",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = statusTitle, style = MaterialTheme.typography.titleMedium)
                    Text(text = statusDescription, style = MaterialTheme.typography.bodyMedium)
                    if (roleState != ScreeningRoleState.GRANTED &&
                        roleState != ScreeningRoleState.UNAVAILABLE
                    ) {
                        Button(
                            onClick = onEnableScreening,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                if (roleState == ScreeningRoleState.NOT_ENABLED) {
                                    "Set up call screening"
                                } else {
                                    "Try again"
                                }
                            )
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "First release", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "• Incoming SIM calls only\\n" +
                        "• User-controlled local screening\\n" +
                        "• Callvanta-observed activity only\\n" +
                        "• No call recording or AI answering",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = "Callvanta does not access cellular call audio. Screening decisions will remain local and will not depend on an internet connection.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

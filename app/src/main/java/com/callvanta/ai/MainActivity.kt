package com.callvanta.ai

import android.app.role.RoleManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.callvanta.ai.data.rules.ScreeningRuleStore
import com.callvanta.ai.domain.screening.*
import java.util.UUID

private enum class ScreeningRoleState { UNAVAILABLE, NOT_ENABLED, GRANTED, DENIED, REVOKED }
private enum class RuleType { EXACT, PREFIX, WITHHELD_UNKNOWN }

class MainActivity : ComponentActivity() {
    private var roleState by mutableStateOf(ScreeningRoleState.NOT_ENABLED)
    private var rules by mutableStateOf<List<ScreeningRule>>(emptyList())
    private var requestAttempted = false
    private var roleWasGranted = false
    private val ruleStore by lazy { ScreeningRuleStore(applicationContext) }
    private val roleRequest = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        refreshRoleState()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rules = ruleStore.loadRules()
        CallvantaApplication.replaceRules(rules)
        refreshRoleState()
        setContent {
            MaterialTheme {
                CallvantaHome(roleState, rules, ::requestScreeningRole, ::saveRule, ::toggleRule, ::deleteRule)
            }
        }
    }

    override fun onResume() { super.onResume(); refreshRoleState() }

    private fun saveRule(existingId: String?, type: RuleType, value: String, action: ScreeningAction) {
        val matcher = when (type) {
            RuleType.EXACT -> CallerMatcher.ExactNumber(value.trim())
            RuleType.PREFIX -> CallerMatcher.NumberPrefix(value.trim())
            RuleType.WITHHELD_UNKNOWN -> CallerMatcher.WithheldOrUnknown
        }
        val old = rules.firstOrNull { it.id == existingId }
        val updated = if (old == null) {
            rules + ScreeningRule(UUID.randomUUID().toString(), 0,
                (rules.maxOfOrNull { it.creationOrder } ?: 0L) + 1L, true, matcher, action)
        } else rules.map { if (it.id == existingId) it.copy(matcher = matcher, action = action) else it }
        publishRules(updated)
    }

    private fun toggleRule(id: String, enabled: Boolean) =
        publishRules(rules.map { if (it.id == id) it.copy(enabled = enabled) else it })

    private fun deleteRule(id: String) = publishRules(rules.filterNot { it.id == id })

    private fun publishRules(updated: List<ScreeningRule>) {
        rules = updated
        ruleStore.saveRules(updated)
        CallvantaApplication.replaceRules(updated)
    }

    private fun requestScreeningRole() {
        val manager = getSystemService(RoleManager::class.java)
        if (!manager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING)) roleState = ScreeningRoleState.UNAVAILABLE
        else if (manager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)) {
            roleState = ScreeningRoleState.GRANTED; roleWasGranted = true
        } else {
            requestAttempted = true
            roleRequest.launch(manager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING))
        }
    }

    private fun refreshRoleState() {
        val manager = getSystemService(RoleManager::class.java)
        roleState = when {
            !manager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING) -> ScreeningRoleState.UNAVAILABLE
            manager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING) -> {
                roleWasGranted = true; ScreeningRoleState.GRANTED
            }
            roleWasGranted -> ScreeningRoleState.REVOKED
            requestAttempted -> ScreeningRoleState.DENIED
            else -> ScreeningRoleState.NOT_ENABLED
        }
    }
}

@Composable
private fun CallvantaHome(
    roleState: ScreeningRoleState,
    rules: List<ScreeningRule>,
    onEnableScreening: () -> Unit,
    onSaveRule: (String?, RuleType, String, ScreeningAction) -> Unit,
    onToggleRule: (String, Boolean) -> Unit,
    onDeleteRule: (String) -> Unit
) {
    var selectedId by remember { mutableStateOf<String?>(null) }
    var ruleType by remember { mutableStateOf(RuleType.EXACT) }
    var number by remember { mutableStateOf("") }
    var action by remember { mutableStateOf(ScreeningAction.REJECT) }
    var validation by remember { mutableStateOf<String?>(null) }

    val status = when (roleState) {
        ScreeningRoleState.UNAVAILABLE -> "Call screening is unavailable on this device."
        ScreeningRoleState.NOT_ENABLED -> "Enable Android call screening to activate Callvanta."
        ScreeningRoleState.GRANTED -> "Android has granted Callvanta the call-screening role."
        ScreeningRoleState.DENIED -> "The call-screening role was not granted. You can try again."
        ScreeningRoleState.REVOKED -> "Call screening access was revoked. Re-enable it to screen calls."
    }

    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Callvanta", style = MaterialTheme.typography.headlineLarge)
            Text("Your cellular call assistant", style = MaterialTheme.typography.bodyLarge)
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(if (roleState == ScreeningRoleState.GRANTED) "Call screening enabled" else "Call screening setup")
                    Text(status)
                    if (roleState != ScreeningRoleState.GRANTED && roleState != ScreeningRoleState.UNAVAILABLE) {
                        Button(onClick = onEnableScreening) { Text("Set up call screening") }
                    }
                }
            }
            Text("Screening rules", style = MaterialTheme.typography.titleLarge)
            Text("Rules stay on this device. If no rule matches, the call is allowed.")
            Text("Caller type", style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                RuleType.entries.forEach { type ->
                    if (ruleType == type) Button(onClick = { ruleType = type; number = "" }) { Text(type.label()) }
                    else OutlinedButton(onClick = { ruleType = type; number = "" }) { Text(type.label()) }
                }
            }
            if (ruleType != RuleType.WITHHELD_UNKNOWN) {
                OutlinedTextField(
                    value = number, onValueChange = { number = it },
                    label = { Text(if (ruleType == RuleType.EXACT) "Phone number" else "Number prefix") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true
                )
            }
            Text("Action", style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                ScreeningAction.entries.forEach { candidate ->
                    if (action == candidate) Button(onClick = { action = candidate }) { Text(candidate.label()) }
                    else OutlinedButton(onClick = { action = candidate }) { Text(candidate.label()) }
                }
            }
            validation?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    if (ruleType != RuleType.WITHHELD_UNKNOWN && number.trim().isEmpty()) {
                        validation = "Enter a phone number or prefix."
                    } else {
                        onSaveRule(selectedId, ruleType, number, action)
                        selectedId = null; number = ""; ruleType = RuleType.EXACT
                        action = ScreeningAction.REJECT; validation = null
                    }
                }) { Text(if (selectedId == null) "Add rule" else "Save changes") }
                if (selectedId != null) OutlinedButton(onClick = {
                    selectedId = null; number = ""; ruleType = RuleType.EXACT
                    action = ScreeningAction.REJECT; validation = null
                }) { Text("Cancel edit") }
            }
            Text("Your rules (${rules.size})", style = MaterialTheme.typography.titleLarge)
            if (rules.isEmpty()) Text("No rules yet. Add a number or withheld/unknown caller rule above.")
            rules.forEach { rule ->
                Card {
                    Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        Text(rule.title(), style = MaterialTheme.typography.titleMedium)
                        Text("Action: ${rule.action.label()}")
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Enabled", modifier = Modifier.weight(1f))
                            Switch(checked = rule.enabled, onCheckedChange = { onToggleRule(rule.id, it) })
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = {
                                selectedId = rule.id; action = rule.action
                                when (val matcher = rule.matcher) {
                                    is CallerMatcher.ExactNumber -> { ruleType = RuleType.EXACT; number = matcher.normalized }
                                    is CallerMatcher.NumberPrefix -> { ruleType = RuleType.PREFIX; number = matcher.prefix }
                                    CallerMatcher.WithheldOrUnknown -> { ruleType = RuleType.WITHHELD_UNKNOWN; number = "" }
                                }
                                validation = null
                            }) { Text("Edit") }
                            OutlinedButton(onClick = {
                                onDeleteRule(rule.id)
                                if (selectedId == rule.id) selectedId = null
                            }) { Text("Delete") }
                        }
                    }
                }
            }
            Text(
                "No call audio is accessed. Screening is local. Activity retention is 30 days; cloud backup is disabled.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun RuleType.label() = when (this) {
    RuleType.EXACT -> "Exact"
    RuleType.PREFIX -> "Prefix"
    RuleType.WITHHELD_UNKNOWN -> "Withheld / unknown"
}
private fun ScreeningAction.label() = when (this) {
    ScreeningAction.ALLOW -> "Allow"
    ScreeningAction.SILENCE -> "Silence"
    ScreeningAction.REJECT -> "Reject"
}
private fun ScreeningRule.title() = when (val value = matcher) {
    is CallerMatcher.ExactNumber -> value.normalized
    is CallerMatcher.NumberPrefix -> "Starts with ${value.prefix}"
    CallerMatcher.WithheldOrUnknown -> "Withheld or unknown caller ID"
}

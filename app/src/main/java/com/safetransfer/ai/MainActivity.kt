package com.safetransfer.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SafeTransferApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafeTransferApp() {
    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("SafeTransfer AI") }
                )
            }
        ) { padding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                TransferRiskScreen()
            }
        }
    }
}

@Composable
fun TransferRiskScreen() {
    var amountText by remember { mutableStateOf("") }
    var destinationCountry by remember { mutableStateOf("") }
    var channel by remember { mutableStateOf("") }

    var isNewBeneficiary by remember { mutableStateOf(true) }
    var isInternational by remember { mutableStateOf(false) }
    var isUnusualHour by remember { mutableStateOf(false) }
    var isNewDevice by remember { mutableStateOf(false) }

    var result by remember { mutableStateOf<RiskResult?>(null) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Simulador de riesgo de fraude para transferencias bancarias",
            style = MaterialTheme.typography.titleMedium
        )

        OutlinedTextField(
            value = amountText,
            onValueChange = { amountText = it },
            label = { Text("Monto de la transferencia (COP)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = destinationCountry,
            onValueChange = { destinationCountry = it },
            label = { Text("País destino (ej: CO, US, MX)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = channel,
            onValueChange = { channel = it },
            label = { Text("Canal (ej: app móvil, web, cajero)") },
            modifier = Modifier.fillMaxWidth()
        )

        RiskSwitchRow(
            title = "Beneficiario nuevo",
            description = "Cuenta a la que nunca se ha enviado dinero antes",
            checked = isNewBeneficiary,
            onCheckedChange = { isNewBeneficiary = it }
        )

        RiskSwitchRow(
            title = "Transferencia internacional",
            description = "Fuera del país de origen de la cuenta",
            checked = isInternational,
            onCheckedChange = { isInternational = it }
        )

        RiskSwitchRow(
            title = "Horario inusual",
            description = "Madrugada o fuera del horario habitual del cliente",
            checked = isUnusualHour,
            onCheckedChange = { isUnusualHour = it }
        )

        RiskSwitchRow(
            title = "Dispositivo no reconocido",
            description = "Celular / navegador que el cliente nunca había usado",
            checked = isNewDevice,
            onCheckedChange = { isNewDevice = it }
        )

        Button(
            onClick = {
                val amount = amountText.toDoubleOrNull() ?: 0.0
                val transfer = Transfer(
                    amount = amount,
                    destinationCountry = destinationCountry.trim().uppercase(),
                    channel = channel.trim(),
                    isNewBeneficiary = isNewBeneficiary,
                    isInternational = isInternational,
                    isUnusualHour = isUnusualHour,
                    isNewDevice = isNewDevice
                )
                result = FraudEngine.evaluate(transfer)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Evaluar riesgo")
        }

        result?.let { risk ->
            Spacer(modifier = Modifier.height(16.dp))
            RiskResultCard(risk)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun RiskSwitchRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(text = description, style = MaterialTheme.typography.bodySmall)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun RiskResultCard(risk: RiskResult) {
    val color = when (risk.level) {
        RiskLevel.LOW -> MaterialTheme.colorScheme.primaryContainer
        RiskLevel.MEDIUM -> MaterialTheme.colorScheme.tertiaryContainer
        RiskLevel.HIGH -> MaterialTheme.colorScheme.errorContainer
    }

    Surface(
        color = color,
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 4.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Riesgo: ${risk.level.displayName}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Score estimado: ${risk.score} / 100",
                style = MaterialTheme.typography.bodyMedium
            )
            Divider()
            Text(
                text = "Factores detectados:",
                style = MaterialTheme.typography.bodyMedium
            )
            risk.reasons.forEach { reason ->
                Text("• $reason", style = MaterialTheme.typography.bodySmall)
            }
            if (risk.recommendations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Recomendaciones:",
                    style = MaterialTheme.typography.bodyMedium
                )
                risk.recommendations.forEach { rec ->
                    Text("• $rec", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

/* =====================  MODELOS Y MOTOR DE RIESGO  ===================== */

data class Transfer(
    val amount: Double,
    val destinationCountry: String,
    val channel: String,
    val isNewBeneficiary: Boolean,
    val isInternational: Boolean,
    val isUnusualHour: Boolean,
    val isNewDevice: Boolean
)

data class RiskResult(
    val score: Int,
    val level: RiskLevel,
    val reasons: List<String>,
    val recommendations: List<String>
)

enum class RiskLevel(val displayName: String) {
    LOW("Bajo"),
    MEDIUM("Medio"),
    HIGH("Alto")
}

object FraudEngine {

    fun evaluate(transfer: Transfer): RiskResult {
        var score = 0
        val reasons = mutableListOf<String>()
        val recommendations = mutableListOf<String>()

        // Regla 1: monto
        when {
            transfer.amount >= 20_000_000 -> {
                score += 40
                reasons += "Monto muy alto (${transfer.amount} COP)."
                recommendations += "Aplicar doble verificación con el cliente."
            }
            transfer.amount >= 5_000_000 -> {
                score += 25
                reasons += "Monto alto (${transfer.amount} COP)."
            }
            transfer.amount <= 0 -> {
                reasons += "Monto no válido, se asume 0."
            }
        }

        // Regla 2: internacional
        if (transfer.isInternational) {
            score += 25
            reasons += "Transferencia internacional."
            recommendations += "Verificar país destino frente a listas de alto riesgo."
        }

        // Regla 3: beneficiario nuevo
        if (transfer.isNewBeneficiary) {
            score += 15
            reasons += "Cuenta beneficiaria nueva."
            recommendations += "Recomendar transferencia de prueba de bajo monto."
        }

        // Regla 4: horario inusual
        if (transfer.isUnusualHour) {
            score += 10
            reasons += "Horario inusual para el cliente."
        }

        // Regla 5: dispositivo nuevo
        if (transfer.isNewDevice) {
            score += 10
            reasons += "Dispositivo no reconocido."
            recommendations += "Solicitar un segundo factor de autenticación."
        }

        // Regla 6: país destino
        val highRiskCountries = setOf("PA", "KY", "VG", "BS", "RU")
        if (transfer.destinationCountry in highRiskCountries) {
            score += 15
            reasons += "País destino clasificado como jurisdicción de alto riesgo (${transfer.destinationCountry})."
        }

        if (score > 100) score = 100

        val level = when {
            score >= 70 -> RiskLevel.HIGH
            score >= 40 -> RiskLevel.MEDIUM
            else -> RiskLevel.LOW
        }

        if (reasons.isEmpty()) {
            reasons += "No se detectaron factores de riesgo relevantes."
        }

        if (recommendations.isEmpty()) {
            recommendations += "Permitir la transacción con monitoreo estándar."
        }

        return RiskResult(
            score = score,
            level = level,
            reasons = reasons,
            recommendations = recommendations
        )
    }
}

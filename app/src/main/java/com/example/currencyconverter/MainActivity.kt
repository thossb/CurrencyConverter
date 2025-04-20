package com.example.currencyconverter

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CurrencyConverterApp()
        }
    }
}

// Nilai tukar *dummy* (1 USD = X mata uang target; update sesuai kebutuhan)
val ratesToUSD = mapOf(
    "USD" to 1.0,
    "IDR" to 16000.0,
    "EUR" to 0.93,
    "JPY" to 151.5,
    "GBP" to 0.80,
    "AUD" to 1.5,
    "MYR" to 4.7,
    "SGD" to 1.36,
    "KRW" to 1370.0
)

val currencies = listOf("USD", "IDR", "EUR", "JPY", "GBP", "AUD", "MYR", "SGD", "KRW")

@Composable
fun CurrencyConverterApp() {
    var amountInput by remember { mutableStateOf("") }
    var fromCurrency by remember { mutableStateOf("USD") }
    var toCurrency by remember { mutableStateOf("IDR") }
    var convertedAmount by remember { mutableStateOf("") }
    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        color = Color(0xFFF5F5F5)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Currency Converter",
                fontSize = 26.sp,
                color = Color(0xFF333333),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(22.dp))
            OutlinedTextField(
                value = amountInput,
                onValueChange = { amountInput = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth(0.9f)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CurrencyDropdown(
                    selected = fromCurrency,
                    label = "From",
                    onSelect = { fromCurrency = it }
                )
                CurrencyDropdown(
                    selected = toCurrency,
                    label = "To",
                    onSelect = { toCurrency = it }
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
            Button(
                shape = RoundedCornerShape(10.dp),
                onClick = {
                    val amount = amountInput.toDoubleOrNull()
                    if (amount == null) {
                        Toast.makeText(context, "Masukkan nominal angka yang benar", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (fromCurrency == toCurrency) {
                        convertedAmount = "%.2f".format(amount)
                        return@Button
                    }
                    val amountInUSD = amount / ratesToUSD[fromCurrency]!!
                    val result = amountInUSD * ratesToUSD[toCurrency]!!
                    convertedAmount = "%.2f".format(result)
                },
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text("Convert")
            }
            Spacer(modifier = Modifier.height(30.dp))
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 7.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Box(Modifier.padding(24.dp).fillMaxWidth()) {
                    Text(
                        text = if (convertedAmount.isNotEmpty())
                            "Result: $amountInput $fromCurrency = $convertedAmount $toCurrency"
                        else
                            "Conversion result will appear here",
                        fontSize = 19.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.79f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun CurrencyDropdown(selected: String, label: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text("$label: $selected")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            currencies.forEach { currency ->
                DropdownMenuItem(
                    text = { Text(currency) },
                    onClick = {
                        onSelect(currency)
                        expanded = false
                    }
                )
            }
        }
    }
}

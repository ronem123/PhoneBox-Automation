package com.phone_box_app.ui.components.arch_dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.phone_box_app.data.model.MobileNumberInfo

/**
 * Created by Ram Mandal on 15/10/2025
 * @System: Apple M1 Pro
 */

/**
 * Show input dialog for mobile number
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobileNumberInputDialog(
    onConfirm: (mobileNumberInfo: MobileNumberInfo) -> Unit,
    onDismiss: () -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    // Common country codes (you can expand this list as needed)
    val countryCodes = listOf(
        "+1 (US)", "+44 (UK)", "+91 (India)", "+61 (Australia)",
        "+81 (Japan)", "+49 (Germany)", "+33 (France)", "+39 (Italy)",
        "+86 (China)", "+977 (Nepal)", "+237 (Cameroon)"
    )

    var selectedCountryCode by remember { mutableStateOf(countryCodes[0]) } // Default country code



    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enter Mobile Number") },
        text = {
            Column {
                // Dropdown for Country Code
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedCountryCode,
                        onValueChange = {},
                        label = { Text("Country Code") },
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        countryCodes.forEach { code ->
                            DropdownMenuItem(
                                text = { Text(code) },
                                onClick = {
                                    selectedCountryCode = code.substringBefore(" ")
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Phone Number Field
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Mobile Number") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (phoneNumber.isNotBlank()) {
                        onConfirm(
                            MobileNumberInfo(
                                countryCode = selectedCountryCode,
                                mobileNumber = phoneNumber
                            )
                        )
                        onDismiss()
                    }
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


package com.lad.muscletracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.lad.muscletracker.data.dao.SupplementWithReminders
import com.lad.muscletracker.data.entity.SupplementReminder
import com.lad.muscletracker.ui.theme.*

@Composable
fun SupplementsScreen(
    supplementsWithReminders: List<SupplementWithReminders>,
    onAddSupplement: (name: String, dosage: String) -> Unit,
    onDeleteSupplement: (com.lad.muscletracker.data.entity.Supplement) -> Unit,
    onAddReminder: (supplementId: Long, hour: Int, minute: Int, label: String) -> Unit,
    onDeleteReminder: (SupplementReminder) -> Unit,
    onToggleReminder: (SupplementReminder) -> Unit,
    onBack: () -> Unit
) {
    var showAddSupplementDialog by remember { mutableStateOf(false) }
    var showAddReminderForId by remember { mutableStateOf<Long?>(null) }
    var supplementToDelete by remember { mutableStateOf<com.lad.muscletracker.data.entity.Supplement?>(null) }
    var reminderToDelete by remember { mutableStateOf<SupplementReminder?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Retour", tint = TextPrimary)
            }
            Text(
                "Supplements",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.weight(1f))
            Text(
                "${supplementsWithReminders.size} supplements",
                color = TextMuted,
                fontSize = 12.sp
            )
        }

        Spacer(Modifier.height(12.dp))

        // Supplement list
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (supplementsWithReminders.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = DarkCard,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.Medication,
                                    null,
                                    tint = TextMuted.copy(alpha = 0.5f),
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Aucun supplement",
                                    color = TextMuted,
                                    fontSize = 13.sp
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Ajoute tes supplements et configure des rappels",
                                    color = TextMuted.copy(alpha = 0.7f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            items(supplementsWithReminders, key = { it.supplement.id }) { swr ->
                SupplementCard(
                    supplementWithReminders = swr,
                    onDeleteSupplement = { supplementToDelete = swr.supplement },
                    onAddReminder = { showAddReminderForId = swr.supplement.id },
                    onDeleteReminder = { reminderToDelete = it },
                    onToggleReminder = onToggleReminder
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Add supplement button
        Button(
            onClick = { showAddSupplementDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Blue600)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Ajouter un supplement", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
    }

    // Add supplement dialog
    if (showAddSupplementDialog) {
        AddSupplementDialog(
            onDismiss = { showAddSupplementDialog = false },
            onConfirm = { name, dosage ->
                onAddSupplement(name, dosage)
                showAddSupplementDialog = false
            }
        )
    }

    // Add reminder dialog
    showAddReminderForId?.let { supplementId ->
        AddReminderDialog(
            onDismiss = { showAddReminderForId = null },
            onConfirm = { hour, minute, label ->
                onAddReminder(supplementId, hour, minute, label)
                showAddReminderForId = null
            }
        )
    }

    supplementToDelete?.let { supplement ->
        AlertDialog(
            onDismissRequest = { supplementToDelete = null },
            title = { Text("Supprimer le supplement?", color = TextPrimary) },
            text = { Text("Tous les rappels associes seront aussi supprimes.", color = TextSecondary) },
            containerColor = DarkSurface,
            confirmButton = {
                TextButton(onClick = {
                    onDeleteSupplement(supplement)
                    supplementToDelete = null
                }) { Text("Supprimer", color = Red500) }
            },
            dismissButton = {
                TextButton(onClick = { supplementToDelete = null }) { Text("Annuler", color = TextSecondary) }
            }
        )
    }

    reminderToDelete?.let { reminder ->
        AlertDialog(
            onDismissRequest = { reminderToDelete = null },
            title = { Text("Supprimer ce rappel?", color = TextPrimary) },
            containerColor = DarkSurface,
            confirmButton = {
                TextButton(onClick = {
                    onDeleteReminder(reminder)
                    reminderToDelete = null
                }) { Text("Supprimer", color = Red500) }
            },
            dismissButton = {
                TextButton(onClick = { reminderToDelete = null }) { Text("Annuler", color = TextSecondary) }
            }
        )
    }
}

@Composable
private fun SupplementCard(
    supplementWithReminders: SupplementWithReminders,
    onDeleteSupplement: () -> Unit,
    onAddReminder: () -> Unit,
    onDeleteReminder: (SupplementReminder) -> Unit,
    onToggleReminder: (SupplementReminder) -> Unit
) {
    val supplement = supplementWithReminders.supplement
    val reminders = supplementWithReminders.reminders

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = DarkCard,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.Medication,
                    null,
                    tint = Blue400,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        supplement.name,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                    if (supplement.dosage.isNotBlank()) {
                        Text(
                            supplement.dosage,
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
                IconButton(
                    onClick = onDeleteSupplement,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        "Supprimer",
                        tint = Red500.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Reminders
            if (reminders.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))

                reminders.forEach { reminder ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.Alarm,
                            null,
                            tint = if (reminder.isEnabled) Orange500 else TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            String.format("%02d:%02d", reminder.hour, reminder.minute),
                            color = if (reminder.isEnabled) TextPrimary else TextMuted,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                        if (reminder.timeLabel.isNotBlank()) {
                            Spacer(Modifier.width(8.dp))
                            Text(
                                reminder.timeLabel,
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        Switch(
                            checked = reminder.isEnabled,
                            onCheckedChange = { onToggleReminder(reminder) },
                            modifier = Modifier.height(24.dp),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Green500,
                                checkedTrackColor = Green500.copy(alpha = 0.3f),
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = DarkBorder
                            )
                        )
                        Spacer(Modifier.width(4.dp))
                        IconButton(
                            onClick = { onDeleteReminder(reminder) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                "Supprimer rappel",
                                tint = Red500.copy(alpha = 0.5f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // Add reminder button
            Spacer(Modifier.height(8.dp))
            TextButton(
                onClick = onAddReminder,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.AddAlarm, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Ajouter un rappel", fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun AddSupplementDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, dosage: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = DarkSurface
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "Nouveau supplement",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = Blue500,
                        focusedBorderColor = Blue500,
                        unfocusedBorderColor = DarkBorder,
                        focusedLabelColor = Blue400,
                        unfocusedLabelColor = TextMuted
                    )
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = dosage,
                    onValueChange = { dosage = it },
                    label = { Text("Dosage (ex: 5g, 1 gelule)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = Blue500,
                        focusedBorderColor = Blue500,
                        unfocusedBorderColor = DarkBorder,
                        focusedLabelColor = Blue400,
                        unfocusedLabelColor = TextMuted
                    )
                )

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Annuler")
                    }
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onConfirm(name.trim(), dosage.trim())
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Blue600),
                        enabled = name.isNotBlank()
                    ) {
                        Text("Ajouter")
                    }
                }
            }
        }
    }
}

@Composable
private fun AddReminderDialog(
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int, label: String) -> Unit
) {
    var hourText by remember { mutableStateOf("8") }
    var minuteText by remember { mutableStateOf("00") }
    var label by remember { mutableStateOf("") }

    val presets = listOf(
        Triple(7, 0, "Matin"),
        Triple(12, 0, "Midi"),
        Triple(18, 0, "Soir"),
        Triple(22, 0, "Coucher")
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = DarkSurface
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "Nouveau rappel",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(16.dp))

                // Presets
                Text("Presets", color = TextSecondary, fontSize = 12.sp)
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presets.forEach { (h, m, l) ->
                        FilledTonalButton(
                            onClick = {
                                hourText = "$h"
                                minuteText = String.format("%02d", m)
                                label = l
                            },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Blue500.copy(alpha = 0.2f)
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.weight(1f).height(32.dp)
                        ) {
                            Text(l, fontSize = 11.sp, color = Blue400)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Hour + Minute
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = hourText,
                        onValueChange = { hourText = it.filter { c -> c.isDigit() }.take(2) },
                        label = { Text("Heure") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = Blue500,
                            focusedBorderColor = Blue500,
                            unfocusedBorderColor = DarkBorder,
                            focusedLabelColor = Blue400,
                            unfocusedLabelColor = TextMuted
                        )
                    )
                    OutlinedTextField(
                        value = minuteText,
                        onValueChange = { minuteText = it.filter { c -> c.isDigit() }.take(2) },
                        label = { Text("Minute") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = Blue500,
                            focusedBorderColor = Blue500,
                            unfocusedBorderColor = DarkBorder,
                            focusedLabelColor = Blue400,
                            unfocusedLabelColor = TextMuted
                        )
                    )
                }

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Label (ex: Matin, Soir)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = Blue500,
                        focusedBorderColor = Blue500,
                        unfocusedBorderColor = DarkBorder,
                        focusedLabelColor = Blue400,
                        unfocusedLabelColor = TextMuted
                    )
                )

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Annuler")
                    }
                    Button(
                        onClick = {
                            val hour = hourText.toIntOrNull()?.coerceIn(0, 23) ?: 8
                            val minute = minuteText.toIntOrNull()?.coerceIn(0, 59) ?: 0
                            onConfirm(hour, minute, label.trim())
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Blue600)
                    ) {
                        Text("Ajouter")
                    }
                }
            }
        }
    }
}

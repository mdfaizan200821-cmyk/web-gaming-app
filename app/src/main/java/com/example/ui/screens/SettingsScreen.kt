package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.UserEntity
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    user: UserEntity?,
    onSaveSettings: (music: Boolean, sfx: Boolean, chat: Boolean, language: String) -> Unit,
    onSignOut: () -> Unit
) {
    var musicEnabled by remember { mutableStateOf(user?.musicEnabled ?: true) }
    var sfxEnabled by remember { mutableStateOf(user?.sfxEnabled ?: true) }
    var chatEnabled by remember { mutableStateOf(user?.chatEnabled ?: true) }
    var selectedLanguage by remember { mutableStateOf(user?.selectedLanguage ?: "English") }
    var languageExpanded by remember { mutableStateOf(false) }

    val languages = listOf("English", "Hindi", "Spanish", "Portuguese", "French", "German")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CrispBackground)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "APP & GAME SETTINGS",
            fontWeight = FontWeight.Black,
            fontSize = 20.sp,
            color = DarkCharcoal
        )

        // Audio Controls
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SurfaceCardVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("AUDIO & CHAT CONTROLS", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MediumGray)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Background Music", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DarkCharcoal)
                    Switch(
                        checked = musicEnabled,
                        onCheckedChange = {
                            musicEnabled = it
                            onSaveSettings(musicEnabled, sfxEnabled, chatEnabled, selectedLanguage)
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = RoyalPurple, checkedTrackColor = ElectricIndigo.copy(alpha = 0.3f))
                    )
                }

                Divider(color = LightBorder)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Sound Effects (SFX)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DarkCharcoal)
                    Switch(
                        checked = sfxEnabled,
                        onCheckedChange = {
                            sfxEnabled = it
                            onSaveSettings(musicEnabled, sfxEnabled, chatEnabled, selectedLanguage)
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = RoyalPurple, checkedTrackColor = ElectricIndigo.copy(alpha = 0.3f))
                    )
                }

                Divider(color = LightBorder)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("In-Game Chat Messages", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DarkCharcoal)
                    Switch(
                        checked = chatEnabled,
                        onCheckedChange = {
                            chatEnabled = it
                            onSaveSettings(musicEnabled, sfxEnabled, chatEnabled, selectedLanguage)
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = RoyalPurple, checkedTrackColor = ElectricIndigo.copy(alpha = 0.3f))
                    )
                }
            }
        }

        // Language Selector
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SurfaceCardVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("APP LANGUAGE", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MediumGray)

                ExposedDropdownMenuBox(
                    expanded = languageExpanded,
                    onExpandedChange = { languageExpanded = !languageExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedLanguage,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = languageExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RoyalPurple,
                            unfocusedBorderColor = LightBorder
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = languageExpanded,
                        onDismissRequest = { languageExpanded = false }
                    ) {
                        languages.forEach { lang ->
                            DropdownMenuItem(
                                text = { Text(lang) },
                                onClick = {
                                    selectedLanguage = lang
                                    languageExpanded = false
                                    onSaveSettings(musicEnabled, sfxEnabled, chatEnabled, selectedLanguage)
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onSignOut,
            colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("SIGN OUT", fontWeight = FontWeight.Bold)
        }
    }
}

package com.manalejandro.motivame.ui.screens

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.net.toUri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.manalejandro.motivame.R
import com.manalejandro.motivame.notifications.NotificationHelper
import com.manalejandro.motivame.ui.viewmodel.TaskViewModel
import com.manalejandro.motivame.util.LocaleHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: TaskViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val notificationEnabled by viewModel.notificationEnabled.collectAsState()
    val soundEnabled by viewModel.soundEnabled.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val currentLanguage by viewModel.language.collectAsState()

    BackHandler { onNavigateBack() }

    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
    }

    var languageMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_content_desc))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Idioma ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.language_section),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.language_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val selectedLang = LocaleHelper.SUPPORTED_LANGUAGES
                            .firstOrNull { it.code == currentLanguage }
                            ?: LocaleHelper.SUPPORTED_LANGUAGES.first()

                        Box {
                            OutlinedButton(
                                onClick = { languageMenuExpanded = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "${selectedLang.flag}  ${selectedLang.nativeName}",
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                            DropdownMenu(
                                expanded = languageMenuExpanded,
                                onDismissRequest = { languageMenuExpanded = false }
                            ) {
                                LocaleHelper.SUPPORTED_LANGUAGES.forEach { lang ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(text = lang.flag, style = MaterialTheme.typography.bodyLarge)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(text = lang.nativeName)
                                                if (lang.code == currentLanguage) {
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Icon(
                                                        Icons.Default.Check,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(16.dp),
                                                        tint = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                            }
                                        },
                                        onClick = {
                                            languageMenuExpanded = false
                                            if (lang.code != currentLanguage) {
                                                scope.launch {
                                                    viewModel.setLanguage(lang.code)
                                                    // Recrear la Activity para aplicar el nuevo locale
                                                    (context as? Activity)?.recreate()
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- Notificaciones ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.notifications_section),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.daily_reminders_setting),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = stringResource(R.string.daily_reminders_desc),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = notificationEnabled && hasNotificationPermission,
                                onCheckedChange = { enabled ->
                                    if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
                                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    } else {
                                        viewModel.toggleNotifications(enabled)
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.sound_setting),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = stringResource(R.string.sound_desc),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = soundEnabled,
                                onCheckedChange = { viewModel.toggleSound(it) }
                            )
                        }
                    }
                }
            }

            // --- Prueba ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.test_section),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.test_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    scope.launch {
                                        val notificationHelper = NotificationHelper(context)
                                        notificationHelper.sendMotivationalReminder(tasks, soundEnabled)
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = tasks.isNotEmpty()
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.send_test_notification))
                        }

                        if (tasks.isEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.add_task_to_test),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            // --- Sobre la app ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.about_section),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.app_version),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = stringResource(R.string.app_description),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Desarrollado por
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.developed_by) + " ",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                            )
                            Text(
                                text = stringResource(R.string.developer_url),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                textDecoration = TextDecoration.Underline,
                                modifier = Modifier.clickable {
                                    val intent = Intent(Intent.ACTION_VIEW, "https://manalejandro.com".toUri())
                                    context.startActivity(intent)
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Repositorio GitHub
                        val githubUrl = stringResource(R.string.github_url)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.github_label) + ": ",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                            )
                            Text(
                                text = stringResource(R.string.github_url),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                textDecoration = TextDecoration.Underline,
                                modifier = Modifier.clickable {
                                    val intent = Intent(Intent.ACTION_VIEW, githubUrl.toUri())
                                    context.startActivity(intent)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

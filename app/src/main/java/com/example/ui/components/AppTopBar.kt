package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppNotification
import com.example.model.Campus
import com.example.model.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    selectedCampus: Campus,
    onSelectCampus: (Campus) -> Unit,
    currentRole: UserRole,
    currentUser: com.example.model.UserProfile,
    onOpenRoleSwitcher: () -> Unit,
    notifications: List<AppNotification>,
    onMarkNotificationsRead: () -> Unit,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    var campusMenuExpanded by remember { mutableStateOf(false) }
    var notificationsMenuExpanded by remember { mutableStateOf(false) }
    var userMenuExpanded by remember { mutableStateOf(false) }

    val unreadCount = notifications.count { !it.isRead }

    TopAppBar(
        modifier = modifier.testTag("app_top_bar"),
        title = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "CampusCred",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "MSOT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }

                // Campus selector dropdown chip
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { campusMenuExpanded = true }
                            .padding(vertical = 2.dp)
                            .testTag("campus_selector_dropdown"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Place,
                            contentDescription = "Campus",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = selectedCampus.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = campusMenuExpanded,
                        onDismissRequest = { campusMenuExpanded = false }
                    ) {
                        Campus.values().forEach { campus ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(campus.displayName, fontWeight = if (campus == selectedCampus) FontWeight.Bold else FontWeight.Normal)
                                        Text(campus.city, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                },
                                onClick = {
                                    onSelectCampus(campus)
                                    campusMenuExpanded = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.LocationCity,
                                        contentDescription = null,
                                        tint = if (campus == selectedCampus) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            )
                        }
                    }
                }
            }
        },
        actions = {
            // Role switcher pill
            AssistChip(
                onClick = onOpenRoleSwitcher,
                label = { Text(currentRole.label.split(" ").first(), fontSize = 11.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = when (currentRole) {
                            UserRole.STUDENT -> Icons.Filled.School
                            UserRole.COORDINATOR -> Icons.Filled.Badge
                            UserRole.ADVISOR -> Icons.Filled.SupervisorAccount
                            UserRole.CAMPUS_MANAGER -> Icons.Filled.AdminPanelSettings
                        },
                        contentDescription = "Role",
                        modifier = Modifier.size(14.dp)
                    )
                },
                modifier = Modifier.padding(end = 4.dp).testTag("role_badge_chip")
            )

            // Dark Mode Toggle
            IconButton(
                onClick = onToggleDarkMode,
                modifier = Modifier.testTag("toggle_dark_mode_button")
            ) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                    contentDescription = "Toggle Theme"
                )
            }

            // Notifications Bell with Badge & Dropdown
            Box {
                IconButton(
                    onClick = {
                        notificationsMenuExpanded = true
                        onMarkNotificationsRead()
                    },
                    modifier = Modifier.testTag("notifications_bell_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadCount > 0) {
                                Badge { Text("$unreadCount") }
                            }
                        }
                    ) {
                        Icon(Icons.Filled.Notifications, contentDescription = "Notifications")
                    }
                }

                DropdownMenu(
                    expanded = notificationsMenuExpanded,
                    onDismissRequest = { notificationsMenuExpanded = false },
                    modifier = Modifier.width(300.dp)
                ) {
                    Text(
                        text = "Push Notifications & Alerts",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    Divider()

                    if (notifications.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("No notifications yet", style = MaterialTheme.typography.bodySmall) },
                            onClick = { notificationsMenuExpanded = false }
                        )
                    } else {
                        notifications.take(5).forEach { notif ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(notif.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                                        Text(notif.message, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                                        Text(notif.timestamp, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                },
                                onClick = { notificationsMenuExpanded = false },
                                leadingIcon = {
                                    Icon(
                                        imageVector = when (notif.type) {
                                            "TICKET" -> Icons.Filled.ConfirmationNumber
                                            "EVENT" -> Icons.Filled.Event
                                            "APPROVAL" -> Icons.Filled.Verified
                                            else -> Icons.Filled.AccessTime
                                        },
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // User Profile Avatar & Account Menu
            Box {
                IconButton(
                    onClick = { userMenuExpanded = true },
                    modifier = Modifier.testTag("topbar_user_avatar_button")
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(currentUser.avatarColorHex),
                        modifier = Modifier.size(30.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = currentUser.name.take(1).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                DropdownMenu(
                    expanded = userMenuExpanded,
                    onDismissRequest = { userMenuExpanded = false },
                    modifier = Modifier.width(220.dp)
                ) {
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(currentUser.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Text(currentUser.rollNo, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(currentUser.email, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        },
                        onClick = { userMenuExpanded = false },
                        leadingIcon = {
                            Icon(Icons.Filled.AccountCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    )
                    Divider()
                    DropdownMenuItem(
                        text = { Text("Switch Role") },
                        onClick = {
                            userMenuExpanded = false
                            onOpenRoleSwitcher()
                        },
                        leadingIcon = {
                            Icon(Icons.Filled.SupervisorAccount, contentDescription = null)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Sign Out / Switch", color = MaterialTheme.colorScheme.error) },
                        onClick = {
                            userMenuExpanded = false
                            onSignOut()
                        },
                        leadingIcon = {
                            Icon(Icons.Filled.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        }
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

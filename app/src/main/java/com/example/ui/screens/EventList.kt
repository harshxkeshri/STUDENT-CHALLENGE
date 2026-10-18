package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.firestore.FirestoreEvent
import com.example.data.firestore.FirestoreEventRegistration
import com.example.data.firestore.UiState
import com.example.ui.components.MiraiLogoBadge
import com.example.util.EventReminderManager
import com.example.viewmodel.ClubDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventList(
    viewModel: ClubDashboardViewModel,
    modifier: Modifier = Modifier,
    onRegisterEvent: ((FirestoreEvent) -> Unit)? = null
) {
    val context = LocalContext.current
    val eventsState by viewModel.eventsState.collectAsStateWithLifecycle()
    val userRegistrations by viewModel.userRegistrationsState.collectAsStateWithLifecycle()
    val actionMessage by viewModel.actionMessage.collectAsStateWithLifecycle()

    var filterType by remember { mutableStateOf("All") }
    val filterOptions = listOf("All", "Upcoming", "Inter-Campus", "Tech", "Culture", "Sports")

    val reminderManager = remember { EventReminderManager(context) }
    var selectedEventForAttendance by remember { mutableStateOf<FirestoreEvent?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("event_list")
    ) {
        // Top Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    MiraiLogoBadge(sizeDp = 30.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Upcoming Club Events",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "RSVP, set alerts & track attendance in Firestore",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF2563EB).copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Sort,
                            contentDescription = null,
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Chronological",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2563EB)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Feedback Banner
            AnimatedVisibility(visible = actionMessage != null, enter = fadeIn(), exit = fadeOut()) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = actionMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        IconButton(onClick = { viewModel.clearActionMessage() }, modifier = Modifier.size(20.dp)) {
                            Icon(Icons.Filled.Close, contentDescription = "Dismiss", modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            // Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("events_filter_chips")
            ) {
                items(filterOptions) { opt ->
                    val isSelected = filterType == opt
                    FilterChip(
                        selected = isSelected,
                        onClick = { filterType = opt },
                        label = { Text(opt, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                        leadingIcon = if (isSelected) {
                            { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                        } else null
                    )
                }
            }
        }

        // Event List based on UiState
        when (val state = eventsState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("event_list_loading"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Loading campus events...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            is UiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("event_list_error"),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Filled.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Unable to load events",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { viewModel.retryFetch() },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
            }

            is UiState.Success -> {
                val allEvents = state.data
                val filteredEvents = remember(allEvents, filterType) {
                    when (filterType) {
                        "Inter-Campus" -> allEvents.filter { it.isInterCampus }
                        "Tech" -> allEvents.filter { it.clubId.contains("tech", ignoreCase = true) || it.title.contains("hackathon", ignoreCase = true) }
                        "Culture" -> allEvents.filter { it.clubId.contains("malhar", ignoreCase = true) || it.title.contains("fest", ignoreCase = true) }
                        "Sports" -> allEvents.filter { it.clubId.contains("khel", ignoreCase = true) || it.title.contains("sports", ignoreCase = true) }
                        else -> allEvents
                    }
                }

                if (filteredEvents.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("event_list_empty"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.EventBusy, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No upcoming events found",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Check back soon for new club days and championships.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("events_scrollable_list"),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(filteredEvents, key = { it.eventId }) { event ->
                            val userRegistration = userRegistrations[event.eventId]
                            EventCardItem(
                                event = event,
                                userRegistration = userRegistration,
                                reminderManager = reminderManager,
                                onRsvpClick = {
                                    if (userRegistration != null && userRegistration.attendanceStatus in listOf("REGISTERED", "CONFIRMED", "ATTENDED")) {
                                        selectedEventForAttendance = event
                                    } else {
                                        viewModel.rsvpEvent(event.eventId, attendanceStatus = "REGISTERED")
                                        Toast.makeText(context, "RSVP Confirmed for ${event.title}!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                onManageAttendance = {
                                    selectedEventForAttendance = event
                                },
                                onRegisterClick = { onRegisterEvent?.invoke(event) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Attendance Status Update Dialog
    selectedEventForAttendance?.let { event ->
        val currentReg = userRegistrations[event.eventId]
        AttendanceStatusDialog(
            event = event,
            currentRegistration = currentReg,
            onDismiss = { selectedEventForAttendance = null },
            onUpdateStatus = { newStatus ->
                viewModel.updateAttendanceStatus(event.eventId, newStatus)
                selectedEventForAttendance = null
                Toast.makeText(context, "Attendance updated to $newStatus", Toast.LENGTH_SHORT).show()
            },
            onCancelRsvp = {
                viewModel.cancelRsvp(event.eventId)
                selectedEventForAttendance = null
                Toast.makeText(context, "RSVP Cancelled", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

/**
 * Event card item displayed in the sorted event list with RSVP button and reminder toggle
 */
@Composable
fun EventCardItem(
    event: FirestoreEvent,
    userRegistration: FirestoreEventRegistration?,
    reminderManager: EventReminderManager,
    onRsvpClick: () -> Unit,
    onManageAttendance: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isReminderSet by remember(event.eventId) {
        mutableStateOf(reminderManager.isReminderSet(event.eventId))
    }
    var reminderLabel by remember(event.eventId) {
        mutableStateOf(reminderManager.getSavedReminderLabel(event.eventId))
    }

    val isRsvpd = userRegistration != null && userRegistration.attendanceStatus in listOf("REGISTERED", "CONFIRMED", "ATTENDED")
    val attendanceStatus = userRegistration?.attendanceStatus ?: "NONE"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("event_card_${event.eventId}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top tag row: Date Pill + Duty Leave Badge + Inter-Campus
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Date & Time Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${event.date} • ${event.time}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // Duty Leave & Inter-campus Tag
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (event.isDutyLeaveEligible) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Verified,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Duty Leave",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF10B981)
                                )
                            }
                        }
                    }

                    if (isRsvpd) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFF2563EB).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "RSVP: $attendanceStatus",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2563EB),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Event Title
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Club Name tag & Venue
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = event.clubName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.Filled.Place,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "${event.venue} (${event.campus})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Reminder Toggle Row
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isReminderSet) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reminder_toggle_${event.eventId}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isReminderSet) Icons.Filled.NotificationsActive else Icons.Outlined.Notifications,
                            contentDescription = "Event Reminder Toggle",
                            tint = if (isReminderSet) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isReminderSet) "Reminder Active ($reminderLabel)" else "Event Reminder Alert",
                                fontSize = 12.sp,
                                fontWeight = if (isReminderSet) FontWeight.Bold else FontWeight.Medium,
                                color = if (isReminderSet) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isReminderSet) "Alert saved: notifying before start" else "Alert me 30 mins before start",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = isReminderSet,
                        onCheckedChange = { checked ->
                            if (checked) {
                                val scheduledTime = reminderManager.scheduleReminder(
                                    eventId = event.eventId,
                                    eventTitle = event.title,
                                    venue = event.venue,
                                    eventDate = event.date,
                                    eventTime = event.time,
                                    minutesBefore = 30
                                )
                                isReminderSet = true
                                reminderLabel = "30m before"
                                Toast.makeText(
                                    context,
                                    "Reminder alert saved for ${event.title} (30 mins before)",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                reminderManager.cancelReminder(event.eventId)
                                isReminderSet = false
                                reminderLabel = ""
                                Toast.makeText(context, "Reminder cancelled", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .testTag("reminder_switch_${event.eventId}")
                            .scale(0.85f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Footer: Attendance Capacity + RSVP Button + Passes
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.ConfirmationNumber,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${event.registeredCount} / ${event.capacity} registered",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // RSVP Button
                    if (isRsvpd) {
                        FilledTonalButton(
                            onClick = onManageAttendance,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFF10B981).copy(alpha = 0.18f),
                                contentColor = Color(0xFF047857)
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("rsvp_button_${event.eventId}")
                        ) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = when (attendanceStatus) {
                                    "ATTENDED" -> "Attended ✓"
                                    "CONFIRMED" -> "Confirmed ✓"
                                    else -> "RSVP'd ✓"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Button(
                            onClick = onRsvpClick,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("rsvp_button_${event.eventId}")
                        ) {
                            Icon(Icons.Filled.EventAvailable, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("RSVP", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Passes / Registration Button
                    OutlinedButton(
                        onClick = onRegisterClick,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("register_event_button_${event.eventId}")
                    ) {
                        Text("Passes", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

/**
 * Dialog allowing user to view and update their attendance status in 'registrations' sub-collection
 */
@Composable
fun AttendanceStatusDialog(
    event: FirestoreEvent,
    currentRegistration: FirestoreEventRegistration?,
    onDismiss: () -> Unit,
    onUpdateStatus: (String) -> Unit,
    onCancelRsvp: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().testTag("attendance_status_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Attendance Status",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = event.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${event.date} • ${event.time} @ ${event.venue}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Current Status in Firestore:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = currentRegistration?.attendanceStatus ?: "REGISTERED",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF047857)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Update your attendance status:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Options: CONFIRMED, ATTENDED, CANCEL
                FilledTonalButton(
                    onClick = { onUpdateStatus("CONFIRMED") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Confirm Attendance (Going)")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { onUpdateStatus("ATTENDED") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Badge, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Mark as Checked-in / Attended")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onCancelRsvp,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Cancel, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Cancel RSVP")
                }
            }
        }
    }
}

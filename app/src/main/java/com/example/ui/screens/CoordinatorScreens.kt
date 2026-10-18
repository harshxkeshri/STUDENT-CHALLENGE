package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.QrCodeView

// -------------------------------------------------------------
// 1. COORDINATOR OPERATIONS HUB SCREEN
// -------------------------------------------------------------
@Composable
fun CoordinatorHubScreen(
    user: UserProfile,
    campus: Campus,
    clubs: List<Club>,
    activities: List<ActivityPlan>,
    memberships: List<Membership>,
    attendanceRecords: List<AttendanceRecord>,
    onLaunchStation: (ActivityPlan) -> Unit,
    onOpenCreatePlan: (Club) -> Unit,
    onNavigateToRoster: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Identify coordinator's club (defaults to Kaizen Tech)
    val coordinatorClub = clubs.firstOrNull { it.id == "kaizen_tech" } ?: clubs.first()
    val clubActivities = activities.filter { it.clubId == coordinatorClub.id }
    val activeSession = clubActivities.firstOrNull { it.status == ActivityStatus.CHECK_IN_OPEN } ?: clubActivities.firstOrNull()
    val pendingMembersCount = memberships.count { it.clubId == coordinatorClub.id && it.status == MembershipStatus.PENDING }
    val todayAttendances = attendanceRecords.filter { it.activityId == (activeSession?.id ?: "") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("coordinator_hub_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Operational Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("coord_header_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF0369A1), Color(0xFF0F172A))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Filled.Badge, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("COORDINATOR DESK", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF10B981).copy(alpha = 0.25f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color(0xFF10B981)))
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text("LIVE SESSION READY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA7F3D0))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = coordinatorClub.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "${coordinatorClub.tagLine} • ${campus.displayName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFBAE6FD)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Person, contentDescription = null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Lead Coordinator: ${user.name}", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.width(12.dp))
                            Icon(Icons.Filled.SupervisorAccount, contentDescription = null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Advisor: ${coordinatorClub.facultyAdvisor}", fontSize = 12.sp, color = Color(0xFFE0F2FE))
                        }
                    }
                }
            }
        }

        // Live Check-in Session Banner (Actionable)
        if (activeSession != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("active_station_launcher_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("SATURDAY CLUB DAY STATION", style = MaterialTheme.typography.labelSmall, color = Color(0xFF0284C7), fontWeight = FontWeight.Bold)
                                Text(activeSession.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFE0F2FE)
                            ) {
                                Text(
                                    text = "PIN: ${activeSession.checkInCode}",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF0369A1),
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text("Time: ${activeSession.timeSlot}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Venue: ${activeSession.venue}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { onLaunchStation(activeSession) },
                                modifier = Modifier.weight(1f).testTag("launch_qr_station_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                            ) {
                                Icon(Icons.Filled.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Launch QR Station")
                            }

                            OutlinedButton(
                                onClick = { onOpenCreatePlan(coordinatorClub) },
                                modifier = Modifier.testTag("plan_next_session_button")
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New Plan")
                            }
                        }
                    }
                }
            }
        }

        // Operational Metrics Grid
        item {
            Text("Club Operational Metrics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Metric 1: Members
                Card(
                    modifier = Modifier.weight(1f).clickable { onNavigateToRoster() },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Icon(Icons.Filled.Group, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("${coordinatorClub.memberCount} / ${coordinatorClub.capacity}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Active Roster", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // Metric 2: Pending Applications
                Card(
                    modifier = Modifier.weight(1f).clickable { onNavigateToRoster() },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (pendingMembersCount > 0) Color(0xFFFEF3C7) else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Icon(
                            Icons.Filled.PersonAdd,
                            contentDescription = null,
                            tint = if (pendingMembersCount > 0) Color(0xFFD97706) else Color(0xFF0284C7),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("$pendingMembersCount Pending", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = if (pendingMembersCount > 0) Color(0xFFB45309) else MaterialTheme.colorScheme.onSurface)
                        Text("Requires Approval", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Live Attendance Feed
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Recent Session Check-Ins (${todayAttendances.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }

        if (todayAttendances.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "No student check-ins recorded yet for this session. Launch the QR station to begin scanning.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(todayAttendances) { att ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = if (att.isLate) Color(0xFFF59E0B) else Color(0xFF10B981),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (att.isLate) Icons.Filled.AccessTime else Icons.Filled.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(att.studentName, fontWeight = FontWeight.SemiBold)
                                Text("${att.rollNo} • ${att.timestamp}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (att.isLate) Color(0xFFFEF3C7) else Color(0xFFD1FAE5)
                        ) {
                            Text(
                                text = if (att.isLate) "LATE" else "PRESENT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (att.isLate) Color(0xFFB45309) else Color(0xFF047857),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. COORDINATOR ROSTER SCREEN (Approve/Reject Members)
// -------------------------------------------------------------
@Composable
fun CoordinatorRosterScreen(
    memberships: List<Membership>,
    clubs: List<Club>,
    onApproveMembership: (String) -> Unit,
    onRejectMembership: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("Pending") }
    val coordinatorClub = clubs.firstOrNull { it.id == "kaizen_tech" } ?: clubs.first()

    val clubMemberships = memberships.filter { it.clubId == coordinatorClub.id }
    val pendingList = clubMemberships.filter { it.status == MembershipStatus.PENDING }
    val approvedList = clubMemberships.filter { it.status == MembershipStatus.APPROVED }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("coordinator_roster_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "${coordinatorClub.name} • Membership Roster",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Review student membership applications, verify credentials, and manage active club capacity.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Filter Chips
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = selectedFilter == "Pending",
                    onClick = { selectedFilter = "Pending" },
                    label = { Text("Pending Requests (${pendingList.size})") },
                    leadingIcon = if (selectedFilter == "Pending") {
                        { Icon(Icons.Filled.PendingActions, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null
                )
                FilterChip(
                    selected = selectedFilter == "Enrolled",
                    onClick = { selectedFilter = "Enrolled" },
                    label = { Text("Enrolled Members (${approvedList.size})") },
                    leadingIcon = if (selectedFilter == "Enrolled") {
                        { Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null
                )
            }
        }

        if (selectedFilter == "Pending") {
            if (pendingList.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "No pending membership requests right now. All applicant registrations have been processed.",
                            modifier = Modifier.padding(20.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(pendingList) { mem ->
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("pending_mem_${mem.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFF0284C7),
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(mem.userId.take(2).uppercase(), fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = if (mem.userId == "peer_vikram") "Vikram Patel" else if (mem.userId == "peer_neha") "Neha Rathore" else "Student Applicant (${mem.userId})",
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Applied ${mem.joinedAt} • Role: ${mem.roleInClub}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFFEF3C7)
                                ) {
                                    Text(
                                        text = "PENDING REVIEW",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFB45309),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { onRejectMembership(mem.id) },
                                    modifier = Modifier.weight(1f).testTag("reject_mem_${mem.id}"),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Icon(Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Decline")
                                }

                                Button(
                                    onClick = { onApproveMembership(mem.id) },
                                    modifier = Modifier.weight(1f).testTag("approve_mem_${mem.id}"),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                                ) {
                                    Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Approve")
                                }
                            }
                        }
                    }
                }
            }
        } else {
            items(approvedList) { mem ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Filled.Verified, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (mem.userId == "std_2026_042") "Chirag Sharma" else "Verified Member (${mem.userId})",
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${mem.roleInClub} • Member since ${mem.joinedAt}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFD1FAE5)
                        ) {
                            Text(
                                text = "ENROLLED",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF047857),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. COORDINATOR ACTIVITY PLANS SCREEN
// -------------------------------------------------------------
@Composable
fun CoordinatorPlansScreen(
    clubs: List<Club>,
    activities: List<ActivityPlan>,
    onOpenCreatePlan: (Club) -> Unit,
    modifier: Modifier = Modifier
) {
    val coordinatorClub = clubs.firstOrNull { it.id == "kaizen_tech" } ?: clubs.first()
    val clubActivities = activities.filter { it.clubId == coordinatorClub.id }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("coordinator_plans_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Saturday Activity Plans", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Plan agendas & submit to Faculty Advisor for approval", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Button(
                    onClick = { onOpenCreatePlan(coordinatorClub) },
                    modifier = Modifier.testTag("coord_new_plan_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Plan")
                }
            }
        }

        items(clubActivities) { act ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(act.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("${act.date} • ${act.timeSlot}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (act.status) {
                                ActivityStatus.CHECK_IN_OPEN -> Color(0xFFD1FAE5)
                                ActivityStatus.PLANNED -> Color(0xFFFEF3C7)
                                ActivityStatus.APPROVED -> Color(0xFFE0F2FE)
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        ) {
                            Text(
                                text = when (act.status) {
                                    ActivityStatus.CHECK_IN_OPEN -> "CHECK-IN LIVE"
                                    ActivityStatus.PLANNED -> "PENDING ADVISOR APPROVAL"
                                    ActivityStatus.APPROVED -> "ADVISOR APPROVED"
                                    else -> act.status.name
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (act.status) {
                                    ActivityStatus.CHECK_IN_OPEN -> Color(0xFF047857)
                                    ActivityStatus.PLANNED -> Color(0xFFB45309)
                                    ActivityStatus.APPROVED -> Color(0xFF0369A1)
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Venue: ${act.venue}", style = MaterialTheme.typography.bodySmall)
                    Text("Topic: ${act.topic}", style = MaterialTheme.typography.bodySmall)
                    Text("Deliverable: ${act.deliverableType} (Due: ${act.deadline})", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 4. COORDINATOR LIVE QR CHECK-IN STATION SCREEN
// -------------------------------------------------------------
@Composable
fun CoordinatorStationScreen(
    activities: List<ActivityPlan>,
    attendanceRecords: List<AttendanceRecord>,
    onManualCheckIn: (studentRoll: String, isLate: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeActivity = activities.firstOrNull { it.status == ActivityStatus.CHECK_IN_OPEN } ?: activities.firstOrNull()
    var manualRollInput by remember { mutableStateOf("") }
    var manualSuccessMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("coordinator_station_screen"),
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Live Check-In Station", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                "Project this screen or place at the club room entrance for students to scan.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        if (activeActivity != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(activeActivity.title, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        Text("${activeActivity.date} • ${activeActivity.venue}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Spacer(modifier = Modifier.height(16.dp))

                        // Large Canvas QR Code
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            modifier = Modifier
                                .size(200.dp)
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                QrCodeView(
                                    payload = "CAMPUSCRED:CHECKIN:${activeActivity.id}:${activeActivity.checkInCode}",
                                    modifier = Modifier.size(180.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("OR ENTER 6-DIGIT PIN", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFE0F2FE)
                        ) {
                            Text(
                                text = activeActivity.checkInCode,
                                fontSize = 32.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF0369A1),
                                letterSpacing = 6.sp,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // Manual Student Override Box
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Manual Roll Number Override", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("For students without smartphones or camera issues.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = manualRollInput,
                            onValueChange = { manualRollInput = it },
                            label = { Text("Student Roll No (e.g. MSOT-2023-CS-042)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("manual_roll_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (manualRollInput.isNotBlank()) {
                                        onManualCheckIn(manualRollInput.trim(), false)
                                        manualSuccessMessage = "Recorded Punctual Check-In for $manualRollInput"
                                        manualRollInput = ""
                                    }
                                },
                                modifier = Modifier.weight(1f).testTag("manual_present_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                            ) {
                                Text("Mark Present")
                            }

                            OutlinedButton(
                                onClick = {
                                    if (manualRollInput.isNotBlank()) {
                                        onManualCheckIn(manualRollInput.trim(), true)
                                        manualSuccessMessage = "Recorded Late Check-In for $manualRollInput"
                                        manualRollInput = ""
                                    }
                                },
                                modifier = Modifier.weight(1f).testTag("manual_late_button")
                            ) {
                                Text("Mark Late")
                            }
                        }

                        manualSuccessMessage?.let { msg ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(msg, color = Color(0xFF047857), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

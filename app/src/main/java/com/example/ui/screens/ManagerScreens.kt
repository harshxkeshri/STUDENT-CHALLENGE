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

// -------------------------------------------------------------
// 1. CAMPUS MANAGER EXECUTIVE DASHBOARD SCREEN
// -------------------------------------------------------------
@Composable
fun ManagerDashboardScreen(
    user: UserProfile,
    campus: Campus,
    clubs: List<Club>,
    events: List<Event>,
    tickets: List<Ticket>,
    dutyLeaves: List<DutyLeaveRequest>,
    onNavigateToVenues: () -> Unit,
    onNavigateToFinancials: () -> Unit,
    onNavigateToDutyLeaves: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pendingLeavesCount = dutyLeaves.count { it.status == DutyLeaveStatus.PENDING }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("manager_dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Executive Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("manager_header_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF0F172A), Color(0xFF064E3B))
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
                                    Icon(Icons.Filled.AdminPanelSettings, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("CAMPUS EXECUTIVE DESK", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF10B981).copy(alpha = 0.25f)
                            ) {
                                Text(
                                    "ALL CAMPUSES MONITORED",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFA7F3D0),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "${user.rankTitle} • Overseeing 3 Regional Campuses",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFA7F3D0)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Security, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("AES-256 Daily Encrypted Backups Active • 99.99% Stripe Uptime", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            }
        }

        // Action Alert: Duty Leaves
        if (pendingLeavesCount > 0) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onNavigateToDutyLeaves() },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.AssignmentTurnedIn, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("$pendingLeavesCount Duty Leave Request Awaiting Dean Endorsement", fontWeight = FontWeight.Bold, color = Color(0xFF78350F))
                                Text("Student sports & hackathon representation leaves", style = MaterialTheme.typography.bodySmall, color = Color(0xFF92400E))
                            }
                        }
                        Icon(Icons.Filled.ArrowForward, contentDescription = null, tint = Color(0xFFB45309))
                    }
                }
            }
        }

        // Multi-Campus Comparison Section
        item {
            Text("Regional Campus Network Comparison", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Campus 1: HI-Tech
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("HI-Tech", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Silicon Corridor", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("1,450", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color(0xFF0284C7))
                        Text("Students", fontSize = 10.sp)
                    }
                }

                // Campus 2: Rathinum (Bangalore)
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Rathinum", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Bangalore", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("890", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color(0xFF10B981))
                        Text("Students", fontSize = 10.sp)
                    }
                }

                // Campus 3: Jagannath (Jaipur)
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Jagannath", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Jaipur", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("740", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color(0xFFF59E0B))
                        Text("Students", fontSize = 10.sp)
                    }
                }
            }
        }

        // High-Traffic & Stripe Analytics Quick Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onNavigateToFinancials() },
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
                        Column {
                            Text("STRIPE TICKET REVENUE & INFRASTRUCTURE", style = MaterialTheme.typography.labelSmall, color = Color(0xFF059669), fontWeight = FontWeight.Bold)
                            Text("$12,450.00 Gross Sales", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                        }
                        Icon(Icons.Filled.Payments, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(32.dp))
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Total Tickets Issued: ${tickets.size * 340 + 420} • Peak Throughput: 1,200 req/min", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { 0.88f },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                        color = Color(0xFF059669),
                        trackColor = Color(0xFFD1FAE5)
                    )
                }
            }
        }

        // Fast Action Buttons Grid
        item {
            Text("Executive Controls", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f).clickable { onNavigateToVenues() },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Icon(Icons.Filled.MeetingRoom, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Venues & Clubs", fontWeight = FontWeight.Bold)
                        Text("4 Clubs • 4 Venues", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f).clickable { onNavigateToFinancials() },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Icon(Icons.Filled.Analytics, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Financial Audit", fontWeight = FontWeight.Bold)
                        Text("Stripe Dashboard", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f).clickable { onNavigateToDutyLeaves() },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Icon(Icons.Filled.Campaign, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Broadcast Alert", fontWeight = FontWeight.Bold)
                        Text("Push Alert Console", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. VENUES & LOGISTICS SCREEN
// -------------------------------------------------------------
@Composable
fun ManagerVenuesScreen(
    clubs: List<Club>,
    modifier: Modifier = Modifier
) {
    val venues = listOf(
        Triple("Turing Computer Center - Lab 402", "Kaizen Tech", "Every Saturday 10:00 AM - 1:00 PM • Capacity: 250"),
        Triple("Grand Open-Air Amphitheater", "Malhar", "Every Saturday 1:30 PM - 4:30 PM • Capacity: 350"),
        Triple("Indoor Sports Arena - Courts A & B", "Khel Society", "Every Saturday 7:00 AM - 10:00 AM • Capacity: 280"),
        Triple("Executive Incubation Suites", "Nexcell", "Every Saturday 2:00 PM - 5:00 PM • Capacity: 200")
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("manager_venues_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Campus Venue Allocations", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Manage physical lab, auditorium, and arena schedules for Saturday Club Days.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        items(venues) { (venueName, clubName, schedule) ->
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
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(venueName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFD1FAE5)
                        ) {
                            Text(
                                "ALLOCATED",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF047857),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Assigned Club: $clubName", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                    Text(schedule, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. FINANCIALS & STRIPE ANALYTICS SCREEN
// -------------------------------------------------------------
@Composable
fun ManagerFinancialsScreen(
    events: List<Event>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("manager_financials_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Financial Audit & Stripe Ticketing", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Real-time revenue settlement, transaction throughput, and high-traffic optimization.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("STRIPE NET SETTLEMENT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF34D399))
                    Text("$12,450.00", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    Text("Direct deposit routed to MSOT Institutional Activities Account", fontSize = 12.sp, color = Color(0xFF94A3B8))

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color(0xFF334155))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Disputes", fontSize = 11.sp, color = Color(0xFF94A3B8))
                            Text("0.00%", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Column {
                            Text("Success Rate", fontSize = 11.sp, color = Color(0xFF94A3B8))
                            Text("99.98%", fontWeight = FontWeight.Bold, color = Color(0xFF34D399))
                        }
                        Column {
                            Text("Payout Schedule", fontSize = 11.sp, color = Color(0xFF94A3B8))
                            Text("Daily Rolling", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        item {
            Text("Revenue Breakdown by Event", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        items(events) { evt ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(evt.title, fontWeight = FontWeight.Bold)
                        Text("${evt.clubName} • ${evt.registeredCount}/${evt.capacity} Tickets Sold", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Text(
                        text = "$${evt.registeredCount * 10}",
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF059669),
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 4. DUTY LEAVE & BROADCAST ALERT SCREEN
// -------------------------------------------------------------
@Composable
fun ManagerDutyLeaveScreen(
    dutyLeaves: List<DutyLeaveRequest>,
    onReviewDutyLeave: (requestId: String, isApproved: Boolean, notes: String) -> Unit,
    onBroadcastPush: (title: String, message: String, campus: Campus?) -> Unit,
    modifier: Modifier = Modifier
) {
    var broadcastTitle by remember { mutableStateOf("") }
    var broadcastMessage by remember { mutableStateOf("") }
    var broadcastSuccess by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("manager_duty_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Duty Leave Endorsements & Push Broadcast", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Authorize academic attendance exemptions and transmit high-priority campus alerts.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // Push Broadcast Console
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Emergency & Campus-Wide Push Broadcaster", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = broadcastTitle,
                        onValueChange = { broadcastTitle = it },
                        label = { Text("Announcement Headline") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("broadcast_title_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = broadcastMessage,
                        onValueChange = { broadcastMessage = it },
                        label = { Text("Message Body (sent to all 3 campuses)") },
                        modifier = Modifier.fillMaxWidth().testTag("broadcast_message_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (broadcastTitle.isNotBlank() && broadcastMessage.isNotBlank()) {
                                onBroadcastPush(broadcastTitle.trim(), broadcastMessage.trim(), null)
                                broadcastTitle = ""
                                broadcastMessage = ""
                                broadcastSuccess = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("transmit_broadcast_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                    ) {
                        Icon(Icons.Filled.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Transmit Push Alert to All Campuses")
                    }

                    if (broadcastSuccess) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Broadcast Transmitted to Students in HI-Tech, Bangalore & Jaipur!", color = Color(0xFF047857), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Duty Leave Applications
        item {
            Text("Duty Leave Applications (${dutyLeaves.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        items(dutyLeaves) { req ->
            Card(
                modifier = Modifier.fillMaxWidth().testTag("duty_card_${req.id}"),
                shape = RoundedCornerShape(14.dp),
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
                            Text(req.studentName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text("${req.rollNo} • ${req.campus.displayName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when (req.status) {
                                DutyLeaveStatus.APPROVED -> Color(0xFFD1FAE5)
                                DutyLeaveStatus.PENDING -> Color(0xFFFEF3C7)
                                DutyLeaveStatus.REJECTED -> Color(0xFFFEE2E2)
                            }
                        ) {
                            Text(
                                text = req.status.name,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (req.status) {
                                    DutyLeaveStatus.APPROVED -> Color(0xFF047857)
                                    DutyLeaveStatus.PENDING -> Color(0xFFB45309)
                                    DutyLeaveStatus.REJECTED -> Color(0xFFDC2626)
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Representing Event: ${req.eventTitle}", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                    Text("Excused Dates: ${req.dates}", style = MaterialTheme.typography.bodySmall)
                    Text("Reason: \"${req.reason}\"", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    if (req.status == DutyLeaveStatus.PENDING) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onReviewDutyLeave(req.id, false, "Declined under minimum attendance compliance criteria.") },
                                modifier = Modifier.weight(1f).testTag("decline_duty_button_${req.id}"),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("Decline")
                            }

                            Button(
                                onClick = { onReviewDutyLeave(req.id, true, "Endorsed with Dean digital signature for official collegiate representation.") },
                                modifier = Modifier.weight(1f).testTag("approve_duty_button_${req.id}"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                            ) {
                                Icon(Icons.Filled.Verified, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Endorse Leave")
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.example.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*

@Composable
fun ClubsScreen(
    clubs: List<Club>,
    selectedClubId: String?,
    memberships: List<Membership>,
    activities: List<ActivityPlan>,
    submissions: List<Submission>,
    role: UserRole,
    onJoinClub: (String) -> Unit,
    onLeaveClub: (String) -> Unit,
    onOpenCheckIn: (ActivityPlan) -> Unit,
    onOpenCoordinatorStation: (ActivityPlan) -> Unit,
    onOpenSubmitDeliverable: (ActivityPlan) -> Unit,
    onOpenEvaluateSubmission: (Submission) -> Unit,
    onNavigateToChat: (String) -> Unit,
    onOpenCreateActivityPlan: (Club) -> Unit,
    onApproveActivityPlan: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeClubId by remember(selectedClubId) {
        mutableStateOf(selectedClubId ?: clubs.firstOrNull()?.id ?: "kaizen_tech")
    }

    val currentClub = clubs.firstOrNull { it.id == activeClubId } ?: clubs.first()
    val isMember = memberships.any { it.clubId == currentClub.id }
    val clubActivities = activities.filter { it.clubId == currentClub.id }
    val clubSubmissions = submissions.filter { it.clubName == currentClub.name }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("clubs_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Club Selector Tabs (The 4 Clubs)
        item {
            Text(
                text = "Explore The 4 Clubs",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            ScrollableTabRow(
                selectedTabIndex = clubs.indexOfFirst { it.id == activeClubId }.coerceAtLeast(0),
                edgePadding = 0.dp,
                divider = {},
                modifier = Modifier.fillMaxWidth().testTag("clubs_tab_row")
            ) {
                clubs.forEach { club ->
                    val selected = club.id == activeClubId
                    Tab(
                        selected = selected,
                        onClick = { activeClubId = club.id },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(club.primaryColorHex),
                                    modifier = Modifier.size(10.dp)
                                ) {}
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = club.name,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    )
                }
            }
        }

        // 2. Dedicated Club Header Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("club_header_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(currentClub.primaryColorHex).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = currentClub.category.label,
                                color = Color(currentClub.primaryColorHex),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        AssistChip(
                            onClick = { onNavigateToChat(currentClub.id) },
                            label = { Text("Open Chat", fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(Icons.Filled.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = currentClub.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currentClub.tagLine,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = currentClub.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(12.dp))

                    // Leadership & Meeting Details
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("FACULTY ADVISOR", style = MaterialTheme.typography.labelSmall)
                            Text(currentClub.facultyAdvisor, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("LEAD COORDINATOR", style = MaterialTheme.typography.labelSmall)
                            Text(currentClub.leadCoordinator, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("MEETING SCHEDULE", style = MaterialTheme.typography.labelSmall)
                            Text(currentClub.meetingSchedule, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("CAMPUS HUB", style = MaterialTheme.typography.labelSmall)
                            Text(currentClub.campus.displayName, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }

        // 3. SECTION A: MEMBERSHIP REGISTRATION
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("membership_registration_section"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Membership Registration",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isMember) Color(0xFF10B981) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        ) {
                            Text(
                                text = if (isMember) "VERIFIED MEMBER" else "NOT REGISTERED",
                                color = if (isMember) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Capacity Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Capacity Enrollment", style = MaterialTheme.typography.bodySmall)
                        Text(
                            "${currentClub.memberCount} / ${currentClub.capacity} Members",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { (currentClub.memberCount.toFloat() / currentClub.capacity).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = Color(currentClub.primaryColorHex)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (isMember) {
                        OutlinedButton(
                            onClick = { onLeaveClub(currentClub.id) },
                            modifier = Modifier.fillMaxWidth().testTag("leave_club_button")
                        ) {
                            Icon(Icons.Filled.ExitToApp, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Withdraw / Leave Club")
                        }
                    } else {
                        Button(
                            onClick = { onJoinClub(currentClub.id) },
                            modifier = Modifier.fillMaxWidth().testTag("join_club_button")
                        ) {
                            Icon(Icons.Filled.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Register / Join ${currentClub.name}")
                        }
                    }
                }
            }
        }

        // 4. SECTION B: EVENT SCHEDULES & CLUB DAY CALENDAR
        item {
            Text(
                text = "Event Schedules & Club Days",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (clubActivities.isEmpty()) {
            item {
                Text(
                    text = "No scheduled Club Day sessions for this club currently.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(clubActivities) { act ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("activity_card_${act.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (act.status == ActivityStatus.CHECK_IN_OPEN) Color(0xFF10B981) else MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = act.status.name.replace("_", " "),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (act.status == ActivityStatus.CHECK_IN_OPEN) Color.White else MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(act.date, style = MaterialTheme.typography.labelSmall)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(act.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Venue: ${act.venue} • ${act.timeSlot}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("TOPIC & AGENDA", style = MaterialTheme.typography.labelSmall)
                                Text(act.topic, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("DELIVERABLE: ${act.deliverableType}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                Text("DEADLINE: ${act.deadline}", style = MaterialTheme.typography.labelSmall, color = Color(0xFFF59E0B))
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (role == UserRole.COORDINATOR || role == UserRole.ADVISOR) {
                                Button(
                                    onClick = { onOpenCoordinatorStation(act) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Filled.QrCode, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Station", fontSize = 12.sp)
                                }
                            } else {
                                Button(
                                    onClick = { onOpenCheckIn(act) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Check-In", fontSize = 12.sp)
                                }
                            }

                            FilledTonalButton(
                                onClick = { onOpenSubmitDeliverable(act) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Filled.CloudUpload, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Submit", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // 5. SECTION C: DELIVERABLES & EVALUATION
        item {
            Text(
                text = "Deliverables & Rubric Evaluations",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (clubSubmissions.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No submissions recorded yet for this club.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(clubSubmissions) { sub ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(sub.fileTitle, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            if (sub.isEvaluated) {
                                Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFF10B981)) {
                                    Text(
                                        "${sub.score}/100",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            } else {
                                Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFF59E0B)) {
                                    Text(
                                        "PENDING",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Text("Format: ${sub.fileType} • ${sub.submittedAt}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        if (sub.isEvaluated && sub.feedback != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Feedback: \"${sub.feedback}\" — ${sub.evaluatorName ?: "Evaluator"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        if ((role == UserRole.COORDINATOR || role == UserRole.ADVISOR) && !sub.isEvaluated) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { onOpenEvaluateSubmission(sub) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Filled.Grading, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Score with Rubric")
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.firestore.FirestoreClub
import com.example.data.firestore.UiState
import com.example.ui.components.MiraiLogoBadge
import com.example.viewmodel.ClubDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubDashboard(
    viewModel: ClubDashboardViewModel,
    modifier: Modifier = Modifier,
    onNavigateToChat: ((String) -> Unit)? = null
) {
    val clubsState by viewModel.clubsState.collectAsStateWithLifecycle()
    val memberships by viewModel.membershipsState.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedClub by viewModel.selectedClub.collectAsStateWithLifecycle()
    val actionMessage by viewModel.actionMessage.collectAsStateWithLifecycle()

    val categories = listOf("All", "TECH", "CREATIVE", "SPORTS", "ENTREPRENEUR")

    // Membership lookup set by clubId
    val userMembershipClubIds = remember(memberships) {
        memberships.map { it.clubId }.toSet()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("club_dashboard")
    ) {
        val listState = androidx.compose.foundation.lazy.rememberLazyListState()

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .testTag("clubs_list"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Dashboard Header
            item(key = "header") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            MiraiLogoBadge(sizeDp = 30.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Club Dashboard",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Text(
                            text = "Real-time campus organizations synced with Cloud Firestore",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF10B981).copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Firestore Live",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                        }
                    }
                }
            }

            // Search Bar (Scrolls along with club cards)
            item(key = "search_bar") {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Search clubs by name, category, or advisor...") },
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Filled.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("club_search_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )
            }

            // Category Filter Chips
            item(key = "category_chips") {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("club_categories_row")
                ) {
                    items(categories) { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setCategory(cat) },
                            label = {
                                Text(
                                    text = when (cat) {
                                        "TECH" -> "Technology"
                                        "CREATIVE" -> "Creative Arts"
                                        "SPORTS" -> "Athletics"
                                        "ENTREPRENEUR" -> "Incubator"
                                        else -> "All Domains"
                                    },
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            leadingIcon = if (isSelected) {
                                { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                            } else null
                        )
                    }
                }
            }

            // Action Feedback Snack Banner
            if (actionMessage != null) {
                item(key = "action_banner") {
                    AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.fillMaxWidth()
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
                }
            }

            // Main Content based on UiState
            when (val state = clubsState) {
                is UiState.Loading -> {
                    item(key = "loading_state") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .testTag("club_dashboard_loading"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Connecting to Firestore...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                is UiState.Error -> {
                    item(key = "error_state") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                                .testTag("club_dashboard_error"),
                            contentAlignment = Alignment.Center
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Filled.CloudOff, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(36.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Unable to load clubs from Firestore",
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
                                        Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Retry Connection")
                                    }
                                }
                            }
                        }
                    }
                }

                is UiState.Success -> {
                    val allClubs = state.data
                    val filteredClubs = allClubs.filter { club ->
                        val matchesCategory = if (selectedCategory == "All") true else club.category.equals(selectedCategory, ignoreCase = true)
                        val matchesSearch = if (searchQuery.isBlank()) true else {
                            val query = searchQuery.trim()
                            club.name.contains(query, ignoreCase = true) ||
                                    club.category.contains(query, ignoreCase = true) ||
                                    club.shortCode.contains(query, ignoreCase = true) ||
                                    club.tagLine.contains(query, ignoreCase = true) ||
                                    club.description.contains(query, ignoreCase = true) ||
                                    club.facultyAdvisor.contains(query, ignoreCase = true) ||
                                    (query.equals("tech", ignoreCase = true) && club.category.contains("TECH", ignoreCase = true)) ||
                                    (query.equals("sports", ignoreCase = true) && club.category.contains("SPORTS", ignoreCase = true)) ||
                                    (query.equals("creative", ignoreCase = true) && club.category.contains("CREATIVE", ignoreCase = true))
                        }
                        matchesCategory && matchesSearch
                    }

                    if (filteredClubs.isEmpty()) {
                        item(key = "empty_state") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp)
                                    .testTag("club_dashboard_empty"),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Filled.Diversity3, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(48.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "No clubs match your filters",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Try clearing the search query or domain filter.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        items(filteredClubs, key = { it.clubId }) { club ->
                            val isMember = userMembershipClubIds.contains(club.clubId)

                            ClubCardItem(
                                club = club,
                                isMember = isMember,
                                onClick = { viewModel.selectClub(club) },
                                onJoinClick = { viewModel.joinClub(club.clubId) },
                                onLeaveClick = { viewModel.leaveClub(club.clubId) }
                            )
                        }
                    }
                }
            }
        }

        // Detailed Club Dialog Modal
        selectedClub?.let { club ->
            val isMember = userMembershipClubIds.contains(club.clubId)
            ClubDetailModal(
                club = club,
                isMember = isMember,
                onDismiss = { viewModel.selectClub(null) },
                onJoin = {
                    viewModel.joinClub(club.clubId)
                    viewModel.selectClub(null)
                },
                onLeave = {
                    viewModel.leaveClub(club.clubId)
                    viewModel.selectClub(null)
                },
                onChat = {
                    viewModel.selectClub(null)
                    onNavigateToChat?.invoke(club.clubId)
                }
            )
        }
    }
}

/**
 * Individual Club Card item displayed in the dashboard
 */
@Composable
fun ClubCardItem(
    club: FirestoreClub,
    isMember: Boolean,
    onClick: () -> Unit,
    onJoinClick: () -> Unit,
    onLeaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("club_card_${club.clubId}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(
                listOf(
                    Color(club.primaryColorHex).copy(alpha = 0.5f),
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
                )
            )
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with club avatar, name, and membership status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(club.primaryColorHex)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = club.shortCode.ifBlank { club.name.take(2).uppercase() },
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = club.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${club.category} • ${club.campus}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Membership Status Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isMember) Color(0xFF10B981).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.testTag("membership_badge_${club.clubId}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isMember) Icons.Filled.CheckCircle else Icons.Outlined.PersonAdd,
                            contentDescription = null,
                            tint = if (isMember) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isMember) "Member" else "Not Joined",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isMember) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tagline / Description
            Text(
                text = club.tagLine.ifBlank { club.description },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Metrics row: Meeting schedule & capacity progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Event,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = club.meetingSchedule.ifBlank { "Every Saturday" },
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Group,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${club.memberCount} / ${club.capacity} members",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Capacity Bar
            val fillRatio = if (club.capacity > 0) (club.memberCount.toFloat() / club.capacity.toFloat()).coerceIn(0f, 1f) else 0.5f
            LinearProgressIndicator(
                progress = { fillRatio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = Color(club.primaryColorHex),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onClick,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("view_details_button_${club.clubId}")
                ) {
                    Text("View Details", fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                if (isMember) {
                    FilledTonalButton(
                        onClick = onLeaveClick,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("leave_club_button_${club.clubId}")
                    ) {
                        Text("Leave", color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 12.sp)
                    }
                } else {
                    Button(
                        onClick = onJoinClick,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(club.primaryColorHex)),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("join_club_button_${club.clubId}")
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Join Club", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Detailed Club Information Dialog
 */
@Composable
fun ClubDetailModal(
    club: FirestoreClub,
    isMember: Boolean,
    onDismiss: () -> Unit,
    onJoin: () -> Unit,
    onLeave: () -> Unit,
    onChat: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("club_detail_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Header with close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(club.primaryColorHex)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = club.shortCode.ifBlank { club.name.take(2) },
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = club.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${club.category} • ${club.campus}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Full Description
                Text(
                    text = "About the Organization",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = club.description.ifBlank { club.tagLine },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(14.dp))

                // Leadership & Advisors
                DetailInfoRow(label = "Faculty Advisor", value = club.facultyAdvisor.ifBlank { "Dr. Campus Advisor" })
                DetailInfoRow(label = "Lead Coordinator", value = club.leadCoordinator.ifBlank { "Student Lead" })
                DetailInfoRow(label = "Meeting Schedule", value = club.meetingSchedule)
                DetailInfoRow(label = "Current Roster", value = "${club.memberCount} enrolled of ${club.capacity} max")

                Spacer(modifier = Modifier.height(20.dp))

                // Actions row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onChat,
                        modifier = Modifier.weight(1f).testTag("club_chat_button")
                    ) {
                        Icon(Icons.Filled.Forum, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Club Chat", fontSize = 12.sp)
                    }

                    if (isMember) {
                        Button(
                            onClick = onLeave,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.weight(1f).testTag("modal_leave_button")
                        ) {
                            Text("Leave Club", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    } else {
                        Button(
                            onClick = onJoin,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(club.primaryColorHex)),
                            modifier = Modifier.weight(1f).testTag("modal_join_button")
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Join Membership", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}

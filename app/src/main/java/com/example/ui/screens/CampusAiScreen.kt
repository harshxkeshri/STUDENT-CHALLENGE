package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ClubSphereRepository
import com.example.data.ai.ActivityPlanSuggestion
import com.example.data.ai.GeminiAiService
import com.example.data.ai.TranscriptAiSummary
import com.example.model.*
import com.example.ui.components.MiraiLogoBadge
import kotlinx.coroutines.launch

data class CopilotChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String, // "user" or "copilot"
    val text: String,
    val timestamp: String = "Just now"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampusAiScreen(
    repository: ClubSphereRepository,
    currentUser: UserProfile,
    currentCampus: Campus,
    clubs: List<Club>,
    modifier: Modifier = Modifier,
    onClose: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val aiService = remember { GeminiAiService.instance }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Copilot Chat", "Plan Drafter", "Resume & Transcript", "Rubric Assistant")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .testTag("campus_ai_screen")
    ) {
        // AI Screen Top Header
        Surface(
            color = Color(0xFF1E293B),
            shadowElevation = 4.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MiraiLogoBadge(sizeDp = 36.dp)

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Mirai AI Copilot",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 17.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF2563EB).copy(alpha = 0.25f)
                            ) {
                                Text(
                                    text = "Gemini 3.5",
                                    color = Color(0xFF93C5FD),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Intelligent Co-Curricular & Club Management Assistant",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.testTag("close_ai_screen_button")
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    edgePadding = 0.dp,
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFF38BDF8),
                    divider = {}
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp
                                )
                            },
                            modifier = Modifier.testTag("ai_tab_$index")
                        )
                    }
                }
            }
        }

        // Tab Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when (selectedTab) {
                0 -> CopilotChatTab(
                    aiService = aiService,
                    currentUser = currentUser,
                    currentCampus = currentCampus
                )
                1 -> PlanDrafterTab(
                    aiService = aiService,
                    repository = repository,
                    clubs = clubs,
                    currentCampus = currentCampus
                )
                2 -> TranscriptAiTab(
                    aiService = aiService,
                    repository = repository,
                    currentUser = currentUser
                )
                3 -> RubricAssistantTab(
                    aiService = aiService
                )
            }
        }
    }
}

/**
 * Tab 1: Conversational Campus Copilot
 */
@Composable
fun CopilotChatTab(
    aiService: GeminiAiService,
    currentUser: UserProfile,
    currentCampus: Campus
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var inputMessage by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }

    val messages = remember {
        mutableStateListOf(
            CopilotChatMessage(
                sender = "copilot",
                text = "Hello ${currentUser.name.split(" ").first()}! I am your Mirai Campus Copilot powered by Gemini. You can ask me anything about club activities, earning co-curricular credits, event registration, or duty leaves!"
            )
        )
    }

    val quickChips = listOf(
        "How do I earn verified Co-Curricular Credits?",
        "Suggest a 2-hour Hackathon workshop schedule",
        "How does Club Day QR check-in work?",
        "What are the requirements for honors transcript?",
        "How do I request Duty Leave for an inter-campus event?"
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Quick suggestion chips
        Text(
            text = "QUICK INQUIRIES",
            color = Color(0xFF94A3B8),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().testTag("ai_quick_chips")
        ) {
            items(quickChips) { chip ->
                SuggestionChip(
                    onClick = {
                        inputMessage = chip
                    },
                    label = { Text(chip, fontSize = 12.sp, color = Color(0xFFE2E8F0)) },
                    colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFF1E293B)),
                    border = SuggestionChipDefaults.suggestionChipBorder(borderColor = Color(0xFF334155), enabled = true)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Chat list
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { msg ->
                val isUser = msg.sender == "user"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    if (!isUser) {
                        MiraiLogoBadge(sizeDp = 28.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Card(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUser) Color(0xFF2563EB) else Color(0xFF1E293B)
                        ),
                        modifier = Modifier.widthIn(max = 300.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            SelectionContainer {
                                Text(
                                    text = msg.text,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp
                                )
                            }

                            if (!isUser) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            clipboard.setPrimaryClip(ClipData.newPlainText("Copilot Advice", msg.text))
                                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.ContentCopy,
                                            contentDescription = "Copy text",
                                            tint = Color(0xFF94A3B8),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (isGenerating) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        MiraiLogoBadge(sizeDp = 24.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color(0xFF38BDF8),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Gemini is reasoning...",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Input row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputMessage,
                onValueChange = { inputMessage = it },
                placeholder = { Text("Ask Gemini about clubs, credits, events...", fontSize = 13.sp) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("copilot_input_field"),
                singleLine = false,
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2563EB),
                    unfocusedBorderColor = Color(0xFF334155),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            FilledIconButton(
                onClick = {
                    val query = inputMessage.trim()
                    if (query.isNotBlank() && !isGenerating) {
                        messages.add(CopilotChatMessage(sender = "user", text = query))
                        inputMessage = ""
                        isGenerating = true

                        coroutineScope.launch {
                            val history = messages.map { Pair(it.sender, it.text) }
                            val answer = aiService.askCampusCopilot(
                                userPrompt = query,
                                userRole = currentUser.role,
                                campus = currentCampus,
                                history = history
                            )
                            messages.add(CopilotChatMessage(sender = "copilot", text = answer))
                            isGenerating = false
                            listState.animateScrollToItem(messages.size - 1)
                        }
                    }
                },
                enabled = inputMessage.isNotBlank() && !isGenerating,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("send_copilot_button"),
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF2563EB))
            ) {
                Icon(Icons.Filled.Send, contentDescription = "Send", tint = Color.White)
            }
        }
    }
}

/**
 * Tab 2: Plan Drafter with Gemini
 */
@Composable
fun PlanDrafterTab(
    aiService: GeminiAiService,
    repository: ClubSphereRepository,
    clubs: List<Club>,
    currentCampus: Campus
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedClub by remember { mutableStateOf(clubs.firstOrNull() ?: ClubSphereRepository.instance.clubs.value.first()) }
    var topicHint by remember { mutableStateOf("") }
    var isDrafting by remember { mutableStateOf(false) }
    var generatedPlan by remember { mutableStateOf<ActivityPlanSuggestion?>(null) }
    var planSavedSuccess by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "AI Activity Plan Drafter",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Text(
            text = "Generate comprehensive workshop plans with venue recommendations, deliverables, and rubrics in seconds.",
            color = Color(0xFF94A3B8),
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Club selector chips
        Text(
            text = "SELECT CLUB",
            color = Color(0xFF94A3B8),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(clubs) { club ->
                FilterChip(
                    selected = selectedClub.id == club.id,
                    onClick = { selectedClub = club },
                    label = { Text(club.name) },
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(club.primaryColorHex))
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = topicHint,
            onValueChange = { topicHint = it },
            label = { Text("Activity Topic or Focus Area (Optional)") },
            placeholder = { Text("e.g. Quantum Algorithms, Generative AI, Robotics Prototyping...") },
            modifier = Modifier.fillMaxWidth().testTag("ai_plan_topic_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2563EB),
                unfocusedBorderColor = Color(0xFF334155),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isDrafting = true
                planSavedSuccess = false
                coroutineScope.launch {
                    val suggestion = aiService.generateActivityPlan(
                        clubName = selectedClub.name,
                        category = selectedClub.category.label,
                        topicHint = topicHint,
                        campus = currentCampus
                    )
                    generatedPlan = suggestion
                    isDrafting = false
                }
            },
            enabled = !isDrafting,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("generate_ai_plan_button"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
        ) {
            if (isDrafting) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                Spacer(modifier = Modifier.width(10.dp))
                Text("Drafting with Gemini...")
            } else {
                Icon(Icons.Filled.AutoAwesome, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Draft Plan with Gemini", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Generated Plan Display
        generatedPlan?.let { plan ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("generated_plan_card"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Color(0xFF38BDF8), Color(0xFF2563EB))))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF38BDF8))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI Generated Plan Proposal",
                            color = Color(0xFF38BDF8),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = plan.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = plan.topic, color = Color(0xFFCBD5E1), fontSize = 13.sp)

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFF334155))
                    Spacer(modifier = Modifier.height(12.dp))

                    DetailRow(label = "Suggested Venue", value = plan.venueSuggestion)
                    DetailRow(label = "Time Slot", value = plan.timeSlotSuggestion)
                    DetailRow(label = "Deliverable", value = plan.deliverableType)
                    DetailRow(label = "Suggested Deadline", value = plan.suggestedDeadline)

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Rubric Criteria:", color = Color(0xFFA5B4FC), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    plan.rubricHighlights.forEach { r ->
                        Text(text = "• $r", color = Color(0xFFE2E8F0), fontSize = 12.sp, modifier = Modifier.padding(start = 6.dp, top = 2.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            repository.createActivityPlan(
                                clubId = selectedClub.id,
                                title = plan.title,
                                date = "Saturday, Upcoming Club Day",
                                timeSlot = plan.timeSlotSuggestion,
                                venue = plan.venueSuggestion,
                                topic = plan.topic,
                                deliverableType = plan.deliverableType,
                                deadline = plan.suggestedDeadline
                            )
                            planSavedSuccess = true
                            Toast.makeText(context, "Plan submitted for Advisor approval!", Toast.LENGTH_LONG).show()
                        },
                        modifier = Modifier.fillMaxWidth().testTag("apply_plan_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) {
                        Icon(Icons.Filled.AddModerator, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (planSavedSuccess) "Plan Submitted to Roster ✓" else "Publish to Club Schedule")
                    }
                }
            }
        }
    }
}

/**
 * Tab 3: AI Co-Curricular Transcript & Resume Summary
 */
@Composable
fun TranscriptAiTab(
    aiService: GeminiAiService,
    repository: ClubSphereRepository,
    currentUser: UserProfile
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val attendanceRecords by repository.attendanceRecords.collectAsStateWithLifecycle()
    val submissions by repository.submissions.collectAsStateWithLifecycle()
    val certificates by repository.certificates.collectAsStateWithLifecycle()

    var isAnalyzing by remember { mutableStateOf(false) }
    var aiSummary by remember { mutableStateOf<TranscriptAiSummary?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "AI Resume & Transcript Booster",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Text(
            text = "Synthesizes your verified attendances, project submissions, and certificates into high-impact resume bullets and executive summaries.",
            color = Color(0xFF94A3B8),
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Student Stats Banner
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatColumn(value = "${currentUser.coCurricularCredits}", label = "Credits")
                StatColumn(value = "${attendanceRecords.count { it.status == AttendanceStatus.PRESENT }}", label = "Workshops")
                StatColumn(value = "${submissions.size}", label = "Projects")
                StatColumn(value = "${certificates.size}", label = "Certificates")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isAnalyzing = true
                coroutineScope.launch {
                    val result = aiService.generateTranscriptSummary(
                        studentName = currentUser.name,
                        rollNo = currentUser.rollNo,
                        department = currentUser.department,
                        credits = currentUser.coCurricularCredits,
                        attendedCount = attendanceRecords.size,
                        submissionsCount = submissions.size,
                        certsCount = certificates.size
                    )
                    aiSummary = result
                    isAnalyzing = false
                }
            },
            enabled = !isAnalyzing,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("generate_transcript_ai_button"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
        ) {
            if (isAnalyzing) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                Spacer(modifier = Modifier.width(10.dp))
                Text("Synthesizing Achievements...")
            } else {
                Icon(Icons.Filled.WorkspacePremium, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate AI Resume Bullets", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        aiSummary?.let { summary ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Executive Summary",
                            color = Color(0xFFA78BFA),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        IconButton(
                            onClick = {
                                val textToCopy = "${summary.executiveSummary}\n\nTop Competencies: ${summary.topCompetencies.joinToString(", ")}\n\nResume Bullets:\n" +
                                        summary.resumeBulletPoints.joinToString("\n• ", prefix = "• ")
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Resume Summary", textToCopy))
                                Toast.makeText(context, "Summary copied to clipboard", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(Icons.Filled.ContentCopy, contentDescription = "Copy", tint = Color(0xFF94A3B8))
                        }
                    }

                    Text(
                        text = summary.executiveSummary,
                        color = Color.White,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Top Verified Competencies",
                        color = Color(0xFFA78BFA),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        summary.topCompetencies.forEach { comp ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF2E1065)
                            ) {
                                Text(
                                    text = comp,
                                    color = Color(0xFFDDD6FE),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Resume Action Bullets (LinkedIn / CV Ready)",
                        color = Color(0xFFA78BFA),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    summary.resumeBulletPoints.forEach { bullet ->
                        Row(modifier = Modifier.padding(vertical = 3.dp)) {
                            Text(text = "• ", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold)
                            Text(text = bullet, color = Color(0xFFE2E8F0), fontSize = 12.sp, lineHeight = 18.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Lightbulb, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = summary.careerRecommendation,
                                color = Color(0xFFFDE68A),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Tab 4: Advisor Rubric & Feedback Assistant
 */
@Composable
fun RubricAssistantTab(
    aiService: GeminiAiService
) {
    val coroutineScope = rememberCoroutineScope()

    var projectTitle by remember { mutableStateOf("Quantum Circuit Simulator in Python") }
    var topic by remember { mutableStateOf("Simulating Grover's search algorithm on noisy IBM Q hardware") }
    var deliverableType by remember { mutableStateOf("Jupyter Notebook & Circuit Diagram") }
    var proposedScore by remember { mutableStateOf(92) }
    var generatedFeedback by remember { mutableStateOf<String?>(null) }
    var isGenerating by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "AI Faculty Feedback & Rubric Assistant",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Text(
            text = "Generate insightful, constructive academic feedback and personalized mentorship recommendations.",
            color = Color(0xFF94A3B8),
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = projectTitle,
            onValueChange = { projectTitle = it },
            label = { Text("Project / Activity Title") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2563EB),
                unfocusedBorderColor = Color(0xFF334155),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = topic,
            onValueChange = { topic = it },
            label = { Text("Submission Topic & Goals") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2563EB),
                unfocusedBorderColor = Color(0xFF334155),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Target Evaluation Score: $proposedScore/100", color = Color.White, fontWeight = FontWeight.Bold)
            Slider(
                value = proposedScore.toFloat(),
                onValueChange = { proposedScore = it.toInt() },
                valueRange = 50f..100f,
                steps = 10,
                modifier = Modifier.width(180.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isGenerating = true
                coroutineScope.launch {
                    val feedback = aiService.generateAdvisorFeedback(
                        activityTitle = projectTitle,
                        topic = topic,
                        deliverableType = deliverableType,
                        score = proposedScore
                    )
                    generatedFeedback = feedback
                    isGenerating = false
                }
            },
            enabled = !isGenerating,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("generate_feedback_button"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706))
        ) {
            if (isGenerating) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
            } else {
                Icon(Icons.Filled.RateReview, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate Constructive Feedback", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        generatedFeedback?.let { feedback ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(12.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706))))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Advisor Evaluation Commentary:",
                        color = Color(0xFFFBBF24),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"$feedback\"",
                        color = Color.White,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color(0xFF94A3B8), fontSize = 12.sp)
        Text(text = value, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 12.sp)
    }
}

@Composable
private fun StatColumn(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = Color(0xFF38BDF8), fontWeight = FontWeight.Black, fontSize = 20.sp)
        Text(text = label, color = Color(0xFF94A3B8), fontSize = 11.sp)
    }
}

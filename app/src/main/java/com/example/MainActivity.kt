package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ClubSphereRepository
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

enum class NavigationTab(val label: String, val testTag: String) {
    HOME("Home", "nav_home"),
    CLUBS("4 Clubs", "nav_clubs"),
    EVENTS("Events", "nav_events"),
    CHAT("Chat", "nav_chat"),
    PROFILE("Profile", "nav_profile")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = ClubSphereRepository.instance

        setContent {
            val isDarkMode by repository.isDarkMode.collectAsStateWithLifecycle()

            MyApplicationTheme(darkTheme = isDarkMode) {
                ClubSphereApp(repository = repository)
            }
        }
    }
}

@Composable
fun ClubSphereApp(repository: ClubSphereRepository) {
    val campus by repository.currentCampus.collectAsStateWithLifecycle()
    val user by repository.currentUser.collectAsStateWithLifecycle()
    val role by repository.currentRole.collectAsStateWithLifecycle()
    val clubs by repository.clubs.collectAsStateWithLifecycle()
    val memberships by repository.memberships.collectAsStateWithLifecycle()
    val activities by repository.activities.collectAsStateWithLifecycle()
    val attendanceRecords by repository.attendanceRecords.collectAsStateWithLifecycle()
    val submissions by repository.submissions.collectAsStateWithLifecycle()
    val events by repository.events.collectAsStateWithLifecycle()
    val tickets by repository.tickets.collectAsStateWithLifecycle()
    val certificates by repository.certificates.collectAsStateWithLifecycle()
    val chatMessages by repository.chatMessages.collectAsStateWithLifecycle()
    val notifications by repository.notifications.collectAsStateWithLifecycle()
    val isDarkMode by repository.isDarkMode.collectAsStateWithLifecycle()
    val language by repository.language.collectAsStateWithLifecycle()

    var currentTab by remember { mutableStateOf(NavigationTab.HOME) }
    var selectedClubIdForDetail by remember { mutableStateOf<String?>("kaizen_tech") }
    var selectedChannelIdForChat by remember { mutableStateOf<String?>("kaizen_tech") }

    // Dialog & Modal State
    var paymentModalEventTier by remember { mutableStateOf<Pair<Event, TicketTier>?>(null) }
    var viewingTicketPass by remember { mutableStateOf<Ticket?>(null) }
    var activeCheckInActivity by remember { mutableStateOf<ActivityPlan?>(null) }
    var activeCoordinatorStationActivity by remember { mutableStateOf<ActivityPlan?>(null) }
    var activeSubmitActivity by remember { mutableStateOf<ActivityPlan?>(null) }
    var activeEvaluatingSubmission by remember { mutableStateOf<Submission?>(null) }
    var viewingCertificate by remember { mutableStateOf<Certificate?>(null) }
    var isShowingTranscript by remember { mutableStateOf(false) }
    var isShowingRoleSwitcher by remember { mutableStateOf(false) }
    var activeShareData by remember { mutableStateOf<Pair<String, String>?>(null) }
    var activeCreatePlanClub by remember { mutableStateOf<Club?>(null) }
    val isLoggedIn by repository.isLoggedIn.collectAsStateWithLifecycle()
    val users by repository.users.collectAsStateWithLifecycle()

    // If user is not logged in, show AuthScreen (Sign In / Sign Up)
    if (!isLoggedIn) {
        AuthScreen(
            demoUsers = users,
            onSignIn = { emailOrRoll, pass ->
                repository.signIn(emailOrRoll, pass)
            },
            onSignUp = { name, email, rollNo, pass, campusOption, department, academicYear, roleOption ->
                repository.signUp(name, email, rollNo, pass, campusOption, department, academicYear, roleOption)
            }
        )
        return
    }

    // Support device back press to return to HOME if on sub-screens
    if (currentTab != NavigationTab.HOME) {
        BackHandler {
            currentTab = NavigationTab.HOME
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().testTag("main_scaffold"),
        topBar = {
            AppTopBar(
                selectedCampus = campus,
                onSelectCampus = { repository.setCampus(it) },
                currentRole = role,
                currentUser = user,
                onOpenRoleSwitcher = { isShowingRoleSwitcher = true },
                notifications = notifications,
                onMarkNotificationsRead = { repository.markNotificationsRead() },
                isDarkMode = isDarkMode,
                onToggleDarkMode = { repository.toggleDarkMode() },
                onSignOut = { repository.signOut() }
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_nav_bar")
            ) {
                NavigationTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    val unreadChat = if (tab == NavigationTab.CHAT) 3 else 0

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        modifier = Modifier.testTag(tab.testTag),
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (unreadChat > 0 && !isSelected) {
                                        Badge { Text("$unreadChat") }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = when (tab) {
                                        NavigationTab.HOME -> if (isSelected) Icons.Filled.Home else Icons.Outlined.Home
                                        NavigationTab.CLUBS -> if (isSelected) Icons.Filled.Diversity3 else Icons.Outlined.Diversity3
                                        NavigationTab.EVENTS -> if (isSelected) Icons.Filled.ConfirmationNumber else Icons.Outlined.ConfirmationNumber
                                        NavigationTab.CHAT -> if (isSelected) Icons.Filled.Forum else Icons.Outlined.Forum
                                        NavigationTab.PROFILE -> if (isSelected) Icons.Filled.Person else Icons.Outlined.Person
                                    },
                                    contentDescription = tab.label
                                )
                            }
                        },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                NavigationTab.HOME -> {
                    HomeScreen(
                        campus = campus,
                        user = user,
                        role = role,
                        clubs = clubs,
                        activities = activities,
                        events = events,
                        memberships = memberships,
                        onNavigateToClub = { clubId ->
                            selectedClubIdForDetail = clubId
                            currentTab = NavigationTab.CLUBS
                        },
                        onNavigateToEvents = {
                            currentTab = NavigationTab.EVENTS
                        },
                        onNavigateToChat = { channelId ->
                            selectedChannelIdForChat = channelId
                            currentTab = NavigationTab.CHAT
                        },
                        onOpenCheckIn = { act -> activeCheckInActivity = act },
                        onOpenCoordinatorStation = { act -> activeCoordinatorStationActivity = act },
                        onOpenTranscript = { isShowingTranscript = true }
                    )
                }

                NavigationTab.CLUBS -> {
                    ClubsScreen(
                        clubs = clubs,
                        selectedClubId = selectedClubIdForDetail,
                        memberships = memberships,
                        activities = activities,
                        submissions = submissions,
                        role = role,
                        onJoinClub = { repository.requestMembership(it) },
                        onLeaveClub = { repository.leaveClub(it) },
                        onOpenCheckIn = { act -> activeCheckInActivity = act },
                        onOpenCoordinatorStation = { act -> activeCoordinatorStationActivity = act },
                        onOpenSubmitDeliverable = { act -> activeSubmitActivity = act },
                        onOpenEvaluateSubmission = { sub -> activeEvaluatingSubmission = sub },
                        onNavigateToChat = { clubId ->
                            selectedChannelIdForChat = clubId
                            currentTab = NavigationTab.CHAT
                        },
                        onOpenCreateActivityPlan = { club -> activeCreatePlanClub = club },
                        onApproveActivityPlan = { actId -> repository.approveActivityPlan(actId) }
                    )
                }

                NavigationTab.EVENTS -> {
                    EventsScreen(
                        campus = campus,
                        events = events,
                        tickets = tickets,
                        onBuyTicket = { evt, tier ->
                            paymentModalEventTier = Pair(evt, tier)
                        },
                        onViewTicketPass = { tkt -> viewingTicketPass = tkt },
                        onShareEvent = { evt ->
                            activeShareData = Pair(evt.title, "https://msot.campus/events/${evt.id}")
                        }
                    )
                }

                NavigationTab.CHAT -> {
                    ChatScreen(
                        clubs = clubs,
                        initialChannelId = selectedChannelIdForChat,
                        messages = chatMessages,
                        currentUserId = user.id,
                        onSendMessage = { channelId, text, isDirect ->
                            repository.sendChatMessage(channelId, text, isDirect)
                        },
                        onToggleReaction = { msgId, emoji ->
                            repository.toggleReaction(msgId, emoji)
                        }
                    )
                }

                NavigationTab.PROFILE -> {
                    ProfileScreen(
                        user = user,
                        role = role,
                        campus = campus,
                        memberships = memberships,
                        clubs = clubs,
                        certificates = certificates,
                        tickets = tickets,
                        isDarkMode = isDarkMode,
                        currentLanguage = language,
                        onToggleDarkMode = { repository.toggleDarkMode() },
                        onSelectLanguage = { repository.setLanguage(it) },
                        onOpenTranscript = { isShowingTranscript = true },
                        onOpenCertificate = { viewingCertificate = it },
                        onVerifyCertNumber = { repository.verifyCertificateByNumber(it) },
                        onOpenRoleSwitcher = { isShowingRoleSwitcher = true },
                        onShareProfile = {
                            activeShareData = Pair("Check out my MSOT Co-Curricular Portfolio!", "https://msot.campus/transcript/${user.rollNo}")
                        },
                        onSignOut = { repository.signOut() }
                    )
                }
            }
        }
    }

    // --- MODALS AND DIALOGS ---

    // 1. Stripe Payment Modal
    paymentModalEventTier?.let { (event, tier) ->
        StripePaymentModal(
            event = event,
            initialTier = tier,
            onDismiss = { paymentModalEventTier = null },
            onPaymentSuccess = { ticket ->
                paymentModalEventTier = null
                viewingTicketPass = ticket
            },
            onProcessPayment = { tierName, amount, cardLast4, promoCode ->
                repository.purchaseTicketWithStripe(event.id, tierName, amount, cardLast4, promoCode)
            }
        )
    }

    // 2. Ticket Pass Modal
    viewingTicketPass?.let { ticket ->
        TicketPassModal(
            ticket = ticket,
            onDismiss = { viewingTicketPass = null },
            onShare = {
                activeShareData = Pair("Pass for ${ticket.eventTitle}", "https://msot.campus/pass/${ticket.id}")
            }
        )
    }

    // 3. Student Check-In Modal
    activeCheckInActivity?.let { act ->
        StudentCheckInModal(
            activity = act,
            onDismiss = { activeCheckInActivity = null },
            onCheckIn = { code, isLate ->
                repository.checkInToActivity(act.id, code, isLate)
            }
        )
    }

    // 4. Coordinator QR Station Modal
    activeCoordinatorStationActivity?.let { act ->
        CoordinatorQrModal(
            activity = act,
            onDismiss = { activeCoordinatorStationActivity = null }
        )
    }

    // 5. Deliverable Submission Modal
    activeSubmitActivity?.let { act ->
        DeliverableSubmissionModal(
            activity = act,
            onDismiss = { activeSubmitActivity = null },
            onSubmit = { title, fileType, url ->
                repository.submitDeliverable(act.id, title, fileType, url)
            }
        )
    }

    // 6. Rubric Evaluation Modal
    activeEvaluatingSubmission?.let { sub ->
        RubricEvaluationModal(
            submission = sub,
            onDismiss = { activeEvaluatingSubmission = null },
            onEvaluate = { score, feedback ->
                repository.evaluateSubmission(sub.id, score, feedback, user.name)
            }
        )
    }

    // 7. Certificate View Modal
    viewingCertificate?.let { cert ->
        CertificateViewModal(
            certificate = cert,
            onDismiss = { viewingCertificate = null },
            onShare = {
                activeShareData = Pair("Official Certificate: ${cert.clubOrEventTitle}", "https://msot.campus/verify/${cert.certNumber}")
            }
        )
    }

    // 8. Co-Curricular Transcript Modal
    if (isShowingTranscript) {
        TranscriptViewModal(
            user = user,
            memberships = memberships,
            clubs = clubs,
            attendances = attendanceRecords,
            submissions = submissions,
            certificates = certificates,
            onDismiss = { isShowingTranscript = false }
        )
    }

    // 9. Role Switcher Modal
    if (isShowingRoleSwitcher) {
        RoleSwitcherModal(
            currentRole = role,
            onRoleSelected = { repository.setRole(it) },
            onDismiss = { isShowingRoleSwitcher = false }
        )
    }

    // 10. Social Share Modal
    activeShareData?.let { (title, url) ->
        SocialShareModal(
            title = title,
            url = url,
            onDismiss = { activeShareData = null }
        )
    }

    // 11. Create Activity Plan Modal
    activeCreatePlanClub?.let { club ->
        CreateActivityPlanModal(
            club = club,
            onDismiss = { activeCreatePlanClub = null },
            onSubmit = { title, date, timeSlot, venue, topic, deliverableType, deadline ->
                repository.createActivityPlan(
                    clubId = club.id,
                    title = title,
                    date = date,
                    timeSlot = timeSlot,
                    venue = venue,
                    topic = topic,
                    deliverableType = deliverableType,
                    deadline = deadline
                )
                activeCreatePlanClub = null
            }
        )
    }
}

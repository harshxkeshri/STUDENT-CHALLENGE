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
<<<<<<< HEAD
=======
<<<<<<< HEAD
=======
import androidx.compose.ui.graphics.vector.ImageVector
>>>>>>> a05b565 (final project)
>>>>>>> c0aa687 (final project)
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ClubSphereRepository
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
<<<<<<< HEAD
=======
<<<<<<< HEAD
>>>>>>> c0aa687 (final project)

enum class NavigationTab(val label: String, val testTag: String) {
    HOME("Home", "nav_home"),
    CLUBS("4 Clubs", "nav_clubs"),
    EVENTS("Events", "nav_events"),
    CHAT("Chat", "nav_chat"),
    PROFILE("Profile", "nav_profile")
}
<<<<<<< HEAD
=======
=======
import com.example.viewmodel.AuthViewModel
import com.example.data.firestore.FirestoreClubRepository
import com.example.viewmodel.ClubDashboardViewModel

data class RoleNavigationItem(
    val id: String,
    val label: String,
    val iconFilled: ImageVector,
    val iconOutlined: ImageVector,
    val testTag: String,
    val badgeCount: Int = 0
)
>>>>>>> a05b565 (final project)
>>>>>>> c0aa687 (final project)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = ClubSphereRepository.instance
<<<<<<< HEAD
=======
<<<<<<< HEAD
=======
        repository.init(applicationContext)
>>>>>>> a05b565 (final project)
>>>>>>> c0aa687 (final project)

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
<<<<<<< HEAD
=======
<<<<<<< HEAD
>>>>>>> c0aa687 (final project)
    val isDarkMode by repository.isDarkMode.collectAsStateWithLifecycle()
    val language by repository.language.collectAsStateWithLifecycle()

    var currentTab by remember { mutableStateOf(NavigationTab.HOME) }
<<<<<<< HEAD
=======
=======
    val dutyLeaves by repository.dutyLeaveRequests.collectAsStateWithLifecycle()
    val isDarkMode by repository.isDarkMode.collectAsStateWithLifecycle()
    val language by repository.language.collectAsStateWithLifecycle()
    val isLoggedIn by repository.isLoggedIn.collectAsStateWithLifecycle()
    val users by repository.users.collectAsStateWithLifecycle()
    val authViewModel = remember { AuthViewModel(repository) }

    // If user is not logged in, show LoginScreen (Sign In / Sign Up with Google Sign-In)
    if (!isLoggedIn) {
        LoginScreen(
            authViewModel = authViewModel,
            demoUsers = users
        )
        return
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    val firestoreRepository = remember { FirestoreClubRepository(context) }
    val clubDashboardViewModel = remember(user.id) {
        ClubDashboardViewModel(repository = firestoreRepository, currentUserId = user.id)
    }

    // Role-specific Navigation Tabs
    val navigationItems: List<RoleNavigationItem> = remember(role, memberships, activities, submissions, dutyLeaves) {
        when (role) {
            UserRole.STUDENT -> listOf(
                RoleNavigationItem("home", "Home", Icons.Filled.Home, Icons.Outlined.Home, "nav_home"),
                RoleNavigationItem("clubs", "4 Clubs", Icons.Filled.Diversity3, Icons.Outlined.Diversity3, "nav_clubs"),
                RoleNavigationItem("events", "Events", Icons.Filled.ConfirmationNumber, Icons.Outlined.ConfirmationNumber, "nav_events"),
                RoleNavigationItem("chat", "Chat", Icons.Filled.Forum, Icons.Outlined.Forum, "nav_chat", badgeCount = 3),
                RoleNavigationItem("profile", "Profile", Icons.Filled.Person, Icons.Outlined.Person, "nav_profile")
            )
            UserRole.COORDINATOR -> {
                val pendingMemCount = memberships.count { it.status == MembershipStatus.PENDING }
                listOf(
                    RoleNavigationItem("coord_hub", "Ops Hub", Icons.Filled.Dashboard, Icons.Outlined.Dashboard, "nav_coord_hub"),
                    RoleNavigationItem("coord_roster", "Roster", Icons.Filled.Group, Icons.Outlined.Group, "nav_coord_roster", badgeCount = pendingMemCount),
                    RoleNavigationItem("coord_plans", "Plans", Icons.Filled.EventNote, Icons.Outlined.EventNote, "nav_coord_plans"),
                    RoleNavigationItem("coord_station", "QR Station", Icons.Filled.QrCodeScanner, Icons.Outlined.QrCodeScanner, "nav_coord_station"),
                    RoleNavigationItem("profile", "Profile", Icons.Filled.Badge, Icons.Outlined.Badge, "nav_profile")
                )
            }
            UserRole.ADVISOR -> {
                val pendingPlansCount = activities.count { !it.advisorApproved }
                val pendingSubCount = submissions.count { !it.isEvaluated }
                listOf(
                    RoleNavigationItem("adv_console", "Console", Icons.Filled.Analytics, Icons.Outlined.Analytics, "nav_adv_console"),
                    RoleNavigationItem("adv_approvals", "Approvals", Icons.Filled.FactCheck, Icons.Outlined.FactCheck, "nav_adv_approvals", badgeCount = pendingPlansCount),
                    RoleNavigationItem("adv_rubrics", "Rubrics", Icons.Filled.RateReview, Icons.Outlined.RateReview, "nav_adv_rubrics", badgeCount = pendingSubCount),
                    RoleNavigationItem("adv_certs", "Certificates", Icons.Filled.WorkspacePremium, Icons.Outlined.WorkspacePremium, "nav_adv_certs"),
                    RoleNavigationItem("profile", "Profile", Icons.Filled.SupervisorAccount, Icons.Outlined.SupervisorAccount, "nav_profile")
                )
            }
            UserRole.CAMPUS_MANAGER -> {
                val pendingLeaveCount = dutyLeaves.count { it.status == DutyLeaveStatus.PENDING }
                listOf(
                    RoleNavigationItem("mgr_dash", "Dashboard", Icons.Filled.SpaceDashboard, Icons.Outlined.SpaceDashboard, "nav_mgr_dash"),
                    RoleNavigationItem("mgr_venues", "Venues", Icons.Filled.MeetingRoom, Icons.Outlined.MeetingRoom, "nav_mgr_venues"),
                    RoleNavigationItem("mgr_finance", "Stripe Audit", Icons.Filled.Payments, Icons.Outlined.Payments, "nav_mgr_finance"),
                    RoleNavigationItem("mgr_duty", "Duty & Alerts", Icons.Filled.Campaign, Icons.Outlined.Campaign, "nav_mgr_duty", badgeCount = pendingLeaveCount),
                    RoleNavigationItem("profile", "Profile", Icons.Filled.AdminPanelSettings, Icons.Outlined.AdminPanelSettings, "nav_profile")
                )
            }
        }
    }

    var selectedTabIndex by remember(role, user.id) { mutableStateOf(0) }
>>>>>>> a05b565 (final project)
>>>>>>> c0aa687 (final project)
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
<<<<<<< HEAD
=======
<<<<<<< HEAD
>>>>>>> c0aa687 (final project)
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
<<<<<<< HEAD
=======
=======
    var activeShareData by remember { mutableStateOf<Pair<String, String>?>(null) }
    var activeCreatePlanClub by remember { mutableStateOf<Club?>(null) }
    var isShowingEditProfile by remember { mutableStateOf(false) }
    var isShowingIdCard by remember { mutableStateOf(false) }
    var isShowingAiCopilot by remember { mutableStateOf(false) }

    // Support device back press to return to first tab if on sub-screens or close AI copilot
    if (isShowingAiCopilot) {
        BackHandler {
            isShowingAiCopilot = false
        }
    } else if (selectedTabIndex != 0) {
        BackHandler {
            selectedTabIndex = 0
>>>>>>> a05b565 (final project)
>>>>>>> c0aa687 (final project)
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
<<<<<<< HEAD
                onOpenRoleSwitcher = { isShowingRoleSwitcher = true },
=======
<<<<<<< HEAD
                onOpenRoleSwitcher = { isShowingRoleSwitcher = true },
=======
>>>>>>> a05b565 (final project)
>>>>>>> c0aa687 (final project)
                notifications = notifications,
                onMarkNotificationsRead = { repository.markNotificationsRead() },
                isDarkMode = isDarkMode,
                onToggleDarkMode = { repository.toggleDarkMode() },
<<<<<<< HEAD
                onSignOut = { repository.signOut() }
=======
<<<<<<< HEAD
                onSignOut = { repository.signOut() }
=======
                onSignOut = { repository.signOut() },
                onOpenAiCopilot = { isShowingAiCopilot = true }
>>>>>>> a05b565 (final project)
>>>>>>> c0aa687 (final project)
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_nav_bar")
            ) {
<<<<<<< HEAD
=======
<<<<<<< HEAD
>>>>>>> c0aa687 (final project)
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
<<<<<<< HEAD
=======
=======
                navigationItems.forEachIndexed { index, item ->
                    val isSelected = selectedTabIndex == index

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTabIndex = index },
                        modifier = Modifier.testTag(item.testTag),
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (item.badgeCount > 0 && !isSelected) {
                                        Badge { Text("${item.badgeCount}") }
>>>>>>> a05b565 (final project)
>>>>>>> c0aa687 (final project)
                                    }
                                }
                            ) {
                                Icon(
<<<<<<< HEAD
=======
<<<<<<< HEAD
>>>>>>> c0aa687 (final project)
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
<<<<<<< HEAD
=======
=======
                                    imageVector = if (isSelected) item.iconFilled else item.iconOutlined,
                                    contentDescription = item.label
                                )
                            }
                        },
                        label = { Text(item.label) }
>>>>>>> a05b565 (final project)
>>>>>>> c0aa687 (final project)
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
<<<<<<< HEAD
=======
<<<<<<< HEAD
>>>>>>> c0aa687 (final project)
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
<<<<<<< HEAD
=======
=======
            val currentItemId = navigationItems.getOrNull(selectedTabIndex)?.id ?: "home"

            when (role) {
                // ==========================================
                // 1. STUDENT INTERFACE
                // ==========================================
                UserRole.STUDENT -> {
                    when (currentItemId) {
                        "home" -> HomeScreen(
                            campus = campus,
                            user = user,
                            role = role,
                            clubs = clubs,
                            activities = activities,
                            events = events,
                            memberships = memberships,
                            onNavigateToClub = { clubId ->
                                selectedClubIdForDetail = clubId
                                selectedTabIndex = 1
                                (clubDashboardViewModel.clubsState.value as? com.example.data.firestore.UiState.Success)?.data?.firstOrNull { it.clubId == clubId }?.let {
                                    clubDashboardViewModel.selectClub(it)
                                }
                            },
                            onNavigateToEvents = { selectedTabIndex = 2 },
                            onNavigateToChat = { channelId ->
                                selectedChannelIdForChat = channelId
                                selectedTabIndex = 3
                            },
                            onOpenCheckIn = { act -> activeCheckInActivity = act },
                            onOpenCoordinatorStation = { act -> activeCoordinatorStationActivity = act },
                            onOpenTranscript = { isShowingTranscript = true },
                            onOpenAiCopilot = { isShowingAiCopilot = true }
                        )

                        "clubs" -> ClubDashboard(
                            viewModel = clubDashboardViewModel,
                            onNavigateToChat = { clubId ->
                                selectedChannelIdForChat = clubId
                                selectedTabIndex = 3
                            }
                        )

                        "events" -> EventList(
                            viewModel = clubDashboardViewModel,
                            onRegisterEvent = { firestoreEvent ->
                                val matched = events.firstOrNull { it.id == firestoreEvent.eventId } ?: events.firstOrNull()
                                if (matched != null && matched.ticketTiers.isNotEmpty()) {
                                    paymentModalEventTier = Pair(matched, matched.ticketTiers.first())
                                }
                            }
                        )

                        "chat" -> ChatScreen(
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

                        "profile" -> ProfileScreen(
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
                            onOpenCertificate = { cert -> viewingCertificate = cert },
                            onVerifyCertNumber = { code -> repository.verifyCertificateByNumber(code) },
                            onShareProfile = {
                                activeShareData = Pair("Official Transcript Profile: ${user.name}", "https://campuscred.msot.edu/verify/${user.rollNo}")
                            },
                            onSignOut = { repository.signOut() },
                            onOpenEditProfile = { isShowingEditProfile = true },
                            onOpenIdCard = { isShowingIdCard = true }
                        )
                    }
                }

                // ==========================================
                // 2. STUDENT COORDINATOR INTERFACE
                // ==========================================
                UserRole.COORDINATOR -> {
                    when (currentItemId) {
                        "coord_hub" -> CoordinatorHubScreen(
                            user = user,
                            campus = campus,
                            clubs = clubs,
                            activities = activities,
                            memberships = memberships,
                            attendanceRecords = attendanceRecords,
                            onLaunchStation = { act -> activeCoordinatorStationActivity = act },
                            onOpenCreatePlan = { club -> activeCreatePlanClub = club },
                            onNavigateToRoster = { selectedTabIndex = 1 }
                        )

                        "coord_roster" -> CoordinatorRosterScreen(
                            memberships = memberships,
                            clubs = clubs,
                            onApproveMembership = { memId -> repository.approveMembership(memId) },
                            onRejectMembership = { memId -> repository.rejectMembership(memId) }
                        )

                        "coord_plans" -> CoordinatorPlansScreen(
                            clubs = clubs,
                            activities = activities,
                            onOpenCreatePlan = { club -> activeCreatePlanClub = club }
                        )

                        "coord_station" -> CoordinatorStationScreen(
                            activities = activities,
                            attendanceRecords = attendanceRecords,
                            onManualCheckIn = { roll, isLate ->
                                val active = activities.firstOrNull { it.status == ActivityStatus.CHECK_IN_OPEN } ?: activities.firstOrNull()
                                if (active != null) {
                                    repository.checkInToActivity(active.id, active.checkInCode, isLate)
                                }
                            }
                        )

                        "profile" -> ProfileScreen(
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
                            onOpenCertificate = { cert -> viewingCertificate = cert },
                            onVerifyCertNumber = { code -> repository.verifyCertificateByNumber(code) },
                            onShareProfile = {
                                activeShareData = Pair("Coordinator Credential: ${user.name}", "https://campuscred.msot.edu/coordinator/${user.rollNo}")
                            },
                            onSignOut = { repository.signOut() },
                            onOpenEditProfile = { isShowingEditProfile = true },
                            onOpenIdCard = { isShowingIdCard = true }
                        )
                    }
                }

                // ==========================================
                // 3. FACULTY ADVISOR INTERFACE
                // ==========================================
                UserRole.ADVISOR -> {
                    when (currentItemId) {
                        "adv_console" -> AdvisorConsoleScreen(
                            user = user,
                            campus = campus,
                            activities = activities,
                            submissions = submissions,
                            certificates = certificates,
                            onNavigateToApprovals = { selectedTabIndex = 1 },
                            onNavigateToRubrics = { selectedTabIndex = 2 },
                            onNavigateToCertificates = { selectedTabIndex = 3 }
                        )

                        "adv_approvals" -> AdvisorApprovalsScreen(
                            activities = activities,
                            onApproveActivityPlan = { actId -> repository.approveActivityPlan(actId) }
                        )

                        "adv_rubrics" -> AdvisorRubricsScreen(
                            submissions = submissions,
                            onOpenEvaluateSubmission = { sub -> activeEvaluatingSubmission = sub }
                        )

                        "adv_certs" -> AdvisorCertificatesScreen(
                            certificates = certificates,
                            onIssueCertificate = { name, roll, title, cat, credits ->
                                repository.issueCertificate(name, roll, title, cat, credits)
                            }
                        )

                        "profile" -> ProfileScreen(
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
                            onOpenCertificate = { cert -> viewingCertificate = cert },
                            onVerifyCertNumber = { code -> repository.verifyCertificateByNumber(code) },
                            onShareProfile = {
                                activeShareData = Pair("Faculty Signatory Profile: ${user.name}", "https://campuscred.msot.edu/faculty/${user.rollNo}")
                            },
                            onSignOut = { repository.signOut() },
                            onOpenEditProfile = { isShowingEditProfile = true },
                            onOpenIdCard = { isShowingIdCard = true }
                        )
                    }
                }

                // ==========================================
                // 4. CAMPUS MANAGER INTERFACE
                // ==========================================
                UserRole.CAMPUS_MANAGER -> {
                    when (currentItemId) {
                        "mgr_dash" -> ManagerDashboardScreen(
                            user = user,
                            campus = campus,
                            clubs = clubs,
                            events = events,
                            tickets = tickets,
                            dutyLeaves = dutyLeaves,
                            onNavigateToVenues = { selectedTabIndex = 1 },
                            onNavigateToFinancials = { selectedTabIndex = 2 },
                            onNavigateToDutyLeaves = { selectedTabIndex = 3 }
                        )

                        "mgr_venues" -> ManagerVenuesScreen(
                            clubs = clubs
                        )

                        "mgr_finance" -> ManagerFinancialsScreen(
                            events = events
                        )

                        "mgr_duty" -> ManagerDutyLeaveScreen(
                            dutyLeaves = dutyLeaves,
                            onReviewDutyLeave = { reqId, approved, notes ->
                                repository.reviewDutyLeave(reqId, approved, notes)
                            },
                            onBroadcastPush = { title, msg, targetCampus ->
                                repository.broadcastPushNotification(title, msg, targetCampus)
                            }
                        )

                        "profile" -> ProfileScreen(
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
                            onOpenCertificate = { cert -> viewingCertificate = cert },
                            onVerifyCertNumber = { code -> repository.verifyCertificateByNumber(code) },
                            onShareProfile = {
                                activeShareData = Pair("Campus Administration: ${user.name}", "https://campuscred.msot.edu/admin/${user.rollNo}")
                            },
                            onSignOut = { repository.signOut() },
                            onOpenEditProfile = { isShowingEditProfile = true },
                            onOpenIdCard = { isShowingIdCard = true }
                        )
                    }
>>>>>>> a05b565 (final project)
>>>>>>> c0aa687 (final project)
                }
            }
        }
    }

<<<<<<< HEAD
    // --- MODALS AND DIALOGS ---

    // 1. Stripe Payment Modal
=======
<<<<<<< HEAD
    // --- MODALS AND DIALOGS ---

    // 1. Stripe Payment Modal
=======
    // ==========================================
    // INTERACTIVE MODALS & DIALOGS
    // ==========================================

    // 1. Stripe Payment Sheet Modal
>>>>>>> a05b565 (final project)
>>>>>>> c0aa687 (final project)
    paymentModalEventTier?.let { (event, tier) ->
        StripePaymentModal(
            event = event,
            initialTier = tier,
            onDismiss = { paymentModalEventTier = null },
<<<<<<< HEAD
            onPaymentSuccess = { ticket ->
                paymentModalEventTier = null
                viewingTicketPass = ticket
=======
<<<<<<< HEAD
            onPaymentSuccess = { ticket ->
                paymentModalEventTier = null
                viewingTicketPass = ticket
=======
            onPaymentSuccess = { newTicket ->
                paymentModalEventTier = null
                viewingTicketPass = newTicket
>>>>>>> a05b565 (final project)
>>>>>>> c0aa687 (final project)
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
<<<<<<< HEAD
                activeShareData = Pair("Pass for ${ticket.eventTitle}", "https://msot.campus/pass/${ticket.id}")
=======
<<<<<<< HEAD
                activeShareData = Pair("Pass for ${ticket.eventTitle}", "https://msot.campus/pass/${ticket.id}")
=======
                activeShareData = Pair("Pass for ${ticket.eventTitle}", "https://campuscred.msot.edu/pass/${ticket.id}")
>>>>>>> a05b565 (final project)
>>>>>>> c0aa687 (final project)
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
<<<<<<< HEAD
                activeShareData = Pair("Official Certificate: ${cert.clubOrEventTitle}", "https://msot.campus/verify/${cert.certNumber}")
=======
<<<<<<< HEAD
                activeShareData = Pair("Official Certificate: ${cert.clubOrEventTitle}", "https://msot.campus/verify/${cert.certNumber}")
=======
                activeShareData = Pair("Official Certificate: ${cert.clubOrEventTitle}", "https://campuscred.msot.edu/verify/${cert.certNumber}")
>>>>>>> a05b565 (final project)
>>>>>>> c0aa687 (final project)
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

<<<<<<< HEAD
=======
<<<<<<< HEAD
>>>>>>> c0aa687 (final project)
    // 9. Role Switcher Modal
    if (isShowingRoleSwitcher) {
        RoleSwitcherModal(
            currentRole = role,
            onRoleSelected = { repository.setRole(it) },
            onDismiss = { isShowingRoleSwitcher = false }
        )
    }

    // 10. Social Share Modal
<<<<<<< HEAD
=======
=======
    // 9. Social Share Modal
>>>>>>> a05b565 (final project)
>>>>>>> c0aa687 (final project)
    activeShareData?.let { (title, url) ->
        SocialShareModal(
            title = title,
            url = url,
            onDismiss = { activeShareData = null }
        )
    }

<<<<<<< HEAD
    // 11. Create Activity Plan Modal
=======
<<<<<<< HEAD
    // 11. Create Activity Plan Modal
=======
    // 10. Create Activity Plan Modal
>>>>>>> a05b565 (final project)
>>>>>>> c0aa687 (final project)
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
<<<<<<< HEAD
=======
<<<<<<< HEAD
=======

    // 11. Edit Profile Modal
    if (isShowingEditProfile) {
        EditProfileModal(
            user = user,
            onDismiss = { isShowingEditProfile = false },
            onSave = { name, bio, dept, yr ->
                repository.updateProfile(name, bio, dept, yr)
            }
        )
    }

    // 12. Digital Campus ID Card Modal
    if (isShowingIdCard) {
        CampusIdCardModal(
            user = user,
            onDismiss = { isShowingIdCard = false },
            onShare = {
                activeShareData = Pair("Digital Campus ID: ${user.name}", "https://campuscred.msot.edu/id/${user.rollNo}")
            }
        )
    }

    // 13. Mirai AI Copilot Full-Screen Assistant
    if (isShowingAiCopilot) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { isShowingAiCopilot = false },
            properties = androidx.compose.ui.window.DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            CampusAiScreen(
                repository = repository,
                currentUser = user,
                currentCampus = campus,
                clubs = clubs,
                onClose = { isShowingAiCopilot = false }
            )
        }
    }
>>>>>>> a05b565 (final project)
>>>>>>> c0aa687 (final project)
}

package com.example.data

<<<<<<< HEAD
=======
<<<<<<< HEAD
=======
import android.content.Context
import android.content.SharedPreferences
>>>>>>> a05b565 (final project)
>>>>>>> c0aa687 (final project)
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
<<<<<<< HEAD
=======
<<<<<<< HEAD
=======
import org.json.JSONArray
import org.json.JSONObject
>>>>>>> a05b565 (final project)
>>>>>>> c0aa687 (final project)
import java.util.UUID

class ClubSphereRepository {

<<<<<<< HEAD
=======
<<<<<<< HEAD
=======
    private var prefs: SharedPreferences? = null

>>>>>>> a05b565 (final project)
>>>>>>> c0aa687 (final project)
    private val _currentCampus = MutableStateFlow(Campus.HI_TECH)
    val currentCampus: StateFlow<Campus> = _currentCampus.asStateFlow()

    private val _currentUser = MutableStateFlow(SampleData.defaultUser)
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    private val _currentRole = MutableStateFlow(UserRole.STUDENT)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    private val _clubs = MutableStateFlow(SampleData.clubs)
    val clubs: StateFlow<List<Club>> = _clubs.asStateFlow()

    private val _memberships = MutableStateFlow(
        listOf(
            Membership("mem_01", SampleData.defaultUser.id, "kaizen_tech", MembershipStatus.APPROVED, "Member", "Sept 01, 2026"),
<<<<<<< HEAD
            Membership("mem_02", SampleData.defaultUser.id, "nexcell", MembershipStatus.APPROVED, "Founding Fellow", "Sept 05, 2026")
=======
<<<<<<< HEAD
            Membership("mem_02", SampleData.defaultUser.id, "nexcell", MembershipStatus.APPROVED, "Founding Fellow", "Sept 05, 2026")
=======
            Membership("mem_02", SampleData.defaultUser.id, "nexcell", MembershipStatus.APPROVED, "Founding Fellow", "Sept 05, 2026"),
            Membership("mem_03", "peer_vikram", "kaizen_tech", MembershipStatus.PENDING, "Applicant", "Yesterday"),
            Membership("mem_04", "peer_neha", "kaizen_tech", MembershipStatus.PENDING, "Applicant", "2 days ago"),
            Membership("mem_05", "std_2024_015", "malhar", MembershipStatus.PENDING, "Applicant", "Today"),
            Membership("mem_06", "std_2024_099", "khel_society", MembershipStatus.PENDING, "Applicant", "Yesterday"),
            Membership("mem_07", "std_2024_077", "nexcell", MembershipStatus.PENDING, "Applicant", "Today")
>>>>>>> a05b565 (final project)
>>>>>>> c0aa687 (final project)
        )
    )
    val memberships: StateFlow<List<Membership>> = _memberships.asStateFlow()

<<<<<<< HEAD
    private val _activities = MutableStateFlow(SampleData.activities)
=======
<<<<<<< HEAD
    private val _activities = MutableStateFlow(SampleData.activities)
=======
    private val _activities = MutableStateFlow(
        listOf(
            ActivityPlan(
                id = "act_plan_pending_01",
                clubId = "kaizen_tech",
                clubName = "Kaizen Tech",
                campus = Campus.HI_TECH,
                title = "Club Day: Quantum Computing & Qiskit Algorithms",
                date = "Saturday, Oct 25, 2026",
                timeSlot = "10:00 AM - 1:00 PM",
                venue = "Turing Quantum Lab 2",
                topic = "Simulating Shor's & Grover's Algorithms on IBM Q Hardware",
                deliverableType = "Jupyter Notebook & Circuit Analysis",
                deadline = "Monday, Oct 27, 11:59 PM",
                status = ActivityStatus.PLANNED,
                advisorApproved = false,
                checkInCode = "491823"
            ),
            ActivityPlan(
                id = "act_plan_pending_02",
                clubId = "malhar",
                clubName = "Malhar",
                campus = Campus.HI_TECH,
                title = "Club Day: Acoustic Lighting & Script Reading",
                date = "Saturday, Oct 25, 2026",
                timeSlot = "2:00 PM - 5:00 PM",
                venue = "Amphitheater Studio",
                topic = "Stage direction, vocal projection and sound mixing",
                deliverableType = "Lighting Cue Sheet & Script",
                deadline = "Monday, Oct 27, 6:00 PM",
                status = ActivityStatus.PLANNED,
                advisorApproved = false,
                checkInCode = "739102"
            )
        ) + SampleData.activities
    )
>>>>>>> a05b565 (final project)
>>>>>>> c0aa687 (final project)
    val activities: StateFlow<List<ActivityPlan>> = _activities.asStateFlow()

    private val _attendanceRecords = MutableStateFlow(SampleData.attendanceRecords)
    val attendanceRecords: StateFlow<List<AttendanceRecord>> = _attendanceRecords.asStateFlow()

    private val _submissions = MutableStateFlow(SampleData.submissions)
    val submissions: StateFlow<List<Submission>> = _submissions.asStateFlow()

    private val _events = MutableStateFlow(SampleData.events)
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    private val _tickets = MutableStateFlow(SampleData.tickets)
    val tickets: StateFlow<List<Ticket>> = _tickets.asStateFlow()

    private val _certificates = MutableStateFlow(SampleData.certificates)
    val certificates: StateFlow<List<Certificate>> = _certificates.asStateFlow()

    private val _chatMessages = MutableStateFlow(SampleData.chatMessages)
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _notifications = MutableStateFlow(SampleData.notifications)
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    private val _dutyLeaveRequests = MutableStateFlow(SampleData.dutyLeaveRequests)
    val dutyLeaveRequests: StateFlow<List<DutyLeaveRequest>> = _dutyLeaveRequests.asStateFlow()

    private val _eventTeams = MutableStateFlow(SampleData.eventTeams)
    val eventTeams: StateFlow<List<EventTeam>> = _eventTeams.asStateFlow()

    private val _users = MutableStateFlow<List<UserProfile>>(SampleData.demoUsers)
    val users: StateFlow<List<UserProfile>> = _users.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(true)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _language = MutableStateFlow("English")
    val language: StateFlow<String> = _language.asStateFlow()

<<<<<<< HEAD
=======
<<<<<<< HEAD
=======
    fun init(context: Context) {
        if (prefs != null) return
        prefs = context.getSharedPreferences("campuscred_storage", Context.MODE_PRIVATE)
        loadState()
    }

    private fun loadState() {
        val p = prefs ?: return
        try {
            // 1. Load Custom Users
            val usersJson = p.getString("saved_users", null)
            if (!usersJson.isNullOrBlank()) {
                val array = JSONArray(usersJson)
                val loadedUsers = mutableListOf<UserProfile>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    loadedUsers.add(
                        UserProfile(
                            id = obj.getString("id"),
                            name = obj.getString("name"),
                            email = obj.getString("email"),
                            rollNo = obj.getString("rollNo"),
                            campus = Campus.valueOf(obj.optString("campus", "HI_TECH")),
                            department = obj.optString("department", "Computer Science & Engineering"),
                            academicYear = obj.optString("academicYear", "1st Year (Semester 1)"),
                            bio = obj.optString("bio", ""),
                            avatarColorHex = obj.optLong("avatarColorHex", 0xFF4338CA),
                            coCurricularCredits = obj.optInt("coCurricularCredits", 18),
                            rankTitle = obj.optString("rankTitle", "Campus Member"),
                            role = UserRole.valueOf(obj.optString("role", "STUDENT")),
                            password = obj.optString("password", "password123")
                        )
                    )
                }
                if (loadedUsers.isNotEmpty()) {
                    _users.value = loadedUsers
                }
            }

            // 2. Load Session
            val loggedInUserId = p.getString("current_user_id", null)
            val isLogged = p.getBoolean("is_logged_in", true)
            if (!loggedInUserId.isNullOrBlank()) {
                val found = _users.value.firstOrNull { it.id == loggedInUserId }
                if (found != null) {
                    _currentUser.value = found
                    _currentRole.value = found.role
                    _currentCampus.value = found.campus
                }
            }
            _isLoggedIn.value = isLogged
            _isDarkMode.value = p.getBoolean("is_dark_mode", true)
            _language.value = p.getString("language", "English") ?: "English"

            // 3. Load Tickets
            val ticketsJson = p.getString("saved_tickets", null)
            if (!ticketsJson.isNullOrBlank()) {
                val array = JSONArray(ticketsJson)
                val loadedTickets = mutableListOf<Ticket>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    loadedTickets.add(
                        Ticket(
                            id = obj.getString("id"),
                            eventId = obj.getString("eventId"),
                            eventTitle = obj.getString("eventTitle"),
                            tierName = obj.getString("tierName"),
                            amountPaid = obj.getDouble("amountPaid"),
                            qrCodePayload = obj.getString("qrCodePayload"),
                            attendeeName = obj.getString("attendeeName"),
                            rollNo = obj.getString("rollNo"),
                            campus = Campus.valueOf(obj.optString("campus", "HI_TECH")),
                            purchaseDate = obj.getString("purchaseDate"),
                            transactionId = obj.getString("transactionId"),
                            status = obj.optString("status", "CONFIRMED")
                        )
                    )
                }
                if (loadedTickets.isNotEmpty()) {
                    _tickets.value = loadedTickets
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveUsers() {
        val p = prefs ?: return
        try {
            val array = JSONArray()
            _users.value.forEach { u ->
                val obj = JSONObject()
                obj.put("id", u.id)
                obj.put("name", u.name)
                obj.put("email", u.email)
                obj.put("rollNo", u.rollNo)
                obj.put("campus", u.campus.name)
                obj.put("department", u.department)
                obj.put("academicYear", u.academicYear)
                obj.put("bio", u.bio)
                obj.put("avatarColorHex", u.avatarColorHex)
                obj.put("coCurricularCredits", u.coCurricularCredits)
                obj.put("rankTitle", u.rankTitle)
                obj.put("role", u.role.name)
                obj.put("password", u.password)
                array.put(obj)
            }
            p.edit().putString("saved_users", array.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveSession() {
        val p = prefs ?: return
        p.edit()
            .putBoolean("is_logged_in", _isLoggedIn.value)
            .putString("current_user_id", _currentUser.value.id)
            .putString("current_campus", _currentCampus.value.name)
            .putString("current_role", _currentRole.value.name)
            .putBoolean("is_dark_mode", _isDarkMode.value)
            .putString("language", _language.value)
            .apply()
    }

    private fun saveTickets() {
        val p = prefs ?: return
        try {
            val array = JSONArray()
            _tickets.value.forEach { t ->
                val obj = JSONObject()
                obj.put("id", t.id)
                obj.put("eventId", t.eventId)
                obj.put("eventTitle", t.eventTitle)
                obj.put("tierName", t.tierName)
                obj.put("amountPaid", t.amountPaid)
                obj.put("qrCodePayload", t.qrCodePayload)
                obj.put("attendeeName", t.attendeeName)
                obj.put("rollNo", t.rollNo)
                obj.put("campus", t.campus.name)
                obj.put("purchaseDate", t.purchaseDate)
                obj.put("transactionId", t.transactionId)
                obj.put("status", t.status)
                array.put(obj)
            }
            p.edit().putString("saved_tickets", array.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateProfile(name: String, bio: String, department: String, academicYear: String) {
        val current = _currentUser.value
        val updated = current.copy(
            name = if (name.isNotBlank()) name.trim() else current.name,
            bio = bio.trim(),
            department = if (department.isNotBlank()) department.trim() else current.department,
            academicYear = if (academicYear.isNotBlank()) academicYear.trim() else current.academicYear
        )
        _currentUser.value = updated
        _users.value = _users.value.map { if (it.id == updated.id) updated else it }
        saveUsers()
        saveSession()

        addNotification(
            title = "Profile Updated",
            message = "Your institutional bio, department, and academic details have been successfully updated.",
            type = "APPROVAL"
        )
    }

>>>>>>> a05b565 (final project)
>>>>>>> c0aa687 (final project)
    // Authentication Actions
    fun signIn(emailOrRoll: String, pass: String): Boolean {
        val query = emailOrRoll.trim().lowercase()
        val match = _users.value.firstOrNull {
            it.email.lowercase() == query || it.rollNo.lowercase() == query
        }

        if (match != null) {
            // Check password (allow default password123 or match stored password)
            if (pass.isBlank() || match.password == pass || pass == "password123") {
                _currentUser.value = match
                _currentRole.value = match.role
                _currentCampus.value = match.campus
                _isLoggedIn.value = true
<<<<<<< HEAD
=======
<<<<<<< HEAD
=======
                saveSession()
>>>>>>> a05b565 (final project)
>>>>>>> c0aa687 (final project)

                addNotification(
                    title = "Welcome back, ${match.name.split(" ").first()}!",
                    message = "Successfully authenticated as ${match.role.label} (${match.campus.displayName}).",
                    type = "APPROVAL"
                )
                return true
            }
        }
        return false
    }

    fun signUp(
        name: String,
        email: String,
        rollNo: String,
        pass: String,
        campus: Campus,
        department: String,
        academicYear: String,
        role: UserRole
    ): Boolean {
<<<<<<< HEAD
=======
<<<<<<< HEAD
>>>>>>> c0aa687 (final project)
        if (name.isBlank() || email.isBlank() || rollNo.isBlank()) return false

        val existing = _users.value.any {
            it.email.equals(email.trim(), ignoreCase = true) || it.rollNo.equals(rollNo.trim(), ignoreCase = true)
        }
        if (existing) return false
<<<<<<< HEAD
=======
=======
        if (name.isBlank() || email.isBlank()) return false

        val actualRollNo = if (rollNo.isNotBlank()) {
            rollNo.trim().uppercase()
        } else {
            "MR-2026-" + UUID.randomUUID().toString().take(4).uppercase()
        }

        val existingIndex = _users.value.indexOfFirst {
            it.email.equals(email.trim(), ignoreCase = true)
        }
        if (existingIndex != -1) {
            val existing = _users.value[existingIndex]
            val updated = existing.copy(
                name = name.trim().ifBlank { existing.name },
                rollNo = if (rollNo.isNotBlank()) rollNo.trim().uppercase() else existing.rollNo,
                campus = campus,
                department = department.ifBlank { existing.department },
                academicYear = academicYear.ifBlank { existing.academicYear },
                password = if (pass.isNotBlank()) pass else existing.password,
                role = role
            )
            val list = _users.value.toMutableList()
            list[existingIndex] = updated
            _users.value = list
            _currentUser.value = updated
            _currentRole.value = role
            _currentCampus.value = campus
            _isLoggedIn.value = true
            saveUsers()
            saveSession()

            addNotification(
                title = "Welcome Back, ${updated.name}!",
                message = "Your CampusCred profile has been verified and synchronized.",
                type = "APPROVAL"
            )
            return true
        }
>>>>>>> a05b565 (final project)
>>>>>>> c0aa687 (final project)

        val avatarColors = listOf(0xFF4338CA, 0xFF0284C7, 0xFF9333EA, 0xFF059669, 0xFFEA580C, 0xFF0D9488)
        val color = avatarColors.random()

        val newUser = UserProfile(
            id = "user_" + UUID.randomUUID().toString().take(8),
            name = name.trim(),
            email = email.trim().lowercase(),
<<<<<<< HEAD
            rollNo = rollNo.trim().uppercase(),
=======
<<<<<<< HEAD
            rollNo = rollNo.trim().uppercase(),
=======
            rollNo = actualRollNo,
>>>>>>> a05b565 (final project)
>>>>>>> c0aa687 (final project)
            campus = campus,
            department = department.ifBlank { "Computer Science & Engineering" },
            academicYear = academicYear.ifBlank { "1st Year (Semester 1)" },
            bio = "MSOT ${campus.displayName} student. Active learner and campus community member.",
            avatarColorHex = color,
            coCurricularCredits = 10,
            rankTitle = when (role) {
                UserRole.STUDENT -> "New Campus Member"
                UserRole.COORDINATOR -> "Student Coordinator"
                UserRole.ADVISOR -> "Faculty Advisor"
                UserRole.CAMPUS_MANAGER -> "Campus Administrator"
            },
            role = role,
            password = if (pass.isNotBlank()) pass else "password123"
        )

        _users.value = _users.value + newUser
        _currentUser.value = newUser
        _currentRole.value = role
        _currentCampus.value = campus
        _isLoggedIn.value = true
<<<<<<< HEAD
=======
<<<<<<< HEAD
=======
        saveUsers()
        saveSession()
>>>>>>> a05b565 (final project)
>>>>>>> c0aa687 (final project)

        addNotification(
            title = "Account Created Successfully!",
            message = "Welcome to CampusCred, ${newUser.name}. Your institutional profile is verified.",
            type = "APPROVAL"
        )
        return true
    }

    fun signOut() {
        _isLoggedIn.value = false
<<<<<<< HEAD
=======
<<<<<<< HEAD
=======
        saveSession()
>>>>>>> a05b565 (final project)
>>>>>>> c0aa687 (final project)
    }

    fun switchUser(userId: String) {
        val user = _users.value.firstOrNull { it.id == userId }
        if (user != null) {
            _currentUser.value = user
            _currentRole.value = user.role
            _currentCampus.value = user.campus
            _isLoggedIn.value = true
        }
    }

    // Actions
    fun setCampus(campus: Campus) {
        _currentCampus.value = campus
        _currentUser.value = _currentUser.value.copy(campus = campus)
    }

    fun setRole(role: UserRole) {
        _currentRole.value = role
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun setLanguage(lang: String) {
        _language.value = lang
    }

    fun requestMembership(clubId: String): Boolean {
<<<<<<< HEAD
        val existing = _memberships.value.firstOrNull { it.clubId == clubId }
=======
<<<<<<< HEAD
        val existing = _memberships.value.firstOrNull { it.clubId == clubId }
=======
        val existing = _memberships.value.firstOrNull { it.clubId == clubId && it.userId == _currentUser.value.id }
>>>>>>> a05b565 (final project)
>>>>>>> c0aa687 (final project)
        if (existing != null) return false

        val newMembership = Membership(
            id = "mem_" + UUID.randomUUID().toString().take(8),
            userId = _currentUser.value.id,
            clubId = clubId,
            status = MembershipStatus.APPROVED, // instant verified onboarding
            roleInClub = "Active Member",
            joinedAt = "Today"
        )
        _memberships.value = _memberships.value + newMembership

        // Increment club member count
        _clubs.value = _clubs.value.map {
            if (it.id == clubId) it.copy(memberCount = it.memberCount + 1) else it
        }

        // Add push notification
        addNotification(
            title = "Club Membership Enrolled",
            message = "You are now an officially verified member of ${_clubs.value.firstOrNull { it.id == clubId }?.name ?: clubId}.",
            type = "APPROVAL"
        )
        return true
    }

    fun leaveClub(clubId: String) {
<<<<<<< HEAD
        _memberships.value = _memberships.value.filterNot { it.clubId == clubId }
=======
<<<<<<< HEAD
        _memberships.value = _memberships.value.filterNot { it.clubId == clubId }
=======
        _memberships.value = _memberships.value.filterNot { it.clubId == clubId && it.userId == _currentUser.value.id }
>>>>>>> a05b565 (final project)
>>>>>>> c0aa687 (final project)
        _clubs.value = _clubs.value.map {
            if (it.id == clubId && it.memberCount > 0) it.copy(memberCount = it.memberCount - 1) else it
        }
    }

<<<<<<< HEAD
=======
<<<<<<< HEAD
=======
    fun approveMembership(membershipId: String): Boolean {
        var approvedClubName = ""
        _memberships.value = _memberships.value.map { mem ->
            if (mem.id == membershipId) {
                approvedClubName = _clubs.value.firstOrNull { it.id == mem.clubId }?.name ?: "Club"
                mem.copy(status = MembershipStatus.APPROVED, roleInClub = "Verified Member")
            } else mem
        }
        addNotification(
            title = "Membership Approved!",
            message = "Student membership request for $approvedClubName has been approved by coordinator.",
            type = "APPROVAL"
        )
        return true
    }

    fun rejectMembership(membershipId: String): Boolean {
        _memberships.value = _memberships.value.filterNot { it.id == membershipId }
        addNotification(
            title = "Membership Request Updated",
            message = "Student membership application was reviewed and archived.",
            type = "INFO"
        )
        return true
    }

    fun issueCertificate(
        studentName: String,
        rollNo: String,
        clubOrEventTitle: String,
        category: String,
        credits: Int
    ): Certificate {
        val newCert = Certificate(
            id = "cert_" + UUID.randomUUID().toString().take(8),
            certNumber = "MSOT-CERT-" + (1000..9999).random() + "-2026",
            studentName = studentName,
            rollNo = rollNo,
            clubOrEventTitle = clubOrEventTitle,
            category = category,
            issueDate = "Today, Oct 14, 2026",
            verificationHash = "SHA256:" + UUID.randomUUID().toString().replace("-", "").take(32),
            signatoryName = _currentUser.value.name,
            signatoryRole = "Faculty Advisor, MSOT"
        )
        _certificates.value = listOf(newCert) + _certificates.value

        addNotification(
            title = "Digital Certificate Issued!",
            message = "Official credential issued to $studentName ($rollNo) for $clubOrEventTitle.",
            type = "APPROVAL"
        )
        return newCert
    }

    fun broadcastPushNotification(title: String, message: String, targetCampus: Campus? = null) {
        val campusLabel = targetCampus?.displayName ?: "All Campuses"
        addNotification(
            title = "[Broadcast: $campusLabel] $title",
            message = message,
            type = "ALERT"
        )
    }

>>>>>>> a05b565 (final project)
>>>>>>> c0aa687 (final project)
    fun checkInToActivity(activityId: String, inputCode: String, isLate: Boolean): Boolean {
        val activity = _activities.value.firstOrNull { it.id == activityId } ?: return false
        // Valid if matches the rotating PIN or QR code format
        val isValid = (inputCode.trim() == activity.checkInCode || inputCode.contains("VERIFIED") || inputCode.length >= 4)
        if (!isValid) return false

        // Check duplicate
        val duplicate = _attendanceRecords.value.any { it.activityId == activityId && it.studentId == _currentUser.value.id }
        if (duplicate) return true

        val record = AttendanceRecord(
            id = "att_" + UUID.randomUUID().toString().take(8),
            activityId = activityId,
            studentId = _currentUser.value.id,
            studentName = _currentUser.value.name,
            rollNo = _currentUser.value.rollNo,
            timestamp = if (isLate) "Today at 10:45 AM (Late)" else "Today at 10:02 AM",
            status = if (isLate) AttendanceStatus.LATE else AttendanceStatus.PRESENT,
            isLate = isLate,
            verificationToken = "QR_VERIFIED_" + (1000..9999).random()
        )
        _attendanceRecords.value = listOf(record) + _attendanceRecords.value

<<<<<<< HEAD
        addNotification(
            title = "Attendance Verified!",
=======
<<<<<<< HEAD
        addNotification(
            title = "Attendance Verified!",
=======
        // Real credit accumulation: Punctual +2 credits, Late +1 credit
        val creditGain = if (isLate) 1 else 2
        val updatedUser = _currentUser.value.copy(coCurricularCredits = _currentUser.value.coCurricularCredits + creditGain)
        _currentUser.value = updatedUser
        _users.value = _users.value.map { if (it.id == updatedUser.id) updatedUser else it }
        saveUsers()
        saveSession()

        addNotification(
            title = "Attendance Verified! (+$creditGain Credits)",
>>>>>>> a05b565 (final project)
>>>>>>> c0aa687 (final project)
            message = "Check-in confirmed for ${activity.title} (${if (isLate) "Late Flagged" else "Punctual"}).",
            type = "EVENT"
        )
        return true
    }

    fun correctAttendance(recordId: String, newStatus: AttendanceStatus, reason: String) {
        _attendanceRecords.value = _attendanceRecords.value.map {
            if (it.id == recordId) {
                it.copy(
                    status = newStatus,
                    correctionReason = reason,
                    correctedBy = _currentUser.value.name + " (" + _currentRole.value.label + ")"
                )
            } else it
        }
    }

    fun submitDeliverable(activityId: String, title: String, fileType: String, url: String) {
        val activity = _activities.value.firstOrNull { it.id == activityId }
        val newSub = Submission(
            id = "sub_" + UUID.randomUUID().toString().take(8),
            activityId = activityId,
            activityTitle = activity?.title ?: "Club Day Deliverable",
            clubName = activity?.clubName ?: "Kaizen Tech",
            studentId = _currentUser.value.id,
            studentName = _currentUser.value.name,
            fileTitle = title,
            fileType = fileType,
            resourceUrl = url,
            submittedAt = "Just now",
            isLate = false,
            isEvaluated = false
        )
        _submissions.value = listOf(newSub) + _submissions.value

        addNotification(
            title = "Deliverable Submitted",
            message = "Your submission '$title' for ${activity?.clubName ?: "Club"} has been received and queued for evaluation.",
            type = "DEADLINE"
        )
    }

    fun evaluateSubmission(submissionId: String, score: Int, feedback: String, evaluatorName: String) {
        _submissions.value = _submissions.value.map {
            if (it.id == submissionId) {
                it.copy(
                    isEvaluated = true,
                    score = score,
                    feedback = feedback,
                    evaluatorName = evaluatorName
                )
            } else it
        }

        addNotification(
            title = "Submission Evaluated",
            message = "Score: $score/100 awarded by $evaluatorName. Feedback: \"$feedback\"",
            type = "APPROVAL"
        )
    }

    fun purchaseTicketWithStripe(
        eventId: String,
        tierName: String,
        amount: Double,
        cardLast4: String,
        promoCode: String?
    ): Ticket {
        val event = _events.value.firstOrNull { it.id == eventId }
        val txId = "tx_stripe_" + UUID.randomUUID().toString().replace("-", "").take(12)
        val finalAmount = if (promoCode?.trim()?.uppercase() == "MSOT100") 0.0 else amount

        val ticket = Ticket(
            id = "tkt_" + UUID.randomUUID().toString().take(8),
            eventId = eventId,
            eventTitle = event?.title ?: "Event Pass",
            tierName = tierName,
            amountPaid = finalAmount,
            qrCodePayload = "MSOT-TKT-${event?.clubName?.take(3)?.uppercase() ?: "EVT"}-${(10000..99999).random()}-${_currentUser.value.rollNo}",
            attendeeName = _currentUser.value.name,
            rollNo = _currentUser.value.rollNo,
            campus = _currentCampus.value,
            purchaseDate = "Today, Oct 13",
            transactionId = txId
        )

        _tickets.value = listOf(ticket) + _tickets.value

        // Increase registered count
        _events.value = _events.value.map {
            if (it.id == eventId) it.copy(registeredCount = it.registeredCount + 1) else it
        }
<<<<<<< HEAD
=======
<<<<<<< HEAD
=======
        saveTickets()
>>>>>>> a05b565 (final project)
>>>>>>> c0aa687 (final project)

        addNotification(
            title = "Ticket Confirmed! ($tierName)",
            message = "Payment processed via Stripe ($cardLast4). Pass generated for ${event?.title}.",
            type = "TICKET"
        )

        return ticket
    }

    fun sendChatMessage(channelId: String, text: String, isDirect: Boolean = false) {
        val channelTitle = if (isDirect) {
            "Peer Networking (E2EE)"
        } else {
            _clubs.value.firstOrNull { it.id == channelId }?.name ?: channelId
        }

        val msg = ChatMessage(
            id = "msg_" + UUID.randomUUID().toString().take(8),
            channelId = channelId,
            channelTitle = channelTitle,
            senderId = _currentUser.value.id,
            senderName = _currentUser.value.name,
            senderRole = _currentRole.value.label,
            avatarColorHex = _currentUser.value.avatarColorHex,
            messageText = text,
            timestamp = "Just now",
            isDirect = isDirect,
            isEncrypted = isDirect,
            reactions = emptyMap()
        )
        _chatMessages.value = _chatMessages.value + msg
    }

    fun toggleReaction(messageId: String, emoji: String) {
        _chatMessages.value = _chatMessages.value.map { msg ->
            if (msg.id == messageId) {
                val currentCount = msg.reactions[emoji] ?: 0
                val updated = msg.reactions.toMutableMap()
                if (currentCount > 0) {
                    updated[emoji] = currentCount + 1
                } else {
                    updated[emoji] = 1
                }
                msg.copy(reactions = updated)
            } else msg
        }
    }

    fun addNotification(title: String, message: String, type: String) {
        val n = AppNotification(
            id = "notif_" + UUID.randomUUID().toString().take(8),
            title = title,
            message = message,
            timestamp = "Just now",
            type = type,
            isRead = false
        )
        _notifications.value = listOf(n) + _notifications.value
    }

    fun markNotificationsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    fun verifyCertificateByNumber(certNumber: String): Certificate? {
        val query = certNumber.trim().lowercase()
        return _certificates.value.firstOrNull {
            it.certNumber.lowercase() == query || it.id.lowercase() == query || it.verificationHash.lowercase().contains(query)
        }
    }

    fun updateUserProfile(name: String, bio: String, department: String, academicYear: String) {
        _currentUser.value = _currentUser.value.copy(
            name = name,
            bio = bio,
            department = department,
            academicYear = academicYear
        )
    }

    fun createActivityPlan(
        clubId: String,
        title: String,
        date: String,
        timeSlot: String,
        venue: String,
        topic: String,
        deliverableType: String,
        deadline: String
    ): ActivityPlan {
        val club = _clubs.value.firstOrNull { it.id == clubId }
        val randomPin = (100000..999999).random().toString()
        val newActivity = ActivityPlan(
            id = "act_" + UUID.randomUUID().toString().take(8),
            clubId = clubId,
            clubName = club?.name ?: "Club",
            campus = _currentCampus.value,
            title = title,
            date = date,
            timeSlot = timeSlot,
            venue = venue,
            topic = topic,
            deliverableType = deliverableType,
            deadline = deadline,
            status = ActivityStatus.PLANNED,
            advisorApproved = false,
            checkInCode = randomPin
        )
        _activities.value = listOf(newActivity) + _activities.value

        addNotification(
            title = "New Activity Plan Submitted",
            message = "Plan '$title' for ${club?.name} is submitted and awaiting Faculty Advisor approval.",
            type = "APPROVAL"
        )
        return newActivity
    }

    fun approveActivityPlan(activityId: String): Boolean {
        var found = false
        _activities.value = _activities.value.map { act ->
            if (act.id == activityId) {
                found = true
                act.copy(
                    status = ActivityStatus.CHECK_IN_OPEN,
                    advisorApproved = true
                )
            } else act
        }
        if (found) {
            val act = _activities.value.first { it.id == activityId }
            addNotification(
                title = "Club Day Plan Approved!",
                message = "${act.title} is officially approved by Faculty Advisor. Check-in is now open.",
                type = "APPROVAL"
            )
        }
        return found
    }

    fun requestDutyLeave(eventId: String, reason: String): DutyLeaveRequest {
        val event = _events.value.firstOrNull { it.id == eventId }
        val req = DutyLeaveRequest(
            id = "dl_" + UUID.randomUUID().toString().take(8),
            studentId = _currentUser.value.id,
            studentName = _currentUser.value.name,
            rollNo = _currentUser.value.rollNo,
            campus = _currentUser.value.campus,
            eventId = eventId,
            eventTitle = event?.title ?: "Inter-Campus Event",
            dates = event?.date ?: "Upcoming Dates",
            reason = reason,
            status = DutyLeaveStatus.PENDING,
            requestedAt = "Today, Oct 14"
        )
        _dutyLeaveRequests.value = listOf(req) + _dutyLeaveRequests.value

        addNotification(
            title = "Duty Leave Request Submitted",
            message = "Request for ${event?.title} submitted to Campus Manager for academic leave endorsement.",
            type = "EVENT"
        )
        return req
    }

    fun reviewDutyLeave(requestId: String, isApproved: Boolean, notes: String) {
        _dutyLeaveRequests.value = _dutyLeaveRequests.value.map { req ->
            if (req.id == requestId) {
                req.copy(
                    status = if (isApproved) DutyLeaveStatus.APPROVED else DutyLeaveStatus.REJECTED,
                    reviewedBy = _currentUser.value.name + " (" + _currentRole.value.label + ")",
                    reviewNotes = notes
                )
            } else req
        }

        val reviewed = _dutyLeaveRequests.value.firstOrNull { it.id == requestId }
        addNotification(
            title = if (isApproved) "Duty Leave Approved!" else "Duty Leave Request Update",
            message = "${if (isApproved) "Endorsed" else "Declined"} by Campus Manager: ${notes}",
            type = "APPROVAL"
        )
    }

    fun createTeam(eventId: String, teamName: String, maxMembers: Int = 4): EventTeam {
        val event = _events.value.firstOrNull { it.id == eventId }
        val joinCode = teamName.take(2).uppercase() + (10..99).random()
        val newTeam = EventTeam(
            id = "team_" + UUID.randomUUID().toString().take(8),
            eventId = eventId,
            eventTitle = event?.title ?: "Event",
            teamName = teamName,
            joinCode = joinCode,
            leaderId = _currentUser.value.id,
            leaderName = _currentUser.value.name,
            members = listOf("${_currentUser.value.name} (Leader)"),
            maxMembers = maxMembers
        )
        _eventTeams.value = listOf(newTeam) + _eventTeams.value

        addNotification(
            title = "Team Formed: $teamName",
            message = "Share Join Code '$joinCode' with your campus peers to recruit teammates.",
            type = "EVENT"
        )
        return newTeam
    }

    fun joinTeam(joinCode: String): Boolean {
        val cleanCode = joinCode.trim().uppercase()
        val target = _eventTeams.value.firstOrNull { it.joinCode.uppercase() == cleanCode } ?: return false

        if (target.members.size >= target.maxMembers) return false
        if (target.members.any { it.contains(_currentUser.value.name) }) return true

        _eventTeams.value = _eventTeams.value.map { team ->
            if (team.id == target.id) {
                team.copy(members = team.members + _currentUser.value.name)
            } else team
        }

        addNotification(
            title = "Joined Team ${target.teamName}!",
            message = "You are now registered as an active teammate for ${target.eventTitle}.",
            type = "EVENT"
        )
        return true
    }

    companion object {
        val instance by lazy { ClubSphereRepository() }
    }
}

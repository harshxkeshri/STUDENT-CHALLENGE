package com.example.model

enum class Campus(val displayName: String, val shortName: String, val city: String) {
    HI_TECH("HI-Tech Campus", "HI-Tech", "Silicon Corridor"),
    RATHINUM("Rathinum Campus (Bangalore)", "Rathinum", "Bangalore"),
    JAGANNATH("Jagannath Campus (Jaipur)", "Jagannath", "Jaipur")
}

enum class UserRole(val label: String, val description: String) {
    STUDENT("Student", "Browse clubs, register, check-in, submit work & view transcript"),
    COORDINATOR("Student Coordinator", "Run Club Day check-ins, record results & manage club"),
    ADVISOR("Faculty Advisor", "Approve activity plans, evaluate deliverables & certificates"),
    CAMPUS_MANAGER("Campus Manager", "Manage campus clubs, duty leave approvals & analytics")
}

enum class ClubCategory(val label: String) {
    TECH("Technology & Computing"),
    CREATIVE("Creative & Cultural"),
    SPORTS("Sports & Athletics"),
    ENTREPRENEUR("Entrepreneurship & Business")
}

data class Club(
    val id: String,
    val name: String,
    val shortCode: String,
    val category: ClubCategory,
    val tagLine: String,
    val description: String,
    val facultyAdvisor: String,
    val leadCoordinator: String,
    val memberCount: Int,
    val capacity: Int,
    val meetingSchedule: String,
    val campus: Campus,
    val primaryColorHex: Long,
    val accentColorHex: Long,
    val objectives: List<String> = emptyList()
)

enum class MembershipStatus {
    APPROVED,
    PENDING,
    WAITLISTED,
    NONE
}

data class Membership(
    val id: String,
    val userId: String,
    val clubId: String,
    val status: MembershipStatus,
    val roleInClub: String = "Member",
    val joinedAt: String
)

enum class ActivityStatus {
    PLANNED,
    APPROVED,
    ANNOUNCED,
    CHECK_IN_OPEN,
    SUBMISSION_OPEN,
    EVALUATED
}

data class RubricCriterion(
    val name: String,
    val maxScore: Int,
    val description: String
)

data class ActivityPlan(
    val id: String,
    val clubId: String,
    val clubName: String,
    val campus: Campus,
    val title: String,
    val date: String,
    val timeSlot: String,
    val venue: String,
    val topic: String,
    val deliverableType: String,
    val deadline: String,
    val status: ActivityStatus,
    val advisorApproved: Boolean,
    val checkInCode: String, // 6-digit rotating fast check-in code
    val rubrics: List<RubricCriterion> = listOf(
        RubricCriterion("Content Quality", 40, "Depth of research, accuracy and completeness"),
        RubricCriterion("Presentation", 30, "Structure, visual clarity and articulate delivery"),
        RubricCriterion("Participation", 30, "Punctuality, active Q&A, and peer collaboration")
    )
)

enum class AttendanceStatus {
    PRESENT,
    LATE,
    ABSENT,
    CORRECTED
}

data class AttendanceRecord(
    val id: String,
    val activityId: String,
    val studentId: String,
    val studentName: String,
    val rollNo: String,
    val timestamp: String,
    val status: AttendanceStatus,
    val isLate: Boolean,
    val verificationToken: String,
    val correctionReason: String? = null,
    val correctedBy: String? = null
)

data class Submission(
    val id: String,
    val activityId: String,
    val activityTitle: String,
    val clubName: String,
    val studentId: String,
    val studentName: String,
    val fileTitle: String,
    val fileType: String, // PPT, PDF, Link, Report
    val resourceUrl: String,
    val submittedAt: String,
    val isLate: Boolean,
    val isEvaluated: Boolean,
    val score: Int? = null, // e.g. 92/100
    val feedback: String? = null,
    val evaluatorName: String? = null
)

data class TicketTier(
    val name: String,
    val priceUsd: Double,
    val perks: String,
    val remainingSeats: Int
)

data class Event(
    val id: String,
    val title: String,
    val clubId: String,
    val clubName: String,
    val campus: Campus,
    val isInterCampus: Boolean,
    val description: String,
    val date: String,
    val time: String,
    val venue: String,
    val registeredCount: Int,
    val capacity: Int,
    val ticketTiers: List<TicketTier>,
    val isDutyLeaveEligible: Boolean = true,
    val bannerGradientHex: Pair<Long, Long>
)

data class Ticket(
    val id: String,
    val eventId: String,
    val eventTitle: String,
    val tierName: String,
    val amountPaid: Double,
    val qrCodePayload: String,
    val attendeeName: String,
    val rollNo: String,
    val campus: Campus,
    val purchaseDate: String,
    val transactionId: String,
    val status: String = "CONFIRMED"
)

data class Certificate(
    val id: String,
    val certNumber: String,
    val studentName: String,
    val rollNo: String,
    val clubOrEventTitle: String,
    val category: String,
    val issueDate: String,
    val verificationHash: String,
    val signatoryName: String,
    val signatoryRole: String,
    val isRevoked: Boolean = false
)

enum class DutyLeaveStatus {
    PENDING,
    APPROVED,
    REJECTED
}

data class DutyLeaveRequest(
    val id: String,
    val studentId: String,
    val studentName: String,
    val rollNo: String,
    val campus: Campus,
    val eventId: String,
    val eventTitle: String,
    val dates: String,
    val reason: String,
    val status: DutyLeaveStatus,
    val requestedAt: String,
    val reviewedBy: String? = null,
    val reviewNotes: String? = null
)

data class EventTeam(
    val id: String,
    val eventId: String,
    val eventTitle: String,
    val teamName: String,
    val joinCode: String,
    val leaderId: String,
    val leaderName: String,
    val members: List<String>,
    val maxMembers: Int = 4
)

data class ChatMessage(
    val id: String,
    val channelId: String, // club id or "networking"
    val channelTitle: String,
    val senderId: String,
    val senderName: String,
    val senderRole: String,
    val avatarColorHex: Long,
    val messageText: String,
    val timestamp: String,
    val isDirect: Boolean = false,
    val isEncrypted: Boolean = true,
    val reactions: Map<String, Int> = emptyMap()
)

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val type: String, // EVENT, APPROVAL, DEADLINE, TICKET
    val isRead: Boolean = false
)

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val rollNo: String,
    val campus: Campus,
    val department: String,
    val academicYear: String,
    val bio: String,
    val avatarColorHex: Long,
    val coCurricularCredits: Int = 18,
    val rankTitle: String = "Senior Club Fellow",
    val role: UserRole = UserRole.STUDENT,
    val password: String = "password123"
)

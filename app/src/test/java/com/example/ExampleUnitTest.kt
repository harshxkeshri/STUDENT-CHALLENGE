package com.example

import com.example.data.ClubSphereRepository
import com.example.model.AttendanceStatus
import com.example.model.Campus
import com.example.model.ClubCategory
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ExampleUnitTest {

    private lateinit var repo: ClubSphereRepository

    @Before
    fun setUp() {
        repo = ClubSphereRepository()
    }

    @Test
    fun testTheFourClubsExistAndCategoriesMatch() {
        val clubs = repo.clubs.value
        assertEquals(4, clubs.size)

        val kaizenTech = clubs.firstOrNull { it.id == "kaizen_tech" }
        assertNotNull(kaizenTech)
        assertEquals("Kaizen Tech", kaizenTech!!.name)
        assertEquals(ClubCategory.TECH, kaizenTech.category)

        val malhar = clubs.firstOrNull { it.id == "malhar" }
        assertNotNull(malhar)
        assertEquals("Malhar", malhar!!.name)
        assertEquals(ClubCategory.CREATIVE, malhar.category)

        val khelSociety = clubs.firstOrNull { it.id == "khel_society" }
        assertNotNull(khelSociety)
        assertEquals("Khel Society", khelSociety!!.name)
        assertEquals(ClubCategory.SPORTS, khelSociety.category)

        val nexcell = clubs.firstOrNull { it.id == "nexcell" }
        assertNotNull(nexcell)
        assertEquals("Nexcell", nexcell!!.name)
        assertEquals(ClubCategory.ENTREPRENEUR, nexcell.category)
    }

    @Test
    fun testCampusSwitching() {
        assertEquals(Campus.HI_TECH, repo.currentCampus.value)
        repo.setCampus(Campus.RATHINUM)
        assertEquals(Campus.RATHINUM, repo.currentCampus.value)
        assertEquals(Campus.RATHINUM, repo.currentUser.value.campus)

        repo.setCampus(Campus.JAGANNATH)
        assertEquals(Campus.JAGANNATH, repo.currentCampus.value)
        assertEquals(Campus.JAGANNATH, repo.currentUser.value.campus)
    }

    @Test
    fun testMembershipRegistration() {
        val clubId = "malhar"
        val initialMembers = repo.clubs.value.first { it.id == clubId }.memberCount

        val joined = repo.requestMembership(clubId)
        assertTrue(joined)

        val updatedMembers = repo.clubs.value.first { it.id == clubId }.memberCount
        assertEquals(initialMembers + 1, updatedMembers)

        // Prevent duplicate joining
        val duplicateJoin = repo.requestMembership(clubId)
        assertFalse(duplicateJoin)
    }

    @Test
    fun testCheckInWorkflow() {
        val activity = repo.activities.value.first()
        val success = repo.checkInToActivity(activity.id, activity.checkInCode, isLate = false)
        assertTrue(success)

        val record = repo.attendanceRecords.value.firstOrNull { it.activityId == activity.id }
        assertNotNull(record)
        assertEquals(AttendanceStatus.PRESENT, record!!.status)
        assertFalse(record.isLate)
        assertTrue(record.verificationToken.startsWith("QR_VERIFIED_"))
    }

    @Test
    fun testDeliverableSubmissionAndRubricEvaluation() {
        val activity = repo.activities.value.first()
        repo.submitDeliverable(
            activityId = activity.id,
            title = "Test AI System Repo",
            fileType = "GitHub Repo",
            url = "https://github.com/student/test-ai"
        )

        val submission = repo.submissions.value.firstOrNull { it.fileTitle == "Test AI System Repo" }
        assertNotNull(submission)
        assertFalse(submission!!.isEvaluated)

        repo.evaluateSubmission(
            submissionId = submission.id,
            score = 95,
            feedback = "Great system design",
            evaluatorName = "Dr. Ramanujan"
        )

        val evaluated = repo.submissions.value.first { it.id == submission.id }
        assertTrue(evaluated.isEvaluated)
        assertEquals(95, evaluated.score)
        assertEquals("Great system design", evaluated.feedback)
    }

    @Test
    fun testStripePaymentAndTicketGeneration() {
        val event = repo.events.value.first()
        val initialRegistrations = event.registeredCount

        val ticket = repo.purchaseTicketWithStripe(
            eventId = event.id,
            tierName = "Student Standard",
            amount = 10.0,
            cardLast4 = "4242",
            promoCode = null
        )

        assertNotNull(ticket)
        assertEquals(10.0, ticket.amountPaid, 0.01)
        assertTrue(ticket.transactionId.startsWith("tx_stripe_"))
        assertTrue(ticket.qrCodePayload.startsWith("MSOT-TKT-"))

        val updatedEvent = repo.events.value.first { it.id == event.id }
        assertEquals(initialRegistrations + 1, updatedEvent.registeredCount)
    }

    @Test
    fun testStripe100PercentPromoDiscount() {
        val event = repo.events.value.first()
        val ticket = repo.purchaseTicketWithStripe(
            eventId = event.id,
            tierName = "Student Standard",
            amount = 10.0,
            cardLast4 = "4242",
            promoCode = "MSOT100"
        )

        assertEquals(0.0, ticket.amountPaid, 0.01)
    }

    @Test
    fun testRealTimeChatAndReactions() {
        val initialCount = repo.chatMessages.value.size
        repo.sendChatMessage(
            channelId = "kaizen_tech",
            text = "Hello Kaizen members!",
            isDirect = false
        )

        assertEquals(initialCount + 1, repo.chatMessages.value.size)
        val sentMsg = repo.chatMessages.value.last()
        assertEquals("Hello Kaizen members!", sentMsg.messageText)

        repo.toggleReaction(sentMsg.id, "🚀")
        val reactedMsg = repo.chatMessages.value.first { it.id == sentMsg.id }
        assertEquals(1, reactedMsg.reactions["🚀"])
    }

    @Test
    fun testCertificateVerification() {
        val validCert = repo.verifyCertificateByNumber("MSOT-CERT-KT-2026-0841")
        assertNotNull(validCert)
        assertEquals("Chirag Sharma", validCert!!.studentName)

        val invalidCert = repo.verifyCertificateByNumber("INVALID-CERT-999")
        assertNull(invalidCert)
    }

    @Test
    fun testSignInWithValidCredentials() {
        val success = repo.signIn("chirushi07@gmail.com", "password123")
        assertTrue(success)
        assertTrue(repo.isLoggedIn.value)
        assertEquals("Chirag Sharma", repo.currentUser.value.name)
    }

    @Test
    fun testSignInWithRollNumber() {
        val success = repo.signIn("MSOT-2023-CS-042", "password123")
        assertTrue(success)
        assertTrue(repo.isLoggedIn.value)
        assertEquals("Chirag Sharma", repo.currentUser.value.name)
    }

    @Test
    fun testSignInWithInvalidPassword() {
        val success = repo.signIn("chirushi07@gmail.com", "wrongpass")
        assertFalse(success)
    }

    @Test
    fun testSignUpNewStudentWithCampus() {
        val initialUsers = repo.users.value.size
        val success = repo.signUp(
            name = "Ravi Kumar",
            email = "ravi.kumar@msot.edu",
            rollNo = "MSOT-2025-CS-199",
            pass = "mypassword456",
            campus = Campus.RATHINUM,
            department = "Computer Science & Engineering",
            academicYear = "1st Year (Semester 1)",
            role = com.example.model.UserRole.STUDENT
        )

        assertTrue(success)
        assertTrue(repo.isLoggedIn.value)
        assertEquals(initialUsers + 1, repo.users.value.size)
        assertEquals("Ravi Kumar", repo.currentUser.value.name)
        assertEquals(Campus.RATHINUM, repo.currentUser.value.campus)
        assertEquals(Campus.RATHINUM, repo.currentCampus.value)

        // Duplicate registration with same email should fail
        val duplicate = repo.signUp(
            name = "Ravi Duplicate",
            email = "ravi.kumar@msot.edu",
            rollNo = "MSOT-2025-CS-999",
            pass = "password",
            campus = Campus.HI_TECH,
            department = "IT",
            academicYear = "1st Year",
            role = com.example.model.UserRole.STUDENT
        )
        assertFalse(duplicate)
    }

    @Test
    fun testSignOut() {
        repo.signOut()
        assertFalse(repo.isLoggedIn.value)
    }
}

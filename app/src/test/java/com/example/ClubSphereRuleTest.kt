package com.example

import com.example.base.FirestoreEmulatorTestBase
import com.google.firebase.Timestamp
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Date

class ClubSphereRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun testUserCanCreateAndReadOwnProfile() = runTest {
        val uid = signInTestUser("student_tester@campus.edu")
        val userMap = mapOf(
            "userId" to uid,
            "name" to "Test Student",
            "email" to "student_tester@campus.edu",
            "role" to "STUDENT",
            "campus" to "HI-Tech Campus",
            "department" to "Computer Science",
            "createdAt" to Timestamp(Date())
        )

        val docRef = firestore.collection("users").document(uid)
        docRef.set(userMap).await()

        val snapshot = docRef.get().await()
        assertTrue(snapshot.exists())
        assertEquals("Test Student", snapshot.getString("name"))
        assertEquals("STUDENT", snapshot.getString("role"))
    }

    @Test
    fun testUserCannotSwitchTheirRole() = runTest {
        val uid = signInTestUser("coordinator_tester@campus.edu")
        val userMap = mapOf(
            "userId" to uid,
            "name" to "Coord Student",
            "email" to "coordinator_tester@campus.edu",
            "role" to "COORDINATOR",
            "campus" to "HI-Tech Campus",
            "department" to "Electronics",
            "createdAt" to Timestamp(Date())
        )

        val docRef = firestore.collection("users").document(uid)
        docRef.set(userMap).await()

        var updateFailed = false
        try {
            docRef.update("role", "CAMPUS_MANAGER").await()
        } catch (e: Exception) {
            updateFailed = true
        }
        assertTrue("Role update should be rejected by security rules", updateFailed)
    }

    @Test
    fun testCreateAndReadClub() = runTest {
        val uid = signInTestUser("advisor_tester@campus.edu")
        val clubMap = mapOf(
            "clubId" to "club_robotics_101",
            "name" to "Robotics Guild",
            "category" to "Technology",
            "campus" to "HI-Tech Campus",
            "leadCoordinator" to "Alex",
            "facultyAdvisor" to "Dr. Rao",
            "memberCount" to 30,
            "createdAt" to Timestamp(Date())
        )

        val clubRef = firestore.collection("clubs").document("club_robotics_101")
        clubRef.set(clubMap).await()

        val snapshot = clubRef.get().await()
        assertTrue(snapshot.exists())
        assertEquals("Robotics Guild", snapshot.getString("name"))
    }
}

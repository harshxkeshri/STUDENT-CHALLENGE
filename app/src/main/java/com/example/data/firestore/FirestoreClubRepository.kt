package com.example.data.firestore

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.SampleData
import com.example.model.MembershipStatus
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

data class FirestoreClub(
    val clubId: String = "",
    val name: String = "",
    val shortCode: String = "",
    val category: String = "",
    val tagLine: String = "",
    val description: String = "",
    val facultyAdvisor: String = "",
    val facultyAdvisorId: String = "",
    val leadCoordinator: String = "",
    val leadCoordinatorId: String = "",
    val memberCount: Int = 0,
    val capacity: Int = 100,
    val campus: String = "",
    val meetingSchedule: String = "",
    val primaryColorHex: Long = 0xFF2563EB,
    val accentColorHex: Long = 0xFF38BDF8,
    val status: String = "ACTIVE",
    @ServerTimestamp val createdAt: Date? = null,
    @ServerTimestamp val updatedAt: Date? = null
)

data class FirestoreEvent(
    val eventId: String = "",
    val title: String = "",
    val clubId: String = "",
    val clubName: String = "",
    val campus: String = "",
    val isInterCampus: Boolean = false,
    val description: String = "",
    val date: String = "", // e.g. "2026-10-24" or "Saturday, Oct 24, 2026"
    val time: String = "",
    val venue: String = "",
    val registeredCount: Int = 0,
    val capacity: Int = 100,
    val organizerId: String = "",
    val status: String = "UPCOMING",
    val isDutyLeaveEligible: Boolean = true,
    @ServerTimestamp val createdAt: Date? = null,
    @ServerTimestamp val updatedAt: Date? = null
)

data class FirestoreMembership(
    val membershipId: String = "",
    val userId: String = "",
    val clubId: String = "",
    val roleInClub: String = "Member",
    val status: String = "APPROVED", // APPROVED, PENDING, NONE
    @ServerTimestamp val createdAt: Date? = null
)

data class FirestoreEventRegistration(
    val registrationId: String = "",
    val eventId: String = "",
    val userId: String = "",
    val userName: String = "",
    val attendanceStatus: String = "REGISTERED", // REGISTERED, ATTENDED, CANCELLED, CONFIRMED
    @ServerTimestamp val registeredAt: Date? = null,
    @ServerTimestamp val updatedAt: Date? = null
)

class FirestoreClubRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    companion object {
        private const val TAG = "FirestoreClubRepo"
    }

    // Convenience constructor resolving custom databaseId from string resources
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid ?: "user_default_student"
    }

    fun getDefaultClubs(): List<FirestoreClub> {
        return SampleData.clubs.map { c ->
            FirestoreClub(
                clubId = c.id,
                name = c.name,
                shortCode = c.shortCode,
                category = c.category.name,
                tagLine = c.tagLine,
                description = c.description,
                facultyAdvisor = c.facultyAdvisor,
                facultyAdvisorId = "adv_${c.id}",
                leadCoordinator = c.leadCoordinator,
                leadCoordinatorId = "coord_${c.id}",
                memberCount = c.memberCount,
                capacity = c.capacity,
                campus = c.campus.name,
                meetingSchedule = c.meetingSchedule,
                primaryColorHex = c.primaryColorHex,
                accentColorHex = c.accentColorHex,
                status = "ACTIVE"
            )
        }
    }

    fun getDefaultEvents(): List<FirestoreEvent> {
        return SampleData.events.map { ev ->
            FirestoreEvent(
                eventId = ev.id,
                title = ev.title,
                clubId = ev.clubId,
                clubName = ev.clubName,
                campus = ev.campus.name,
                isInterCampus = ev.isInterCampus,
                description = ev.description,
                date = ev.date,
                time = ev.time,
                venue = ev.venue,
                registeredCount = ev.registeredCount,
                capacity = ev.capacity,
                organizerId = "lead_${ev.clubId}",
                status = "UPCOMING",
                isDutyLeaveEligible = ev.isDutyLeaveEligible
            )
        }
    }

    /**
     * Real-time Flow of Clubs from Firestore `/clubs` collection with fallback
     */
    fun observeClubs(): Flow<List<FirestoreClub>> = callbackFlow {
        // Emit default clubs immediately to ensure zero UI delay/flicker
        trySend(getDefaultClubs())

        val collectionRef = db.collection("clubs")
        val listener = collectionRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w(TAG, "Notice observing clubs from Firestore, using catalog: ${error.message}")
                trySend(getDefaultClubs())
                return@addSnapshotListener
            }
            if (snapshot != null) {
                if (snapshot.isEmpty) {
                    trySend(getDefaultClubs())
                } else {
                    val list = snapshot.documents.mapNotNull { doc ->
                        try {
                            val club = doc.toObject(FirestoreClub::class.java)
                            club?.copy(clubId = if (club.clubId.isNotBlank()) club.clubId else doc.id)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(if (list.isNotEmpty()) list else getDefaultClubs())
                }
            }
        }
        awaitClose { listener.remove() }
    }

    /**
     * Real-time Flow of Upcoming Events from Firestore `/events` collection with fallback
     */
    fun observeUpcomingEvents(): Flow<List<FirestoreEvent>> = callbackFlow {
        // Emit default events immediately
        trySend(getDefaultEvents())

        val query = db.collection("events")
        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w(TAG, "Notice observing events from Firestore, using catalog: ${error.message}")
                trySend(getDefaultEvents())
                return@addSnapshotListener
            }
            if (snapshot != null) {
                if (snapshot.isEmpty) {
                    trySend(getDefaultEvents())
                } else {
                    val list = snapshot.documents.mapNotNull { doc ->
                        try {
                            val event = doc.toObject(FirestoreEvent::class.java)
                            event?.copy(eventId = if (event.eventId.isNotBlank()) event.eventId else doc.id)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(if (list.isNotEmpty()) list else getDefaultEvents())
                }
            }
        }
        awaitClose { listener.remove() }
    }

    /**
     * Real-time Flow of current user's memberships
     */
    fun observeUserMemberships(userId: String): Flow<List<FirestoreMembership>> = callbackFlow {
        val query = db.collection("users").document(userId).collection("memberships")
        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w(TAG, "Error observing user memberships: ${error.message}")
                // If subcollection doesn't exist yet, send empty list instead of failing
                trySend(emptyList())
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.documents.mapNotNull { doc ->
                    try {
                        val m = doc.toObject(FirestoreMembership::class.java)
                        m?.copy(membershipId = doc.id)
                    } catch (e: Exception) {
                        null
                    }
                }
                trySend(list)
            }
        }
        awaitClose { listener.remove() }
    }

    /**
     * Seeds initial clubs and events into Firestore if collection is empty
     */
    suspend fun seedInitialDataIfEmpty() {
        try {
            val clubsSnap = db.collection("clubs").limit(1).get().await()
            if (clubsSnap.isEmpty) {
                Log.i(TAG, "Seeding default clubs to Firestore...")
                val defaultClubs = SampleData.clubs.map { c ->
                    FirestoreClub(
                        clubId = c.id,
                        name = c.name,
                        shortCode = c.shortCode,
                        category = c.category.name,
                        tagLine = c.tagLine,
                        description = c.description,
                        facultyAdvisor = c.facultyAdvisor,
                        facultyAdvisorId = "adv_${c.id}",
                        leadCoordinator = c.leadCoordinator,
                        leadCoordinatorId = "coord_${c.id}",
                        memberCount = c.memberCount,
                        capacity = c.capacity,
                        campus = c.campus.name,
                        meetingSchedule = c.meetingSchedule,
                        primaryColorHex = c.primaryColorHex,
                        accentColorHex = c.accentColorHex,
                        status = "ACTIVE"
                    )
                }
                val batch = db.batch()
                defaultClubs.forEach { club ->
                    val docRef = db.collection("clubs").document(club.clubId)
                    val data = hashMapOf(
                        "clubId" to club.clubId,
                        "name" to club.name,
                        "shortCode" to club.shortCode,
                        "category" to club.category,
                        "tagLine" to club.tagLine,
                        "description" to club.description,
                        "facultyAdvisor" to club.facultyAdvisor,
                        "facultyAdvisorId" to club.facultyAdvisorId,
                        "leadCoordinator" to club.leadCoordinator,
                        "leadCoordinatorId" to club.leadCoordinatorId,
                        "memberCount" to club.memberCount,
                        "capacity" to club.capacity,
                        "campus" to club.campus,
                        "meetingSchedule" to club.meetingSchedule,
                        "primaryColorHex" to club.primaryColorHex,
                        "accentColorHex" to club.accentColorHex,
                        "status" to club.status,
                        "createdAt" to Timestamp.now()
                    )
                    batch.set(docRef, data)
                }
                batch.commit().await()
                Log.i(TAG, "Clubs successfully seeded to Firestore.")
            }

            val eventsSnap = db.collection("events").limit(1).get().await()
            if (eventsSnap.isEmpty) {
                Log.i(TAG, "Seeding default events to Firestore...")
                val defaultEvents = SampleData.events.map { ev ->
                    FirestoreEvent(
                        eventId = ev.id,
                        title = ev.title,
                        clubId = ev.clubId,
                        clubName = ev.clubName,
                        campus = ev.campus.name,
                        isInterCampus = ev.isInterCampus,
                        description = ev.description,
                        date = ev.date,
                        time = ev.time,
                        venue = ev.venue,
                        registeredCount = ev.registeredCount,
                        capacity = ev.capacity,
                        organizerId = "lead_${ev.clubId}",
                        status = "UPCOMING",
                        isDutyLeaveEligible = ev.isDutyLeaveEligible
                    )
                }
                val batch = db.batch()
                defaultEvents.forEach { ev ->
                    val docRef = db.collection("events").document(ev.eventId)
                    val data = hashMapOf(
                        "eventId" to ev.eventId,
                        "title" to ev.title,
                        "clubId" to ev.clubId,
                        "clubName" to ev.clubName,
                        "campus" to ev.campus,
                        "isInterCampus" to ev.isInterCampus,
                        "description" to ev.description,
                        "date" to ev.date,
                        "time" to ev.time,
                        "venue" to ev.venue,
                        "registeredCount" to ev.registeredCount,
                        "capacity" to ev.capacity,
                        "organizerId" to ev.organizerId,
                        "status" to ev.status,
                        "isDutyLeaveEligible" to ev.isDutyLeaveEligible,
                        "createdAt" to Timestamp.now()
                    )
                    batch.set(docRef, data)
                }
                batch.commit().await()
                Log.i(TAG, "Events successfully seeded to Firestore.")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Non-fatal warning seeding Firestore data: ${e.message}")
        }
    }

    /**
     * Request membership in a club
     */
    suspend fun requestClubMembership(clubId: String, userId: String): Boolean {
        // 1. Immediately enroll in local repository so UI, roster, and notifications update instantaneously
        try {
            com.example.data.ClubSphereRepository.instance.requestMembership(clubId)
        } catch (e: Exception) {
            Log.w(TAG, "Local repo membership update: ${e.message}")
        }

        // 2. Synchronize document to Cloud Firestore
        return try {
            val userMembershipRef = db.collection("users")
                .document(userId)
                .collection("memberships")
                .document(clubId)

            val data = hashMapOf(
                "membershipId" to clubId,
                "userId" to userId,
                "clubId" to clubId,
                "roleInClub" to "Member",
                "status" to "APPROVED",
                "createdAt" to Timestamp.now()
            )
            userMembershipRef.set(data).await()

            // Also persist to root 'memberships' collection
            try {
                val rootMembershipRef = db.collection("memberships").document("${userId}_${clubId}")
                rootMembershipRef.set(data).await()
            } catch (e: Exception) {
                Log.w(TAG, "Root memberships doc warning: ${e.message}")
            }

            // Increment member count in club doc
            try {
                val clubRef = db.collection("clubs").document(clubId)
                clubRef.update("memberCount", FieldValue.increment(1)).await()
            } catch (e: Exception) {
                Log.w(TAG, "Update club member count warning: ${e.message}")
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Firestore write warning for club $clubId (local enrollment succeeded)", e)
            true
        }
    }

    /**
     * Leave a club
     */
    suspend fun leaveClub(clubId: String, userId: String): Boolean {
        try {
            com.example.data.ClubSphereRepository.instance.leaveClub(clubId)
        } catch (e: Exception) {
            Log.w(TAG, "Local repo leave club: ${e.message}")
        }

        return try {
            db.collection("users")
                .document(userId)
                .collection("memberships")
                .document(clubId)
                .delete()
                .await()

            try {
                db.collection("memberships")
                    .document("${userId}_${clubId}")
                    .delete()
                    .await()
            } catch (e: Exception) {
                Log.w(TAG, "Root memberships delete warning: ${e.message}")
            }

            try {
                val clubRef = db.collection("clubs").document(clubId)
                clubRef.update("memberCount", FieldValue.increment(-1)).await()
            } catch (e: Exception) {
                Log.w(TAG, "Update club member count warning: ${e.message}")
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Firestore delete warning for club $clubId (local leave succeeded)", e)
            true
        }
    }

    /**
     * Real-time Flow of current user's event registrations across events (collection group)
     */
    fun observeUserRegistrations(userId: String): Flow<Map<String, FirestoreEventRegistration>> = callbackFlow {
        val query = db.collectionGroup("registrations").whereEqualTo("userId", userId)
        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w(TAG, "Notice observing user registrations: ${error.message}")
                trySend(emptyMap())
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val map = mutableMapOf<String, FirestoreEventRegistration>()
                for (doc in snapshot.documents) {
                    try {
                        val reg = doc.toObject(FirestoreEventRegistration::class.java)
                        val evId = if (!reg?.eventId.isNullOrBlank()) reg!!.eventId else (doc.reference.parent.parent?.id ?: "")
                        if (reg != null && evId.isNotBlank()) {
                            map[evId] = reg.copy(
                                registrationId = if (reg.registrationId.isNotBlank()) reg.registrationId else doc.id,
                                eventId = evId
                            )
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing registration doc ${doc.id}", e)
                    }
                }
                trySend(map)
            }
        }
        awaitClose { listener.remove() }
    }

    /**
     * Real-time Flow of registrations for a specific event
     */
    fun observeEventRegistrations(eventId: String): Flow<List<FirestoreEventRegistration>> = callbackFlow {
        val query = db.collection("events").document(eventId).collection("registrations")
        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w(TAG, "Notice observing event registrations for $eventId: ${error.message}")
                trySend(emptyList())
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.documents.mapNotNull { doc ->
                    try {
                        val reg = doc.toObject(FirestoreEventRegistration::class.java)
                        reg?.copy(registrationId = doc.id, eventId = eventId)
                    } catch (e: Exception) {
                        null
                    }
                }
                trySend(list)
            }
        }
        awaitClose { listener.remove() }
    }

    /**
     * Registers a user for an event, updating their attendance status in 'registrations' sub-collection
     */
    suspend fun registerForEvent(
        eventId: String,
        userId: String,
        userName: String,
        attendanceStatus: String = "REGISTERED"
    ): Boolean {
        return try {
            val regRef = db.collection("events")
                .document(eventId)
                .collection("registrations")
                .document(userId)

            val existingSnap = try { regRef.get().await() } catch (e: Exception) { null }
            val wasAlreadyRegistered = existingSnap != null && existingSnap.exists() && existingSnap.getString("attendanceStatus") in listOf("REGISTERED", "CONFIRMED")

            val data = hashMapOf(
                "registrationId" to userId,
                "eventId" to eventId,
                "userId" to userId,
                "userName" to userName,
                "attendanceStatus" to attendanceStatus,
                "registeredAt" to (existingSnap?.getTimestamp("registeredAt") ?: Timestamp.now()),
                "updatedAt" to Timestamp.now()
            )
            regRef.set(data).await()

            // Update registeredCount in event doc if new registration
            if (!wasAlreadyRegistered && attendanceStatus in listOf("REGISTERED", "CONFIRMED")) {
                try {
                    val eventRef = db.collection("events").document(eventId)
                    eventRef.update("registeredCount", com.google.firebase.firestore.FieldValue.increment(1)).await()
                } catch (e: Exception) {
                    Log.w(TAG, "Warning updating event registeredCount: ${e.message}")
                }
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to register for event $eventId", e)
            false
        }
    }

    /**
     * Updates user's attendance status in 'registrations' sub-collection (e.g. "REGISTERED", "CANCELLED", "CONFIRMED", "ATTENDED")
     */
    suspend fun updateEventAttendanceStatus(
        eventId: String,
        userId: String,
        newStatus: String
    ): Boolean {
        return try {
            val regRef = db.collection("events")
                .document(eventId)
                .collection("registrations")
                .document(userId)

            val existingDoc = try { regRef.get().await() } catch (e: Exception) { null }
            val oldStatus = existingDoc?.getString("attendanceStatus")

            val updates = hashMapOf<String, Any>(
                "attendanceStatus" to newStatus,
                "updatedAt" to Timestamp.now()
            )
            regRef.set(updates, com.google.firebase.firestore.SetOptions.merge()).await()

            // Adjust registeredCount if changing between active and cancelled
            val wasActive = oldStatus in listOf("REGISTERED", "CONFIRMED")
            val isNowActive = newStatus in listOf("REGISTERED", "CONFIRMED")
            if (wasActive && !isNowActive) {
                try {
                    db.collection("events").document(eventId)
                        .update("registeredCount", com.google.firebase.firestore.FieldValue.increment(-1))
                        .await()
                } catch (e: Exception) {
                    Log.w(TAG, "Warning updating count on cancel: ${e.message}")
                }
            } else if (!wasActive && isNowActive) {
                try {
                    db.collection("events").document(eventId)
                        .update("registeredCount", com.google.firebase.firestore.FieldValue.increment(1))
                        .await()
                } catch (e: Exception) {
                    Log.w(TAG, "Warning updating count on activate: ${e.message}")
                }
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update attendance status for event $eventId", e)
            false
        }
    }

    /**
     * Cancels an event registration by updating attendance status to 'CANCELLED'
     */
    suspend fun cancelEventRegistration(eventId: String, userId: String): Boolean {
        return updateEventAttendanceStatus(eventId, userId, "CANCELLED")
    }
}

package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.firestore.FirestoreClub
import com.example.data.firestore.FirestoreClubRepository
import com.example.data.firestore.FirestoreEvent
import com.example.data.firestore.FirestoreMembership
import com.example.data.firestore.UiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ClubDashboardViewModel(
    private val repository: FirestoreClubRepository,
    private val currentUserId: String
) : ViewModel() {

    companion object {
        private const val TAG = "ClubDashboardVM"
        private const val STOP_TIMEOUT_MILLIS = 5000L
    }

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedClub = MutableStateFlow<FirestoreClub?>(null)
    val selectedClub: StateFlow<FirestoreClub?> = _selectedClub.asStateFlow()

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage.asStateFlow()

    init {
        // Seed initial data if Firestore is empty
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    // Two-tier Flow error handling: repository flow is caught and mapped before stateIn
    val clubsState: StateFlow<UiState<List<FirestoreClub>>> = repository.observeClubs()
        .map<List<FirestoreClub>, UiState<List<FirestoreClub>>> { list ->
            UiState.Success(list.ifEmpty { repository.getDefaultClubs() })
        }
        .catch { error ->
            Log.w(TAG, "Error in clubs stream, fallback to catalog", error)
            emit(UiState.Success(repository.getDefaultClubs()))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Success(repository.getDefaultClubs())
        )

    val membershipsState: StateFlow<List<FirestoreMembership>> = combine(
        repository.observeUserMemberships(currentUserId),
        com.example.data.ClubSphereRepository.instance.memberships
    ) { firestoreMems, localMems ->
        val localConverted = localMems.filter { it.userId == currentUserId }.map {
            FirestoreMembership(
                membershipId = it.clubId,
                userId = it.userId,
                clubId = it.clubId,
                roleInClub = it.roleInClub,
                status = it.status.name
            )
        }
        (firestoreMems + localConverted).distinctBy { it.clubId }
    }
        .catch { error ->
            Log.w(TAG, "Error in memberships stream", error)
            emit(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = emptyList()
        )

    val eventsState: StateFlow<UiState<List<FirestoreEvent>>> = repository.observeUpcomingEvents()
        .map<List<FirestoreEvent>, UiState<List<FirestoreEvent>>> { list ->
            val data = list.ifEmpty { repository.getDefaultEvents() }
            val sorted = data.sortedWith(
                compareBy<FirestoreEvent> { it.date }.thenBy { it.time }
            )
            UiState.Success(sorted)
        }
        .catch { error ->
            Log.w(TAG, "Error in events stream, fallback to catalog", error)
            emit(UiState.Success(repository.getDefaultEvents()))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Success(repository.getDefaultEvents())
        )

    val userRegistrationsState: StateFlow<Map<String, com.example.data.firestore.FirestoreEventRegistration>> =
        repository.observeUserRegistrations(currentUserId)
            .catch { error ->
                Log.w(TAG, "Error in user registrations stream", error)
                emit(emptyMap())
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = emptyMap()
            )

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectClub(club: FirestoreClub?) {
        _selectedClub.value = club
    }

    fun clearActionMessage() {
        _actionMessage.value = null
    }

    fun joinClub(clubId: String) {
        viewModelScope.launch {
            val success = repository.requestClubMembership(clubId, currentUserId)
            if (success) {
                _actionMessage.value = "Successfully joined club!"
            } else {
                _actionMessage.value = "Failed to update membership. Please try again."
            }
        }
    }

    fun leaveClub(clubId: String) {
        viewModelScope.launch {
            val success = repository.leaveClub(clubId, currentUserId)
            if (success) {
                _actionMessage.value = "You have left the club."
            } else {
                _actionMessage.value = "Failed to leave club. Please try again."
            }
        }
    }

    /**
     * RSVP to an event, updating user's attendance status in 'registrations' sub-collection
     */
    fun rsvpEvent(eventId: String, attendanceStatus: String = "REGISTERED", userName: String = "Student") {
        viewModelScope.launch {
            val success = repository.registerForEvent(
                eventId = eventId,
                userId = currentUserId,
                userName = userName,
                attendanceStatus = attendanceStatus
            )
            if (success) {
                _actionMessage.value = "RSVP Confirmed! Attendance status recorded."
            } else {
                _actionMessage.value = "Failed to record RSVP. Please try again."
            }
        }
    }

    /**
     * Updates attendance status in event registrations subcollection
     */
    fun updateAttendanceStatus(eventId: String, attendanceStatus: String) {
        viewModelScope.launch {
            val success = repository.updateEventAttendanceStatus(
                eventId = eventId,
                userId = currentUserId,
                newStatus = attendanceStatus
            )
            if (success) {
                _actionMessage.value = "Attendance status updated to $attendanceStatus"
            } else {
                _actionMessage.value = "Failed to update attendance status."
            }
        }
    }

    /**
     * Cancels RSVP for an event
     */
    fun cancelRsvp(eventId: String) {
        viewModelScope.launch {
            val success = repository.cancelEventRegistration(eventId, currentUserId)
            if (success) {
                _actionMessage.value = "RSVP Cancelled."
            } else {
                _actionMessage.value = "Failed to cancel RSVP."
            }
        }
    }

    fun retryFetch() {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }
}

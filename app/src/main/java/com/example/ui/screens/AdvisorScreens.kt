package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.QrCodeView

// -------------------------------------------------------------
// 1. ADVISOR GOVERNANCE CONSOLE SCREEN
// -------------------------------------------------------------
@Composable
fun AdvisorConsoleScreen(
    user: UserProfile,
    campus: Campus,
    activities: List<ActivityPlan>,
    submissions: List<Submission>,
    certificates: List<Certificate>,
    onNavigateToApprovals: () -> Unit,
    onNavigateToRubrics: () -> Unit,
    onNavigateToCertificates: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pendingPlans = activities.filter { !it.advisorApproved }
    val pendingSubmissions = submissions.filter { !it.isEvaluated }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("advisor_console_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Governance Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("advisor_header_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF581C87), Color(0xFF1E1B4B))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Filled.SupervisorAccount, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("ACADEMIC GOVERNANCE CONSOLE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFA855F7).copy(alpha = 0.3f)
                            ) {
                                Text(
                                    "FACULTY AUTHORITY",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF3E8FF),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "${user.department} • ${campus.displayName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFE9D5FF)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.School, contentDescription = null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Official Signatory for Co-Curricular Credits & Deliverable Rubrics", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            }
        }

        // Actionable Pending Alerts
        if (pendingPlans.isNotEmpty() || pendingSubmissions.isNotEmpty()) {
            item {
                Text("Action Required by Faculty", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (pendingPlans.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { onNavigateToApprovals() },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.PendingActions, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("${pendingPlans.size} Activity Plans Pending Approval", fontWeight = FontWeight.Bold, color = Color(0xFF78350F))
                                        Text("Review and sign off on Saturday session agendas", style = MaterialTheme.typography.bodySmall, color = Color(0xFF92400E))
                                    }
                                }
                                Icon(Icons.Filled.ArrowForward, contentDescription = null, tint = Color(0xFFB45309))
                            }
                        }
                    }

                    if (pendingSubmissions.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { onNavigateToRubrics() },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E8FF))
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.RateReview, contentDescription = null, tint = Color(0xFF7E22CE), modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("${pendingSubmissions.size} Deliverable Awaiting Rubric Evaluation", fontWeight = FontWeight.Bold, color = Color(0xFF581C87))
                                        Text("Score student repos & presentation decks", style = MaterialTheme.typography.bodySmall, color = Color(0xFF6B21A8))
                                    }
                                }
                                Icon(Icons.Filled.ArrowForward, contentDescription = null, tint = Color(0xFF7E22CE))
                            }
                        }
                    }
                }
            }
        }

        // Fast Action Buttons Grid
        item {
            Text("Faculty Management Tools", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f).clickable { onNavigateToApprovals() },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Icon(Icons.Filled.FactCheck, contentDescription = null, tint = Color(0xFF7C3AED), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Plan Approvals", fontWeight = FontWeight.Bold)
                        Text("${activities.size} Total Plans", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f).clickable { onNavigateToRubrics() },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Icon(Icons.Filled.Grading, contentDescription = null, tint = Color(0xFF7C3AED), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Rubric Grading", fontWeight = FontWeight.Bold)
                        Text("${submissions.size} Submissions", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f).clickable { onNavigateToCertificates() },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Icon(Icons.Filled.WorkspacePremium, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Certificates", fontWeight = FontWeight.Bold)
                        Text("${certificates.size} Issued", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. ADVISOR APPROVALS SCREEN
// -------------------------------------------------------------
@Composable
fun AdvisorApprovalsScreen(
    activities: List<ActivityPlan>,
    onApproveActivityPlan: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("advisor_approvals_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Activity Plan Approvals", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Review proposed club session plans submitted by student coordinators before they go live.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        items(activities) { act ->
            Card(
                modifier = Modifier.fillMaxWidth().testTag("plan_card_${act.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF7C3AED).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    act.clubName.uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF7C3AED),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(act.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (act.advisorApproved) Color(0xFFD1FAE5) else Color(0xFFFEF3C7)
                        ) {
                            Text(
                                text = if (act.advisorApproved) "OFFICIALLY APPROVED" else "PENDING SIGNATURE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (act.advisorApproved) Color(0xFF047857) else Color(0xFFB45309),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Date & Time: ${act.date} (${act.timeSlot})", style = MaterialTheme.typography.bodySmall)
                    Text("Venue: ${act.venue}", style = MaterialTheme.typography.bodySmall)
                    Text("Core Learning Topic: ${act.topic}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Required Deliverable: ${act.deliverableType} • Deadline: ${act.deadline}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)

                    if (!act.advisorApproved) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { onApproveActivityPlan(act.id) },
                                modifier = Modifier.fillMaxWidth().testTag("advisor_approve_plan_${act.id}"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                            ) {
                                Icon(Icons.Filled.Verified, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Approve Plan & Authorize Session")
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. ADVISOR RUBRIC EVALUATIONS SCREEN
// -------------------------------------------------------------
@Composable
fun AdvisorRubricsScreen(
    submissions: List<Submission>,
    onOpenEvaluateSubmission: (Submission) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("advisor_rubrics_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Student Deliverables & Rubrics", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Evaluate student project submissions against academic rubrics and award verified co-curricular credits.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        items(submissions) { sub ->
            Card(
                modifier = Modifier.fillMaxWidth().testTag("sub_card_${sub.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(sub.studentName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text("${sub.clubName} • ${sub.activityTitle}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        if (sub.isEvaluated) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFD1FAE5)
                            ) {
                                Text(
                                    text = "GRADED: ${sub.score}/100",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF047857),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFEF3C7)
                            ) {
                                Text(
                                    text = "AWAITING GRADING",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB45309),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("File: ${sub.fileTitle} (${sub.fileType})", style = MaterialTheme.typography.bodySmall)
                    Text("Resource URL: ${sub.resourceUrl}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)

                    if (sub.isEvaluated && sub.feedback != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Advisor Remarks: \"${sub.feedback}\" — ${sub.evaluatorName}",
                                modifier = Modifier.padding(10.dp),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { onOpenEvaluateSubmission(sub) },
                        modifier = Modifier.fillMaxWidth().testTag("grade_submission_button_${sub.id}"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (sub.isEvaluated) Color(0xFF7C3AED) else Color(0xFF0284C7)
                        )
                    ) {
                        Icon(Icons.Filled.RateReview, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (sub.isEvaluated) "Re-Evaluate Rubric" else "Evaluate with Official Rubric")
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 4. ADVISOR CERTIFICATES SCREEN
// -------------------------------------------------------------
@Composable
fun AdvisorCertificatesScreen(
    certificates: List<Certificate>,
    onIssueCertificate: (studentName: String, rollNo: String, clubOrEvent: String, category: String, credits: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var studentNameInput by remember { mutableStateOf("") }
    var rollNoInput by remember { mutableStateOf("") }
    var clubOrEventInput by remember { mutableStateOf("Kaizen Tech - HackSprint Finalist") }
    var categoryInput by remember { mutableStateOf("Technical Excellence & Leadership") }
    var creditsInput by remember { mutableStateOf("15") }
    var showSuccessBanner by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("advisor_certificates_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Issue Digital Certificates", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Cryptographically signed digital credentials verified by faculty signature.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // Issuance Form Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Official Credential Issuance Desk", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = studentNameInput,
                        onValueChange = { studentNameInput = it },
                        label = { Text("Recipient Student Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("cert_student_name_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = rollNoInput,
                        onValueChange = { rollNoInput = it },
                        label = { Text("Student Roll Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("cert_roll_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = clubOrEventInput,
                        onValueChange = { clubOrEventInput = it },
                        label = { Text("Club or Milestone Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("cert_title_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = creditsInput,
                        onValueChange = { creditsInput = it },
                        label = { Text("Co-Curricular Credits Awarded") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("cert_credits_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (studentNameInput.isNotBlank() && rollNoInput.isNotBlank()) {
                                onIssueCertificate(
                                    studentNameInput.trim(),
                                    rollNoInput.trim().uppercase(),
                                    clubOrEventInput.trim(),
                                    categoryInput,
                                    creditsInput.toIntOrNull() ?: 10
                                )
                                studentNameInput = ""
                                rollNoInput = ""
                                showSuccessBanner = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("advisor_issue_cert_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B))
                    ) {
                        Icon(Icons.Filled.WorkspacePremium, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sign & Issue Digital Certificate")
                    }

                    if (showSuccessBanner) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Official Certificate Issued and Added to Student's Verified Transcript!", color = Color(0xFF047857), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Registry of Issued Certificates
        item {
            Text("Issued Certificate Registry (${certificates.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        items(certificates) { cert ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(cert.studentName, fontWeight = FontWeight.Bold)
                        Text("${cert.rollNo} • ${cert.clubOrEventTitle}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(cert.certNumber, fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                    }

                    Icon(Icons.Filled.Verified, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

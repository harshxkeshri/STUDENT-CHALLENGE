package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Campus
import com.example.model.UserProfile
import com.example.model.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    demoUsers: List<UserProfile>,
    onSignIn: (emailOrRollNo: String, pass: String) -> Boolean,
    onSignUp: (
        name: String,
        email: String,
        rollNo: String,
        pass: String,
        campus: Campus,
        department: String,
        academicYear: String,
        role: UserRole
    ) -> Boolean,
    modifier: Modifier = Modifier
) {
    var isSignUpTab by remember { mutableStateOf(false) }

    // Sign In Fields
    var signInEmailOrRoll by remember { mutableStateOf("chirushi07@gmail.com") }
    var signInPassword by remember { mutableStateOf("password123") }
    var signInPasswordVisible by remember { mutableStateOf(false) }
    var signInError by remember { mutableStateOf<String?>(null) }

    // Sign Up Fields
    var signUpName by remember { mutableStateOf("") }
    var signUpEmail by remember { mutableStateOf("") }
    var signUpRollNo by remember { mutableStateOf("") }
    var signUpPassword by remember { mutableStateOf("") }
    var signUpConfirmPassword by remember { mutableStateOf("") }
    var signUpPasswordVisible by remember { mutableStateOf(false) }
    var signUpCampus by remember { mutableStateOf(Campus.HI_TECH) }
    var signUpDepartment by remember { mutableStateOf("Computer Science & Engineering") }
    var signUpAcademicYear by remember { mutableStateOf("1st Year (Semester 1)") }
    var signUpRole by remember { mutableStateOf(UserRole.STUDENT) }
    var signUpError by remember { mutableStateOf<String?>(null) }

    val departments = listOf(
        "Computer Science & Engineering",
        "Electronics & Communication",
        "Information Technology",
        "Mechanical Engineering",
        "School of Management & Business",
        "Design & Fine Arts"
    )

    val academicYears = listOf(
        "1st Year (Semester 1)",
        "2nd Year (Semester 3)",
        "3rd Year (Semester 5)",
        "4th Year (Semester 7)",
        "Postgraduate / Research"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E1B4B),
                        Color(0xFF0F172A)
                    )
                )
            )
            .testTag("auth_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // App Emblem / Badge
            Surface(
                shape = CircleShape,
                color = Color(0xFF6366F1),
                modifier = Modifier.size(68.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.School,
                        contentDescription = "CampusCred Logo",
                        tint = Color.White,
                        modifier = Modifier.size(38.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "CampusCred",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 1.sp
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF6366F1).copy(alpha = 0.3f)
                ) {
                    Text(
                        text = "MSOT UNIFIED NETWORK",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA5B4FC),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = "Verified student activities, attendance, club days & transcripts across all campuses.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFCBD5E1),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Sign In / Sign Up Segmented Tabs
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF1E293B),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                isSignUpTab = false
                                signInError = null
                            }
                            .testTag("tab_sign_in"),
                        color = if (!isSignUpTab) Color(0xFF6366F1) else Color.Transparent
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(vertical = 10.dp)
                        ) {
                            Text(
                                text = "Sign In",
                                fontWeight = FontWeight.Bold,
                                color = if (!isSignUpTab) Color.White else Color(0xFF94A3B8),
                                fontSize = 14.sp
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                isSignUpTab = true
                                signUpError = null
                            }
                            .testTag("tab_sign_up"),
                        color = if (isSignUpTab) Color(0xFF6366F1) else Color.Transparent
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(vertical = 10.dp)
                        ) {
                            Text(
                                text = "Create Account",
                                fontWeight = FontWeight.Bold,
                                color = if (isSignUpTab) Color.White else Color(0xFF94A3B8),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ==========================================
            // SIGN IN TAB
            // ==========================================
            if (!isSignUpTab) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sign_in_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Sign In to Your Account",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Enter your institutional email or Student Roll Number",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = signInEmailOrRoll,
                            onValueChange = {
                                signInEmailOrRoll = it
                                signInError = null
                            },
                            label = { Text("Email or Roll Number") },
                            placeholder = { Text("e.g. MSOT-2023-CS-042 or chirushi07@gmail.com") },
                            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null, tint = Color(0xFFA5B4FC)) },
                            modifier = Modifier.fillMaxWidth().testTag("sign_in_email_input"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF6366F1),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = signInPassword,
                            onValueChange = {
                                signInPassword = it
                                signInError = null
                            },
                            label = { Text("Password") },
                            placeholder = { Text("Default: password123") },
                            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = Color(0xFFA5B4FC)) },
                            trailingIcon = {
                                IconButton(onClick = { signInPasswordVisible = !signInPasswordVisible }) {
                                    Icon(
                                        imageVector = if (signInPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                        contentDescription = "Toggle password visibility",
                                        tint = Color(0xFF94A3B8)
                                    )
                                }
                            },
                            visualTransformation = if (signInPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth().testTag("sign_in_password_input"),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF6366F1),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        if (signInError != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = signInError!!,
                                color = Color(0xFFF43F5E),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                val success = onSignIn(signInEmailOrRoll, signInPassword)
                                if (!success) {
                                    signInError = "Invalid credentials. Ensure your email or roll number matches, or tap a Demo Login below."
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("submit_sign_in_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
                        ) {
                            Icon(Icons.Filled.Login, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sign In", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "New to CampusCred? ",
                                color = Color(0xFF94A3B8),
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Create an account",
                                color = Color(0xFF818CF8),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.clickable {
                                    isSignUpTab = true
                                    signUpError = null
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // One-Tap Demo Logins for Quick Testing
                Text(
                    text = "One-Tap Demo Sign-In (Select Role & Campus)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFA5B4FC)
                )

                Spacer(modifier = Modifier.height(10.dp))

                demoUsers.forEach { demoUser ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                            .clickable {
                                onSignIn(demoUser.email, demoUser.password)
                            }
                            .testTag("demo_login_${demoUser.id}"),
                        color = Color(0xFF1E293B).copy(alpha = 0.7f)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(demoUser.avatarColorHex),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = demoUser.name.first().toString(),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = demoUser.name,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
<<<<<<< HEAD
=======
<<<<<<< HEAD
>>>>>>> c0aa687 (final project)
                                        color = Color(0xFF6366F1).copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = demoUser.role.label.split(" ").first(),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFA5B4FC),
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
<<<<<<< HEAD
=======
=======
                                        color = when (demoUser.role) {
                                            UserRole.STUDENT -> Color(0xFF6366F1).copy(alpha = 0.2f)
                                            UserRole.COORDINATOR -> Color(0xFF0284C7).copy(alpha = 0.2f)
                                            UserRole.ADVISOR -> Color(0xFF9333EA).copy(alpha = 0.2f)
                                            UserRole.CAMPUS_MANAGER -> Color(0xFF10B981).copy(alpha = 0.2f)
                                        }
                                    ) {
                                        Text(
                                            text = demoUser.role.label,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (demoUser.role) {
                                                UserRole.STUDENT -> Color(0xFFA5B4FC)
                                                UserRole.COORDINATOR -> Color(0xFF7DD3FC)
                                                UserRole.ADVISOR -> Color(0xFFD8B4FE)
                                                UserRole.CAMPUS_MANAGER -> Color(0xFF6EE7B7)
                                            },
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
>>>>>>> a05b565 (final project)
>>>>>>> c0aa687 (final project)
                                        )
                                    }
                                }
                                Text(
                                    text = "${demoUser.campus.shortName} • ${demoUser.email}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            Icon(
                                Icons.Filled.ArrowForwardIos,
                                contentDescription = "Sign In",
                                tint = Color(0xFF6366F1),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            } else {
                // ==========================================
                // SIGN UP TAB (With mandatory Campus Selection)
                // ==========================================
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sign_up_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Student & Staff Registration",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Create your profile to join clubs and track verified achievements",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Full Name
                        OutlinedTextField(
                            value = signUpName,
                            onValueChange = {
                                signUpName = it
                                signUpError = null
                            },
                            label = { Text("Full Name") },
                            placeholder = { Text("e.g. Tanmay Sharma") },
                            leadingIcon = { Icon(Icons.Filled.Badge, contentDescription = null, tint = Color(0xFFA5B4FC)) },
                            modifier = Modifier.fillMaxWidth().testTag("sign_up_name_input"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF6366F1),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Institutional Email
                        OutlinedTextField(
                            value = signUpEmail,
                            onValueChange = {
                                signUpEmail = it
                                signUpError = null
                            },
                            label = { Text("Email Address") },
                            placeholder = { Text("e.g. tanmay.s@msot.edu") },
                            leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null, tint = Color(0xFFA5B4FC)) },
                            modifier = Modifier.fillMaxWidth().testTag("sign_up_email_input"),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF6366F1),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Student ID / Roll Number
                        OutlinedTextField(
                            value = signUpRollNo,
                            onValueChange = {
                                signUpRollNo = it
                                signUpError = null
                            },
                            label = { Text("Student Roll No / ID") },
                            placeholder = { Text("e.g. MSOT-2025-CS-108") },
                            leadingIcon = { Icon(Icons.Filled.ConfirmationNumber, contentDescription = null, tint = Color(0xFFA5B4FC)) },
                            modifier = Modifier.fillMaxWidth().testTag("sign_up_roll_input"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF6366F1),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // CAMPUS SELECTION (HI-Tech, Rathinum Bangalore, Jagannath Jaipur)
                        Text(
                            text = "Select Your Campus",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFA5B4FC)
                        )
                        Text(
                            text = "Your club schedules and local attendance will sync to this campus",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Campus.values().forEach { campusOption ->
                            val isSelected = campusOption == signUpCampus
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) Color(0xFF6366F1) else Color(0xFF334155),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable { signUpCampus = campusOption }
                                    .testTag("campus_option_${campusOption.name}"),
                                color = if (isSelected) Color(0xFF6366F1).copy(alpha = 0.2f) else Color(0xFF0F172A)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { signUpCampus = campusOption },
                                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF6366F1))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = campusOption.displayName,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = Color.White,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = "City: ${campusOption.city}",
                                            fontSize = 11.sp,
                                            color = Color(0xFF94A3B8)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Department Selection
                        Text(
                            text = "Academic Department",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFA5B4FC)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        var deptExpanded by remember { mutableStateOf(false) }
                        Box {
                            OutlinedTextField(
                                value = signUpDepartment,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    IconButton(onClick = { deptExpanded = true }) {
                                        Icon(Icons.Filled.ArrowDropDown, contentDescription = null, tint = Color.White)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { deptExpanded = true }
                                    .testTag("sign_up_dept_dropdown"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF6366F1),
                                    unfocusedBorderColor = Color(0xFF334155),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                            DropdownMenu(
                                expanded = deptExpanded,
                                onDismissRequest = { deptExpanded = false }
                            ) {
                                departments.forEach { dept ->
                                    DropdownMenuItem(
                                        text = { Text(dept) },
                                        onClick = {
                                            signUpDepartment = dept
                                            deptExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Role Selection
                        Text(
                            text = "Institutional Role",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFA5B4FC)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            UserRole.values().forEach { roleOption ->
                                val isSelected = roleOption == signUpRole
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { signUpRole = roleOption },
                                    label = { Text(roleOption.label.split(" ").first(), fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF6366F1),
                                        selectedLabelColor = Color.White,
                                        containerColor = Color(0xFF0F172A),
                                        labelColor = Color(0xFFCBD5E1)
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Password & Confirm Password
                        OutlinedTextField(
                            value = signUpPassword,
                            onValueChange = {
                                signUpPassword = it
                                signUpError = null
                            },
                            label = { Text("Password") },
                            placeholder = { Text("At least 6 characters") },
                            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = Color(0xFFA5B4FC)) },
                            visualTransformation = if (signUpPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { signUpPasswordVisible = !signUpPasswordVisible }) {
                                    Icon(
                                        imageVector = if (signUpPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                        contentDescription = null,
                                        tint = Color(0xFF94A3B8)
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("sign_up_password_input"),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF6366F1),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = signUpConfirmPassword,
                            onValueChange = {
                                signUpConfirmPassword = it
                                signUpError = null
                            },
                            label = { Text("Confirm Password") },
                            placeholder = { Text("Repeat password") },
                            leadingIcon = { Icon(Icons.Filled.LockClock, contentDescription = null, tint = Color(0xFFA5B4FC)) },
                            visualTransformation = if (signUpPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth().testTag("sign_up_confirm_password_input"),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF6366F1),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        if (signUpError != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = signUpError!!,
                                color = Color(0xFFF43F5E),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                if (signUpName.isBlank()) {
                                    signUpError = "Please enter your full name."
                                } else if (signUpEmail.isBlank() || !signUpEmail.contains("@")) {
                                    signUpError = "Please enter a valid institutional email."
                                } else if (signUpRollNo.isBlank()) {
                                    signUpError = "Please enter your student Roll Number or ID."
                                } else if (signUpPassword.isNotBlank() && signUpPassword != signUpConfirmPassword) {
                                    signUpError = "Passwords do not match."
                                } else {
                                    val success = onSignUp(
                                        signUpName,
                                        signUpEmail,
                                        signUpRollNo,
                                        signUpPassword.ifEmpty { "password123" },
                                        signUpCampus,
                                        signUpDepartment,
                                        signUpAcademicYear,
                                        signUpRole
                                    )
                                    if (!success) {
                                        signUpError = "An account with this email or roll number already exists."
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("submit_sign_up_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
                        ) {
                            Icon(Icons.Filled.PersonAdd, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Create Verified Profile", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Already have an account? ",
                                color = Color(0xFF94A3B8),
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Sign In",
                                color = Color(0xFF818CF8),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.clickable {
                                    isSignUpTab = false
                                    signInError = null
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

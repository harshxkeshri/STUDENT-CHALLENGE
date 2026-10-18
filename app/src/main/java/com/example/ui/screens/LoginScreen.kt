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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.Campus
import com.example.model.UserProfile
import com.example.model.UserRole
import com.example.ui.components.MiraiLogoHeader
import com.example.viewmodel.AuthState
import com.example.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    demoUsers: List<UserProfile> = emptyList(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val isLoading by authViewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by authViewModel.errorMessage.collectAsStateWithLifecycle()

    var isSignUpTab by remember { mutableStateOf(false) }

    // Sign In form fields
    var emailOrRoll by remember { mutableStateOf("chirushi07@gmail.com") }
    var password by remember { mutableStateOf("password123") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Sign Up form fields
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
                        Color(0xFF0C1E3C), // Deep Mirai Navy
                        Color(0xFF0F172A),
                        Color(0xFF1E1B4B)
                    )
                )
            )
            .testTag("login_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // Mirai Official Branding & Logo Header
            // ==========================================
            MiraiLogoHeader(
                logoSize = 88.dp,
                textColor = Color.White,
                subtextColor = Color(0xFFA5B4FC)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Tab Switcher (Sign In vs Register)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .testTag("auth_tab_switcher"),
                color = Color(0xFF1E293B).copy(alpha = 0.8f)
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(4.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (!isSignUpTab) Color(0xFF2563EB) else Color.Transparent)
                            .clickable {
                                isSignUpTab = false
                                authViewModel.clearError()
                            }
                            .testTag("tab_sign_in"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sign In",
                            color = Color.White,
                            fontWeight = if (!isSignUpTab) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(4.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSignUpTab) Color(0xFF2563EB) else Color.Transparent)
                            .clickable {
                                isSignUpTab = true
                                authViewModel.clearError()
                            }
                            .testTag("tab_sign_up"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Create Account",
                            color = Color.White,
                            fontWeight = if (isSignUpTab) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Error Banner
            AnimatedVisibility(
                visible = errorMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .testTag("auth_error_card"),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFBE123C).copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(Color(0xFFF43F5E), Color(0xFFBE123C))))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ErrorOutline,
                            contentDescription = "Error",
                            tint = Color(0xFFF43F5E),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = Color(0xFFFECDD3),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // ==========================================
            // Main Card (Sign In / Register)
            // ==========================================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_form_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.9f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Google Sign-In Button (Prominent M3 component)
                    Button(
                        onClick = {
                            authViewModel.signInWithGoogle(context)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("google_sign_in_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF1F2937)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color(0xFF2563EB),
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Authenticating...",
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1F2937)
                            )
                        } else {
                            // Google G Logo Symbol
                            Surface(
                                shape = CircleShape,
                                color = Color.Transparent,
                                modifier = Modifier.size(22.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "G",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                        color = Color(0xFF4285F4)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Continue with Google",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFF1F2937)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Divider with "or institutional credentials"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFF334155)
                        )
                        Text(
                            text = "  OR INSTITUTIONAL ID  ",
                            color = Color(0xFF64748B),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFF334155)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    if (!isSignUpTab) {
                        // ------------------------------------------
                        // SIGN IN FORM
                        // ------------------------------------------
                        OutlinedTextField(
                            value = emailOrRoll,
                            onValueChange = {
                                emailOrRoll = it
                                authViewModel.clearError()
                            },
                            label = { Text("Email or Roll Number") },
                            placeholder = { Text("e.g. chirushi07@gmail.com") },
                            leadingIcon = {
                                Icon(Icons.Filled.AccountCircle, contentDescription = null, tint = Color(0xFF93C5FD))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("sign_in_email_input"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2563EB),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedLabelColor = Color(0xFF93C5FD),
                                unfocusedLabelColor = Color(0xFF94A3B8)
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                authViewModel.clearError()
                            },
                            label = { Text("Password") },
                            leadingIcon = {
                                Icon(Icons.Filled.Lock, contentDescription = null, tint = Color(0xFF93C5FD))
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                        tint = Color(0xFF94A3B8)
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("sign_in_password_input"),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2563EB),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedLabelColor = Color(0xFF93C5FD),
                                unfocusedLabelColor = Color(0xFF94A3B8)
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                authViewModel.signInWithEmail(emailOrRoll, password)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("submit_sign_in_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                            } else {
                                Icon(Icons.Filled.Login, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Sign In with Mirai ID", fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        // ------------------------------------------
                        // SIGN UP FORM
                        // ------------------------------------------
                        OutlinedTextField(
                            value = signUpName,
                            onValueChange = { signUpName = it; authViewModel.clearError() },
                            label = { Text("Full Name") },
                            placeholder = { Text("e.g. Chirushi S") },
                            leadingIcon = { Icon(Icons.Filled.Badge, contentDescription = null, tint = Color(0xFF93C5FD)) },
                            modifier = Modifier.fillMaxWidth().testTag("sign_up_name_input"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2563EB),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = signUpEmail,
                            onValueChange = { signUpEmail = it; authViewModel.clearError() },
                            label = { Text("Institutional Email") },
                            placeholder = { Text("name@mirai.msot.edu") },
                            leadingIcon = { Icon(Icons.Filled.Mail, contentDescription = null, tint = Color(0xFF93C5FD)) },
                            modifier = Modifier.fillMaxWidth().testTag("sign_up_email_input"),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2563EB),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = signUpRollNo,
                            onValueChange = { signUpRollNo = it; authViewModel.clearError() },
                            label = { Text("Roll Number (Optional)") },
                            placeholder = { Text("e.g. MR-2026-CS (Auto-generated if empty)") },
                            leadingIcon = { Icon(Icons.Filled.Numbers, contentDescription = null, tint = Color(0xFF93C5FD)) },
                            modifier = Modifier.fillMaxWidth().testTag("sign_up_roll_input"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2563EB),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedLabelColor = Color(0xFF93C5FD),
                                unfocusedLabelColor = Color(0xFF94A3B8)
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = signUpPassword,
                            onValueChange = { signUpPassword = it; authViewModel.clearError() },
                            label = { Text("Password (Min 6 characters)") },
                            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = Color(0xFF93C5FD)) },
                            trailingIcon = {
                                IconButton(onClick = { signUpPasswordVisible = !signUpPasswordVisible }) {
                                    Icon(
                                        imageVector = if (signUpPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                        contentDescription = if (signUpPasswordVisible) "Hide password" else "Show password",
                                        tint = Color(0xFF94A3B8)
                                    )
                                }
                            },
                            visualTransformation = if (signUpPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth().testTag("sign_up_password_input"),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2563EB),
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedLabelColor = Color(0xFF93C5FD),
                                unfocusedLabelColor = Color(0xFF94A3B8)
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                authViewModel.signUpWithEmail(
                                    name = signUpName,
                                    email = signUpEmail,
                                    rollNo = signUpRollNo,
                                    pass = signUpPassword.ifBlank { "password123" },
                                    campus = signUpCampus,
                                    department = signUpDepartment,
                                    academicYear = signUpAcademicYear,
                                    role = signUpRole
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("submit_sign_up_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                            } else {
                                Icon(Icons.Filled.PersonAdd, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Register Mirai Profile", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ==========================================
            // Quick Demo Switcher (Instant Explore)
            // ==========================================
            if (demoUsers.isNotEmpty()) {
                Text(
                    text = "EXPLORE DEMO ROLES (ONE-TAP ACCESS)",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    demoUsers.take(4).forEach { demoUser ->
                        val roleBadgeColor = when (demoUser.role) {
                            UserRole.STUDENT -> Color(0xFF2563EB)
                            UserRole.COORDINATOR -> Color(0xFF059669)
                            UserRole.ADVISOR -> Color(0xFFD97706)
                            UserRole.CAMPUS_MANAGER -> Color(0xFF9333EA)
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    authViewModel.selectDemoProfile(demoUser)
                                }
                                .testTag("demo_user_${demoUser.role.name.lowercase()}"),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(12.dp),
                            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(roleBadgeColor.copy(alpha = 0.4f), Color(0xFF334155))))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(roleBadgeColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (demoUser.role) {
                                            UserRole.STUDENT -> Icons.Filled.School
                                            UserRole.COORDINATOR -> Icons.Filled.SupervisorAccount
                                            UserRole.ADVISOR -> Icons.Filled.Analytics
                                            UserRole.CAMPUS_MANAGER -> Icons.Filled.AdminPanelSettings
                                        },
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = demoUser.name,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "${demoUser.role.label} • ${demoUser.campus.displayName}",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 11.sp
                                    )
                                }

                                Text(
                                    text = "Log In",
                                    color = roleBadgeColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// -------------------------------------------------------------
// 1. STRIPE PAYMENT MODAL
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StripePaymentModal(
    event: Event,
    initialTier: TicketTier,
    onDismiss: () -> Unit,
    onPaymentSuccess: (Ticket) -> Unit,
    onProcessPayment: (tierName: String, amount: Double, cardLast4: String, promoCode: String?) -> Ticket
) {
    var selectedTier by remember { mutableStateOf(initialTier) }
    var cardNumber by remember { mutableStateOf("4242 4242 4242 4242") }
    var expiryDate by remember { mutableStateOf("12/28") }
    var cvc by remember { mutableStateOf("842") }
    var promoCode by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var confirmedTicket by remember { mutableStateOf<Ticket?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val discountApplied = promoCode.trim().uppercase() == "MSOT100"
    val finalPrice = if (discountApplied) 0.0 else selectedTier.priceUsd

    Dialog(onDismissRequest = { if (!isProcessing) onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("stripe_payment_modal"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (confirmedTicket != null) {
                    // Success View
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Success",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Payment Confirmed!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Stripe Transaction: ${confirmedTicket!!.transactionId}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    QrCodeView(
                        payload = confirmedTicket!!.qrCodePayload,
                        size = 140.dp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = confirmedTicket!!.tierName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Show this QR code at the gate on event day.",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = { onPaymentSuccess(confirmedTicket!!) },
                        modifier = Modifier.fillMaxWidth().testTag("view_ticket_pass_button")
                    ) {
                        Text("View Digital Pass")
                    }
                } else {
                    // Checkout View
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Lock,
                                    contentDescription = "Secure",
                                    tint = Color(0xFF6366F1),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Stripe Secure Checkout",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = event.title,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Filled.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tier Selector
                    Text(
                        text = "Select Ticket Tier",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    event.ticketTiers.forEach { tier ->
                        val isSelected = tier.name == selectedTier.name
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedTier = tier },
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = tier.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = tier.perks,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "$${tier.priceUsd.toInt()}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Card Inputs
                    OutlinedTextField(
                        value = cardNumber,
                        onValueChange = { cardNumber = it },
                        label = { Text("Card Number") },
                        leadingIcon = { Icon(Icons.Filled.CreditCard, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("card_number_input"),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = expiryDate,
                            onValueChange = { expiryDate = it },
                            label = { Text("MM/YY") },
                            modifier = Modifier.weight(1f).testTag("card_expiry_input"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = cvc,
                            onValueChange = { cvc = it },
                            label = { Text("CVC") },
                            modifier = Modifier.weight(1f).testTag("card_cvc_input"),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = promoCode,
                        onValueChange = { promoCode = it },
                        label = { Text("Promo Code (e.g. MSOT100)") },
                        modifier = Modifier.fillMaxWidth().testTag("promo_code_input"),
                        singleLine = true,
                        trailingIcon = {
                            if (discountApplied) {
                                Text("100% OFF", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 8.dp))
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Total & Pay Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Total to Pay", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = if (finalPrice == 0.0) "FREE ($0.00)" else "$${finalPrice}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            isProcessing = true
                            coroutineScope.launch {
                                delay(1200) // Realistic Stripe processing delay
                                val ticket = onProcessPayment(
                                    selectedTier.name,
                                    finalPrice,
                                    cardNumber.takeLast(4),
                                    promoCode
                                )
                                confirmedTicket = ticket
                                isProcessing = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("pay_with_stripe_button"),
                        enabled = !isProcessing
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Authorizing via Stripe...")
                        } else {
                            Icon(Icons.Filled.Payment, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Pay $${finalPrice} with Stripe")
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. DIGITAL TICKET PASS MODAL
// -------------------------------------------------------------
@Composable
fun TicketPassModal(
    ticket: Ticket,
    onDismiss: () -> Unit,
    onShare: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("ticket_pass_modal"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Official Ticket Pass",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Ticket Card Visual
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = ticket.eventTitle,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        SuggestionChip(
                            onClick = {},
                            label = { Text(ticket.tierName) }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        QrCodeView(
                            payload = ticket.qrCodePayload,
                            size = 160.dp
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = ticket.qrCodePayload,
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("ATTENDEE", style = MaterialTheme.typography.labelSmall)
                                Text(ticket.attendeeName, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("ROLL NO", style = MaterialTheme.typography.labelSmall)
                                Text(ticket.rollNo, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("CAMPUS", style = MaterialTheme.typography.labelSmall)
                                Text(ticket.campus.displayName, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("TRANSACTION", style = MaterialTheme.typography.labelSmall)
                                Text(ticket.transactionId.take(10) + "...", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = onShare,
                        modifier = Modifier.weight(1f).testTag("share_ticket_button")
                    ) {
                        Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share")
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Done")
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. STUDENT CHECK-IN MODAL (QR or 6-digit PIN)
// -------------------------------------------------------------
@Composable
fun StudentCheckInModal(
    activity: ActivityPlan,
    onDismiss: () -> Unit,
    onCheckIn: (code: String, isLate: Boolean) -> Boolean
) {
    var inputCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf(false) }
    var isSimulatedLate by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("check_in_modal"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Club Day Attendance",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // GPS Location Indicator
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Geo-Verified: Inside ${activity.venue}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF047857),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (success) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Attendance Recorded!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Your participation token has been cryptographically logged on CampusCred.",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                        Text("Close")
                    }
                } else {
                    Text(
                        text = "Enter the 6-digit session PIN displayed by your club coordinator or scan the rotating QR code:",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = inputCode,
                        onValueChange = {
                            if (it.length <= 6) inputCode = it
                            errorMessage = null
                        },
                        label = { Text("6-Digit PIN (Hint: ${activity.checkInCode})") },
                        modifier = Modifier.fillMaxWidth().testTag("check_in_pin_input"),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isSimulatedLate,
                            onCheckedChange = { isSimulatedLate = it },
                            modifier = Modifier.testTag("late_checkin_checkbox")
                        )
                        Text(
                            text = "Simulate late check-in flag (>15 min)",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val ok = onCheckIn(inputCode.ifEmpty { activity.checkInCode }, isSimulatedLate)
                            if (ok) {
                                success = true
                            } else {
                                errorMessage = "Invalid check-in code. Try PIN: ${activity.checkInCode}"
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("submit_checkin_button")
                    ) {
                        Icon(Icons.Filled.QrCodeScanner, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Confirm Attendance")
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 4. COORDINATOR ROTATING QR DISPLAY MODAL
// -------------------------------------------------------------
@Composable
fun CoordinatorQrModal(
    activity: ActivityPlan,
    onDismiss: () -> Unit
) {
    var timerSeconds by remember { mutableStateOf(29) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            if (timerSeconds > 1) {
                timerSeconds--
            } else {
                timerSeconds = 30
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("coordinator_qr_modal"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Coordinator Check-In Station",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = "${activity.clubName} - ${activity.title}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Rotating QR code
                QrCodeView(
                    payload = "MSOT-CHECKIN-${activity.id}-${activity.checkInCode}-T$timerSeconds",
                    size = 190.dp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Token rotating in ${timerSeconds}s",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Fast 6-Digit PIN",
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = activity.checkInCode,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 4.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Close Station")
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 5. DELIVERABLE SUBMISSION MODAL
// -------------------------------------------------------------
@Composable
fun DeliverableSubmissionModal(
    activity: ActivityPlan,
    onDismiss: () -> Unit,
    onSubmit: (title: String, fileType: String, url: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var fileType by remember { mutableStateOf("PDF Document") }
    var urlOrName by remember { mutableStateOf("") }

    val fileTypes = listOf("PDF Document", "Presentation PPT", "GitHub Repo Link", "Technical Report", "Video Demo Link")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("deliverable_submission_modal"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Submit Deliverable",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = "For: ${activity.title}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Deliverable Title / Topic") },
                    placeholder = { Text("e.g. Distributed Task Queue Redis") },
                    modifier = Modifier.fillMaxWidth().testTag("submission_title_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Deliverable Format", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    fileTypes.take(3).forEach { ft ->
                        FilterChip(
                            selected = fileType == ft,
                            onClick = { fileType = ft },
                            label = { Text(ft.split(" ").first(), style = MaterialTheme.typography.bodySmall) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = urlOrName,
                    onValueChange = { urlOrName = it },
                    label = { Text("URL / Drive / GitHub Link") },
                    placeholder = { Text("https://github.com/my-project") },
                    modifier = Modifier.fillMaxWidth().testTag("submission_url_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onSubmit(
                                title.trim(),
                                fileType,
                                urlOrName.ifEmpty { "https://msot.campus.storage/submissions/${title.replace(" ", "_")}.pdf" }
                            )
                            onDismiss()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("submit_deliverable_button"),
                    enabled = title.isNotBlank()
                ) {
                    Icon(Icons.Filled.CloudUpload, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Upload & Submit")
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 6. RUBRIC EVALUATION MODAL (Advisors & Coordinators)
// -------------------------------------------------------------
@Composable
fun RubricEvaluationModal(
    submission: Submission,
    onDismiss: () -> Unit,
    onEvaluate: (score: Int, feedback: String) -> Unit
) {
    var contentScore by remember { mutableStateOf(36f) }
    var presentationScore by remember { mutableStateOf(27f) }
    var participationScore by remember { mutableStateOf(28f) }
    var feedback by remember { mutableStateOf("Well-researched architecture and articulate documentation.") }

    val totalScore = (contentScore + presentationScore + participationScore).toInt()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("rubric_evaluation_modal"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Rubric Evaluation",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = "Student: ${submission.studentName} (${submission.fileTitle})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Rubric 1: Content Quality (Max 40)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Content Quality", fontWeight = FontWeight.SemiBold)
                    Text("${contentScore.toInt()} / 40", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = contentScore,
                    onValueChange = { contentScore = it },
                    valueRange = 0f..40f,
                    steps = 39,
                    modifier = Modifier.testTag("content_score_slider")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Rubric 2: Presentation (Max 30)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Presentation & Clarity", fontWeight = FontWeight.SemiBold)
                    Text("${presentationScore.toInt()} / 30", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = presentationScore,
                    onValueChange = { presentationScore = it },
                    valueRange = 0f..30f,
                    steps = 29,
                    modifier = Modifier.testTag("presentation_score_slider")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Rubric 3: Participation (Max 30)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Participation & Timeliness", fontWeight = FontWeight.SemiBold)
                    Text("${participationScore.toInt()} / 30", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = participationScore,
                    onValueChange = { participationScore = it },
                    valueRange = 0f..30f,
                    steps = 29,
                    modifier = Modifier.testTag("participation_score_slider")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total Weighted Score", fontWeight = FontWeight.Bold)
                        Text(
                            text = "$totalScore / 100",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = feedback,
                    onValueChange = { feedback = it },
                    label = { Text("Evaluator Feedback & Suggestions") },
                    modifier = Modifier.fillMaxWidth().testTag("evaluation_feedback_input"),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        onEvaluate(totalScore, feedback)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth().testTag("publish_evaluation_button")
                ) {
                    Icon(Icons.Filled.Grading, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Publish Verified Score")
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 7. OFFICIAL CERTIFICATE MODAL
// -------------------------------------------------------------
@Composable
fun CertificateViewModal(
    certificate: Certificate,
    onDismiss: () -> Unit,
    onShare: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("certificate_modal"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Verified Credential",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Certificate Parchment Box
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, Color(0xFFD97706), RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFFBEB)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "MSOT INSTITUTION OF TECHNOLOGY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF78350F),
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "CERTIFICATE OF ACHIEVEMENT",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF92400E)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "This is proudly presented to",
                            fontSize = 12.sp,
                            color = Color(0xFF78350F)
                        )
                        Text(
                            text = certificate.studentName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E1B4B)
                        )
                        Text(
                            text = "Roll No: ${certificate.rollNo}",
                            fontSize = 12.sp,
                            color = Color(0xFF4B5563)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "For outstanding participation & performance in",
                            fontSize = 11.sp,
                            color = Color(0xFF78350F)
                        )
                        Text(
                            text = certificate.clubOrEventTitle,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF1E1B4B)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        QrCodeView(
                            payload = "https://msot.campus/verify/${certificate.certNumber}",
                            size = 110.dp,
                            backgroundColor = Color.White
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = certificate.certNumber,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF4B5563)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(horizontalAlignment = Alignment.Start) {
                                Text(
                                    text = certificate.signatoryName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E1B4B)
                                )
                                Text(
                                    text = certificate.signatoryRole,
                                    fontSize = 9.sp,
                                    color = Color(0xFF4B5563)
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = certificate.issueDate,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E1B4B)
                                )
                                Text(
                                    text = "Issue Date",
                                    fontSize = 9.sp,
                                    color = Color(0xFF4B5563)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = onShare,
                        modifier = Modifier.weight(1f).testTag("share_cert_button")
                    ) {
                        Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share")
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Download PDF")
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 8. CO-CURRICULAR TRANSCRIPT MODAL
// -------------------------------------------------------------
@Composable
fun TranscriptViewModal(
    user: UserProfile,
    memberships: List<Membership>,
    clubs: List<Club>,
    attendances: List<AttendanceRecord>,
    submissions: List<Submission>,
    certificates: List<Certificate>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("transcript_modal"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Official Co-Curricular Transcript",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = "MSOT University Unified Credit Register",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Student Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(user.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(user.rollNo, style = MaterialTheme.typography.bodySmall)
                                Text("${user.department} - ${user.campus.displayName}", style = MaterialTheme.typography.bodySmall)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Credits Earned", style = MaterialTheme.typography.labelSmall)
                                Text(
                                    "${user.coCurricularCredits} pts",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 1: Enrolled Clubs
                Text("1. Verified Club Memberships", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                memberships.forEach { mem ->
                    val club = clubs.firstOrNull { it.id == mem.clubId }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("• ${club?.name ?: mem.clubId} (${mem.roleInClub})", style = MaterialTheme.typography.bodyMedium)
                        Text("Verified", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 2: Club Day Attendance
                Text("2. Club Day Check-Ins (${attendances.size} sessions)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                attendances.forEach { att ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("• ${att.timestamp.split(" at ").first()} - ${att.verificationToken}", style = MaterialTheme.typography.bodySmall)
                        Text(
                            if (att.isLate) "Late (+5 pts)" else "Present (+10 pts)",
                            color = if (att.isLate) Color(0xFFF59E0B) else Color(0xFF10B981),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section 3: Evaluated Deliverables
                Text("3. Evaluated Deliverables & Rubrics", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                submissions.forEach { sub ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("• ${sub.fileTitle}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                        Text(
                            if (sub.isEvaluated) "${sub.score}/100" else "Pending Review",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = if (sub.isEvaluated) MaterialTheme.colorScheme.primary else Color(0xFFF59E0B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().testTag("export_transcript_button")
                ) {
                    Icon(Icons.Filled.Download, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export Official Transcript (PDF/CSV)")
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 9. ROLE SWITCHER MODAL
// -------------------------------------------------------------
@Composable
fun RoleSwitcherModal(
    currentRole: UserRole,
    onRoleSelected: (UserRole) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("role_switcher_modal"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Switch User Persona",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = "Select a persona to test role-specific capabilities:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                UserRole.values().forEach { role ->
                    val isSelected = role == currentRole
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                onRoleSelected(role)
                                onDismiss()
                            },
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    onRoleSelected(role)
                                    onDismiss()
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(role.label, fontWeight = FontWeight.Bold)
                                Text(role.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 10. SOCIAL SHARE MODAL
// -------------------------------------------------------------
@Composable
fun SocialShareModal(
    title: String,
    url: String,
    onDismiss: () -> Unit
) {
    var copied by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("social_share_modal"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Share Activity & Event",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // WhatsApp
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FilledIconButton(
                            onClick = onDismiss,
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF25D366))
                        ) {
                            Icon(Icons.Filled.Chat, contentDescription = "WhatsApp", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("WhatsApp", fontSize = 11.sp)
                    }

                    // LinkedIn
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FilledIconButton(
                            onClick = onDismiss,
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF0A66C2))
                        ) {
                            Icon(Icons.Filled.Share, contentDescription = "LinkedIn", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("LinkedIn", fontSize = 11.sp)
                    }

                    // Twitter/X
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FilledIconButton(
                            onClick = onDismiss,
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFF0F172A))
                        ) {
                            Icon(Icons.Filled.Send, contentDescription = "X", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("X", fontSize = 11.sp)
                    }

                    // Copy Link
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FilledIconButton(
                            onClick = { copied = true },
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Filled.ContentCopy, contentDescription = "Copy Link", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(if (copied) "Copied!" else "Copy Link", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Done")
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 11. CREATE ACTIVITY PLAN MODAL (Coordinators & Advisors)
// -------------------------------------------------------------
@Composable
fun CreateActivityPlanModal(
    club: Club,
    onDismiss: () -> Unit,
    onSubmit: (
        title: String,
        date: String,
        timeSlot: String,
        venue: String,
        topic: String,
        deliverableType: String,
        deadline: String
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("Saturday, Oct 25, 2026") }
    var timeSlot by remember { mutableStateOf("10:00 AM - 1:00 PM") }
    var venue by remember { mutableStateOf("Turing Lab 402") }
    var topic by remember { mutableStateOf("") }
    var deliverableType by remember { mutableStateOf("Architecture Report & Code Repo") }
    var deadline by remember { mutableStateOf("Monday, Oct 27, 11:59 PM") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("create_activity_modal"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Plan Club Day Activity",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = "For: ${club.name} (${club.campus.displayName})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Activity Title") },
                    placeholder = { Text("e.g. Club Day: Real-Time AI Systems") },
                    modifier = Modifier.fillMaxWidth().testTag("plan_title_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = topic,
                    onValueChange = { topic = it },
                    label = { Text("Topic & Key Agenda") },
                    placeholder = { Text("e.g. Deploying LLM fine-tuning pipelines") },
                    modifier = Modifier.fillMaxWidth().testTag("plan_topic_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Date") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = timeSlot,
                        onValueChange = { timeSlot = it },
                        label = { Text("Time Slot") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = venue,
                    onValueChange = { venue = it },
                    label = { Text("Venue / Lab") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = deliverableType,
                    onValueChange = { deliverableType = it },
                    label = { Text("Required Deliverable Format") },
                    placeholder = { Text("e.g. GitHub Repo / PPT Deck / Report") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = deadline,
                    onValueChange = { deadline = it },
                    label = { Text("Submission Deadline (48h Default)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        if (title.isNotBlank() && topic.isNotBlank()) {
                            onSubmit(title.trim(), date, timeSlot, venue, topic.trim(), deliverableType, deadline)
                            onDismiss()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("submit_plan_button"),
                    enabled = title.isNotBlank() && topic.isNotBlank()
                ) {
                    Icon(Icons.Filled.Send, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Submit for Advisor Approval")
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 12. DUTY LEAVE REQUEST MODAL (Students)
// -------------------------------------------------------------
@Composable
fun DutyLeaveRequestModal(
    event: Event,
    onDismiss: () -> Unit,
    onSubmit: (reason: String) -> Unit
) {
    var reason by remember { mutableStateOf("Official university finalist delegation representing MSOT in competitive tracks.") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("duty_leave_request_modal"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Duty Leave Endorsement",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = "Event: ${event.title}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Dates: ${event.date} • Venue: ${event.venue}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Academic Justification / Contingent Reason",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Justification for Campus Manager") },
                    modifier = Modifier.fillMaxWidth().testTag("duty_leave_reason_input"),
                    minLines = 3,
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Info, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Approved requests automatically notify department professors and preserve semester attendance.",
                            fontSize = 11.sp,
                            color = Color(0xFF047857)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        if (reason.isNotBlank()) {
                            onSubmit(reason.trim())
                            onDismiss()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("submit_duty_leave_button"),
                    enabled = reason.isNotBlank()
                ) {
                    Icon(Icons.Filled.VerifiedUser, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Submit Request")
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 13. DUTY LEAVE REVIEW MODAL (Campus Managers & Admins)
// -------------------------------------------------------------
@Composable
fun DutyLeaveReviewModal(
    requests: List<DutyLeaveRequest>,
    onDismiss: () -> Unit,
    onReview: (requestId: String, isApproved: Boolean, notes: String) -> Unit
) {
    var reviewNotes by remember { mutableStateOf("Endorsed under MSOT inter-campus representation policy.") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("duty_leave_review_modal"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Duty Leave Applications",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = "Campus Manager Review Console",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (requests.isEmpty()) {
                    Text(
                        text = "No pending duty leave applications at this time.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    requests.forEach { req ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(req.studentName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = when (req.status) {
                                            DutyLeaveStatus.APPROVED -> Color(0xFF10B981)
                                            DutyLeaveStatus.PENDING -> Color(0xFFF59E0B)
                                            DutyLeaveStatus.REJECTED -> Color(0xFFF43F5E)
                                        }
                                    ) {
                                        Text(
                                            text = req.status.name,
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Text("Roll: ${req.rollNo} • Campus: ${req.campus.displayName}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Event: ${req.eventTitle}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                                Text("Dates: ${req.dates}", fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Justification: \"${req.reason}\"", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)

                                if (req.status == DutyLeaveStatus.PENDING) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { onReview(req.id, true, reviewNotes) },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                                        ) {
                                            Text("Approve", fontSize = 11.sp)
                                        }
                                        OutlinedButton(
                                            onClick = { onReview(req.id, false, "Leave declined due to exam schedule overlap.") },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Reject", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Close Console")
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 14. TEAM FORMATION MODAL (Hackathons & Conclaves)
// -------------------------------------------------------------
@Composable
fun TeamFormationModal(
    event: Event,
    teams: List<EventTeam>,
    onDismiss: () -> Unit,
    onCreateTeam: (teamName: String, maxMembers: Int) -> Unit,
    onJoinTeam: (joinCode: String) -> Boolean
) {
    var newTeamName by remember { mutableStateOf("") }
    var inputJoinCode by remember { mutableStateOf("") }
    var joinError by remember { mutableStateOf<String?>(null) }
    var joinSuccess by remember { mutableStateOf(false) }

    val eventTeams = teams.filter { it.eventId == event.id }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("team_formation_modal"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Team Registration",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = event.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Section 1: Form a New Team
                Text("Form a New Squad / Team", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = newTeamName,
                    onValueChange = { newTeamName = it },
                    label = { Text("Team Name") },
                    placeholder = { Text("e.g. Apex Innovators") },
                    modifier = Modifier.fillMaxWidth().testTag("team_name_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (newTeamName.isNotBlank()) {
                            onCreateTeam(newTeamName.trim(), 4)
                            newTeamName = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("create_team_button"),
                    enabled = newTeamName.isNotBlank()
                ) {
                    Icon(Icons.Filled.GroupAdd, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Create Team & Get Code")
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(14.dp))

                // Section 2: Join with Code
                Text("Join an Existing Squad", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputJoinCode,
                        onValueChange = {
                            inputJoinCode = it
                            joinError = null
                        },
                        label = { Text("Join Code (e.g. CK88)") },
                        modifier = Modifier.weight(1f).testTag("join_team_code_input"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val ok = onJoinTeam(inputJoinCode)
                            if (ok) {
                                joinSuccess = true
                                joinError = null
                            } else {
                                joinError = "Invalid code or squad is full."
                            }
                        },
                        modifier = Modifier.testTag("submit_join_team_button"),
                        enabled = inputJoinCode.isNotBlank()
                    ) {
                        Text("Join")
                    }
                }

                if (joinSuccess) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Successfully joined team!", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                if (joinError != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(joinError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(14.dp))

                // Section 3: Registered Teams Roster
                Text("Registered Squads (${eventTeams.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(6.dp))

                if (eventTeams.isEmpty()) {
                    Text("No squads formed yet. Create the first team above!", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    eventTeams.forEach { squad ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(squad.teamName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Text(
                                            text = "CODE: ${squad.joinCode}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text("Leader: ${squad.leaderName} • ${squad.members.size}/${squad.maxMembers} members", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Roster: ${squad.members.joinToString(", ")}", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Done")
                }
            }
        }
    }
}


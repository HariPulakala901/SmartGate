package com.example.demo.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.demo.ui.components.GlassBanner
import com.example.demo.ui.components.GlassButton
import com.example.demo.ui.components.GlassButtonStyle
import com.example.demo.ui.components.GlassChip
import com.example.demo.ui.components.GlossyDivider
import com.example.demo.ui.components.LiquidGlassBackground
import com.example.demo.ui.components.LiquidGlassCard
import com.example.demo.ui.components.SectionLabel
import com.example.demo.ui.components.glassTextFieldColors
import com.example.demo.ui.theme.AccentBlue
import com.example.demo.ui.theme.SemanticAmber
import com.example.demo.ui.theme.SemanticGreen
import com.example.demo.ui.theme.SemanticRed
import com.example.demo.ui.theme.TextPrimary
import com.example.demo.ui.theme.TextSecondary
import com.example.demo.ui.theme.TextTertiary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Registerscreen(navController: NavHostController, isEdit: Boolean = false) {

    var name          by remember { mutableStateOf("") }
    var phone         by remember { mutableStateOf("") }
    var purpose       by remember { mutableStateOf("") }
    var persontomeet  by remember { mutableStateOf("") }
    var visitDate     by remember { mutableStateOf("") }
    var selectedIdType      by remember { mutableStateOf("AADHAAR") }
    var idNumber            by remember { mutableStateOf("") }
    var idVerificationState by remember { mutableStateOf<IdVerificationState>(IdVerificationState.Idle) }
    var isFlaggedProxy      by remember { mutableStateOf(false) }
    var lockedIdType        by remember { mutableStateOf<String?>(null) }
    var showFlaggedDialog   by remember { mutableStateOf(false) }

    val context        = LocalContext.current
    var isLoading      by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val today = remember {
        Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")).apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0);      set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = today,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis >= today
        }
    )

    var visible   by remember { mutableStateOf(false) }
    val scope     = rememberCoroutineScope()
    var requestId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { visible = true }

    BackHandler {
        visible = false
        scope.launch { delay(300); navController.popBackStack() }
    }

    val isIdValid = when (selectedIdType) {
        "AADHAAR" -> isValidAadhaar(idNumber)
        "PAN"     -> isValidPan(idNumber)
        else      -> false
    }

    val isFormValid = name.isNotBlank() && phone.length == 10 && purpose.isNotBlank() &&
            persontomeet.isNotBlank() && visitDate.isNotBlank() && isIdValid

    fun doSubmit() {
        isLoading = true
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) { isLoading = false; Toast.makeText(context, "Session expired. Please restart.", Toast.LENGTH_SHORT).show(); return }
        context.getSharedPreferences("visitor_prefs", android.content.Context.MODE_PRIVATE)
            .edit().putString("uid", uid).apply()

        FirebaseMessaging.getInstance().token.addOnCompleteListener { fcmTask ->
            val token = if (fcmTask.isSuccessful) fcmTask.result else ""

            val dbRef       = FirebaseDatabase.getInstance().getReference("visitorRequests")
            val finalStatus = if (isFlaggedProxy) "FLAGGED" else "PENDING"

            val data = details(
                requestId    = requestId ?: "",
                uid          = uid,
                name         = name,
                phone        = phone,
                purpose      = purpose,
                personToMeet = persontomeet,
                visitDate    = visitDate,
                status       = finalStatus,
                timestamp    = com.google.firebase.database.ServerValue.TIMESTAMP,
                idType       = selectedIdType,
                idNumber     = idNumber,
                isFlagged    = isFlaggedProxy,
                fcmToken     = token
            )

            val task = if (isEdit && requestId != null) {
                dbRef.child(requestId!!).setValue(data)
            } else {
                val newId = dbRef.push().key ?: run { isLoading = false; return@addOnCompleteListener }
                requestId = newId
                dbRef.child(newId).setValue(data.copy(requestId = newId))
            }

            task.addOnSuccessListener {
                if (!isFlaggedProxy) {
                    saveVisitorProfile(selectedIdType, idNumber, name, phone)
                }
                isLoading = false; visible = false
                scope.launch {
                    delay(350)
                    if (isFlaggedProxy) {
                        navController.navigate("flagged") { popUpTo("register") { inclusive = true } }
                    } else {
                        navController.navigate("pending") { popUpTo("register") { inclusive = true } }
                    }
                }
            }.addOnFailureListener {
                isLoading = false
                Toast.makeText(context, "Failed to submit. Try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(isEdit) {
        if (!isEdit) return@LaunchedEffect
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@LaunchedEffect
        FirebaseDatabase.getInstance()
            .getReference("visitorRequests")
            .orderByChild("uid").equalTo(uid).get()
            .addOnSuccessListener { snapshot ->
                val child = snapshot.children.maxByOrNull {
                    it.key.toString()
                } ?: return@addOnSuccessListener
                requestId      = child.key
                name           = child.child("name").value?.toString() ?: ""
                phone          = child.child("phone").value?.toString() ?: ""
                purpose        = child.child("purpose").value?.toString() ?: ""
                persontomeet   = child.child("personToMeet").value?.toString() ?: ""
                visitDate      = child.child("visitDate").value?.toString() ?: ""
                selectedIdType = child.child("idType").value?.toString() ?: "AADHAAR"
                idNumber       = child.child("idNumber").value?.toString() ?: ""
                isFlaggedProxy = child.child("isFlagged").getValue(Boolean::class.java) ?: false
                if (idNumber.isNotEmpty()) idVerificationState = IdVerificationState.ReturningMatch
            }
    }

    if (showFlaggedDialog) {
        AlertDialog(
            onDismissRequest = { showFlaggedDialog = false },
            title   = { Text("Submit Flagged Request?", fontWeight = FontWeight.SemiBold) },
            text    = { Text("This ID was previously used with a different name. Your request will be submitted and flagged for admin review.") },
            confirmButton = {
                TextButton(onClick = { showFlaggedDialog = false; doSubmit() }) {
                    Text("Submit Anyway", color = SemanticAmber, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = { TextButton(onClick = { showFlaggedDialog = false }) { Text("Cancel") } }
        )
    }

    LiquidGlassBackground {
        AnimatedVisibility(
            visible = visible,
            enter   = slideInHorizontally(tween(220)) { it } + fadeIn(tween(220)),
            exit    = slideOutHorizontally(tween(220)) { it } + fadeOut(tween(220))
        ) {
            Scaffold(topBar = {}, containerColor = Color.Transparent) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 20.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(36.dp))

                    Text(
                        text = if (isEdit) "Update Request" else "Enter Details",
                        fontSize = 32.sp, fontWeight = FontWeight.Bold,
                        color = TextPrimary, letterSpacing = (-0.3).sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = if (isEdit) "Edit your visit details below" else "Fill in your visit information",
                        fontSize = 14.sp, color = TextSecondary
                    )

                    Spacer(Modifier.height(24.dp))

                    // ── Government ID card ────────────────────────────────
                    LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            SectionLabel(text = "Government ID Verification")
                            Spacer(Modifier.height(2.dp))
                            Text("Required to prevent duplicate / proxy entries", color = TextTertiary, fontSize = 11.sp)
                            Spacer(Modifier.height(14.dp))

                            Row(
                                modifier              = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                GlassChip(
                                    label    = "Aadhaar Card",
                                    selected = selectedIdType == "AADHAAR",
                                    enabled  = lockedIdType == null || lockedIdType == "AADHAAR",
                                    onClick  = {
                                        if (selectedIdType != "AADHAAR") {
                                            selectedIdType = "AADHAAR"; idNumber = ""
                                            idVerificationState = IdVerificationState.Idle
                                            isFlaggedProxy = false; lockedIdType = null
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                GlassChip(
                                    label    = "PAN Card",
                                    selected = selectedIdType == "PAN",
                                    enabled  = lockedIdType == null || lockedIdType == "PAN",
                                    onClick  = {
                                        if (selectedIdType != "PAN") {
                                            selectedIdType = "PAN"; idNumber = ""
                                            idVerificationState = IdVerificationState.Idle
                                            isFlaggedProxy = false; lockedIdType = null
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(Modifier.height(12.dp))

                            OutlinedTextField(
                                value = idNumber,
                                onValueChange = { input ->
                                    val filtered = when (selectedIdType) {
                                        "AADHAAR" -> input.filter(Char::isDigit).take(12)
                                        "PAN"     -> input.filter { it.isLetterOrDigit() }.take(10).uppercase()
                                        else      -> input
                                    }
                                    idNumber = filtered
                                    idVerificationState = IdVerificationState.Idle
                                    isFlaggedProxy = false; lockedIdType = null
                                },
                                label = { Text(if (selectedIdType == "AADHAAR") "Aadhaar Number" else "PAN Number (e.g. ABCDE1234F)") },
                                leadingIcon = { Icon(Icons.Rounded.Badge, null, tint = AccentBlue) },
                                trailingIcon = {
                                    if (isIdValid && idVerificationState == IdVerificationState.Idle) {
                                        TextButton(onClick = {
                                            if (name.isBlank()) {
                                                Toast.makeText(context, "Security constraint: Please enter your Full Name below first to verify identity", Toast.LENGTH_LONG).show()
                                                return@TextButton
                                            }
                                            idVerificationState = IdVerificationState.Checking
                                            verifyIdInFirebase(selectedIdType, idNumber, name) { state, prefillName, prefillPhone, _ ->
                                                idVerificationState = state
                                                when (state) {
                                                    IdVerificationState.ReturningMatch -> {
                                                        isFlaggedProxy = false; lockedIdType = null
                                                        if (name.isBlank() && prefillName != null) name = prefillName
                                                        if (phone.isBlank() && prefillPhone != null) phone = prefillPhone
                                                    }
                                                    IdVerificationState.ProxyDetected -> { isFlaggedProxy = true; lockedIdType = null }
                                                    is IdVerificationState.WrongIdType -> {
                                                        isFlaggedProxy = false; lockedIdType = state.registeredIdType
                                                        selectedIdType = state.registeredIdType; idNumber = ""
                                                    }
                                                    else -> { isFlaggedProxy = false; lockedIdType = null }
                                                }
                                            }
                                        }) { Text("Verify", color = AccentBlue, fontSize = 13.sp) }
                                    }
                                },
                                visualTransformation = if (selectedIdType == "AADHAAR") AadhaarVisualTransformation() else VisualTransformation.None,
                                keyboardOptions = KeyboardOptions(keyboardType = if (selectedIdType == "AADHAAR") KeyboardType.Number else KeyboardType.Text),
                                modifier = Modifier.fillMaxWidth(),
                                shape    = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                                colors   = glassTextFieldColors(),
                                supportingText = {
                                    when {
                                        selectedIdType == "AADHAAR" && idNumber.isNotEmpty() && !isValidAadhaar(idNumber) ->
                                            Text("Enter valid 12-digit Aadhaar (cannot start with 0 or 1)", color = SemanticRed, fontSize = 11.sp)
                                        selectedIdType == "PAN" && idNumber.isNotEmpty() && !isValidPan(idNumber) ->
                                            Text("Enter valid PAN format: ABCDE1234F", color = SemanticRed, fontSize = 11.sp)
                                        selectedIdType == "AADHAAR" && idNumber.isEmpty() ->
                                            Text("Displays as: 1234-5678-9012", color = TextTertiary, fontSize = 11.sp)
                                        else -> {}
                                    }
                                }
                            )

                            Spacer(Modifier.height(8.dp))

                            when (val state = idVerificationState) {
                                IdVerificationState.Checking -> {
                                    Row(
                                        verticalAlignment     = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        CircularProgressIndicator(Modifier.size(14.dp), color = TextPrimary, strokeWidth = 2.dp)
                                        Text("Checking ID…", color = TextSecondary, fontSize = 12.sp)
                                    }
                                }
                                IdVerificationState.NewVisitor ->
                                    GlassBanner("🆕 New visitor — your ID will be registered on submission.", AccentBlue)
                                IdVerificationState.ReturningMatch ->
                                    GlassBanner("✅ Welcome back! Your details have been filled in.", SemanticGreen)
                                IdVerificationState.ProxyDetected ->
                                    GlassBanner("⚠️ This ID was previously used with a different name. Your request will be flagged for admin review.", SemanticAmber)
                                is IdVerificationState.WrongIdType ->
                                    GlassBanner("🔒 You previously registered using ${idTypeLabel(state.registeredIdType)}. Please use the same ID type. Switched — enter your number below.", SemanticRed)
                                IdVerificationState.Idle ->
                                    if (isIdValid) Text("Enter Full Name below & tap Verify", color = TextTertiary, fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // ── Personal details card ─────────────────────────────
                    LiquidGlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            SectionLabel(text = "Personal Details")
                            Spacer(Modifier.height(12.dp))
                            GlossyDivider()
                            Spacer(Modifier.height(12.dp))

                            OutlinedTextField(
                                value = name, 
                                onValueChange = { 
                                    name = it
                                    if (idVerificationState != IdVerificationState.Idle) {
                                        idVerificationState = IdVerificationState.Idle
                                        isFlaggedProxy = false
                                        lockedIdType = null
                                    }
                                },
                                label = { Text("Full Name") },
                                leadingIcon = { Icon(Icons.Rounded.AccountCircle, null, tint = AccentBlue) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                                colors = glassTextFieldColors()
                            )
                            Spacer(Modifier.height(10.dp))
                            OutlinedTextField(
                                value = phone,
                                onValueChange = { if (it.length <= 10 && it.all(Char::isDigit)) phone = it },
                                label = { Text("Mobile Number") },
                                leadingIcon = { Icon(Icons.Rounded.Phone, null, tint = AccentBlue) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                                colors = glassTextFieldColors(),
                                supportingText = if (phone.isNotEmpty() && phone.length < 10) {
                                    { Text("Enter 10 digit number", color = TextTertiary, fontSize = 11.sp) }
                                } else null
                            )
                            Spacer(Modifier.height(10.dp))
                            OutlinedTextField(
                                value = purpose, onValueChange = { purpose = it },
                                label = { Text("Purpose of Visit") },
                                leadingIcon = { Icon(Icons.Rounded.Edit, null, tint = AccentBlue) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                                colors = glassTextFieldColors()
                            )
                            Spacer(Modifier.height(10.dp))
                            OutlinedTextField(
                                value = persontomeet, onValueChange = { persontomeet = it },
                                label = { Text("Person to Meet") },
                                leadingIcon = { Icon(Icons.Rounded.Person, null, tint = AccentBlue) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                                colors = glassTextFieldColors()
                            )
                            Spacer(Modifier.height(10.dp))
                            OutlinedTextField(
                                value = visitDate, onValueChange = {}, readOnly = true,
                                label = { Text("Visit Date") },
                                leadingIcon = { Icon(Icons.Rounded.DateRange, null, tint = AccentBlue) },
                                placeholder = { Text("Tap to select date", color = TextTertiary) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onFocusChanged { if (it.isFocused && !isLoading) showDatePicker = true },
                                shape  = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                                colors = glassTextFieldColors()
                            )
                        }
                    }

                    if (showDatePicker) {
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    datePickerState.selectedDateMillis?.let {
                                        visitDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it))
                                    }
                                    showDatePicker = false
                                }) { Text("OK") }
                            },
                            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } }
                        ) { DatePicker(state = datePickerState) }
                    }

                    Spacer(Modifier.height(24.dp))

                    GlassButton(
                        text = when {
                            isLoading -> "Submitting…"
                            isEdit    -> "Update Request"
                            else      -> "Submit Request"
                        },
                        style   = GlassButtonStyle.Primary,
                        enabled = isFormValid && !isLoading,
                        onClick = {
                            if (idVerificationState == IdVerificationState.Idle || idVerificationState is IdVerificationState.WrongIdType) {
                                isLoading = true
                                verifyIdInFirebase(selectedIdType, idNumber, name) { state, prefillName, prefillPhone, _ ->
                                    idVerificationState = state
                                    isLoading = false
                                    when (state) {
                                        IdVerificationState.ProxyDetected -> {
                                            isFlaggedProxy = true; lockedIdType = null
                                            showFlaggedDialog = true
                                        }
                                        is IdVerificationState.WrongIdType -> {
                                            isFlaggedProxy = false; lockedIdType = state.registeredIdType
                                            selectedIdType = state.registeredIdType; idNumber = ""
                                            Toast.makeText(context, "Please use your registered ID type", Toast.LENGTH_SHORT).show()
                                        }
                                        IdVerificationState.ReturningMatch -> {
                                            isFlaggedProxy = false; lockedIdType = null
                                            if (name.isBlank() && prefillName != null) name = prefillName
                                            if (phone.isBlank() && prefillPhone != null) phone = prefillPhone
                                            doSubmit()
                                        }
                                        else -> {
                                            isFlaggedProxy = false; lockedIdType = null
                                            doSubmit()
                                        }
                                    }
                                }
                            } else {
                                if (isFlaggedProxy) showFlaggedDialog = true else doSubmit()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(36.dp))
                }
            }
        }
    }
}
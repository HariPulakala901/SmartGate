package com.example.demo.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.TextButton
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
//import androidx.compose.animation.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun registerscreen(navController: NavHostController, isEdit: Boolean = false) {

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var purpose by remember { mutableStateOf("") }
    var persontomeet by remember { mutableStateOf("") }
    var visitDate by remember { mutableStateOf("") }

    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    var visible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var requestId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { visible = true }

    BackHandler {
        visible = false
        scope.launch {
            delay(400)
            navController.popBackStack()
        }
    }

    val isFormValid =
        name.isNotBlank() &&
                phone.length == 10 &&
                purpose.isNotBlank() &&
                persontomeet.isNotBlank() &&
                visitDate.isNotBlank()

    // 🔁 LOAD DATA FOR EDIT MODE
    LaunchedEffect(isEdit) {
        if (!isEdit) return@LaunchedEffect

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@LaunchedEffect
        FirebaseDatabase.getInstance()
            .getReference("visitorRequests")
            .orderByChild("uid")
            .equalTo(uid)
            .get()
            .addOnSuccessListener { snapshot ->
                snapshot.children.firstOrNull()?.let { child ->
                    requestId = child.key
                    name = child.child("name").value?.toString() ?: ""
                    phone = child.child("phone").value?.toString() ?: ""
                    purpose = child.child("purpose").value?.toString() ?: ""
                    persontomeet = child.child("personToMeet").value?.toString() ?: ""
                    visitDate = child.child("visitDate").value?.toString() ?: ""
                }
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF2D30E3),
                        Color(0xFF8D6AAB),
                        Color(0xFF135DC4)
                    )
                )
            )
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(200)
            ) + fadeIn(),
            exit = slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(200)
            ) + fadeOut()
        ) {
            Scaffold(
                topBar = {}, // 🔥 REMOVED TOP BAR
                containerColor = Color.Transparent
            ) { padding ->

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // ✅ VISIT-HISTORY STYLE TITLE
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Enter Details",
                        fontSize = 35.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // ---------- FORM ----------
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        leadingIcon = { Icon(Icons.Rounded.AccountCircle, null, tint = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = outlinedFieldColors()
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = phone,
                        onValueChange = {
                            if (it.length <= 10 && it.all(Char::isDigit)) phone = it
                        },
                        label = { Text("Mobile Number") },
                        leadingIcon = { Icon(Icons.Rounded.Phone, null, tint = Color.White) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = outlinedFieldColors()
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = purpose,
                        onValueChange = { purpose = it },
                        label = { Text("Purpose of Visit") },
                        leadingIcon = { Icon(Icons.Rounded.Edit, null, tint = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = outlinedFieldColors()
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = persontomeet,
                        onValueChange = { persontomeet = it },
                        label = { Text("Person to Meet") },
                        leadingIcon = { Icon(Icons.Rounded.Person, null, tint = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = outlinedFieldColors()
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = visitDate,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Visit Date") },
                        leadingIcon = { Icon(Icons.Rounded.DateRange, null, tint = Color.White) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged {
                                if (it.isFocused && !isLoading) showDatePicker = true
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = outlinedFieldColors()
                    )

                    if (showDatePicker) {
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    datePickerState.selectedDateMillis?.let {
                                        visitDate = java.text.SimpleDateFormat(
                                            "dd MMM yyyy",
                                            java.util.Locale.getDefault()
                                        ).format(java.util.Date(it))
                                    }
                                    showDatePicker = false
                                }) { Text("OK") }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDatePicker = false }) {
                                    Text("Cancel")
                                }
                            }
                        ) {
                            DatePicker(state = datePickerState)
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    GradientButton(
                        text = if (isLoading) "Submitting..." else "Submit Request",
                        enabled = isFormValid && !isLoading,
                        onClick = {
                            isLoading = true
                            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@GradientButton
                            val dbRef = FirebaseDatabase.getInstance().getReference("visitorRequests")

                            val data = details(
                                requestId = requestId ?: "",
                                uid = uid,
                                name = name,
                                phone = phone,
                                purpose = purpose,
                                personToMeet = persontomeet,
                                visitDate = visitDate,
                                status = "PENDING",
                                timestamp = com.google.firebase.database.ServerValue.TIMESTAMP,

                            )

                            val task = if (isEdit && requestId != null) {
                                dbRef.child(requestId!!).setValue(data)
                            } else {
                                val id = dbRef.push().key ?: return@GradientButton
                                dbRef.child(id).setValue(data.copy(requestId = id))
                            }

                            task.addOnSuccessListener {
                                isLoading = false
                                visible = false
                                scope.launch {
                                    delay(450)
                                    navController.navigate("pending") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                    )

                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}


@Composable
fun outlinedFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color.White,
    unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
    focusedLabelColor = Color.White,
    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
    cursorColor = Color.White,

    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White
)
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

    val interactionSource = remember { MutableInteractionSource() }

    var visible by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    var requestId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        visible = true
    }

    BackHandler {
        visible = false   // 🔥 trigger exit animation

        scope.launch {
            delay(400)    // wait for exit animation
            navController.popBackStack()
        }
    }

        val isFormValid =
                    name.isNotBlank() &&
                    phone.length == 10 &&
                    purpose.isNotBlank() &&
                    persontomeet.isNotBlank() &&
                    visitDate.isNotBlank()

        //Your registration UI will go here

    LaunchedEffect(isEdit) {
        if (!isEdit) return@LaunchedEffect

        val uid = FirebaseAuth.getInstance().currentUser?.uid
            ?: return@LaunchedEffect

        val dbRef = FirebaseDatabase.getInstance()
            .getReference("visitorRequests")

        dbRef
            .orderByChild("uid")
            .equalTo(uid)
            .get()
            .addOnSuccessListener { snapshot ->
                snapshot.children.firstOrNull()?.let { child ->
                    requestId = child.key   // 🔥 THIS WAS MISSING

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
                initialOffsetX = { fullWidth -> fullWidth }, // 👈 from right
                animationSpec = tween(200)
            ) + fadeIn(animationSpec = tween(200)),

            exit = slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth }, // 👈 to right
                animationSpec = tween(200)
            ) + fadeOut(animationSpec = tween(200))
        ) {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .background( // ✅ GRADIENT HERE
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF2D30E3),
                                Color(0xFF8D6AAB),
                                Color(0xFF135DC4)
                            )
                        )
                    ),
                topBar ={
                    TopAppBar(
                        title ={Text(text = "Enter Details", fontSize = 30.sp)},
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color(0xFF4030D2),
                            titleContentColor = Color.White
                        )
                    )
                },
                containerColor = Color.Transparent
            ) {padding->
                Column(
                    modifier = Modifier.fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = {name = it},
                        enabled = !isLoading,
                        label = {Text("Full Name")},
                        leadingIcon = {
                            Icon(
                                Icons.Rounded.AccountCircle,
                                contentDescription = null,
                                tint = Color.White
                            )
                        },
                        supportingText = {
                            Text("Enter your full name as per ID", color = Color.White)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = outlinedFieldColors()
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { input ->
                            // allow only digits & max 10 characters
                            if (input.length <= 10 && input.all { it.isDigit() }) {
                                phone = input
                            }
                        },
                        enabled = !isLoading,
                        label = { Text("Mobile Number") },
                        leadingIcon = {
                            Icon(
                                Icons.Rounded.Phone,
                                contentDescription = null,
                                tint = Color.White
                            )
                        },
                        supportingText = {
                            Text(
                                text = "Enter ${phone.length}/10 digits Only",
                                color = Color.White
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        colors = outlinedFieldColors()
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = purpose,
                        onValueChange = {purpose = it},
                        enabled = !isLoading,
                        label = {Text("Purpose of Visit")},
                        leadingIcon = {
                            Icon(
                                Icons.Rounded.Edit,
                                contentDescription = null,
                                tint = Color.White
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = outlinedFieldColors()
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = persontomeet,
                        onValueChange = {persontomeet = it},
                        enabled = !isLoading,
                        label = {Text("Person to meet")},
                        leadingIcon = {
                            Icon(
                                Icons.Rounded.Person,
                                contentDescription = null,
                                tint = Color.White
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = outlinedFieldColors()
                    )

                    Spacer(Modifier.height(12.dp))

//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .clickable(enabled = !isLoading) {
//                            showDatePicker = true
//                        }
//                ) {
//                    OutlinedTextField(
//                        value = visitDate,
//                        onValueChange = {},
//                        enabled = false,
//                        readOnly = true,
//                        label = { Text("Visit Date") },
//                        leadingIcon = {
//                            Icon(
//                                Icons.Rounded.DateRange,
//                                contentDescription = null,
//                                tint = Color.White
//                            )
//                        },
//                        modifier = Modifier.fillMaxWidth(),
//                        shape = RoundedCornerShape(16.dp),
//                        colors = outlinedFieldColors()
//                    )
//                }

                    OutlinedTextField(
                        value = visitDate,
                        onValueChange = {},
                        readOnly = true,
                        enabled = !isLoading,
                        label = { Text("Visit Date") },
                        leadingIcon = {
                            Icon(
                                Icons.Rounded.DateRange,
                                contentDescription = null,
                                tint = Color.White
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused && !isLoading) {
                                    showDatePicker = true
                                }
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = outlinedFieldColors()
                    )

                    if (showDatePicker) {
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    val millis = datePickerState.selectedDateMillis
                                    if (millis != null) {
                                        val formatter = java.text.SimpleDateFormat(
                                            "dd MMM yyyy",
                                            java.util.Locale.getDefault()
                                        )
                                        visitDate = formatter.format(java.util.Date(millis))
                                    }
                                    showDatePicker = false
                                }) {
                                    Text("OK")
                                }
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

                    Spacer(Modifier.height(20.dp))

                    GradientButton(
                        text = if (isLoading) "Submitting..." else "Submit Request",
                        enabled = isFormValid && !isLoading,
                        onClick = {
                            isLoading = true

//                        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@GradientButton
                            val uid = FirebaseAuth.getInstance().currentUser?.uid
                            if (uid == null) {
                                isLoading = false
                                return@GradientButton
                            }

                            val visitorData = details(
                                requestId = requestId ?: "",
                                uid = uid,
                                name = name,
                                phone = phone,
                                purpose = purpose,
                                personToMeet = persontomeet,
                                visitDate = visitDate,
                                status = "PENDING",
                                timestamp = com.google.firebase.database.ServerValue.TIMESTAMP
                            )

                            val dbRef = FirebaseDatabase.getInstance()
                                .getReference("visitorRequests")

                            val task = if (isEdit && requestId != null) {
                                // 🔁 UPDATE existing request
                                dbRef.child(requestId!!).setValue(visitorData)
                            } else {
                                // 🆕 CREATE new request
                                val newRequestId = dbRef.push().key
                                if (newRequestId == null) {
                                    isLoading = false
                                    return@GradientButton
                                }

                                val newVisitorData = visitorData.copy(
                                    requestId = newRequestId
                                )

                                dbRef.child(newRequestId).setValue(newVisitorData)
                            }

                            task
                                .addOnSuccessListener {
                                    isLoading = false

                                    Toast.makeText(
                                        context,
                                        if (isEdit) "Request updated successfully"
                                        else "Request submitted successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    // 🔥 trigger exit animation
                                    visible = false

                                    scope.launch {
                                        delay(450) // SAME as animation duration
                                        navController.navigate("pending") {
                                            popUpTo("register") { inclusive = true }
                                        }
                                    }
                                }
                                .addOnFailureListener { e ->
                                    isLoading = false
                                    Log.e("FirebaseError", e.message ?: "Unknown error")
                                }
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp)
                    )
                }
            }
        }
    }


//    AnimatedVisibility(
//        visible = visible,
//        enter = slideInHorizontally(
//            initialOffsetX = { fullWidth -> fullWidth }, // 👈 from right
//            animationSpec = tween(200)
//        ) + fadeIn(animationSpec = tween(200)),
//
//        exit = slideOutHorizontally(
//            targetOffsetX = { fullWidth -> fullWidth }, // 👈 to right
//            animationSpec = tween(200)
//        ) + fadeOut(animationSpec = tween(200))
//    ) {
//        Scaffold(
//            modifier = Modifier
//                .fillMaxSize()
//                .background( // ✅ GRADIENT HERE
//                    brush = Brush.verticalGradient(
//                        colors = listOf(
//                            Color(0xFF2D30E3),
//                            Color(0xFF8D6AAB),
//                            Color(0xFF135DC4)
//                        )
//                    )
//                ),
//            topBar ={
//                TopAppBar(
//                    title ={Text(text = "Enter Details", fontSize = 30.sp)},
//                    colors = TopAppBarDefaults.topAppBarColors(
//                        containerColor = Color(0xFF4030D2),
//                        titleContentColor = Color.White
//                    )
//                )
//            },
//            containerColor = Color.Transparent
//        ) {padding->
//            Column(
//                modifier = Modifier.fillMaxSize()
//                    .padding(padding)
//                    .padding(horizontal = 14.dp, vertical = 12.dp)
//                    .verticalScroll(rememberScrollState())
//            ) {
//                OutlinedTextField(
//                    value = name,
//                    onValueChange = {name = it},
//                    enabled = !isLoading,
//                    label = {Text("Full Name")},
//                    leadingIcon = {
//                        Icon(
//                            Icons.Rounded.AccountCircle,
//                            contentDescription = null,
//                            tint = Color.White
//                        )
//                    },
//                    supportingText = {
//                        Text("Enter your full name as per ID", color = Color.White)
//                    },
//                    modifier = Modifier.fillMaxWidth(),
//                    shape = RoundedCornerShape(16.dp),
//                    colors = outlinedFieldColors()
//                )
//
//                Spacer(Modifier.height(12.dp))
//
//                OutlinedTextField(
//                    value = phone,
//                    onValueChange = { input ->
//                        // allow only digits & max 10 characters
//                        if (input.length <= 10 && input.all { it.isDigit() }) {
//                            phone = input
//                        }
//                    },
//                    enabled = !isLoading,
//                    label = { Text("Mobile Number") },
//                    leadingIcon = {
//                        Icon(
//                            Icons.Rounded.Phone,
//                            contentDescription = null,
//                            tint = Color.White
//                        )
//                    },
//                    supportingText = {
//                        Text(
//                            text = "Enter ${phone.length}/10 digits Only",
//                            color = Color.White
//                        )
//                    },
//                    modifier = Modifier.fillMaxWidth(),
//                    shape = RoundedCornerShape(16.dp),
//                    keyboardOptions = KeyboardOptions(
//                        keyboardType = KeyboardType.Number
//                    ),
//                    colors = outlinedFieldColors()
//                )
//
//                Spacer(Modifier.height(12.dp))
//
//                OutlinedTextField(
//                    value = purpose,
//                    onValueChange = {purpose = it},
//                    enabled = !isLoading,
//                    label = {Text("Purpose of Visit")},
//                    leadingIcon = {
//                        Icon(
//                            Icons.Rounded.Edit,
//                            contentDescription = null,
//                            tint = Color.White
//                        )
//                    },
//                    modifier = Modifier.fillMaxWidth(),
//                    shape = RoundedCornerShape(16.dp),
//                    colors = outlinedFieldColors()
//                )
//
//                Spacer(Modifier.height(12.dp))
//
//                OutlinedTextField(
//                    value = persontomeet,
//                    onValueChange = {persontomeet = it},
//                    enabled = !isLoading,
//                    label = {Text("Person to meet")},
//                    leadingIcon = {
//                        Icon(
//                            Icons.Rounded.Person,
//                            contentDescription = null,
//                            tint = Color.White
//                        )
//                    },
//                    modifier = Modifier.fillMaxWidth(),
//                    shape = RoundedCornerShape(16.dp),
//                    colors = outlinedFieldColors()
//                )
//
//                Spacer(Modifier.height(12.dp))
//
////                Box(
////                    modifier = Modifier
////                        .fillMaxWidth()
////                        .clickable(enabled = !isLoading) {
////                            showDatePicker = true
////                        }
////                ) {
////                    OutlinedTextField(
////                        value = visitDate,
////                        onValueChange = {},
////                        enabled = false,
////                        readOnly = true,
////                        label = { Text("Visit Date") },
////                        leadingIcon = {
////                            Icon(
////                                Icons.Rounded.DateRange,
////                                contentDescription = null,
////                                tint = Color.White
////                            )
////                        },
////                        modifier = Modifier.fillMaxWidth(),
////                        shape = RoundedCornerShape(16.dp),
////                        colors = outlinedFieldColors()
////                    )
////                }
//
//                OutlinedTextField(
//                    value = visitDate,
//                    onValueChange = {},
//                    readOnly = true,
//                    enabled = !isLoading,
//                    label = { Text("Visit Date") },
//                    leadingIcon = {
//                        Icon(
//                            Icons.Rounded.DateRange,
//                            contentDescription = null,
//                            tint = Color.White
//                        )
//                    },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .onFocusChanged { focusState ->
//                            if (focusState.isFocused && !isLoading) {
//                                showDatePicker = true
//                            }
//                        },
//                    shape = RoundedCornerShape(16.dp),
//                    colors = outlinedFieldColors()
//                )
//
//                if (showDatePicker) {
//                    DatePickerDialog(
//                        onDismissRequest = { showDatePicker = false },
//                        confirmButton = {
//                            TextButton(onClick = {
//                                val millis = datePickerState.selectedDateMillis
//                                if (millis != null) {
//                                    val formatter = java.text.SimpleDateFormat(
//                                        "dd MMM yyyy",
//                                        java.util.Locale.getDefault()
//                                    )
//                                    visitDate = formatter.format(java.util.Date(millis))
//                                }
//                                showDatePicker = false
//                            }) {
//                                Text("OK")
//                            }
//                        },
//                        dismissButton = {
//                            TextButton(onClick = { showDatePicker = false }) {
//                                Text("Cancel")
//                            }
//                        }
//                    ) {
//                        DatePicker(state = datePickerState)
//                    }
//                }
//
//                Spacer(Modifier.height(20.dp))
//
//                GradientButton(
//                    text = if (isLoading) "Submitting..." else "Submit Request",
//                    enabled = isFormValid && !isLoading,
//                    onClick = {
//                        isLoading = true
//
////                        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@GradientButton
//                        val uid = FirebaseAuth.getInstance().currentUser?.uid
//                        if (uid == null) {
//                            isLoading = false
//                            return@GradientButton
//                        }
//
//                        val visitorData = details(
//                            requestId = requestId ?: "",
//                            uid = uid,
//                            name = name,
//                            phone = phone,
//                            purpose = purpose,
//                            personToMeet = persontomeet,
//                            visitDate = visitDate,
//                            status = "PENDING",
//                            timestamp = com.google.firebase.database.ServerValue.TIMESTAMP
//                        )
//
//                        val dbRef = FirebaseDatabase.getInstance()
//                            .getReference("visitorRequests")
//
//                        val task = if (isEdit && requestId != null) {
//                            // 🔁 UPDATE existing request
//                            dbRef.child(requestId!!).setValue(visitorData)
//                        } else {
//                            // 🆕 CREATE new request
//                            val newRequestId = dbRef.push().key
//                            if (newRequestId == null) {
//                                isLoading = false
//                                return@GradientButton
//                            }
//
//                            val newVisitorData = visitorData.copy(
//                                requestId = newRequestId
//                            )
//
//                            dbRef.child(newRequestId).setValue(newVisitorData)
//                        }
//
//                        task
//                            .addOnSuccessListener {
//                                isLoading = false
//
//                                Toast.makeText(
//                                    context,
//                                    if (isEdit) "Request updated successfully"
//                                    else "Request submitted successfully",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//
//                                navController.navigate("pending") {
//                                    popUpTo("register") { inclusive = true }
//                                }
//                            }
//                            .addOnFailureListener { e ->
//                                isLoading = false
//                                Log.e("FirebaseError", e.message ?: "Unknown error")
//                            }
//                    },
//
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 40.dp)
//                )
//            }
//        }
//    }
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
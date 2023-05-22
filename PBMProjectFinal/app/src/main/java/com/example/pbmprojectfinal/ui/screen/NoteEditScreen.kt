package com.example.pbmprojectfinal.ui.screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pbmprojectfinal.R
import com.example.pbmprojectfinal.ui.data.NoteViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

@Composable
fun NoteEditScreen(noteId: Int?, noteViewModel: NoteViewModel, onDone:() -> Unit) {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(noteId) {
        if (noteId != null) { noteViewModel.setNoteFromId(noteId) }
    }

    val noteUiState by noteViewModel.noteUiState.collectAsState()


    val isError = remember { mutableStateOf(false) }
    val isAdded = remember { mutableStateOf(false) }
    var maxChar = 20

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        // Note input field
        OutlinedTextField(
            value = noteUiState.title,
            onValueChange = { title ->
                if(title.length <= maxChar) {
                    noteViewModel.setTitle(title)
                }
            },
            singleLine = true,
            label = { Text(text = "Title") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedLabelColor = MaterialTheme.colors.onPrimary,
                unfocusedLabelColor = MaterialTheme.colors.onPrimary,
                cursorColor = MaterialTheme.colors.secondary,
                focusedBorderColor = MaterialTheme.colors.secondary,
                unfocusedBorderColor = MaterialTheme.colors.secondary
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Note input field
        OutlinedTextField(
            value = noteUiState.note,
            onValueChange = { note -> noteViewModel.setNote(note) },
            label = { Text(text = "Note") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedLabelColor = MaterialTheme.colors.onPrimary,
                unfocusedLabelColor = MaterialTheme.colors.onPrimary,
                cursorColor = MaterialTheme.colors.secondary,
                focusedBorderColor = MaterialTheme.colors.secondary,
                unfocusedBorderColor = MaterialTheme.colors.secondary
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                EditDate(noteUiState.deadlineDate) { date -> noteViewModel.setDate(date) }
            }

            Column {
                EditTime(noteUiState.deadlineTime) { time ->
                    val formattedTime = formatTime(time)
                    noteViewModel.setTime(formattedTime)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Save button
        Button(
            onClick = {
                coroutineScope.launch {
                    if (noteViewModel.isValid()) {
                        noteViewModel.upsertNote()
                        noteViewModel.resetValues()
                        isAdded.value = true
                    } else {
                        isError.value = true
                    }
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.surface,
                contentColor = MaterialTheme.colors.onSurface
            )
        ) {
            Text(text = stringResource(R.string.edit_note))
        }

        // Show the AlertDialog if there was an error
        if (isError.value) {
            AlertDialog(
                onDismissRequest = { isError.value = false }, // Reset the error state on dismiss
                title = { Text(text = stringResource(R.string.error_title)) },
                text = { Text(text = stringResource(R.string.please_fill_in_all_before_saving)) },
                confirmButton = {
                    Button(
                        onClick = { isError.value = false } // Reset the error state on button click
                    ) {
                        Text(text = stringResource(R.string.ok))
                    }
                }
            )
        }

        if (isAdded.value) {
            AlertDialog(
                onDismissRequest = onDone , // Reset the error state on dismiss
                title = { Text(text = stringResource(R.string.success)) },
                text = { Text(text = stringResource(R.string.notes_has_been_edited)) },
                confirmButton = {
                    Button(
                        onClick = onDone// Reset the error state on button click
                    ) {
                        Text(text = stringResource(R.string.ok))
                    }
                }
            )
        }
    }
}

@Suppress("NAME_SHADOWING")
@Composable
fun EditDate(selectedDate: String, onDateSelected: (String) -> Unit) {

    // Fetching the Local Context
    val mContext = LocalContext.current

    // Declaring integer values
    // for year, month and day
    val mYear: Int
    val mMonth: Int
    val mDay: Int

    // Initializing a Calendar
    val mCalendar = Calendar.getInstance()

    // Fetching current year, month and day
    mYear = mCalendar.get(Calendar.YEAR)
    mMonth = mCalendar.get(Calendar.MONTH)
    mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

    mCalendar.time = Date()

    // Declaring a string value to
    // store date in string format
    val mDate = remember { mutableStateOf(selectedDate) }

    // Declaring DatePickerDialog and setting
    // initial values as current values (present year, month and day)
    val mDatePickerDialog = DatePickerDialog(
        mContext,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            mDate.value = "$mDayOfMonth/${mMonth + 1}/$mYear"
            onDateSelected(mDate.value)
        },
        mYear,
        mMonth,
        mDay
    )

    // Creating a button that on
    // click displays/shows the DatePickerDialog
    Button(
        onClick = { mDatePickerDialog.show() },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.surface,
            contentColor = MaterialTheme.colors.onSurface
        )
    ) {
        Text(text = "Date")
    }

    // Displaying the mDate value in the Text
    Text(
        text = mDate.value.ifEmpty { stringResource(R.string.not_selected) },
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.onPrimary
    )

    // Update mDate value when the selectedDate value changes
    LaunchedEffect(selectedDate) {
        mDate.value = selectedDate
    }
}

@Composable
fun EditTime(selectedTime: String, onTimeSelected: (String) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Fetching current hour and minute
    val hour = calendar[Calendar.HOUR_OF_DAY]
    val minute = calendar[Calendar.MINUTE]

    var selectedTimeText by remember { mutableStateOf(selectedTime) }

    val timePicker = remember {
        TimePickerDialog(
            context,
            { _, selectedHour: Int, selectedMinute: Int ->
                selectedTimeText = "$selectedHour:$selectedMinute"
                onTimeSelected(selectedTimeText)
            }, hour, minute, false
        )
    }

    Button(
        onClick = {
            timePicker.show()
        },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.surface,
            contentColor = MaterialTheme.colors.onSurface
        )
    ) {
        Text(text = "Time")
    }

    // Displaying the selectedTimeText value in the Text
    Text(
        text = selectedTimeText.ifEmpty {
            stringResource(R.string.not_selected)
        },
        color = MaterialTheme.colors.onPrimary
    )

    // Update selectedTimeText when the selectedTime value changes
    LaunchedEffect(selectedTime) {
        selectedTimeText = selectedTime
    }
}

// Function to format the time with leading zeros
private fun formatTime(time: String): String {
    val parts = time.split(":")
    val hour = parts.getOrElse(0) { "0" }
    val minute = parts.getOrElse(1) { "0" }
    return "${hour.padStart(2, '0')}:${minute.padStart(2, '0')}"
}
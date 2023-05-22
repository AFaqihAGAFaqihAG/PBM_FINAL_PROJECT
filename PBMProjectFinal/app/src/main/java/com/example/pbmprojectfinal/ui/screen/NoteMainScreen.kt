package com.example.pbmprojectfinal.ui.screen

import android.widget.Space
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.pbmprojectfinal.R
import com.example.pbmprojectfinal.database.Notes
import com.example.pbmprojectfinal.ui.data.NoteViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Date

@Composable
fun NoteMainScreen(
    noteViewModel: NoteViewModel,
    onAddNoteClick:() -> Unit = {},
    onEditNoteClick:(Int) -> Unit = {}
) {
    val notes by noteViewModel.getAllNotes().collectAsState(emptyList())
    val coroutineScope = rememberCoroutineScope()
    val orderedByDateCreated = remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {

            if(notes.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        NoNotesFoundButton(onAddNoteClick = onAddNoteClick)
                    }
                }
            } else {
                itemsIndexed(notes) { index, note ->
                    ContactRow(
                        notes = note,
                        onDeleteNote = {
                            coroutineScope.launch {
                                noteViewModel.deleteNote(note)
                            }
                        },
                        onEditNoteClick = onEditNoteClick,
                        coroutineScope = coroutineScope,
                        noteViewModel = noteViewModel
                    )

                    if (index == notes.lastIndex) {
                        Spacer(modifier = Modifier.height(128.dp))
                    }
                }
            }
        }

        if (notes.isNotEmpty()) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                BottomBar(onAddNoteClick = onAddNoteClick)
            }
        }
    }
}

@Composable
fun NoNotesFoundButton(onAddNoteClick:() -> Unit = {}) {
    Button(
        onClick = onAddNoteClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.surface,
            contentColor = MaterialTheme.colors.onSurface
        )
    ) {
        Text("No notes found. Create one.")
    }
}

@Composable
fun ContactRow(
    notes: Notes,
    onDeleteNote: (Notes) -> Unit,
    onEditNoteClick: (Int) -> Unit,
    coroutineScope: CoroutineScope,
    noteViewModel: NoteViewModel
) {
    val isExpanded = remember { mutableStateOf(false) }
    val isNoteDone = remember { mutableStateOf(notes.isDone) }
    val alertDelete = remember { mutableStateOf(false)  }

    Spacer(modifier = Modifier
        .height(8.dp)
        .fillMaxWidth()
    )

    Card(
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp)
    ) {

        Column(
            modifier = Modifier
                .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
        ) {

            // title, edit dan delete button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = notes.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1F))

                IconButton(onClick = { onEditNoteClick(notes.id) }) {
                    Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit_note))
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = { alertDelete.value = true }) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete_note))
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // if true, expand/show the notes
            if (isExpanded.value) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = notes.note,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // done mark, deadline, and expand button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {

                val inputDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val outputDateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
                val date: Date? = if (notes.deadlineDate.isNotEmpty()) {
                    inputDateFormat.parse(notes.deadlineDate)
                } else {
                    null
                }
                val formattedDate = date?.let { outputDateFormat.format(it) } ?: "No Date"

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxHeight()
                ) {
                    Text(text = "Done?", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.width(8.dp))

                    Checkbox(
                        checked = isNoteDone.value,
                        onCheckedChange = { isChecked ->
                            isNoteDone.value = isChecked
                            coroutineScope.launch {
                                noteViewModel.updateIsDone(
                                    noteId = notes.id,
                                    isDone = isChecked
                                )
                            }
                        },
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = "Deadline: $formattedDate ${notes.deadlineTime}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxHeight()
                )

                IconButton(
                    onClick = {
                        isExpanded.value = !isExpanded.value
                    },
                    modifier = Modifier
                        .size(16.dp)
                        .fillMaxHeight()
                ) {
                    Icon(
                        imageVector = if (isExpanded.value) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded.value) "Collapse" else "Expand",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier
        .height(8.dp)
        .fillMaxWidth()
    )

    if (alertDelete.value) {
        DeleteNoteDialog(notes, onDeleteNote, alertDelete)
    }

}

@Composable
fun BottomBar(onAddNoteClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(MaterialTheme.colors.surface, shape = CircleShape)
        ) {
            IconButton(
                onClick = onAddNoteClick,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = MaterialTheme.colors.onSurface)
            }
        }
    }
}

@Composable
fun DeleteNoteDialog(notes: Notes, onDeleteNote: (Notes) -> Unit, alertDialog: MutableState<Boolean>) {
    AlertDialog(
        onDismissRequest = { alertDialog.value = false },
        title = { Text(text = stringResource(R.string.caution)) },
        text = { Text(text = stringResource(R.string.do_you_want_to_delete_the_note)) },
        confirmButton = {
            Button(
                onClick = {
                    onDeleteNote(notes)
                    alertDialog.value = false
                }
            ) {
                Text(text = stringResource(R.string.ok))
            }
        },
        dismissButton = {
            Button(
                onClick = { alertDialog.value = false }
            ) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    )
}


package com.example.pbmprojectfinal.ui.data

import com.example.pbmprojectfinal.database.Notes


data class NoteUiState(
    val id: Int = 0,
    val deadlineDate: String = "",
    val deadlineTime: String = "",
    val title: String = "",
    val note: String = ""
)

fun toNote(noteUiState: NoteUiState): Notes {
    return Notes(
        id = noteUiState.id,
        deadlineDate = noteUiState.deadlineDate,
        deadlineTime = noteUiState.deadlineTime,
        title = noteUiState.title,
        note = noteUiState.note
    )
}

fun toNoteWithoutId(noteUiState: NoteUiState): Notes {
    return Notes(
        deadlineDate = noteUiState.deadlineDate,
        deadlineTime = noteUiState.deadlineTime,
        title = noteUiState.title,
        note = noteUiState.note
    )
}
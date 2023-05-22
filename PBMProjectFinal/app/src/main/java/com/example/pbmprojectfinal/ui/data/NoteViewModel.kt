package com.example.pbmprojectfinal.ui.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.pbmprojectfinal.database.Notes
import com.example.pbmprojectfinal.database.NotesDao
import com.example.pbmprojectfinal.database.DatabaseApplication
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class NoteViewModel(private val notesDao: NotesDao): ViewModel() {

    private val _noteUiState = MutableStateFlow(NoteUiState())
    val noteUiState: StateFlow<NoteUiState> = _noteUiState.asStateFlow()

    fun isValid(): Boolean {
        return !(toNote(noteUiState.value).deadlineDate.isEmpty() ||
                toNote(noteUiState.value).deadlineTime.isEmpty() ||
                toNote(noteUiState.value).title.isEmpty() ||
                toNote(noteUiState.value).note.isEmpty())
    }

    fun resetValues() {
        _noteUiState.update { currentState->
            currentState.copy(deadlineDate = "", deadlineTime = "", title = "", note = "")
        }
    }

    fun setTitle(title: String) {
        _noteUiState.update { currentState->
            currentState.copy(title = title)
        }
    }

    fun setNote(note: String) {
        _noteUiState.update { currentState->
            currentState.copy(note = note)
        }
    }

    fun setDate(date: String) {
        _noteUiState.update { currentState->
            currentState.copy(deadlineDate = date)
        }
    }

    fun setTime(time: String) {
        _noteUiState.update { currentState->
            currentState.copy(deadlineTime = time)
        }
    }

    suspend fun setNoteFromId(noteId: Int) {
        val selectedNote: Notes? = selectFromId(noteId)
        if (selectedNote != null) {
            _noteUiState.update { currentState ->
                currentState.copy(
                    id = selectedNote.id,
                    deadlineDate = selectedNote.deadlineDate,
                    deadlineTime = selectedNote.deadlineTime,
                    title = selectedNote.title,
                    note = selectedNote.note
                )
            }
        }
        // If selectedNote is null, you might want to handle that case as well
        // For example, setting the state to a default value or showing an error message.
    }


    //Dao implementations
    suspend fun upsertNote() = notesDao.upsertNote(toNote(noteUiState.value))

    suspend fun upsertNoteWithoutId() = notesDao.upsertNote(toNoteWithoutId(noteUiState.value))

    fun getAllNotes(): Flow<List<Notes>> = notesDao.getAllNotes()

    suspend fun deleteNote(notes: Notes) = notesDao.deleteNote(notes)

    suspend fun updateIsDone(noteId: Int, isDone: Boolean) = notesDao.updateIsDone(noteId, isDone)

    private suspend fun selectFromId(noteId: Int): Notes? = notesDao.selectFromId(noteId)
    //End of Dao implementations

    companion object {
        val factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as DatabaseApplication)
                NoteViewModel(application.database.notesDao())
            }
        }
    }
}
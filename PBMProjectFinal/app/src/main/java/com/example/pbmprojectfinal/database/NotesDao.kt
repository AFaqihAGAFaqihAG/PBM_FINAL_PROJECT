package com.example.pbmprojectfinal.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {
    @Upsert
    suspend fun upsertNote(notes: Notes)

    @Query("SELECT * FROM Notes")
    fun getAllNotes(): Flow<List<Notes>>

    @Delete
    suspend fun deleteNote(notes: Notes)

    @Query("UPDATE notes SET isDone = :isDone WHERE id = :noteId")
    suspend fun updateIsDone(noteId: Int, isDone: Boolean)

    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun selectFromId(noteId: Int): Notes?
}
package com.example.pbmprojectfinal.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity
data class Notes(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val deadlineDate: String,
    val deadlineTime: String,
    val title: String,
    val note: String,
    var isDone: Boolean = false,
    val dateCreated: String = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())
)

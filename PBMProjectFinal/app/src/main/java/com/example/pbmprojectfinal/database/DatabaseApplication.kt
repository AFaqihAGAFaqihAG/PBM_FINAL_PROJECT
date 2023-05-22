package com.example.pbmprojectfinal.database

import android.app.Application

class DatabaseApplication: Application() {
    val database: NoteDatabase by lazy { NoteDatabase.getDatabase(this) }
}
package com.example.notes.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.database.DataBaseHelper
import com.example.notes.model.Note
import com.example.notes.view.NotesAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var list: List<Note>

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val db = DataBaseHelper(this)
        val rec = findViewById<RecyclerView>(R.id.recycler_view)
        list = db.getAllNotes()

        notesAdapter = NotesAdapter(this, list, true)
        rec.adapter = notesAdapter
        rec.layoutManager = LinearLayoutManager(this)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun newNote(view: View) {
        val intent = Intent(this, CreateNoteActivity::class.java)
        startActivity(intent)
    }

    fun openNotes(view: View) {
    }

    fun openTags(view: View) {
        val intent = Intent(this, TagActivity::class.java)
        startActivity(intent)
    }
}
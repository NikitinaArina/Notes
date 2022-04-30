package com.example.notes.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.database.DataBaseHelper
import com.example.notes.model.Note
import com.example.notes.model.Tag
import com.example.notes.view.NotesAdapter

@RequiresApi(Build.VERSION_CODES.P)
class CreateTagActivity : AppCompatActivity() {
    private lateinit var tagName: EditText
    private lateinit var notes: RecyclerView
    private lateinit var notesAdapter: NotesAdapter
    private val notesList: ArrayList<Note> = ArrayList()
    private var id = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_tag)
        val db = DataBaseHelper(this)
        tagName = findViewById(R.id.tagName)
        notes = findViewById(R.id.recycler_view_notes_tag)

        getIntentData()

        notesList.addAll(db.getAllNotes(id))

        notesAdapter = NotesAdapter(this, notesList, false, intent)
        notes.adapter = notesAdapter
        notes.layoutManager = LinearLayoutManager(this)
    }

    fun deleteTag(view: View) {
        val db = DataBaseHelper(this)

        db.deleteTag(id)
        newActivity()
    }

    fun doneButton(view: View) {
        val db = DataBaseHelper(this)

        if (tagName.text.isNotBlank()) {
            if (id == -1L) {
                db.addTag(tagName.text.toString())
                db.close()
            } else {
                val tag = updateTag(db.getTag(id))
                db.updateTag(tag)
                db.close()
            }
            newActivity()
        } else Toast.makeText(this, "Tag name can not be empty", Toast.LENGTH_SHORT).show()
    }

    fun backButton(view: View) {
        newActivity()
    }

    private fun updateTag(tag: Tag): Tag {
        val tag = tag
        tag.name = tagName.text.toString()

        return tag
    }

    private fun getIntentData() {
        if (intent.hasExtra(Tag.TAG_ID)) {
            id = intent.extras?.get(Tag.TAG_ID) as Long
            setIntentData()
        }
    }

    private fun setIntentData() {
        val db = DataBaseHelper(this)
        val tag: Tag = db.getTag(id)

        tagName.setText(tag.name)
    }

    private fun newActivity() {
        val intent = Intent(this, TagActivity::class.java)
        startActivity(intent)
    }
}
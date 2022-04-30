package com.example.notes.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.notes.R
import com.example.notes.database.DataBaseHelper
import com.example.notes.model.Note
import com.example.notes.model.Note.Companion.getTagsList
import com.example.notes.model.Tag
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.P)
class CreateNoteActivity : AppCompatActivity() {
    private lateinit var noteTitle: EditText
    private lateinit var noteDesk: EditText
    private lateinit var dateTime: TextView
    private lateinit var tags: TextView
    private lateinit var spinnerTags: Spinner
    private lateinit var tagsList: ArrayList<Tag>
    private var noteTagsList = HashSet<Tag>()
    private val SPINNER_ITEM = "Select tag(s)"
    private var flag = true
    private var id = -1
    private var tagId = -1L


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)
        noteDesk = findViewById(R.id.noteDesc)
        noteTitle = findViewById(R.id.NoteTitle)
        dateTime = findViewById(R.id.dateTime)
        spinnerTags = findViewById(R.id.spinnerTags)
        tags = findViewById(R.id.tags)

        val date = LocalDateTime.now()
        dateTime.text = "${date.dayOfMonth} ${date.dayOfWeek} ${date.year}"

        getIntentData()
        showDataSpinner()
        spinnerTags.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                noteTagsList = setTags(tagsList[p2])
                tags.clearComposingText()
                tags.text = getTagsList(noteTagsList)
                spinnerTags.setSelection(0)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
    }

    fun doneButton(view: View) {
        val db = DataBaseHelper(this)

        if (noteTitle.text.isNotBlank() && noteDesk.text.isNotBlank()) {
            if (id == -1) {
                db.addNote(noteDesk.text.toString(), noteTitle.text.toString(), noteTagsList)
                db.close()
            } else {
                val note = updateNote(db.getNote(id.toLong()))
                db.updateNote(note)
                db.close()
            }

            newActivity()
        } else Toast.makeText(this, "Fields can not be empty", Toast.LENGTH_SHORT).show()
    }

    fun backButton(view: View) {
        newActivity()
    }

    fun deleteNote(view: View) {
        val db = DataBaseHelper(this)

        db.deleteNote(id.toLong())
        newActivity()
    }

    private fun newActivity() {
        val intent: Intent
        if (flag) {
            intent = Intent(this, MainActivity::class.java)
        } else {
            intent = Intent(this, CreateTagActivity::class.java)
            intent.putExtra(Tag.TAG_ID, tagId)
        }
        startActivity(intent)
    }

    private fun getIntentData() {
        if (intent.hasExtra(Note.COLUMN_ID)) {
            id = intent.extras?.get(Note.COLUMN_ID) as Int
            if(intent.hasExtra(Tag.TAG_ID)){
                tagId = intent.extras?.get(Tag.TAG_ID) as Long
            }
            flag = intent.extras?.get("flag") as Boolean
            setIntentData()
        }
    }

    private fun setIntentData() {
        val db = DataBaseHelper(this)
        val note: Note = db.getNote(id.toLong())

        noteTitle.setText(note.title)
        noteDesk.setText(note.note)
        if (note.tagsList!!.isNotEmpty()) {
            noteTagsList = note.tagsList!!
            tags.text = getTagsList(noteTagsList)
            tags.visibility = View.VISIBLE
        }
    }

    private fun updateNote(note: Note): Note {
        val note = note
        note.note = noteDesk.text.toString()
        note.title = noteTitle.text.toString()
        note.tagsList = noteTagsList

        return note
    }

    private fun showDataSpinner() {
        val db = DataBaseHelper(this)
        tagsList = db.getAllTags() as ArrayList<Tag>
        tagsList.add(0, Tag(0, SPINNER_ITEM))

        val arrayAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, tagsList)
        spinnerTags.adapter = arrayAdapter
    }

    private fun setTags(tag: Tag): HashSet<Tag> {
        if (tag.name.isNotBlank() && !tag.name.contentEquals(SPINNER_ITEM)) {
            val checkTag = noteTagsList.removeIf { x -> x.name.contentEquals(tag.name) }

            if (!checkTag) noteTagsList.add(tag)

            if (noteTagsList.isEmpty()) {
                tags.visibility = View.GONE
            } else tags.visibility = View.VISIBLE
        }
        return noteTagsList
    }
}
package com.example.notes.view

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.activity.CreateNoteActivity
import com.example.notes.model.Note
import com.example.notes.model.Note.Companion.getTagsList
import com.example.notes.model.Tag
import java.time.format.DateTimeFormatter

class NotesAdapter(
    private val c: Context,
    private val noteList: List<Note>,
    private val flag: Boolean,
    private val oldIntent: Intent?
) :
    RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {
    constructor(c: Context, noteList: List<Note>, flag: Boolean) : this(
        c,
        noteList,
        flag,
        Intent()
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.item_notes, parent, false)
        return NotesViewHolder(v)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val newList = noteList[position]
        holder.title.text = newList.title
        holder.desc.text = newList.note
        val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        holder.time.text = newList.timestamp.format(dateTimeFormatter).toString()
        holder.tags.text = getTagsList(newList.tagsList)
        holder.layout.setOnClickListener {
            val intent = Intent(c, CreateNoteActivity::class.java)
            intent.putExtra(Note.COLUMN_ID, newList.id)
            intent.putExtra("flag", flag)
            if (oldIntent != null && oldIntent.hasExtra(Tag.TAG_ID)) {
                val tagId = oldIntent?.extras?.get(Tag.TAG_ID) as Long
                intent.putExtra(Tag.TAG_ID, tagId)
            }
            c.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return noteList.size
    }

    class NotesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val desc: TextView = view.findViewById(R.id.desc)
        val time: TextView = view.findViewById(R.id.iDateTime)
        val tags: TextView = view.findViewById(R.id.tags)
        val layout: ConstraintLayout = view.findViewById(R.id.layoutNote)
    }
}

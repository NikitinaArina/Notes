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
import com.example.notes.model.Tag
import com.example.notes.view.NotesAdapter
import com.example.notes.view.TagsAdapter

class TagActivity : AppCompatActivity() {
    private lateinit var tagsAdapter: TagsAdapter
    private lateinit var list: List<Tag>

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tag)
        val db = DataBaseHelper(this)
        val rec = findViewById<RecyclerView>(R.id.recycler_view_tag)
        list = db.getAllTags()

        tagsAdapter = TagsAdapter(this, list)
        rec.adapter = tagsAdapter
        rec.layoutManager = LinearLayoutManager(this)
    }

    fun openNotesT(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun openTagsT(view: View) {
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun newTag(view: View) {
        val intent = Intent(this, CreateTagActivity::class.java)
        startActivity(intent)
    }
}
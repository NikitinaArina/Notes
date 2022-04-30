package com.example.notes.view

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.R
import com.example.notes.activity.CreateTagActivity
import com.example.notes.model.Tag

class TagsAdapter(private val c: Context, private val tagList: List<Tag>) :
    RecyclerView.Adapter<TagsAdapter.TagViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.item_tags, parent, false)
        return TagViewHolder(v)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val newList = tagList[position]
        holder.tag.text = newList.name
        holder.layout.setOnClickListener {
            val intent = Intent(c, CreateTagActivity::class.java)
            intent.putExtra(Tag.TAG_ID, newList.id)
            c.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return tagList.size
    }

    class TagViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tag: TextView = view.findViewById(R.id.tag)
        val layout: LinearLayout = view.findViewById(R.id.layoutTag)
    }
}

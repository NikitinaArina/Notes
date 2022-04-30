package com.example.notes.model

import java.time.LocalDateTime
import java.util.*
import kotlin.collections.HashSet

class Note(var id: Int, var title: String?, var note: String?, var timestamp: LocalDateTime, var tagsList: HashSet<Tag>?) {
    constructor(id: Int, title: String?, note: String?, timestamp: LocalDateTime) : this(id, title,note, timestamp, HashSet<Tag>())

    companion object {
        const val TABLE_NAME = "notes"
        const val TABLE_NOTE_TAG = "note_tag"
        const val COLUMN_ID = "id"
        const val COLUMN_NOTE = "note"
        const val COLUMN_TITLE = "title"
        const val COLUMN_TIMESTAMP = "timestamp"
        const val COLUMN_TAGS = "tags"
        const val COLUMN_ID_NOTE = "idNote"

        const val CREATE_TABLE =
            ("CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_TITLE TEXT, $COLUMN_NOTE TEXT, $COLUMN_TIMESTAMP DATETIME DEFAULT CURRENT_TIMESTAMP, $COLUMN_TAGS TEXT)")
        const val CREATE_NOTE_TAG_TABLE = (
            "CREATE TABLE $TABLE_NOTE_TAG ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_ID_NOTE INTEGER, ${Tag.COLUMN_TAG_ID} INTEGER, FOREIGN KEY ($COLUMN_ID_NOTE) REFERENCES $TABLE_NAME($COLUMN_ID) FOREIGN KEY (${Tag.COLUMN_TAG_ID}) REFERENCES ${Tag.TABLE_NAME}($COLUMN_ID))"
        )
        const val DELETE_TABLE = "DROP TABLE IF EXISTS "

        fun getTagsList(tagsList: HashSet<Tag>?): String{
            val tags: StringJoiner = StringJoiner(", ")
            tagsList?.forEach { x -> tags.add(x.name) }
            return tags.toString()
        }
    }
}
package com.example.notes.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.OpenParams
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.notes.model.Note
import com.example.notes.model.Tag
import com.example.notes.model.Tag.Companion.GET_NOTES_BY_TAG_ID
import com.example.notes.model.Tag.Companion.GET_NOTE_TAGS
import java.lang.String.valueOf
import java.time.LocalDateTime


@RequiresApi(Build.VERSION_CODES.P)
class DataBaseHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, DATABASE_VERSION, OpenParams.Builder().build()) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(Note.CREATE_TABLE)
        db?.execSQL(Tag.CREATE_TABLE)
        db?.execSQL(Note.CREATE_NOTE_TAG_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db!!.execSQL(Note.DELETE_TABLE + Note.TABLE_NAME)
        with(db) {
            execSQL(Note.DELETE_TABLE + Tag.TABLE_NAME)
            execSQL(Note.DELETE_TABLE + Note.TABLE_NOTE_TAG)
            onCreate(this)
        }
    }

    fun addNote(note: String, title: String, tags: HashSet<Tag>): Boolean {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(Note.COLUMN_TITLE, title)
        values.put(Note.COLUMN_NOTE, note)
        values.put(Note.COLUMN_TIMESTAMP, LocalDateTime.now().toString())

        val id = db.insert(Note.TABLE_NAME, null, values)
        db.close()

        for (tag in tags) {
            addNoteTag(id, tag)
        }

        return id != -1L
    }

    fun deleteNote(id: Long): Boolean {
        val db = this.writableDatabase

        val idN = db.delete(Note.TABLE_NAME, "${Note.COLUMN_ID}=?", arrayOf(valueOf(id)))
        db.close()

        return idN != -1
    }

    fun updateNote(note: Note): Boolean {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(Note.COLUMN_TITLE, note.title)
        values.put(Note.COLUMN_NOTE, note.note)
        values.put(Note.COLUMN_TIMESTAMP, LocalDateTime.now().toString())

        val id =
            db.update(Note.TABLE_NAME, values, "${Note.COLUMN_ID}=?", arrayOf(valueOf(note.id)))

        val cursor: Cursor = getNoteTags(db, note.id.toLong())
        val tagsFromCursor = getTagsFromCursor(cursor)

        tagsFromCursor.forEach { x -> deleteNoteTag(note.id.toLong(), x) }

        for (tag in note.tagsList!!) {
            addNoteTag(note.id.toLong(), tag)
        }

        return id != -1
    }

    @SuppressLint("Range", "Recycle")
    fun getNote(id: Long): Note {
        val db = this.readableDatabase

        var cursor: Cursor? = db.query(
            Note.TABLE_NAME,
            arrayOf(Note.COLUMN_ID, Note.COLUMN_TITLE, Note.COLUMN_NOTE, Note.COLUMN_TIMESTAMP),
            "${Note.COLUMN_ID}=?",
            arrayOf(valueOf(id)),
            null,
            null,
            null,
            null
        )
        cursor?.moveToFirst()

        val note = Note(
            cursor!!.getInt(cursor.getColumnIndex(Note.COLUMN_ID)),
            cursor.getString(cursor.getColumnIndex(Note.COLUMN_TITLE)),
            cursor.getString(cursor.getColumnIndex(Note.COLUMN_NOTE)),
            LocalDateTime.parse(cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP))),
        )

        cursor = getNoteTags(db, id)
        note.tagsList = getTagsFromCursor(cursor)

        db.close()
        cursor.close()

        return note
    }

    @SuppressLint("Range")
    fun getAllNotes(): List<Note> {
        val noteList: ArrayList<Note> = ArrayList()
        val selectQuery = "SELECT * FROM ${Note.TABLE_NAME} ORDER BY ${Note.COLUMN_TIMESTAMP} DESC"
        val db = this.readableDatabase

        val cursor: Cursor?
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        noteList.addAll(getNotesFromCursor(db, cursor))

        db.close()
        cursor.close()

        return noteList
    }

    @SuppressLint("Range")
    fun getTag(id: Long): Tag {
        val db = this.readableDatabase

        val cursor: Cursor? = db.query(
            Tag.TABLE_NAME,
            arrayOf(Tag.COLUMN_ID, Tag.COLUMN_TAG),
            "${Tag.COLUMN_ID}=?",
            arrayOf(valueOf(id)),
            null,
            null,
            null,
            null
        )
        cursor?.moveToFirst()

        val tag = Tag(
            cursor!!.getLong(cursor.getColumnIndex(Tag.COLUMN_ID)),
            cursor.getString(cursor.getColumnIndex(Tag.COLUMN_TAG)),
        )

        cursor.close()

        return tag
    }

    @SuppressLint("Range")
    fun getAllTags(): List<Tag> {
        val tagList: ArrayList<Tag> = ArrayList()
        val selectQuery = "SELECT * FROM ${Tag.TABLE_NAME}"
        val db = this.readableDatabase

        val cursor: Cursor?
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        if (cursor != null) while (cursor.moveToNext()) {
            val tag = Tag(
                cursor.getLong(cursor.getColumnIndex(Tag.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Tag.COLUMN_TAG)),
            )
            tagList.add(tag)
        }

        db.close()
        cursor.close()

        return tagList
    }

    fun getAllNotes(tagId: Long): ArrayList<Note> {
        val db = this.readableDatabase
        val notes: List<Note?>

        val cursor: Cursor = db.rawQuery(GET_NOTES_BY_TAG_ID, arrayOf(tagId.toString()))
        notes = getNotesFromCursor(db, cursor)
        cursor.close()
        return notes
    }

    fun addTag(tag: String): Boolean {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(Tag.COLUMN_TAG, tag)

        val id = db.insert(Tag.TABLE_NAME, null, values)
        db.close()

        return id != -1L
    }

    fun deleteTag(id: Long): Boolean {
        val db = this.writableDatabase

        val idT = db.delete(Tag.TABLE_NAME, "${Tag.COLUMN_ID}=?", arrayOf(valueOf(id)))
        db.close()

        return idT != -1
    }

    fun updateTag(tag: Tag): Boolean {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(Tag.COLUMN_TAG, tag.name)

        val id =
            db.update(Tag.TABLE_NAME, values, "${Tag.COLUMN_ID}=?", arrayOf(valueOf(tag.id)))
        db.close()

        return id != -1
    }

    private fun addNoteTag(idNote: Long, tag: Tag): Long {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(Note.COLUMN_ID_NOTE, idNote)
        values.put(Tag.COLUMN_TAG_ID, tag.id)

        return db.insert(Note.TABLE_NOTE_TAG, null, values)
    }

    @SuppressLint("Range", "Recycle")
    private fun deleteNoteTag(idNote: Long, tag: Tag): Boolean {
        val db = this.writableDatabase

        val cursor: Cursor? = db.query(
            Note.TABLE_NOTE_TAG,
            arrayOf(Note.COLUMN_ID),
            "${Note.COLUMN_ID_NOTE}=${idNote} AND ${Tag.COLUMN_TAG_ID}=${tag.id}",
            null,
            null,
            null,
            null,
            null
        )

        cursor?.moveToFirst()
        val id = cursor?.getInt(cursor.getColumnIndex(Note.COLUMN_ID))

        val idNT = db.delete(
            Note.TABLE_NOTE_TAG, "${Note.COLUMN_ID}=?", arrayOf(valueOf(id))
        )
        db.close()

        return idNT == -1
    }

    @SuppressLint("Range")
    private fun getNotesFromCursor(db: SQLiteDatabase, cursor: Cursor): ArrayList<Note> {
        val noteList = ArrayList<Note>()
        while (cursor.moveToNext()) {
            val note = Note(
                cursor.getInt(cursor.getColumnIndex(Note.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_TITLE)),
                cursor.getString(cursor.getColumnIndex(Note.COLUMN_NOTE)),
                LocalDateTime.parse(cursor.getString(cursor.getColumnIndex(Note.COLUMN_TIMESTAMP)))
            )

            val cursorForTags: Cursor = db.rawQuery(GET_NOTE_TAGS, arrayOf(note.id.toString()))
            note.tagsList = getTagsFromCursor(cursorForTags)
            noteList.add(note)
        }
        return noteList
    }

    @SuppressLint("Range")
    private fun getTagsFromCursor(cursor: Cursor): HashSet<Tag> {
        val tags = HashSet<Tag>()
        if (cursor.moveToFirst()) {
            do {
                val idTag = cursor.getLong(cursor.getColumnIndex(Tag.COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndex(Tag.COLUMN_TAG))
                tags.add(Tag(idTag, name))
            } while (cursor.moveToNext())
        }
        return tags
    }

    private fun getNoteTags(
        db: SQLiteDatabase,
        id: Long
    ) = db.rawQuery(
        GET_NOTE_TAGS,
        arrayOf(
            valueOf(id)
        )
    )

    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "NotesDB"
    }
}
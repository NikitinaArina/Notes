package com.example.notes.model

class Tag(
    var id: Long,
    var name: String
) {
    companion object {
        const val TABLE_NAME = "tags"
        const val COLUMN_ID = "id"
        const val TAG_ID = "tagID"
        const val COLUMN_TAG = "tag"
        const val COLUMN_TAG_ID = "idTag"

        const val CREATE_TABLE =
            ("CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_TAG TEXT)")
        const val GET_NOTE_TAGS =
            "SELECT $TABLE_NAME.$COLUMN_ID,$TABLE_NAME.$COLUMN_TAG FROM ${Note.TABLE_NOTE_TAG} INNER JOIN $TABLE_NAME ON ${Note.TABLE_NOTE_TAG}.$COLUMN_TAG_ID = $TABLE_NAME.$COLUMN_ID WHERE ${Note.TABLE_NOTE_TAG}.${Note.COLUMN_ID_NOTE} = ?"
        const val GET_BY_ID =
            "SELECT ${Note.COLUMN_ID} FROM ${Note.TABLE_NOTE_TAG} WHERE ${Note.COLUMN_ID_NOTE}=? AND $COLUMN_TAG_ID=?"
        const val GET_NOTES_BY_TAG_ID =
            "SELECT ${Note.TABLE_NAME}.${Note.COLUMN_ID}, ${Note.TABLE_NAME}.${Note.COLUMN_TITLE}, ${Note.TABLE_NAME}.${Note.COLUMN_TIMESTAMP}, ${Note.TABLE_NAME}.${Note.COLUMN_NOTE} FROM ${Note.TABLE_NAME} INNER JOIN ${Note.TABLE_NOTE_TAG} ON ${Note.TABLE_NAME}.${Note.COLUMN_ID} = ${Note.TABLE_NOTE_TAG}.${Note.COLUMN_ID_NOTE} WHERE ${Note.TABLE_NOTE_TAG}.$COLUMN_TAG_ID = ? ORDER BY ${Note.COLUMN_TIMESTAMP} DESC"
    }

    override fun toString(): String {
        return name
    }
}
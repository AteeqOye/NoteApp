package com.example.notesapp.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Notes")
class Notes : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

    @ColumnInfo(name = "title")
    var title: String? = null

    @ColumnInfo(name = "note_text")
    var noteText: String? = null

    @ColumnInfo(name = "img_path")
    var imgPath: String? = null

    @ColumnInfo(name = "web_link")
    var webLink: String? = null

    @ColumnInfo(name = "color")
    var color: String? = null

    @ColumnInfo(name = "is_bold")
    var isBold: Boolean = false

    @ColumnInfo(name = "is_italic")
    var isItalic: Boolean = false

    @ColumnInfo(name = "is_underlined")
    var isUnderlined: Boolean = false
}

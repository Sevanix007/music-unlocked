package com.example.musicunlocked.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.PrimaryKey

@Entity(
    tableName = "Track",
//
//    foreignKeys = [
//        ForeignKey(
//            entity = User::class,
//            parentColumns = ["userId"],
//            childColumns = ["userId"]
//        )
//    ]
)
data class Track(
    @PrimaryKey(autoGenerate = true)
    val trackId: Int = 0,

    @ColumnInfo(name = "trackName")
    val trackName: String,

    @ColumnInfo(name = "trackAuthor")
    val trackAuthor: String,

    @ColumnInfo(name = "trackDuration")
    val trackDuration: Long,

    @ColumnInfo(name = "trackListeners")
    val trackListeners: Int,

    @ColumnInfo(name = "trackLikes")
    val trackLikes: Int,

    @ColumnInfo(name = "trackLink")
    val trackLink: String
)



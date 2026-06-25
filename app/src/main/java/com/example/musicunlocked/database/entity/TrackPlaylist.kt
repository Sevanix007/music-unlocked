
package com.example.musicunlocked.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "TrackPlaylist",

    foreignKeys = [
        ForeignKey(
            entity = Track::class,
            parentColumns = ["trackId"],
            childColumns = ["trackId"]
        ),
                ForeignKey(
            entity = Playlist::class,
            parentColumns = ["playlistId"],
            childColumns = ["playlistId"]
        )
    ]
)
data class TrackPlaylist(

    @PrimaryKey(autoGenerate = true)
    val trackPlaylistId: Int = 0,

    @ColumnInfo(name = "playlistId")
    val playlistId: Int,

    @ColumnInfo(name = "trackId")
    val trackId: Int
)



//package com.example.musicunlocked.database.entity
//
//import androidx.room.Entity
//import androidx.room.PrimaryKey
//import androidx.room.ColumnInfo
//import androidx.room.ForeignKey
//
//@Entity(
//    tableName = "TrackPlaylist",
//    foreignKeys = [
//        ForeignKey(
//            entity = Track::class,
//            parentColumns = ["trackId"],
//            childColumns = ["trackId"],
//            onDelete = ForeignKey.CASCADE
//        ),
//        ForeignKey(
//            entity = Playlist::class,
//            parentColumns = ["playlistId"],
//            childColumns = ["playlistId"],
//            onDelete = ForeignKey.CASCADE
//        )
//    ]
//)
//data class TrackPlaylist(
//
//    @PrimaryKey(autoGenerate = true)
//    val trackPlaylistId: Int = 0,
//
//    @ColumnInfo(name = "playlistId")
//    val playlistId: Int,
//
//    @ColumnInfo(name = "trackId")
//    val trackId: Int
//) {
//    init {
//        require(playlistId > 0) {
//            "Playlist ID must be positive"
//        }
//        require(trackId > 0) {
//            "Track ID must be positive"
//        }
//    }
//}
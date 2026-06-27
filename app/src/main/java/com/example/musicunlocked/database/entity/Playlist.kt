//package com.example.musicunlocked.database.entity
//
//import androidx.room.Entity
//import androidx.room.PrimaryKey
//import androidx.room.ColumnInfo
//import androidx.room.ForeignKey
//
//
//@Entity(
//    tableName = "Playlist",
//    foreignKeys = [
//        ForeignKey(
//            entity = User::class,
//            parentColumns = ["userId"],
//            childColumns = ["userId"],
//            onDelete = ForeignKey.CASCADE
//        )
//    ]
//)
//data class Playlist(
//
//    @PrimaryKey(autoGenerate = true)
//    val playlistId: Int = 0,
//
//    @ColumnInfo(name = "playlistName")
//    val playlistName: String,
//
//    @ColumnInfo(name = "createdAt")
//    val createdAt: Long,
//
//    @ColumnInfo(name = "userId")
//    val userId: Int
//) {
//    init {
//        require(playlistName.length in 1..20) {
//            "Playlist name must be between 1 and 20 characters"
//        }
//        require(userId > 0) {
//            "User ID must be positive"
//        }
//    }
//}
//
//
package com.example.musicunlocked.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Playlist",

    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"]
        )
    ]
)
data class Playlist(

    @PrimaryKey(autoGenerate = true)
    val playlistId: Int = 0,

    @ColumnInfo(name = "playlistName")
    val playlistName: String,

    @ColumnInfo(name = "createdAt")
    val createdAt: Long,

    @ColumnInfo(name = "userId")
    val userId: Int,

    @ColumnInfo(name = "isSystem", defaultValue = "0") // добавляем новое поле
    val isSystem: Boolean = false // по умолчанию false


){
init{
    require(playlistName.length in 1..20) {
            "Playlist name must be between 1 and 20 characters"
        }
}}
//) {
//    init {
//        require(username.length in 3..20) {
//            "Username must be between 3 and 20 characters"
//        }
//        require(email.length in 5..100) {
//            "Email must be between 5 and 100 characters"
//        }
//        require(email.contains('@')) {
//            "Email must contain @"
//        }
//    }
//}
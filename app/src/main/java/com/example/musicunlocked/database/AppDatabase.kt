package com.example.musicunlocked.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.musicunlocked.database.dao.PlaylistDao
import com.example.musicunlocked.database.dao.UserDao
import com.example.musicunlocked.database.entity.User
import com.example.musicunlocked.database.entity.Track
import com.example.musicunlocked.database.entity.TrackPlaylist
import com.example.musicunlocked.database.entity.Playlist
import com.example.musicunlocked.database.dao.TrackPlaylistDao
import com.example.musicunlocked.database.dao.TrackDao
import kotlin.reflect.KClass


@Database(
    entities = [User::class, Playlist::class,Track::class, TrackPlaylist::class,] ,
    version = 6
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun TrackDao(): TrackDao
    abstract fun PlaylistDao(): PlaylistDao
    abstract fun TrackPlaylistDao(): TrackPlaylistDao

}


//package com.example.musicunlocked.database
//
//import androidx.room.Database
//import androidx.room.RoomDatabase
//import com.example.musicunlocked.database.dao.*
//import com.example.musicunlocked.database.entity.*
//
//@Database(
//    entities = [User::class, Track::class, Playlist::class, TrackPlaylist::class],
//    version = 1
//)
//abstract class AppDatabase : RoomDatabase() {
//
//    abstract fun userDao(): UserDao
//    abstract fun trackDao(): TrackDao
//    abstract fun playlistDao(): PlaylistDao
//    abstract fun trackPlaylistDao(): TrackPlaylistDao
//}
package com.example.musicunlocked.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.musicunlocked.database.entity.Playlist

@Dao
interface PlaylistDao {

    @Insert
    suspend fun insert(playlist: Playlist)

    @Query("SELECT * FROM Playlist")
    suspend fun getAllPlaylists(): List<Playlist>

    @Query("SELECT * FROM Playlist WHERE userId = :userId")
    suspend fun getPlaylistsByUser(userId: Int): List<Playlist>

    @Query("SELECT * FROM Playlist WHERE userId = :userId AND playlistName = :name AND isSystem = 1 LIMIT 1")
    suspend fun getSystemPlaylistByName(userId: Int, name: String): Playlist?
}
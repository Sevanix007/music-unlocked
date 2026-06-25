package com.example.musicunlocked.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.musicunlocked.database.entity.TrackPlaylist

@Dao
interface TrackPlaylistDao {

    @Insert
    suspend fun insert(trackPlaylist: TrackPlaylist)

    @Query("SELECT * FROM TrackPlaylist")
    suspend fun getAllTrackPlaylists(): List<TrackPlaylist>

    @Query("SELECT * FROM TrackPlaylist WHERE playlistId = :playlistId")
    suspend fun getTracksInPlaylist(playlistId: Int): List<TrackPlaylist>
}
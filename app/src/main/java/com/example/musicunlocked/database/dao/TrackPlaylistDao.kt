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

    @Query("SELECT EXISTS(SELECT 1 FROM TrackPlaylist WHERE playlistId = :playlistId AND trackId = :trackId)")
    suspend fun isTrackInPlaylist(playlistId: Int, trackId: Int): Boolean

    @Query("DELETE FROM TrackPlaylist WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun deleteTrackFromPlaylist(playlistId: Int, trackId: Int)
}
package com.example.musicunlocked.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.musicunlocked.database.entity.Track

@Dao
interface TrackDao {

    @Insert
    suspend fun insert(track: Track)

    @androidx.room.Update
    suspend fun update(track: Track)

    @Query("SELECT * FROM Track")
    suspend fun getAllTracks(): List<Track>

    @Query("SELECT * FROM Track WHERE trackId = :trackId")
    suspend fun getTrackById(trackId: Int): Track?


    @Query("DELETE FROM Track WHERE trackId = :trackId")
    suspend fun deleteById(trackId: Long)


    @Query("UPDATE Track SET trackName = :newName WHERE trackId = :trackId")
    suspend fun updateTrackNameById(trackId: Int, newName: String)

    // Функция для обновления только trackAuthor по ID
    @Query("UPDATE Track SET trackAuthor = :newAuthor WHERE trackId = :trackId")
    suspend fun updateTrackAuthorById(trackId: Int, newAuthor: String)

    // Функция для обновления только trackLink по ID
    @Query("UPDATE Track SET trackLink = :newLink WHERE trackId = :trackId")
    suspend fun updateTrackLinkById(trackId: Int, newLink: String)

    @Query("SELECT Track.* FROM Track INNER JOIN TrackPlaylist ON Track.trackId = TrackPlaylist.trackId WHERE TrackPlaylist.playlistId = :playlistId")
    suspend fun getTracksByPlaylistId(playlistId: Int): List<Track>

    @Query("""
    INSERT INTO Track (trackName, trackAuthor, trackDuration, trackListeners, trackLikes, trackLink)
    VALUES (:trackName, :trackAuthor, 0, 0, 0, :trackLink)
""")
    suspend fun insertTestTrack(trackName: String, trackAuthor: String, trackLink: String)

}
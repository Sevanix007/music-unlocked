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

}
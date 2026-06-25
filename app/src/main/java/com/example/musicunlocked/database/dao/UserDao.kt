package com.example.musicunlocked.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.musicunlocked.database.entity.User

@Dao
interface UserDao {

    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM User")
    suspend fun getAllUsers(): List<User>


    @Query("SELECT * FROM User WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM User WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?


    @Query("SELECT email FROM User WHERE userId = :id")
    suspend fun getEmailById(id: Int): String?

    @Query("SELECT * FROM User WHERE Pass = :password LIMIT 1")
    suspend fun getUserByPassword(password: String): User?

//Update
    @Query("SELECT * FROM User WHERE userId = :id LIMIT 1")
    suspend fun getUserById(id: Int): User?

    @androidx.room.Update
    suspend fun updateUser(user: User)
}
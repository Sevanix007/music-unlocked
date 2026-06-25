//package com.example.musicunlocked.database.entity
//
//import androidx.room.Entity
//import androidx.room.PrimaryKey
//import androidx.room.ColumnInfo
//
//@Entity(tableName = "User")
//data class User(
//
//    @PrimaryKey(autoGenerate = true)
//    val userId: Int = 0,
//
//    @ColumnInfo(name = "username")
//    val username: String,
//
//    @ColumnInfo(name = "email")
//    val email: String,
//
//
//    @ColumnInfo(name = "Pass")
//    val password: String
//
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


package com.example.musicunlocked.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "User",
    indices = [
        Index(value = ["username"], unique = true),
        Index(value = ["email"], unique = true)
    ]
)
data class User(

    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,

    @ColumnInfo(name = "username")
    val username: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "Pass")
    val password: String

) {
    init {
        require(username.length in 3..20) {
            "Username must be between 3 and 20 characters, latin"
        }

        require(email.length in 5..100) {
            "Email must be between 5 and 100 characters"
        }

        require(password.length in 5..100) {
            "Password must be between 5 and 100 characters"
        }

        require(email.contains('@')) {
            "Email must contain @"
        }
    }
}
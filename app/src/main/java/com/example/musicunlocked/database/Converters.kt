//package com.example.musicunlocked.database
//
//import androidx.room.TypeConverter
//import java.time.LocalTime
//import java.time.LocalDateTime
//import java.time.format.DateTimeFormatter
//
//class Converters {
//    private val timeFormatter = DateTimeFormatter.ISO_LOCAL_TIME
//    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
//
//    @TypeConverter
//    fun fromLocalTime(value: LocalTime?): String? {
//        return value?.format(timeFormatter)
//    }
//
//    @TypeConverter
//    fun toLocalTime(value: String?): LocalTime? {
//        return value?.let { LocalTime.parse(it, timeFormatter) }
//    }
//
//    @TypeConverter
//    fun fromLocalDateTime(value: LocalDateTime?): String? {
//        return value?.format(dateTimeFormatter)
//    }
//
//    @TypeConverter
//    fun toLocalDateTime(value: String?): LocalDateTime? {
//        return value?.let { LocalDateTime.parse(it, dateTimeFormatter) }
//    }
//}

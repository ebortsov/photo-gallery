package com.github.ebortsov.photogallery.data.database

import androidx.room.TypeConverter
import java.time.Instant

class Converters {
    @TypeConverter
    fun toInstant(seconds: Long): Instant {
        return Instant.ofEpochSecond(seconds)
    }

    @TypeConverter
    fun fromInstant(instant: Instant): Long {
        return instant.epochSecond
    }
}
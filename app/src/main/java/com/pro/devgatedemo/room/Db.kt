package com.pro.devgatedemo.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pro.devgatedemo.models.Image
import com.pro.devgatedemo.models.User

@Database(entities = [User::class, Image::class], version = 1, exportSchema = false)
abstract class Db : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun imageDao(): ImageDao

    companion object {
        private var INSTANCE: Db? = null
        fun getDatabase(context: Context): Db {
            if (INSTANCE == null) {
                INSTANCE =
                    Room.databaseBuilder(context.applicationContext, Db::class.java, "database")
                        .build()
            }
            return INSTANCE!!
        }
    }
}
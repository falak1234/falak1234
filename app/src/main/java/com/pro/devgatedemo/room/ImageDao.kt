package com.pro.devgatedemo.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pro.devgatedemo.models.Image

@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addImage(image: Image)

    @Query("select * from images order by id asc")
    fun readAllImage(): LiveData<MutableList<Image>>

    @Query("Delete From images where name=:name")
    suspend fun deleteImage(name: String)

    @Query("SELECT EXISTS(SELECT * FROM images WHERE encryptionPassword = :password and name =:name)")
    suspend fun isCorrectPassword(password: String, name: String): Boolean?
}
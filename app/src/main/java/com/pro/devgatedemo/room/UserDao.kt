package com.pro.devgatedemo.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pro.devgatedemo.models.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(user: User)

    @Query("select * from user_table order by id asc")
    fun readAllData(): LiveData<MutableList<User>>

    @Query("SELECT EXISTS(SELECT * FROM user_table WHERE password = :password)")
    suspend fun isExist(password: String): Boolean?
}
package com.pro.devgatedemo.repository

import androidx.lifecycle.LiveData
import com.pro.devgatedemo.models.Image
import com.pro.devgatedemo.models.User
import com.pro.devgatedemo.room.ImageDao
import com.pro.devgatedemo.room.UserDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(private val userDao: UserDao, private val imageDao: ImageDao) {
    val readAllData: LiveData<MutableList<User>> = userDao.readAllData()
    val readAllImage: LiveData<MutableList<Image>> = imageDao.readAllImage()

    suspend fun addUser(user: User) {
        userDao.addUser(user)
    }

    suspend fun isExit(password: String): Boolean? {
        return userDao.isExist(password)
    }

    suspend fun addImage(image: Image) {
        imageDao.addImage(image)
    }

    suspend fun deleteImage(name: String) {
        imageDao.deleteImage(name)
    }

    suspend fun isCorrectPassword(password: String, name: String): Boolean? {
        return imageDao.isCorrectPassword(password, name)
    }
}
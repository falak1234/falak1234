package com.pro.devgatedemo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pro.devgatedemo.models.Image
import com.pro.devgatedemo.models.User
import com.pro.devgatedemo.repository.Repository
import com.pro.devgatedemo.room.Db
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CamViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {
    val readAllData: LiveData<MutableList<User>>
    val readAllImage: LiveData<MutableList<Image>>
    var image: MutableLiveData<Image> = MutableLiveData(Image(0, "", "", ""))
    private val repository: Repository

    init {
        val userDao = Db.getDatabase(application).userDao()
        val imageDao = Db.getDatabase(application).imageDao()
        repository = Repository(userDao, imageDao)
        readAllData = repository.readAllData
        readAllImage = repository.readAllImage
    }

    suspend fun addUser(user: User) {
        viewModelScope.launch {
            repository.addUser(user)
        }
    }

    suspend fun isExist(password: String): Boolean? {
        return repository.isExit(password)
    }


    suspend fun addImage(image: Image) {
        viewModelScope.launch {
            repository.addImage(image)
        }
    }

    suspend fun deleteImage(name: String) {
        viewModelScope.launch {
            repository.deleteImage(name)
        }
    }

    suspend fun isCorrectPassword(password: String, name: String): Boolean? {
        return repository.isCorrectPassword(password, name)
    }
}
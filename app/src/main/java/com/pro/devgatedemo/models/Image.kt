package com.pro.devgatedemo.models

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "images")
data class Image(
    @PrimaryKey(autoGenerate = true)
    var id:Int,
    var name:String,
    var path:String,
    var encryptionPassword:String
)

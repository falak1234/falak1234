package com.pro.devgatedemo.hilt

import android.content.Context
import com.pro.devgatedemo.databinding.ActivityMainBinding
import com.pro.devgatedemo.views.activities.MainActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext

@Module
@InstallIn(ActivityComponent::class)
class ActivityBindingInjection {

    @Provides
    fun mainActivityBinding(@ActivityContext context: Context): ActivityMainBinding {
        return ActivityMainBinding.inflate((context as MainActivity).layoutInflater)
    }
}
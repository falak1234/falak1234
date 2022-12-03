package com.pro.devgatedemo.hilt

import android.content.Context
import com.pro.devgatedemo.databinding.FragmentCameraBinding
import com.pro.devgatedemo.databinding.FragmentImageBinding
import com.pro.devgatedemo.databinding.FragmentLoginBinding
import com.pro.devgatedemo.views.activities.MainActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ActivityContext

@Module
@InstallIn(FragmentComponent::class)
class FragmentBindingInjection {

    @Provides
    fun cameraFragmentBinding(@ActivityContext context: Context): FragmentCameraBinding {
        return FragmentCameraBinding.inflate((context as MainActivity).layoutInflater)
    }

    @Provides
    fun loginFragmentBinding(@ActivityContext context: Context): FragmentLoginBinding {
        return FragmentLoginBinding.inflate((context as MainActivity).layoutInflater)
    }

    @Provides
    fun imageFragmentBinding(@ActivityContext context: Context): FragmentImageBinding {
        return FragmentImageBinding.inflate((context as MainActivity).layoutInflater)
    }

}
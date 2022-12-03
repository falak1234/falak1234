package com.pro.devgatedemo.views.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.pro.devgatedemo.BuildConfig
import com.pro.devgatedemo.databinding.DeleteDialogBinding
import com.pro.devgatedemo.databinding.FragmentImageBinding
import com.pro.devgatedemo.viewmodel.CamViewModel
import com.pro.devgatedemo.views.activities.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class ImageFragment : Fragment() {
    @Inject
    lateinit var binding: FragmentImageBinding

    @Inject
    lateinit var camViewModel: CamViewModel
    private lateinit var backPressedCallback: OnBackPressedCallback
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentImageBinding.inflate(layoutInflater)

        try {
            backPressedCallback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    configureBackPress()
                }
            }
            requireActivity().onBackPressedDispatcher.addCallback(
                viewLifecycleOwner,
                backPressedCallback
            )
        } catch (e: Exception) {
        } catch (e: java.lang.Exception) {
        } catch (e: java.lang.IllegalStateException) {
        } catch (e: java.lang.IllegalArgumentException) {
        }
        initObserver()
        iniListener()
        return binding.root
    }

    private fun initObserver() {
        camViewModel.image.observe(viewLifecycleOwner) {
            with(binding) {
                imageName.text = it.name
                Glide.with(requireContext()).load(it.path).into(image)
            }
        }
    }

    private fun iniListener() {
        with(binding) {
            back.setOnClickListener {
                configureBackPress()
            }
            delete.setOnClickListener {
                showDeleteDialog()
            }
            share.setOnClickListener {
                activity?.let {
                    if (it is MainActivity) {
                        it.dataDir?.listFiles()?.forEach { it1 ->
                            if (it1.nameWithoutExtension == camViewModel.image.value?.name) {
                                shareFile(requireContext(), it1)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showDeleteDialog() {
        val binding = DeleteDialogBinding.inflate(layoutInflater)
        val dialogs = AlertDialog.Builder(requireContext()).create()
        dialogs?.setCanceledOnTouchOutside(true)
        dialogs?.setCancelable(true)
        dialogs?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogs?.setView(binding.root)
        binding.cancel.setOnClickListener {
            dialogs.dismiss()
        }
        binding.delete.setOnClickListener {
            activity?.let {
                if (it is MainActivity) {
                    it.dataDir?.listFiles()?.forEach { it1 ->
                        if (it1.nameWithoutExtension == camViewModel.image.value?.name) {
                            deleteFile(it1)
                        }
                    }
                }
            }

            dialogs.dismiss()
        }
        dialogs?.show()
    }

    private fun deleteFile(file: File) {
        Log.i("file", "${file.path}")
        try {
            if (file.exists()) {
                CoroutineScope(IO).launch {
                    camViewModel.image.value?.name?.let { camViewModel.deleteImage(it) }
                }
                var f = file.delete()
                Toast.makeText(requireContext(), "Successfully deleted", Toast.LENGTH_SHORT).show()
                configureBackPress()
                Log.i("file", "${f}")
            }
        } catch (e: Exception) {
            Log.i("file", "\$e")
        }
    }

    private fun shareFile(context: Context, file: File) {
        try {
            val authority = "${BuildConfig.APPLICATION_ID}.provider"
            FileProvider.getUriForFile(context, authority, file)?.let {
                val shareIntent = Intent()
                    .setAction(Intent.ACTION_SEND)
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    .setDataAndType(it, context.contentResolver.getType(it))
                    .putExtra(Intent.EXTRA_STREAM, it)
                context.startActivity(Intent.createChooser(shareIntent, "Share to:"))
            }
        } catch (e: Exception) {
            Log.i("info", "\$e")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::backPressedCallback.isInitialized) {
            backPressedCallback.isEnabled = false
            backPressedCallback.remove()
        }
    }

    private fun configureBackPress() {
        try {
            findNavController().popBackStack()
        } catch (e: java.lang.IllegalStateException) {
        } catch (e: java.lang.Exception) {
        } catch (e: java.lang.IllegalArgumentException) {
        }
    }
}
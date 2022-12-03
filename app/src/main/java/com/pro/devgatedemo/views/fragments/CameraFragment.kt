package com.pro.devgatedemo.views.fragments

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.nabinbhandari.android.permissions.PermissionHandler
import com.nabinbhandari.android.permissions.Permissions
import com.pro.devgatedemo.BuildConfig
import com.pro.devgatedemo.R
import com.pro.devgatedemo.databinding.EncryptionDialogBinding
import com.pro.devgatedemo.databinding.FragmentCameraBinding
import com.pro.devgatedemo.databinding.OpenImageDialogBinding
import com.pro.devgatedemo.models.Image
import com.pro.devgatedemo.viewmodel.CamViewModel
import com.pro.devgatedemo.views.adapters.GalleryAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import kotlin.system.exitProcess


@AndroidEntryPoint
class CameraFragment : Fragment() {
    @Inject
    lateinit var binding: FragmentCameraBinding

    @Inject
    lateinit var camViewModel: CamViewModel
    private var doubleTap = false
    private lateinit var backPressedCallback: OnBackPressedCallback
    private val galleryAdapter by lazy { GalleryAdapter() }
    val TAG = "testingTag"
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
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
        initListener()
        initObserver()
        return binding.root
    }

    private fun initObserver() {
        binding.picRec.adapter = galleryAdapter
        camViewModel.readAllImage.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) {
                binding.picRec.visibility = View.GONE
                binding.noImage.visibility = View.VISIBLE
            } else {
                binding.picRec.visibility = View.VISIBLE
                binding.noImage.visibility = View.GONE
                galleryAdapter.setData(it)
            }
        }
    }

    private fun initListener() {
        galleryAdapter.listener = object : GalleryAdapter.OnClickListener {
            override fun onClickListener(image: Image) {
                Log.i(TAG, "onClickListener: called")
                showOpenDialog(image)
            }

        }
        binding.captureBtn.setOnClickListener {
            val permissions = arrayOf(
                android.Manifest.permission.CAMERA
            )
            val rationale = "App required camera permission"
            val options: Permissions.Options = Permissions.Options()
                .setRationaleDialogTitle("Camera Permission")
                .setSettingsDialogTitle("Permission")
            Permissions.check(
                requireContext(),
                permissions,
                rationale,
                options,
                object : PermissionHandler() {
                    override fun onGranted() {
                        takePictures.run()
                    }

                    override fun onDenied(
                        context: Context?,
                        deniedPermissions: ArrayList<String>?
                    ) {
                        super.onDenied(context, deniedPermissions)
                    }
                })
        }
    }

    private fun getTmpFileUri(): Uri {

        val tmpFile = File.createTempFile("image_file", ".png", activity?.dataDir).apply {
            createNewFile()
            deleteOnExit()
        }
        return FileProvider.getUriForFile(
            requireContext(),
            "${BuildConfig.APPLICATION_ID}.provider",
            tmpFile
        )
    }

    private val takePictures: Runnable = Runnable {
        imageUri = getTmpFileUri()
        try {
            takePicture.launch(imageUri)
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "No Camera Found",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private val takePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { bool ->
            Log.i(TAG, ":  called")
            bool?.let {
                if (it) {
                    Log.i(TAG, ":  Successful")
                    showEncryptionDialog(imageUri)
                } else {
                    Log.i(TAG, ":  failed")
                }
            }
        }

    private fun showEncryptionDialog(imageUri: Uri?) {
        val binding = EncryptionDialogBinding.inflate(layoutInflater)
        val dialogs = AlertDialog.Builder(requireContext()).create()
        dialogs?.setCanceledOnTouchOutside(false)
        dialogs?.setCancelable(false)
        dialogs?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogs?.setView(binding.root)
        binding.encryptionPassword.setText(LoginFragment.userPassword)
        binding.encryptionPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 8) {
                    Toast.makeText(
                        requireContext(),
                        "Only eight character allow",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
        binding.encryption.setOnClickListener {
            if (binding.encryptionPassword.text?.isNotEmpty() == true) {
                val file = File(imageUri.toString())
                CoroutineScope(IO).launch {
                    camViewModel.addImage(
                        Image(
                            0,
                            file.nameWithoutExtension,
                            imageUri.toString(),
                            binding.encryptionPassword.text.toString()
                        )
                    )
                    withContext(Main) {
                        dialogs.dismiss()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "password is required", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        dialogs?.show()
    }

    private fun showOpenDialog(image: Image) {
        val binding = OpenImageDialogBinding.inflate(layoutInflater)
        val dialogs = AlertDialog.Builder(requireContext()).create()
        dialogs?.setCanceledOnTouchOutside(true)
        dialogs?.setCancelable(true)
        dialogs?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogs?.setView(binding.root)
        binding.encryptionPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 8) {
                    Toast.makeText(
                        requireContext(),
                        "Only eight character allow",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
        binding.open.setOnClickListener {
            if (binding.encryptionPassword.text?.isNotEmpty() == true) {
                CoroutineScope(IO).launch {
                    val isCorrect = camViewModel.isCorrectPassword(
                        binding.encryptionPassword.text.toString(),
                        image.name
                    )
                    withContext(Main) {
                        if (isCorrect == true) {
                            Toast.makeText(
                                requireContext(),
                                "Successfully Open",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            dialogs.dismiss()
                            camViewModel.image.postValue(image)
                            findNavController().navigate(R.id.imageFragment)
                        } else {
                            Toast.makeText(requireContext(), "Invalid password", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Password is required", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        dialogs?.show()
    }

    private fun configureBackPress() {
        if (doubleTap) {
            exitProcess(0)
        }
        Toast.makeText(requireContext(), "Tap Again To Exit", Toast.LENGTH_SHORT).show()
        doubleTap = true
        Handler(Looper.getMainLooper()).postDelayed({ doubleTap = false }, 2000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::backPressedCallback.isInitialized) {
            backPressedCallback.isEnabled = false
            backPressedCallback.remove()
        }
    }
}
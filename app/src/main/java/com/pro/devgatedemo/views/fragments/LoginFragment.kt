package com.pro.devgatedemo.views.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.pro.devgatedemo.R
import com.pro.devgatedemo.databinding.FragmentLoginBinding
import com.pro.devgatedemo.models.User
import com.pro.devgatedemo.viewmodel.CamViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.system.exitProcess

@AndroidEntryPoint
class LoginFragment : Fragment() {

    @Inject
    lateinit var binding: FragmentLoginBinding

    @Inject
    lateinit var camViewModel: CamViewModel
    private lateinit var backPressedCallback: OnBackPressedCallback
    private var doubleTap = false

    companion object {
        var userPassword = ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        try {
            backPressedCallback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    configureBackPress()
                }
            }
            requireActivity().onBackPressedDispatcher.addCallback(
                viewLifecycleOwner, backPressedCallback
            )
        } catch (e: Exception) {
        } catch (e: java.lang.Exception) {
        } catch (e: java.lang.IllegalStateException) {
        } catch (e: java.lang.IllegalArgumentException) {
        }
        initObserver()
        initListener()
        return binding.root
    }

    private fun initObserver() {
        camViewModel.readAllData.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) {
                binding.signUpCard.visibility = View.VISIBLE
                binding.loginCard.visibility = View.GONE
            } else {
                binding.signUpCard.visibility = View.GONE
                binding.loginCard.visibility = View.VISIBLE
            }
        }
    }

    private fun initListener() {

        binding.signUp.setOnClickListener {
            if (binding.name.text.isNotEmpty() && binding.password.text?.isNotEmpty() == true) {
                CoroutineScope(IO).launch {
                    camViewModel.addUser(
                        User(
                            0, binding.name.text.toString(), binding.password.text.toString()
                        )
                    )
                    withContext(Main) {
                        Toast.makeText(requireContext(), "Successfully Sign Up", Toast.LENGTH_SHORT)
                            .show()
                        binding.name.text.clear()
                        binding.password.text!!.clear()
                        binding.signUpCard.visibility = View.GONE
                        binding.loginCard.visibility = View.VISIBLE
                    }
                }
            } else {
                if (binding.name.text.isEmpty() && binding.password.text?.isEmpty() == true) {
                    Toast.makeText(requireContext(), "Both fields Compulsory", Toast.LENGTH_SHORT)
                        .show()
                } else if (binding.name.text.isEmpty()) {
                    Toast.makeText(requireContext(), "Name is Compulsory", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(requireContext(), "Password is Compulsory", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        binding.login.setOnClickListener {
            CoroutineScope(IO).launch {
                val isExit = camViewModel.isExist(binding.loginPassword.text.toString())
                if (isExit == true) {
                    withContext(Main) {
                        userPassword = binding.loginPassword.text.toString()
                        binding.loginPassword.text?.clear()
                        findNavController().navigate(R.id.cameraFragment)
                    }
                } else {
                    withContext(Main) {
                        Toast.makeText(
                            requireContext(), "Please Enter Correct Password", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        binding.name.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 15) {
                    Toast.makeText(
                        requireContext(), "Only fifteen character allow", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })

        binding.password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 8) {
                    Toast.makeText(
                        requireContext(), "Only eight character allow", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })

        binding.loginPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 8) {
                    Toast.makeText(
                        requireContext(), "Only eight character allow", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
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
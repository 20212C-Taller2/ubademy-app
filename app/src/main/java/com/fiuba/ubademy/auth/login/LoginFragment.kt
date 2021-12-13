package com.fiuba.ubademy.auth.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.fiuba.ubademy.MainActivity
import com.fiuba.ubademy.R
import com.fiuba.ubademy.databinding.FragmentLoginBinding
import com.fiuba.ubademy.utils.BusyFragment
import com.fiuba.ubademy.utils.hideError
import com.fiuba.ubademy.utils.hideKeyboard
import com.fiuba.ubademy.utils.showError
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class LoginFragment : Fragment() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: FragmentLoginBinding

    private lateinit var googleSignInClient : GoogleSignInClient

    private var emailValid = false
    private var passwordValid = false

    private var signInWithGoogleActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        try {
            // the task returned from this call is always completed
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_login,
            container,
            false
        )

        binding.loginButton.setOnClickListener {
            lifecycleScope.launch {
                login(it)
            }
        }

        binding.createAccountButton.setOnClickListener {
            it.findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToCreateAccountFragment())
        }

        binding.googleSignInButton.setOnClickListener {
            signInWithGoogle(it)
        }

        binding.loginViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupValidators()

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), googleSignInOptions)

        // TODO: remove this
        googleSignInClient.signOut()
        Firebase.auth.signOut()

        return binding.root
    }

    private fun setupValidators() {
        viewModel.email.observe(viewLifecycleOwner, { newValue ->
            emailValid = checkEmail(newValue)
        })

        viewModel.password.observe(viewLifecycleOwner, { newValue ->
            passwordValid = checkPassword(newValue)
        })
    }

    private fun checkEmail(newValue : String?) : Boolean {
        if (newValue.isNullOrBlank())
            return binding.emailLoginLayout.showError(getString(R.string.should_have_value))

        return binding.emailLoginLayout.hideError()
    }

    private fun checkPassword(newValue : String?) : Boolean {
        if (newValue.isNullOrBlank())
            return binding.passwordLoginLayout.showError(getString(R.string.should_have_value))

        return binding.passwordLoginLayout.hideError()
    }

    private fun checkForm() : Boolean {
        val emailOk = emailValid || checkEmail(viewModel.email.value)
        val passwordOk = passwordValid || checkPassword(viewModel.password.value)
        return emailOk && passwordOk
    }

    private suspend fun login(view: View) {
        view.hideKeyboard()

        if (!checkForm())
            return

        BusyFragment.show(parentFragmentManager)
        val loginStatus = viewModel.login()
        Firebase.auth.signInAnonymously().await()
        BusyFragment.hide()

        when (loginStatus) {
            LoginStatus.SUCCESS -> {
                goToMain()
            }
            LoginStatus.INVALID_CREDENTIALS -> Toast.makeText(context, R.string.invalid_credentials, Toast.LENGTH_LONG).show()
            LoginStatus.FAIL -> Toast.makeText(context, R.string.request_failed, Toast.LENGTH_LONG).show()
        }
    }

    private fun signInWithGoogle(view: View) {
        view.hideKeyboard()

        // TODO: remove this
        googleSignInClient.signOut()
        Firebase.auth.signOut()

        signInWithGoogleActivityResultLauncher.launch(googleSignInClient.signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        BusyFragment.show(parentFragmentManager)

        lifecycleScope.launch {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = Firebase.auth.signInWithCredential(credential).await()
                val getTokenResult = authResult.user!!.getIdToken(false).await()
                viewModel.loginWithGoogle(getTokenResult.token!!)
                goToMain()
            } catch (e: Exception) {
                Timber.e(e)
                Toast.makeText(context, R.string.request_failed, Toast.LENGTH_LONG).show()
            }
            BusyFragment.hide()
        }
    }

    private fun goToMain() {
        val mainIntent = Intent(this.context, MainActivity::class.java)
        startActivity(mainIntent)
    }
}
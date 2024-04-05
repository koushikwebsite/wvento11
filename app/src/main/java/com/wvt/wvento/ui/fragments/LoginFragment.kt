package com.wvt.wvento.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.wvt.wvento.R
import com.wvt.wvento.databinding.FragmentLoginBinding
import com.wvt.wvento.ui.activity.HomeActivity
import com.wvt.wvento.ui.activity.OnLocationActivity
import com.wvt.wvento.util.Constants
import com.wvt.wvento.viewModel.EventViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private var builder = CustomTabsIntent.Builder()

    private lateinit var viewModel: EventViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[EventViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        auth = Firebase.auth

        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        binding.GoogleLogin.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)
        }

        val spanLoc = SpannableString("By continuing, you agree to Wvento's Privacy Policy \nand Terms and Conditions")

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val customTabsIntent : CustomTabsIntent = builder.build()
                customTabsIntent.launchUrl(requireContext(), Uri.parse(Constants.POLICY))
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.parseColor("#FF03DAC5")
            }
        }

        spanLoc.setSpan(clickableSpan,37,51, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

        val clickableSpan1 = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val customTabsIntent : CustomTabsIntent = builder.build()
                customTabsIntent.launchUrl(requireContext(), Uri.parse(Constants.TERMS))
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.parseColor("#FF03DAC5")
            }
        }
        spanLoc.setSpan(clickableSpan1,spanLoc.length-20,spanLoc.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

        binding.textView2.text = spanLoc
        binding.textView2.movementMethod = LinkMovementMethod.getInstance()

        return binding.root
    }


    private var launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("LoginActivity", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    Log.w("LoginActivity", "Google sign in failed", e)
                }
            }
        }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d("LoginActivity", "signInWithCredential:success")
                    //retrieveAndStoreToken()
                    updateUI()

                } else {
                    Log.w("LoginActivity", "signInWithCredential:failure", task.exception)
                }
            }
    }

    override fun onStart() {
        super.onStart()
        if (GoogleSignIn.getLastSignedInAccount(requireContext()) != null) {
            updateUI()
        }
    }

    private fun updateUI() {
        if(onLocationFinished()){
            startActivity(Intent(requireContext(),HomeActivity::class.java))
            requireActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
            requireActivity().finish()
        }else{
            startActivity(Intent(requireContext(),OnLocationActivity::class.java))
            requireActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
            requireActivity().finish()
        }
    }

    private fun onLocationFinished(): Boolean {
        val sharedPref = requireActivity().getSharedPreferences("ScreenComplete", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("LocFinish", false)
    }
}
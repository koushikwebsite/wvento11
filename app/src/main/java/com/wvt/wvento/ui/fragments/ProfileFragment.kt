package com.wvt.wvento.ui.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.ImageLoader
import coil.load
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.transform.RoundedCornersTransformation
import com.wvt.wvento.R
import com.wvt.wvento.databinding.FragmentProfileBinding
import com.wvt.wvento.ui.activity.LoginActivity
import com.wvt.wvento.util.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.io.FileOutputStream

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth
    private var builder = CustomTabsIntent.Builder()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        mAuth = FirebaseAuth.getInstance()

        val user = mAuth.currentUser
        user?.let{
            binding.profileUserName.text = user.displayName
            binding.userEmail.text = user.email

            binding.userImg.load(user.photoUrl) {
                crossfade(600)
                placeholder(R.drawable.ic_profile_holder)
                error(R.drawable.ic_profile_holder)
                transformations(RoundedCornersTransformation(100f))
            }

        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        binding.logout.setOnClickListener {

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Do you want to logout ?")
                .setPositiveButton("Yes") { _, _ ->
                    logout()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        lifecycleScope.launchWhenStarted {
            if(ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

                convertBitmap()
            }
        }

        binding.help.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_helpActivity)
        }

        binding.privacy.setOnClickListener {
            val customTabsIntent : CustomTabsIntent = builder.build()
            customTabsIntent.launchUrl(requireContext(), Uri.parse(Constants.POLICY))
        }

        binding.invite.setOnClickListener {
            shareIntent()
        }

        binding.settings.setOnClickListener {
           findNavController().navigate(R.id.action_profileFragment_to_settingsActivity)
        }

        return binding.root
    }

    private fun logout() {
        mAuth.signOut()

        googleSignInClient.signOut().addOnCompleteListener {
            startActivity(Intent(activity,LoginActivity::class.java))
            activity?.finish()
        }
    }

    private fun shareIntent() {
        try {

            val strShareMessage = "Let me recommend you this application\n" + "https://play.google.com/store/apps/details?id="+requireActivity().packageName

            val file = File(requireContext().externalCacheDir!!.absolutePath+"/shared_files/poster.png")
            val imageFullPath: String = file.absolutePath

            val i = Intent(Intent.ACTION_SEND)
            i.type = "image/*"
            i.putExtra(Intent.EXTRA_TEXT, "Wevento $strShareMessage")
            i.putExtra(Intent.EXTRA_STREAM, Uri.parse(imageFullPath))
            startActivity(Intent.createChooser(i, "Select App to Share Text and Image"))

        } catch (e: Exception) {

            Toast.makeText(requireContext(),e.toString(),Toast.LENGTH_LONG).show()
        }
    }

    private suspend fun convertBitmap()  {
        val loading = ImageLoader(requireContext())
        val request = ImageRequest.Builder(requireContext())
            .data("https://cdn.searchenginejournal.com/wp-content/uploads/2019/07/the-essential-guide-to-using-images-legally-online-1520x800.png")
            .build()
        val result = (loading.execute(request) as SuccessResult).drawable

        saveImage((result as BitmapDrawable).bitmap)
    }

    private fun saveImage(finalBitmap: Bitmap) {
        val root: String = requireContext().externalCacheDir!!.absolutePath
        val myDir = File("$root/shared_files")
        myDir.mkdirs()
        val fName = "poster.png"
        val file = File(myDir, fName)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

}
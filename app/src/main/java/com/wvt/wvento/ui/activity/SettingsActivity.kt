package com.wvt.wvento.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.ActivityNavigator
import com.wvt.wvento.R
import com.wvt.wvento.databinding.ActivitySettingsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File


class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val window: Window = this.window
        window.navigationBarColor = ContextCompat.getColor(this, R.color.background)

        binding.Toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.cacheSettings.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Clear Cache")
                .setMessage("Application cache will be cleared")
                .setPositiveButton("OK") { _, _ ->
                    deleteCache(this)
                }
                .setNegativeButton("cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        binding.notifySettings.setOnClickListener {
            openAppNotificationSettings(this)
        }
    }

    private fun openAppNotificationSettings(context: Context) {
        val intent = Intent().apply {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                    action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
                    action = "android.settings.APP_NOTIFICATION_SETTINGS"
                    putExtra("app_package", context.packageName)
                    putExtra("app_uid", context.applicationInfo.uid)
                }
                else -> {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    addCategory(Intent.CATEGORY_DEFAULT)
                    data = Uri.parse("package:" + context.packageName)
                }
            }
        }

        context.startActivity(intent)
    }

    private fun deleteCache(context: Context) {
        try {
            val dir: File = context.cacheDir
            deleteDir(dir)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun deleteDir(dir: File?): Boolean {
        return if (dir != null && dir.isDirectory) {
            val children: Array<String> = dir.list() as Array<String>
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
            dir.delete()
        } else if (dir != null && dir.isFile) {
            dir.delete()
        } else {
            false
        }
    }

    override fun finish() {
        super.finish()
        ActivityNavigator.applyPopAnimationsToPendingTransition(this)
    }

}
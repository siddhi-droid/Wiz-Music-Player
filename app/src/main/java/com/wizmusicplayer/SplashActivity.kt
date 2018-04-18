package com.wizmusicplayer

import android.Manifest
import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import com.github.florent37.runtimepermission.kotlin.askPermission
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.AnkoLogger
import android.support.v4.content.ContextCompat


class SplashActivity : AppCompatActivity(), AnkoLogger {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        if (!hasGrantedPermission()) {
            checkWritePermissions()
            clickListeners()
        } else {
            navigateToDashboard()
        }
    }

    private fun hasGrantedPermission(): Boolean {
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return permissionCheck == PackageManager.PERMISSION_GRANTED
    }

    private fun clickListeners() {
        btnGrantPermission.setOnClickListener { requestPermission() }
    }

    private fun checkWritePermissions() {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater ?: return

        val view = inflater.inflate(R.layout.layout_check_permission, null)

        val dialog = AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton("Grant", null)
                .setNegativeButton("No", null)
                .create()

        dialog.setOnShowListener {
            val btnPositive = (it as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            btnPositive.setOnClickListener {
                dialog.dismiss()
                requestPermission()
            }
            val btnNegative = it.getButton(AlertDialog.BUTTON_NEGATIVE)
            btnNegative.setOnClickListener {
                dialog.dismiss()
                showPermissionDeniedLayout()
            }
        }

        dialog.setCancelable(false)
        dialog.show()
    }

    private fun requestPermission() {
        askPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
        }.onDeclined {
            showPermissionDeniedLayout()
        }.runtimePermission.onForeverDenied {
            startActivity(Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)))
        }.onAccepted {
            showLoadingLayout()
        }
    }

    private fun showLoadingLayout() {
        lottieLayoutPermissionDenied.visibility = View.GONE
        lottieLayout.visibility = View.VISIBLE
        animationView.playAnimation()
        animationView.speed = 0.5F
        animationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                navigateToDashboard()
            }
        })
    }

    private fun navigateToDashboard() {
        startActivity(Intent(this@SplashActivity, Dashboard::class.java))
        finish()
    }

    private fun showPermissionDeniedLayout() {
        lottieLayoutPermissionDenied.visibility = View.VISIBLE
        animationViewPermissionDenied.playAnimation()
    }
}

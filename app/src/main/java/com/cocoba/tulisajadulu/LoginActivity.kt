package com.cocoba.tulisajadulu

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

/**
 * Created by Chandra on 06/03/19.
 * Need some help?
 * Contact me at y.pristyan.chandra@gmail.com
 */
class LoginActivity : AppCompatActivity() {

    private val handler = Handler()
    private val task = Runnable {
        if (auth.currentUser == null) {
            view_center?.visibility = View.VISIBLE
            pb_login?.visibility = View.GONE
        }

        auth.currentUser?.let {
            startActivity<MainActivity>()
            finish()
        }
    }

    companion object {
        private const val DELAY = 500L
        private const val GOOGLE_SIGN_IN = 10
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Glide.with(this).load(R.drawable.img_meditation).into(img_center)
        btn_login?.setOnClickListener { doLogin() }
        handler.postDelayed(task, DELAY)
    }

    private fun doLogin() {
        val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(), GOOGLE_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) startActivity<MainActivity>().also { finish() }
            else response?.error?.message?.let { toast(it) }
        }
    }

    override fun onDestroy() {
        handler.removeCallbacks(task)
        super.onDestroy()
    }
}
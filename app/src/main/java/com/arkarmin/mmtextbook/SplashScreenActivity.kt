package com.arkarmin.mmtextbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val from_top =AnimationUtils.loadAnimation(this,R.anim.from_left)
        iv_splash.startAnimation(from_top)

        val from_left = AnimationUtils.loadAnimation(this,R.anim.from_right)
        tv_app_name.startAnimation(from_left)

//        val fade_in = AnimationUtils.loadAnimation(this,R.anim.fade_in)
//        tv_description.startAnimation(fade_in)

        Handler().postDelayed({
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        },2500)
    }
}
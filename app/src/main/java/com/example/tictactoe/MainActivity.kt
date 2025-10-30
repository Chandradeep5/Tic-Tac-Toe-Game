package com.example.tictactoe

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet

class MainActivity : AppCompatActivity() {

    private lateinit var layout: ConstraintLayout
    private lateinit var btnTwoVTwo: Button
    private lateinit var btnBot: Button
    private lateinit var btnExit: Button
    private lateinit var settingsBtn: ImageButton
    private lateinit var titleText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 🔹 Initialize Views
        layout = findViewById(R.id.mainLayout)
        btnTwoVTwo = findViewById(R.id.btnTwoVTwo)
        btnBot = findViewById(R.id.btnBot)
        btnExit = findViewById(R.id.btnExit)
        settingsBtn = findViewById(R.id.btnSettings)
        titleText = findViewById(R.id.tvTitle)

        // 🔹 Apply saved theme (wallpaper + text colors)
        ThemeUtils.applyTheme(this, layout, listOf(titleText, btnTwoVTwo, btnBot))

        // 🔹 Exit button color
        btnExit.setTextColor(Color.RED)

        // 🔹 Position settings button properly
        val constraintSet = ConstraintSet()
        constraintSet.clone(layout)
        constraintSet.setMargin(R.id.btnSettings, ConstraintSet.TOP, 80)
        constraintSet.applyTo(layout)
        settingsBtn.bringToFront()

        // 🌈 Add smooth glossy intro animation when activity starts
        playIntroAnimation()

        // 🔹 Navigation buttons
        btnTwoVTwo.setOnClickListener {
            startActivity(Intent(this, TwoVTwoActivity::class.java))
        }

        btnBot.setOnClickListener {
            startActivity(Intent(this, BotActivity::class.java))
        }

        // ⚙️ Settings page glossy transition
        settingsBtn.setOnClickListener {
            val loc = IntArray(2)
            settingsBtn.getLocationOnScreen(loc)
            val cx = loc[0] + settingsBtn.width / 2
            val cy = loc[1] + settingsBtn.height / 2

            val intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra(SettingsActivity.EXTRA_REVEAL_X, cx)
            intent.putExtra(SettingsActivity.EXTRA_REVEAL_Y, cy)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        // ✨ Small bounce effect for settings button
        settingsBtn.setOnLongClickListener {
            pulseButton(settingsBtn)
            true
        }

        // 🔚 Exit game
        btnExit.setOnClickListener {
            finishAffinity()
        }
    }

    override fun onResume() {
        super.onResume()
        ThemeUtils.applyTheme(this, layout, listOf(titleText, btnTwoVTwo, btnBot))
    }

    // 🌈 Glossy scale+fade intro animation
    private fun playIntroAnimation() {
        layout.alpha = 0f
        layout.scaleX = 0.9f
        layout.scaleY = 0.9f

        val scaleX = ObjectAnimator.ofFloat(layout, View.SCALE_X, 0.9f, 1f)
        val scaleY = ObjectAnimator.ofFloat(layout, View.SCALE_Y, 0.9f, 1f)
        val fadeIn = ObjectAnimator.ofFloat(layout, View.ALPHA, 0f, 1f)

        AnimatorSet().apply {
            playTogether(scaleX, scaleY, fadeIn)
            interpolator = DecelerateInterpolator()
            duration = 600
            start()
        }
    }

    // 💫 Subtle bounce effect on long press
    private fun pulseButton(button: View) {
        val anim = ObjectAnimator.ofFloat(button, View.SCALE_X, 1f, 1.15f, 1f)
        val anim2 = ObjectAnimator.ofFloat(button, View.SCALE_Y, 1f, 1.15f, 1f)

        AnimatorSet().apply {
            playTogether(anim, anim2)
            interpolator = OvershootInterpolator()
            duration = 300
            start()
        }
    }
}

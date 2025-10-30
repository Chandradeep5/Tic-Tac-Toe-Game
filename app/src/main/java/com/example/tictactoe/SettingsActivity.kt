package com.example.tictactoe

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.edit

class SettingsActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_REVEAL_X = "EXTRA_REVEAL_X"
        const val EXTRA_REVEAL_Y = "EXTRA_REVEAL_Y"
        private const val PICK_IMAGE_REQUEST = 1001
    }

    private lateinit var rootLayout: ConstraintLayout
    private lateinit var wallpaperContainer: LinearLayout
    private lateinit var textColorContainer: LinearLayout
    private lateinit var btnBack: ImageButton
    private lateinit var btnFeedback: ImageButton
    private lateinit var prefs: SharedPreferences

    private val wallpapers = listOf(
        R.drawable.default_wallpaper,

        R.drawable.wall4,
        R.drawable.wall5,
        R.drawable.wall6,
        R.drawable.wall7,
        R.drawable.wall8,
        R.drawable.wall9,
        R.drawable.wall10,
        R.drawable.wall11
    )

    private val textColorCircles = listOf(
        R.drawable.color_white_circle,
        R.drawable.color_black_circle,
        R.drawable.color_blue_circle,
        R.drawable.color_pink_circle,
        R.drawable.color_red_circle,
        R.drawable.color_purple_circle,
        R.drawable.color_orange_circle,
        R.drawable.color_gray_circle,
        R.drawable.color_cyan_circle
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        prefs = getSharedPreferences("TicTacToePrefs", MODE_PRIVATE)

        rootLayout = findViewById(R.id.settingsRoot)
        wallpaperContainer = findViewById(R.id.wallpaperContainer)
        textColorContainer = findViewById(R.id.textColorContainer)
        btnBack = findViewById(R.id.btnBack)
        btnFeedback = findViewById(R.id.btnFeedback)

        val currentWallpaper = prefs.getString("customWallpaperUri", null)
        if (currentWallpaper != null) {
            rootLayout.background = ImageView(this).apply {
                setImageURI(Uri.parse(currentWallpaper))
            }.drawable
        } else {
            rootLayout.setBackgroundResource(prefs.getInt("selectedWallpaper", R.drawable.default_wallpaper))
        }

        loadWallpaperPreviews()
        loadTextColorOptions()

        btnFeedback.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:developer.feedback@example.com")
                putExtra(Intent.EXTRA_SUBJECT, "Tic Tac Toe Feedback")
            }
            startActivity(Intent.createChooser(emailIntent, "Send Feedback"))
        }

        btnBack.setOnClickListener { animateExitAndFinish() }

        rootLayout.visibility = View.INVISIBLE
        rootLayout.postDelayed({ playGlossyReveal() }, 100)
        pulseIcons()
    }

    // ---------------- Animation Functions ----------------

    private fun playGlossyReveal() {
        val cx = intent.getIntExtra(EXTRA_REVEAL_X, rootLayout.width / 2)
        val cy = intent.getIntExtra(EXTRA_REVEAL_Y, rootLayout.height / 2)

        rootLayout.apply {
            pivotX = cx.toFloat()
            pivotY = cy.toFloat()
            scaleX = 0.85f
            scaleY = 0.85f
            alpha = 0f
            visibility = View.VISIBLE
        }

        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(rootLayout, View.SCALE_X, 0.85f, 1f),
                ObjectAnimator.ofFloat(rootLayout, View.SCALE_Y, 0.85f, 1f),
                ObjectAnimator.ofFloat(rootLayout, View.ALPHA, 0f, 1f)
            )
            interpolator = DecelerateInterpolator()
            duration = 550
            start()
        }
    }

    private fun animateExitAndFinish() {
        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(rootLayout, View.SCALE_X, 1f, 0.85f),
                ObjectAnimator.ofFloat(rootLayout, View.SCALE_Y, 1f, 0.85f),
                ObjectAnimator.ofFloat(rootLayout, View.ALPHA, 1f, 0f)
            )
            interpolator = DecelerateInterpolator()
            duration = 400
            start()
            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    finish()
                    overridePendingTransition(0, 0)
                }
            })
        }
    }

    private fun pulseIcons() {
        val anim = android.view.animation.ScaleAnimation(
            0.9f, 1f, 0.9f, 1f,
            android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f,
            android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 450
            interpolator = OvershootInterpolator()
        }
        btnFeedback.startAnimation(anim)
        btnBack.startAnimation(anim)
    }

    // ---------------- Wallpaper Section ----------------

    private fun loadWallpaperPreviews() {
        wallpaperContainer.removeAllViews()

        // ➕ Add Custom Wallpaper Button
        val addButton = ImageView(this).apply {
            setImageResource(R.drawable.ic_add)
            layoutParams = LinearLayout.LayoutParams(260, LinearLayout.LayoutParams.MATCH_PARENT).apply {
                setMargins(16, 8, 16, 8)
            }
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            setBackgroundResource(R.drawable.bg_glass_light)
            setPadding(30, 30, 30, 30)
            isClickable = true
            isFocusable = true
            setOnClickListener { openGalleryForWallpaper() }
        }
        wallpaperContainer.addView(addButton)

        // Default wallpapers
        for (wall in wallpapers) {
            val iv = ImageView(this).apply {
                setImageResource(wall)
                layoutParams = LinearLayout.LayoutParams(260, LinearLayout.LayoutParams.MATCH_PARENT).apply {
                    setMargins(16, 8, 16, 8)
                }
                scaleType = ImageView.ScaleType.CENTER_CROP
                isClickable = true
                isFocusable = true
                setOnClickListener { showWallpaperConfirmPopup(wall) }
            }
            wallpaperContainer.addView(iv)
        }
    }

    private fun openGalleryForWallpaper() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data ?: return
            prefs.edit {
                putString("customWallpaperUri", selectedImageUri.toString())
                remove("selectedWallpaper")
            }
            restartApp()
        }
    }

    private fun showWallpaperConfirmPopup(selectedWallpaper: Int) {
        val popup = layoutInflater.inflate(R.layout.popup_confirm, null)
        val tvTitle = popup.findViewById<TextView>(R.id.popTitle)
        val tvMsg = popup.findViewById<TextView>(R.id.popMessage)
        tvTitle.text = "Apply wallpaper?"
        tvMsg.text = "Applying this wallpaper will restart the app. Continue?"

        AlertDialog.Builder(this)
            .setView(popup)
            .setCancelable(true)
            .setPositiveButton("Yes") { _, _ ->
                prefs.edit {
                    putInt("selectedWallpaper", selectedWallpaper)
                    remove("customWallpaperUri")
                }
                restartApp()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun restartApp() {
        val pm = baseContext.packageManager
        val intent = pm.getLaunchIntentForPackage(baseContext.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finishAffinity()
    }

    // ---------------- Text Color Section ----------------

    private fun loadTextColorOptions() {
        textColorContainer.removeAllViews()
        for (drawable in textColorCircles) {
            val v = ImageView(this).apply {
                setImageResource(drawable)
                layoutParams = LinearLayout.LayoutParams(120, 120).apply {
                    setMargins(16, 12, 16, 12)
                }
                scaleType = ImageView.ScaleType.CENTER_INSIDE
                isClickable = true
                setOnClickListener {
                    prefs.edit { putInt("selectedTextColorDrawable", drawable) }
                    showSmallSuccess()
                    ThemeUtils.applyTheme(this@SettingsActivity, rootLayout, findTextViewsForPreview())
                }
            }
            textColorContainer.addView(v)
        }
    }

    private fun findTextViewsForPreview(): List<TextView> {
        return listOfNotNull(findViewById(R.id.settingsTitle))
    }

    private fun showSmallSuccess() {
        val v = layoutInflater.inflate(R.layout.popup_success, null)
        val d = AlertDialog.Builder(this)
            .setView(v)
            .setCancelable(true)
            .create()
        d.show()
        v.postDelayed({ if (d.isShowing) d.dismiss() }, 900)
    }

    override fun onBackPressed() {
        animateExitAndFinish()
    }
}

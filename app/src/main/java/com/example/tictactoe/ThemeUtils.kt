package com.example.tictactoe

import android.content.Context
import android.content.SharedPreferences
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat

object ThemeUtils {

    fun applyTheme(context: Context, rootLayout: ViewGroup, textViews: List<TextView>) {
        val prefs: SharedPreferences = context.getSharedPreferences("TicTacToePrefs", Context.MODE_PRIVATE)

        // 🌄 Apply wallpaper
        val selectedWallpaper = prefs.getInt("selectedWallpaper", R.drawable.default_wallpaper)
        rootLayout.setBackgroundResource(selectedWallpaper)

        // 🎨 Apply text color theme
        val selectedTextColorDrawable = prefs.getInt("selectedTextColorDrawable", R.drawable.color_white_circle)
        val textColor = when (selectedTextColorDrawable) {
            R.drawable.color_white_circle -> ContextCompat.getColor(context, android.R.color.white)
            R.drawable.color_black_circle -> ContextCompat.getColor(context, android.R.color.black)
            R.drawable.color_blue_circle -> ContextCompat.getColor(context, android.R.color.holo_blue_light)
            else -> ContextCompat.getColor(context, android.R.color.white)
        }

        // 🖋️ Update text color for all TextViews passed
        for (tv in textViews) {
            tv.setTextColor(textColor)
        }
    }
}

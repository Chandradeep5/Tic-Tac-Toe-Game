package com.example.tictactoe

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.widget.GridLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat

object AvatarSelectionDialog {

    private val avatars = listOf(
        R.drawable.ic_ironman,
        R.drawable.ic_captain,
        R.drawable.ic_batman2,
        R.drawable.ic_batman1,
        R.drawable.ic_batman3,
        R.drawable.ic_batman4,
        R.drawable.ic_superman1,
        R.drawable.ic_superman2,
        R.drawable.ic_deadpool,
        R.drawable.ic_deadpool2,
        R.drawable.ic_ghost,
        R.drawable.ic_ghost1,
        R.drawable.ic_ghost2,
        R.drawable.ic_ghost3,
        R.drawable.ic_pumpkin,
        R.drawable.ic_spiderman1,
        R.drawable.ic_spiderman2,
        R.drawable.ic_spiderman3,
        R.drawable.ic_thor1,
        R.drawable.ic_thor2,
        R.drawable.ic_thor3,
        R.drawable.ic_thor4
    )

    fun show(context: Context, targetView: ImageView, onSelected: (Drawable?) -> Unit) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_avatar_selection, null)
        val avatarGrid = dialogView.findViewById<GridLayout>(R.id.avatarGrid)
        avatarGrid.removeAllViews()

        // Create dialog first
        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        for (avatarRes in avatars) {
            val imageView = ImageView(context).apply {
                setImageResource(avatarRes)
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 160
                    height = 160
                    setMargins(16, 16, 16, 16)
                }
                background = ContextCompat.getDrawable(context, R.drawable.round_glass_bg)
                scaleType = ImageView.ScaleType.CENTER_CROP
                isClickable = true
                isFocusable = true
            }

            imageView.setOnClickListener {
                val selectedDrawable = ContextCompat.getDrawable(context, avatarRes)
                targetView.setImageDrawable(selectedDrawable)
                onSelected(selectedDrawable)
                dialog.dismiss()
            }

            avatarGrid.addView(imageView)
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }
}

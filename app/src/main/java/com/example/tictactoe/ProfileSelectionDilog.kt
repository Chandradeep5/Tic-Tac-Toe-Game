package com.example.tictactoe


import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.GridLayout
import android.widget.ImageView

class ProfileSelectionDialog(
    context: Context,
    private val onProfileSelected: (Int) -> Unit
) : Dialog(context) {

    // Add your custom character icons here
    private val profiles = listOf(
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_profile_selection)
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        val gridLayout = findViewById<GridLayout>(R.id.gridProfiles)
        for (icon in profiles) {
            val img = ImageView(context)
            img.setImageResource(icon)
            val params = GridLayout.LayoutParams()
            params.width = 160
            params.height = 160
            params.setMargins(16, 16, 16, 16)
            img.layoutParams = params
            img.setOnClickListener {
                onProfileSelected(icon)
                dismiss()
            }
            gridLayout.addView(img)
        }
    }
}

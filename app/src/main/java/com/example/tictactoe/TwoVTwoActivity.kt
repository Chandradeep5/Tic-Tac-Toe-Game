package com.example.tictactoe

import android.graphics.drawable.Drawable
import android.os.*
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.random.Random

class TwoVTwoActivity : AppCompatActivity() {

    private lateinit var gridLayout: GridLayout
    private lateinit var turnStatusText: TextView
    private lateinit var winnerText: TextView
    private lateinit var restartBtn: Button
    private lateinit var exitBtn: Button
    private lateinit var rootLayout: ConstraintLayout
    private lateinit var playerXIcon: ImageView
    private lateinit var playerOIcon: ImageView

    private var board = Array(3) { CharArray(3) { ' ' } }
    private var isPlayerXTurn = true
    private var gameActive = true
    private val handler = Handler(Looper.getMainLooper())

    private var playerXSymbol = "X"
    private var playerOSymbol = "O"

    private var playerXImage: Drawable? = null
    private var playerOImage: Drawable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_two_v_two)

        rootLayout = findViewById(R.id.twoVTwoLayout)
        gridLayout = findViewById(R.id.gridLayout)
        turnStatusText = findViewById(R.id.tvTurnStatus)
        winnerText = findViewById(R.id.tvWinner)
        restartBtn = findViewById(R.id.btnRestart)
        exitBtn = findViewById(R.id.btnExit)
        playerXIcon = findViewById(R.id.playerXIcon)
        playerOIcon = findViewById(R.id.playerOIcon)

        ThemeUtils.applyTheme(this, rootLayout, listOf(turnStatusText, winnerText, restartBtn))
        exitBtn.setTextColor(android.graphics.Color.RED)

        restartBtn.setOnClickListener { resetGame() }
        exitBtn.setOnClickListener { finish() }

        setListeners()
        setupAvatarSelection()

        isPlayerXTurn = Random.nextBoolean()
        updateTurnText()

        winnerText.visibility = View.INVISIBLE
    }

    /** ------------------ Avatar Selection ------------------ **/
    private fun setupAvatarSelection() {
        playerXIcon.setOnLongClickListener {
            AvatarSelectionDialog.show(this, playerXIcon) { drawable ->
                playerXImage = drawable
            }
            true
        }

        playerOIcon.setOnLongClickListener {
            AvatarSelectionDialog.show(this, playerOIcon) { drawable ->
                playerOImage = drawable
            }
            true
        }
    }

    /** ------------------ Grid Button Logic ------------------ **/
    private fun setListeners() {
        for (i in 0 until gridLayout.childCount) {
            val button = gridLayout.getChildAt(i) as Button
            val row = i / 3
            val col = i % 3

            button.setOnClickListener {
                if (button.text.isEmpty() && gameActive) {
                    handlePlayerMove(button, row, col)
                }
            }
        }
    }

    private fun handlePlayerMove(button: Button, row: Int, col: Int) {
        if (isPlayerXTurn) {
            if (playerXImage != null) {
                button.background = playerXImage
                button.text = ""
            } else button.text = playerXSymbol
            board[row][col] = 'X'
        } else {
            if (playerOImage != null) {
                button.background = playerOImage
                button.text = ""
            } else button.text = playerOSymbol
            board[row][col] = 'O'
        }

        // Button press animation
        button.animate()
            .scaleX(1.1f).scaleY(1.1f)
            .setDuration(100)
            .withEndAction {
                button.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
            }.start()

        // Check game status
        when {
            checkWin(if (isPlayerXTurn) 'X' else 'O') -> showWinner()
            isBoardFull() -> showDraw()
            else -> {
                isPlayerXTurn = !isPlayerXTurn
                updateTurnText()
            }
        }
    }

    /** ------------------ Game Result Display ------------------ **/
    private fun showWinner() {
        if (!gameActive) return
        gameActive = false
        turnStatusText.text = ""

        winnerText.visibility = View.VISIBLE
        winnerText.alpha = 0f
        winnerText.text = if (isPlayerXTurn) "Player X Wins 🎉" else "Player O Wins 🏆"

        try {
            val fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
            winnerText.startAnimation(fadeIn)
        } catch (_: Exception) { }

        winnerText.animate().alpha(1f).setDuration(500).start()

        // Background flash safely
        try { flashBackground() } catch (_: Exception) { }

        restartAfterDelay()
    }

    private fun showDraw() {
        if (!gameActive) return
        gameActive = false
        winnerText.text = "It’s a Draw 🤝"
        winnerText.visibility = View.VISIBLE

        try {
            val fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
            winnerText.startAnimation(fadeIn)
        } catch (_: Exception) { }

        winnerText.animate().alpha(1f).setDuration(500).start()
        turnStatusText.text = ""
        restartAfterDelay()
    }

    /** ------------------ Background Flash ------------------ **/
    private fun flashBackground() {
        rootLayout.animate().alpha(0.8f).setDuration(200).withEndAction {
            rootLayout.animate().alpha(1f).setDuration(200).start()
        }.start()
    }

    /** ------------------ Game Logic ------------------ **/
    private fun checkWin(player: Char): Boolean {
        for (i in 0..2) {
            if (
                (board[i][0] == player && board[i][1] == player && board[i][2] == player) ||
                (board[0][i] == player && board[1][i] == player && board[2][i] == player)
            ) return true
        }
        return (board[0][0] == player && board[1][1] == player && board[2][2] == player) ||
                (board[0][2] == player && board[1][1] == player && board[2][0] == player)
    }

    private fun isBoardFull(): Boolean =
        board.all { row -> !row.contains(' ') }

    /** ------------------ Reset ------------------ **/
    private fun resetGame() {
        board = Array(3) { CharArray(3) { ' ' } }
        gameActive = true
        isPlayerXTurn = Random.nextBoolean()
        winnerText.text = ""
        winnerText.visibility = View.INVISIBLE

        for (i in 0 until gridLayout.childCount) {
            val button = gridLayout.getChildAt(i) as Button
            button.text = ""
            button.background = getDrawable(R.drawable.grid_button_bg)
            button.scaleX = 1f
            button.scaleY = 1f
        }

        updateTurnText()
    }

    private fun restartAfterDelay() {
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({
            if (!isFinishing && !gameActive) {
                resetGame()
            }
        }, 4000)
    }

    private fun updateTurnText() {
        turnStatusText.text =
            if (isPlayerXTurn) "Player X’s Turn"
            else "Player O’s Turn"
    }
}

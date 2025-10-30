package com.example.tictactoe

import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.math.max
import kotlin.math.min

class BotActivity : AppCompatActivity() {

    private lateinit var gridLayout: GridLayout
    private lateinit var tvTurnStatus: TextView
    private lateinit var tvWinner: TextView
    private lateinit var tvTitle: TextView
    private lateinit var btnRestart: Button
    private lateinit var btnExit: Button
    private lateinit var rootLayout: ConstraintLayout
    private lateinit var userIcon: ImageView
    private lateinit var botIcon: ImageView

    private var board = Array(3) { CharArray(3) { ' ' } }
    private var gameActive = true

    private val human = 'X'
    private val bot = 'O'

    private var userAvatar: Drawable? = null
    private var botAvatar: Drawable? = null

    private val handler = Handler(Looper.getMainLooper())
    private val thinkingHandler = Handler(Looper.getMainLooper())
    private var isBotThinking = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bot)

        rootLayout = findViewById(R.id.botLayout)
        gridLayout = findViewById(R.id.gridLayout)
        tvTitle = findViewById(R.id.tvTitle)
        tvTurnStatus = findViewById(R.id.tvTurnStatus)
        tvWinner = findViewById(R.id.tvWinner)
        btnRestart = findViewById(R.id.btnRestart)
        btnExit = findViewById(R.id.btnExit)
        userIcon = findViewById(R.id.playerUserIcon)
        botIcon = findViewById(R.id.playerBotIcon)

        ThemeUtils.applyTheme(
            this,
            rootLayout,
            listOf(tvTitle, tvTurnStatus, tvWinner, btnRestart)
        )

        btnExit.setTextColor(Color.RED)
        btnRestart.setOnClickListener { resetGame() }
        btnExit.setOnClickListener { finish() }

        setupProfileIcons()
        setListeners()
        startGame()
    }

    // -------------------- Profile Setup --------------------

    private fun setupProfileIcons() {
        botAvatar = getDrawable(R.drawable.ic_bot_default)
        botIcon.setImageDrawable(botAvatar)

        // Long press on user icon → Avatar selection
        userIcon.setOnLongClickListener {
            AvatarSelectionDialog.show(this, userIcon) { drawable ->
                userAvatar = drawable
            }
            true
        }

        // Long press on bot icon → Avatar selection
        botIcon.setOnLongClickListener {
            AvatarSelectionDialog.show(this, botIcon) { drawable ->
                botAvatar = drawable
            }
            true
        }
    }

    // -------------------- Game Logic --------------------

    private fun setListeners() {
        for (i in 0 until gridLayout.childCount) {
            val button = gridLayout.getChildAt(i) as Button
            val row = i / 3
            val col = i % 3

            button.setOnClickListener {
                if (button.text.isEmpty() && gameActive && tvTurnStatus.text.contains("Your Turn")) {
                    if (userAvatar != null) {
                        button.background = userAvatar
                        button.text = ""
                    } else {
                        button.text = human.toString()
                    }

                    board[row][col] = human
                    animateButton(button)

                    when {
                        checkWin(human) -> endGame("You Win 🎉")
                        isBoardFull() -> endGame("Draw 🤝")
                        else -> {
                            tvTurnStatus.text = "Bot Thinking"
                            startThinkingAnimation()
                            handler.postDelayed({ botMove() }, 1000)
                        }
                    }
                }
            }
        }
    }

    private fun botMove() {
        stopThinkingAnimation()
        val move = findBestMove()

        if (move.first != -1 && gameActive) {
            val index = move.first * 3 + move.second
            val button = gridLayout.getChildAt(index) as Button

            if (botAvatar != null) {
                button.background = botAvatar
                button.text = ""
            } else {
                button.text = bot.toString()
            }

            board[move.first][move.second] = bot
            animateButton(button)

            when {
                checkWin(bot) -> endGame("Bot Wins 🤖")
                isBoardFull() -> endGame("Draw 🤝")
                else -> tvTurnStatus.text = "Your Turn"
            }
        }
    }

    // -------------------- Animations --------------------

    private fun animateButton(button: Button) {
        val scaleX = ObjectAnimator.ofFloat(button, "scaleX", 0.8f, 1f)
        val scaleY = ObjectAnimator.ofFloat(button, "scaleY", 0.8f, 1f)
        scaleX.duration = 200
        scaleY.duration = 200
        scaleX.start()
        scaleY.start()
    }

    private fun startThinkingAnimation() {
        isBotThinking = true
        var dots = ""

        thinkingHandler.post(object : Runnable {
            override fun run() {
                if (!isBotThinking) return
                dots = if (dots.length < 3) dots + "." else ""
                tvTurnStatus.text = "Bot Thinking$dots"
                thinkingHandler.postDelayed(this, 400)
            }
        })
    }

    private fun stopThinkingAnimation() {
        isBotThinking = false
    }

    // -------------------- Game Utilities --------------------

    private fun resetGame() {
        board = Array(3) { CharArray(3) { ' ' } }
        gameActive = true
        tvWinner.text = ""
        tvTurnStatus.text = "Your Turn"

        for (i in 0 until gridLayout.childCount) {
            val button = gridLayout.getChildAt(i) as Button
            button.text = ""
            button.background = getDrawable(R.drawable.round_glass_bg)
        }
    }

    private fun endGame(message: String) {
        gameActive = false
        tvWinner.text = message
        tvTurnStatus.text = "Game Over"

        // Automatically restart game after delay
        handler.postDelayed({ resetGame() }, 3500)
    }

    private fun isBoardFull(): Boolean {
        for (row in board) {
            for (cell in row) {
                if (cell == ' ') return false
            }
        }
        return true
    }

    private fun checkWin(player: Char): Boolean {
        for (i in 0..2) {
            if ((board[i][0] == player && board[i][1] == player && board[i][2] == player) ||
                (board[0][i] == player && board[1][i] == player && board[2][i] == player)
            ) return true
        }
        if ((board[0][0] == player && board[1][1] == player && board[2][2] == player) ||
            (board[0][2] == player && board[1][1] == player && board[2][0] == player)
        ) return true
        return false
    }

    private fun findBestMove(): Pair<Int, Int> {
        var bestVal = Int.MIN_VALUE
        var bestMove = Pair(-1, -1)

        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j] == ' ') {
                    board[i][j] = bot
                    val moveVal = minimax(0, false)
                    board[i][j] = ' '
                    if (moveVal > bestVal) {
                        bestMove = Pair(i, j)
                        bestVal = moveVal
                    }
                }
            }
        }
        return bestMove
    }

    private fun minimax(depth: Int, isMax: Boolean): Int {
        if (checkWin(bot)) return 10 - depth
        if (checkWin(human)) return depth - 10
        if (isBoardFull()) return 0

        return if (isMax) {
            var best = Int.MIN_VALUE
            for (i in 0..2) {
                for (j in 0..2) {
                    if (board[i][j] == ' ') {
                        board[i][j] = bot
                        best = max(best, minimax(depth + 1, false))
                        board[i][j] = ' '
                    }
                }
            }
            best
        } else {
            var best = Int.MAX_VALUE
            for (i in 0..2) {
                for (j in 0..2) {
                    if (board[i][j] == ' ') {
                        board[i][j] = human
                        best = min(best, minimax(depth + 1, true))
                        board[i][j] = ' '
                    }
                }
            }
            best
        }
    }

    private fun startGame() {
        tvTurnStatus.text = "Your Turn"
    }
}

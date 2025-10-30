package com.example.tictactoe

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class GameActivity : AppCompatActivity() {

    private lateinit var gridButtons: Array<Array<Button>>
    private var isPlayerTurn = true
    private var moveCount = 0
    private val handler = Handler(Looper.getMainLooper())




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        // Initialize grid buttons
        gridButtons = Array(3) { row ->
            Array(3) { col ->
                val buttonId = resources.getIdentifier("btn$row$col", "id", packageName)
                findViewById<Button>(buttonId).apply {
                    setOnClickListener { onGridButtonClick(this, row, col) }
                }
            }
        }

        // Restart button
        findViewById<Button>(R.id.btnRestart).setOnClickListener {
            resetBoard()
        }

        // Exit button
        findViewById<Button>(R.id.btnExit).setOnClickListener {
            finishAffinity()
        }

        // Player always starts first
        isPlayerTurn = true
        Toast.makeText(this, "You start first!", Toast.LENGTH_SHORT).show()

    }

    private fun onGridButtonClick(button: Button, row: Int, col: Int) {
        if (!isPlayerTurn || button.text.isNotEmpty()) {
            Toast.makeText(this, "Invalid move!", Toast.LENGTH_SHORT).show()
            return
        }

        button.text = "X"
        moveCount++

        if (checkWinner()) {
            showWinnerDialog("You win!")
            return
        } else if (moveCount == 9) {
            showWinnerDialog("It's a Draw!")
            return
        }

        isPlayerTurn = false
        handler.postDelayed({ botMove() }, 1000) // bot plays after 1 second
    }

    private fun botMove() {
        val move = findBestMove()
        if (move != null) {
            gridButtons[move.first][move.second].text = "O"
            moveCount++

            if (checkWinner()) {
                showWinnerDialog("Bot wins!")
                return
            } else if (moveCount == 9) {
                showWinnerDialog("It's a Draw!")
                return
            }
        }

        isPlayerTurn = true
    }

    private fun findBestMove(): Pair<Int, Int>? {
        val b = gridButtons

        // Try to win
        for (i in 0..2) {
            for (j in 0..2) {
                if (b[i][j].text.isEmpty()) {
                    b[i][j].text = "O"
                    if (checkWinner()) {
                        b[i][j].text = ""
                        return Pair(i, j)
                    }
                    b[i][j].text = ""
                }
            }
        }

        // Try to block player
        for (i in 0..2) {
            for (j in 0..2) {
                if (b[i][j].text.isEmpty()) {
                    b[i][j].text = "X"
                    if (checkWinner()) {
                        b[i][j].text = ""
                        return Pair(i, j)
                    }
                    b[i][j].text = ""
                }
            }
        }

        // Otherwise random move
        val emptyCells = mutableListOf<Pair<Int, Int>>()
        for (i in 0..2) {
            for (j in 0..2) {
                if (b[i][j].text.isEmpty()) emptyCells.add(Pair(i, j))
            }
        }

        return if (emptyCells.isNotEmpty()) emptyCells.random() else null
    }

    private fun checkWinner(): Boolean {
        val b = gridButtons

        for (i in 0..2) {
            if (b[i][0].text.isNotEmpty() &&
                b[i][0].text == b[i][1].text &&
                b[i][1].text == b[i][2].text
            ) return true
        }

        for (i in 0..2) {
            if (b[0][i].text.isNotEmpty() &&
                b[0][i].text == b[1][i].text &&
                b[1][i].text == b[2][i].text
            ) return true
        }

        if (b[0][0].text.isNotEmpty() &&
            b[0][0].text == b[1][1].text &&
            b[1][1].text == b[2][2].text
        ) return true

        if (b[0][2].text.isNotEmpty() &&
            b[0][2].text == b[1][1].text &&
            b[1][1].text == b[2][0].text
        ) return true

        return false
    }

    private fun showWinnerDialog(message: String) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Game Over")
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Restart") { _, _ -> resetBoard() }
            .setNegativeButton("Exit") { _, _ -> finishAffinity() }
            .create()

        dialog.show()

        // Auto-restart after 5 seconds
        handler.postDelayed({
            if (dialog.isShowing) {
                dialog.dismiss()
                resetBoard()
            }
        }, 5000)
    }

    private fun resetBoard() {
        for (row in 0..2) {
            for (col in 0..2) {
                gridButtons[row][col].text = ""
            }
        }
        moveCount = 0
        isPlayerTurn = true
        Toast.makeText(this, "New Game Started! You play first.", Toast.LENGTH_SHORT).show()
    }
}

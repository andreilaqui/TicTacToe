package ca.alaqui.tictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.alaqui.tictactoe.ui.theme.TicTacToeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TicTacToeTheme {
                TicTacToeScreen()
            }
        }
    }
}

@Composable
fun TicTacToeScreen() {

    //states
    var currentPlayer by remember { mutableStateOf("X") }
    var board by remember { mutableStateOf(List(9) { "" }) }
    var winner by remember { mutableStateOf<String?>(null) }
    var showDrawDialog by remember { mutableStateOf(false) }

    val resetGame = {
        currentPlayer = "X"
        board = List(9) { "" }
        winner = null
        showDrawDialog = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Tic-Tac-Toe",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (winner != null) "Winner: $winner" else "Current Turn: $currentPlayer",
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        TicTacToeBoard(
            board = board,
            onCellClick = { index ->
                // if game is over, cells ignore click
                if (winner != null) return@TicTacToeBoard

                // if cell already filled, ignore click
                if (board[index].isNotEmpty()) return@TicTacToeBoard

                val newBoard = board.toMutableList()
                newBoard[index] = currentPlayer
                board = newBoard

                // check for winner
                val w = checkWinner(board)
                if (w != null) {
                    winner = w
                    return@TicTacToeBoard
                }

                //check for draw
                if (board.all { it.isNotEmpty() }) {
                    showDrawDialog = true
                    return@TicTacToeBoard
                }

                // no draw no winner, other player's  turn
                currentPlayer = if (currentPlayer == "X") "O" else "X"
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = resetGame) {
            Text("Reset Game")
        }

        if (showDrawDialog) {
            AlertDialog(
                onDismissRequest = { resetGame() },
                title = { Text("Draw!") },
                text = { Text("No winner this round.") },
                confirmButton = {
                    Button(onClick = {
                        resetGame()
                    }) {
                        Text("Restart")
                    }
                }
            )
        }

    }
}

@Composable
fun TicTacToeBoard(
    board: List<String>,
    onCellClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .border(2.dp, MaterialTheme.colorScheme.onBackground)
    ) {
        repeat(3) { row ->
            Row(modifier = Modifier.weight(1f)) {
                repeat(3) { col ->
                    val index = row * 3 + col
                    TicTacToeCell(
                        text = board[index],
                        onClick = { onCellClick(index) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun TicTacToeCell(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .border(1.dp, MaterialTheme.colorScheme.onBackground)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

fun checkWinner(board: List<String>): String? {
    val lines = listOf(
        // rows
        listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
        // cols
        listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
        // diagonals
        listOf(0, 4, 8), listOf(2, 4, 6)
    )

    for (line in lines) {
        val a = board[line[0]]
        val b = board[line[1]]
        val c = board[line[2]]
        if (a.isNotEmpty() && a == b && b == c) return a
    }
    return null
}
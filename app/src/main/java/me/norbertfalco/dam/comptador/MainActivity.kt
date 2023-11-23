package me.norbertfalco.dam.comptador

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout


class MainActivity : AppCompatActivity() {

    private var score = 0

    private var gameStarted = false
    private lateinit var countDownTimer: CountDownTimer
    private val initialCountDown: Long = 60000
    private val countDownInterval: Long = 1000


    private lateinit var tapMeButton: Button
    private lateinit var gameScoreTextView: TextView
    private lateinit var timeLeftTextView: TextView
    private var timeLeftOnTimer: Long = 60000


    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private const val KEY_SCORE = "KEY_SCORE"
        private const val KEY_TIME_LEFT = "KEY_TIME_LEFT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate called. Score is $score")

        tapMeButton = findViewById(R.id.tapMeButton)
        gameScoreTextView = findViewById(R.id.gameScoreTextView)
        timeLeftTextView = findViewById(R.id.timeLeftTextView)

        tapMeButton.setOnClickListener { view ->
            val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)
            view.startAnimation(bounceAnimation)
            incrementScore()
        }

        if (savedInstanceState != null) {
            score = savedInstanceState.getInt(KEY_SCORE)
            timeLeftOnTimer = savedInstanceState.getLong(KEY_TIME_LEFT)
            restoreGame()
        } else {
            resetGame()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.actionAbout) {
            showInfo()
        }
        return true
    }

    private fun showInfo() {
        val dialogTitle = getString(R.string.aboutTitle, BuildConfig.VERSION_NAME)
        val dialogMessage = getString(R.string.aboutMessage)

        val builder = AlertDialog.Builder(this)

        builder.setTitle(dialogTitle).setMessage(dialogMessage).create().show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(KEY_SCORE, score)
        outState.putLong(KEY_TIME_LEFT, timeLeftOnTimer)
        countDownTimer.cancel()

        Log.d(TAG, "onSaveInstanceState: Saving Score: $score & Time Left: $timeLeftOnTimer")
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy called.")
        super.onDestroy()
    }

    private fun resetGame() {
        score = 0

        gameScoreTextView.text = getString(R.string.yourScore, score)

        val initialTimeLeft = initialCountDown / 1000
        timeLeftTextView.text = getString(R.string.timeLeft, initialTimeLeft)

        countDownTimer = object : CountDownTimer(initialCountDown, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnTimer = millisUntilFinished

                val timeLeft = millisUntilFinished / 1000
                timeLeftTextView.text = getString(R.string.timeLeft, timeLeft)
            }

            override fun onFinish() {
                endGame()
            }
        }

        gameStarted = false
    }

    private fun restoreGame() {
        gameScoreTextView.text = getString(R.string.yourScore, score)
        val restoredTime = timeLeftOnTimer / 1000
        timeLeftTextView.text = getString(R.string.timeLeft, restoredTime)

        countDownTimer = object : CountDownTimer(timeLeftOnTimer, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnTimer = millisUntilFinished

                val timeLeft = millisUntilFinished / 1000
                timeLeftTextView.text = getString(R.string.timeLeft, timeLeft)
            }

            override fun onFinish() {
                endGame()
            }
        }

        countDownTimer.start()
        gameStarted = true
    }

    private fun startGame() {
        countDownTimer.start()
        gameStarted = true
    }

    private fun incrementScore() {
        if (!gameStarted) {
            startGame()
        }

        val blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink)

        score += 1

        val newScore = getString(R.string.yourScore, score)
        gameScoreTextView.text = newScore
        gameScoreTextView.startAnimation(blinkAnimation)

        showScoreIncrement()
    }

    private fun showScoreIncrement() {
        val scoreIncrementText = TextView(this)
        scoreIncrementText.text = "+1"
        scoreIncrementText.setTextColor(getColor(R.color.orange_1))
        scoreIncrementText.textSize = 28f

        // Log to check if the scoreIncrementText is being created
        Log.d("ScoreIncrement", "TextView created")

        val layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )

        // Set random position within the screen
        layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        layoutParams.marginStart = (Math.random() * 500).toInt()
        layoutParams.topMargin = (Math.random() * 500).toInt()

        scoreIncrementText.layoutParams = layoutParams

        // Log to check visibility before animation
        Log.d("ScoreIncrement", "Before animation - Visibility: ${scoreIncrementText.visibility}")

        // Add the TextView to the container
        val scoreIncrementContainer = findViewById<FrameLayout>(R.id.scoreIncrementContainer)
        scoreIncrementContainer.addView(scoreIncrementText)

        // Log to check visibility after adding to the container
        Log.d("ScoreIncrement", "After adding to container - Visibility: ${scoreIncrementText.visibility}")

        // Animate the TextView
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        scoreIncrementText.startAnimation(fadeInAnimation)

        // Log to check visibility after animation
        Log.d("ScoreIncrement", "After animation - Visibility: ${scoreIncrementText.visibility}")

        // Remove the TextView after a delay
        Handler(Looper.getMainLooper()).postDelayed({
            scoreIncrementContainer.removeView(scoreIncrementText)
        }, 1000) // Adjust the delay as needed
    }

    private fun endGame() {
        Toast.makeText(this, getString(R.string.gameOverMessage, score), Toast.LENGTH_LONG).show()

        resetGame()
    }

}
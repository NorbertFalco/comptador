package me.norbertfalco.dam.comptador

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

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
        return super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu.menu)
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

        gameScoreTextView.text = "Your Score: {score}"

        val initialTimeLeft = initialCountDown / 1000
        timeLeftTextView.text = "Time Left: {initialTimeLeft}"

        countDownTimer = object : CountDownTimer(initialCountDown, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnTimer = millisUntilFinished

                val timeleft = millisUntilFinished / 1000
                timeLeftTextView.text = "Time Left: {timeLeft}"
            }

            override fun onFinish() {
                endGame()
            }
        }

        gameStarted = false

    }


    private fun restoreGame() {
        gameScoreTextView.text = "Your Score: {score}"
        val restoredTime = timeLeftOnTimer / 1000
        timeLeftTextView.text = "Time Left: {restoredTime}"

        countDownTimer = object : CountDownTimer(timeLeftOnTimer, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnTimer = millisUntilFinished

                val timeLeft = millisUntilFinished
                timeLeftTextView.text = "Time Left: {timeLeft}"
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

        val newScore = "Your Score: {score}"
        gameScoreTextView.text = newScore
        gameScoreTextView.startAnimation(blinkAnimation)


    }

    private fun endGame() {
        Toast.makeText(this, "Time's up! Your score was: {score}", Toast.LENGTH_LONG).show()

        resetGame()
    }

}
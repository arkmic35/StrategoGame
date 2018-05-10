package settings

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Spinner
import com.arkmic35.stratego.R
import com.jaygoo.widget.RangeSeekBar
import game.GameActivity


class PreferencesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)
        findViewById<Spinner>(R.id.selectPlayer2).setSelection(3)
    }

    fun startGame(@Suppress("UNUSED_PARAMETER") view: View) {
        val intent = Intent(this, GameActivity::class.java)
        val boardSize = findViewById<RangeSeekBar>(R.id.seekTableSize).currentRange
        val player1Mode = findViewById<Spinner>(R.id.selectPlayer1).selectedItemPosition
        val player2Mode = findViewById<Spinner>(R.id.selectPlayer2).selectedItemPosition

        intent.putExtra("BOARD_SIZE", boardSize[0].toInt())
        intent.putExtra("PLAYER1_TYPE", player1Mode)
        intent.putExtra("PLAYER2_TYPE", player2Mode)
        startActivity(intent)
    }
}

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
    }

    fun startGame(@Suppress("UNUSED_PARAMETER") view: View) {
        val intent = Intent(this, GameActivity::class.java)
        val boardSize = findViewById<RangeSeekBar>(R.id.seekTableSize).currentRange
        val gameMode = findViewById<Spinner>(R.id.selectGameMode).selectedItemPosition

        intent.putExtra("BOARD_SIZE", boardSize[0].toInt())
        intent.putExtra("GAME_MODE", gameMode)
        startActivity(intent)
    }
}

package game

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import java.util.*


class GameOverDialog : DialogFragment() {
    interface GameOverDialogListener {
        fun playAgainClick()
        fun openSettingsClick()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(this.activity!!)
        val listener = activity as GameOverDialogListener
        val arguments = arguments
        val winner = arguments?.getString("WINNER")

        if (arguments == null || winner == null) {
            builder.setMessage("Mamy remis!")
        } else {
            builder.setMessage(String.format(Locale.getDefault(), "Wygrywa %s!", winner))
        }

        builder.setPositiveButton("Zagraj ponownie", { _, _ ->
            listener.playAgainClick()
        }).setNegativeButton("Zmień ustawienia gry", { _, _ ->
            listener.openSettingsClick()
        })

        return builder.create()
    }

}
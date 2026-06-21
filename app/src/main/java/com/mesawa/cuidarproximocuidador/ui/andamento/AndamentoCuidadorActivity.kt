package com.mesawa.cuidarproximocuidador.ui.andamento

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.mesawa.cuidarproximocuidador.R

class AndamentoCuidadorActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_andamento_cuidador)

        findViewById<FrameLayout>(R.id.mapPreview).setOnClickListener { escolherMapa() }
        findViewById<Button>(R.id.buttonEnviarMensagem).setOnClickListener { enviarMensagem() }
    }

    private fun escolherMapa() {
        val opcoes = arrayOf("Abrir no Google Maps", "Ampliar GPS no app")
        AlertDialog.Builder(this)
            .setTitle("Rota até o local")
            .setItems(opcoes) { _, which ->
                if (which == 0) abrirGoogleMaps() else ampliarGps()
            }
            .show()
    }

    private fun abrirGoogleMaps() {
        val destino = Uri.encode("Rua das Flores, 123, Maringá PR")
        val uri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$destino&travelmode=driving")
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    private fun ampliarGps() {
        val panel = findViewById<LinearLayout>(R.id.panelMapaAmpliado)
        panel.visibility = if (panel.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    private fun enviarMensagem() {
        val input = findViewById<EditText>(R.id.editMensagemChat)
        val historico = findViewById<TextView>(R.id.textChatHistorico)
        val mensagem = input.text.toString().trim()
        if (mensagem.isBlank()) return
        historico.text = "${historico.text}\n\nKelly: $mensagem"
        input.setText("")
    }
}

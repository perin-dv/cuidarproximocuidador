package com.mesawa.cuidarproximocuidador.ui.propostas

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.mesawa.cuidarproximocuidador.R
import com.mesawa.cuidarproximocuidador.ui.andamento.AndamentoCuidadorActivity

class PropostaDetalheActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proposta_detalhe)

        findViewById<Button>(R.id.buttonAceitarProposta).setOnClickListener { confirmarAceite() }
        findViewById<Button>(R.id.buttonRecusarProposta).setOnClickListener { finish() }
    }

    private fun confirmarAceite() {
        AlertDialog.Builder(this)
            .setTitle("Confirmar aceite")
            .setMessage("Você confirma que analisou os dados da Irene e consegue realizar esse cuidado com segurança?")
            .setNegativeButton("Revisar", null)
            .setPositiveButton("Confirmo e aceito") { _, _ ->
                Toast.makeText(this, "Proposta aceita. Ela aparecerá na sua agenda.", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, AndamentoCuidadorActivity::class.java))
                finish()
            }
            .show()
    }
}

package com.mesawa.cuidarproximocuidador.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.mesawa.cuidarproximocuidador.R

class InicioCuidadorFragment : Fragment() {

    private var disponivel = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_cuidador_inicio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val nome = FirebaseAuth.getInstance().currentUser?.displayName ?: "Kelly"
        view.findViewById<TextView>(R.id.textInicioNome).text = "Olá, $nome"
        val button = view.findViewById<Button>(R.id.buttonDisponivel)
        val status = view.findViewById<TextView>(R.id.textStatusDisponivel)
        button.setOnClickListener {
            disponivel = !disponivel
            button.text = if (disponivel) "Ficar indisponível" else "Ficar disponível"
            status.text = if (disponivel) {
                "Você está disponível. Quando chegar proposta, o app mostra detalhes antes de aceitar."
            } else {
                "Você está indisponível. O GPS fica desligado até você entrar online."
            }
            status.setBackgroundResource(if (disponivel) R.drawable.bg_status_good else R.drawable.bg_status_warn)
        }
    }
}

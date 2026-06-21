package com.mesawa.cuidarproximocuidador.ui.andamento

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.mesawa.cuidarproximocuidador.R

class AndamentoCuidadorFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_cuidador_andamento, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<Button>(R.id.buttonAbrirAndamentoTeste).setOnClickListener {
            startActivity(Intent(requireContext(), AndamentoCuidadorActivity::class.java))
        }
    }
}

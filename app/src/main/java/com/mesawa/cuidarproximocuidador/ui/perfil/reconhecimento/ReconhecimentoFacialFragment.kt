package com.mesawa.cuidarproximocuidador.ui.perfil.reconhecimento

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mesawa.cuidarproximocuidador.R

class ReconhecimentoFacialFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.activity_perfil_reconhecimento, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ViewModelProvider(this)[ReconhecimentoFacialViewModel::class.java]
        view.findViewById<TextView>(R.id.buttonVoltarReconhecimento).setOnClickListener { requireActivity().finish() }
        view.findViewById<Button>(R.id.buttonIniciarReconhecimento).setOnClickListener {
            Toast.makeText(requireContext(), "Câmera e documento entram na próxima etapa.", Toast.LENGTH_LONG).show()
        }
    }
}

package com.mesawa.cuidarproximocuidador.ui.perfil.avaliacoes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.mesawa.cuidarproximocuidador.R
import com.mesawa.cuidarproximocuidador.ui.HomeCuidadorActivity
import com.mesawa.cuidarproximocuidador.ui.perfil.ImagemUrlLoader
import com.mesawa.cuidarproximocuidador.ui.perfil.PerfilLocalCache

class AvaliacoesFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.activity_perfil_avaliacoes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewModel = ViewModelProvider(this)[AvaliacoesViewModel::class.java]
        view.findViewById<TextView>(R.id.buttonVoltarAvaliacoes).setOnClickListener { requireActivity().finish() }
        viewModel.titulo.observe(viewLifecycleOwner) {
            view.findViewById<TextView>(R.id.textAvaliacoesTitulo).text = it
        }
        viewModel.comentarios.observe(viewLifecycleOwner) {
            view.findViewById<TextView>(R.id.textAvaliacoesLista).text = it
        }
        carregarFoto(view.findViewById(R.id.imageAvaliacaoPerfil))
        val uid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        val cuidadorId = requireActivity().intent.getStringExtra(HomeCuidadorActivity.EXTRA_ID).orEmpty()
        viewModel.carregar(uid, cuidadorId)
    }

    private fun carregarFoto(imageView: ImageView) {
        val cache = PerfilLocalCache(requireContext())
        val id = requireActivity().intent.getStringExtra(HomeCuidadorActivity.EXTRA_ID).orEmpty()
        val fotoUrl = cache.carregar(id)?.fotoUrl
            .orEmpty()
            .ifBlank { requireActivity().intent.getStringExtra(HomeCuidadorActivity.EXTRA_FOTO_URL).orEmpty() }
        ImagemUrlLoader.carregar(imageView, fotoUrl)
    }
}

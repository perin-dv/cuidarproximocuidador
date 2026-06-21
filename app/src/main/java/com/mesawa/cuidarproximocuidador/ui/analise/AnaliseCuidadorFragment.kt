package com.mesawa.cuidarproximocuidador.ui.analise

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.mesawa.cuidarproximocuidador.R
import com.mesawa.cuidarproximocuidador.ui.HomeCuidadorActivity
import com.mesawa.cuidarproximocuidador.ui.perfil.ImagemUrlLoader
import com.mesawa.cuidarproximocuidador.ui.perfil.PerfilLocalCache

class AnaliseCuidadorFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_cuidador_analise, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val nome = requireActivity().intent.getStringExtra(HomeCuidadorActivity.EXTRA_NOME).orEmpty()
        if (nome.isNotBlank()) {
            view.findViewById<TextView>(R.id.textAnaliseNome).text = nome
        }
        carregarFoto(view.findViewById(R.id.imageAnalisePerfil))
    }

    private fun carregarFoto(imageView: ImageView) {
        val cache = PerfilLocalCache(requireContext())
        val uid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        val id = requireActivity().intent.getStringExtra(HomeCuidadorActivity.EXTRA_ID).orEmpty()
        val fotoUrl = cache.carregar(uid)?.fotoUrl
            .orEmpty()
            .ifBlank { cache.carregar(id)?.fotoUrl.orEmpty() }
            .ifBlank { requireActivity().intent.getStringExtra(HomeCuidadorActivity.EXTRA_FOTO_URL).orEmpty() }
        ImagemUrlLoader.carregar(imageView, fotoUrl)
    }
}

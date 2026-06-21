package com.mesawa.cuidarproximocuidador.ui.perfil.avaliacoes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
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
        viewModel.avaliacoes.observe(viewLifecycleOwner) {
            renderizarAvaliacoes(view.findViewById(R.id.containerAvaliacoes), it)
        }
        viewModel.mensagem.observe(viewLifecycleOwner) {
            view.findViewById<TextView>(R.id.textAvaliacoesMensagem).text = it
        }
        carregarFoto(view.findViewById(R.id.imageAvaliacaoPerfil))
        val uid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        val cuidadorId = requireActivity().intent.getStringExtra(HomeCuidadorActivity.EXTRA_ID).orEmpty()
        viewModel.carregar(uid, cuidadorId)
    }

    private fun carregarFoto(imageView: ImageView) {
        val cache = PerfilLocalCache(requireContext())
        val id = requireActivity().intent.getStringExtra(HomeCuidadorActivity.EXTRA_ID).orEmpty()
        val uid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        val fotoUrl = cache.carregar(uid)?.fotoUrl
            .orEmpty()
            .ifBlank { cache.carregar(id)?.fotoUrl.orEmpty() }
            .orEmpty()
            .ifBlank { requireActivity().intent.getStringExtra(HomeCuidadorActivity.EXTRA_FOTO_URL).orEmpty() }
        ImagemUrlLoader.carregar(imageView, fotoUrl)
    }

    private fun renderizarAvaliacoes(container: LinearLayout, avaliacoes: List<AvaliacaoRecebida>) {
        container.removeAllViews()
        avaliacoes.forEach { avaliacao ->
            container.addView(cardAvaliacao(avaliacao))
        }
    }

    private fun cardAvaliacao(avaliacao: AvaliacaoRecebida): View {
        val density = resources.displayMetrics.density
        val card = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundResource(R.drawable.bg_profile_card)
            elevation = 3f * density
            setPadding((18 * density).toInt(), (16 * density).toInt(), (18 * density).toInt(), (16 * density).toInt())
        }
        card.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = (10 * density).toInt()
        }
        card.addView(TextView(requireContext()).apply {
            text = estrelas(avaliacao.estrelas)
            textSize = 25f
            setTextColor(android.graphics.Color.parseColor("#D99A00"))
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        })
        card.addView(TextView(requireContext()).apply {
            text = avaliacao.cliente
            textSize = 17f
            setTextColor(android.graphics.Color.parseColor("#0F172A"))
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        })
        card.addView(TextView(requireContext()).apply {
            text = avaliacao.comentario
            textSize = 14f
            setTextColor(android.graphics.Color.parseColor("#475569"))
            setPadding(0, (6 * density).toInt(), 0, 0)
            setLineSpacing(2f * density, 1.0f)
        })
        if (avaliacao.data.isNotBlank()) {
            card.addView(TextView(requireContext()).apply {
                text = avaliacao.data
                textSize = 12f
                setTextColor(android.graphics.Color.parseColor("#94A3B8"))
                setPadding(0, (10 * density).toInt(), 0, 0)
            })
        }
        return card
    }

    private fun estrelas(nota: Double): String {
        val cheias = nota.toInt().coerceIn(1, 5)
        return List(cheias) { "★" }.joinToString(" ")
    }
}

package com.mesawa.cuidarproximocuidador.ui.perfil

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mesawa.cuidarproximocuidador.R
import com.mesawa.cuidarproximocuidador.Login.LoginActivity
import com.mesawa.cuidarproximocuidador.ui.HomeCuidadorActivity
import com.mesawa.cuidarproximocuidador.ui.perfil.avaliacoes.AvaliacoesActivity
import com.mesawa.cuidarproximocuidador.ui.perfil.configuracoes.ConfiguracoesActivity
import com.mesawa.cuidarproximocuidador.ui.perfil.dados.DadosProfissionaisActivity
import com.mesawa.cuidarproximocuidador.ui.perfil.reconhecimento.ReconhecimentoFacialActivity
import com.mesawa.cuidarproximocuidador.ui.perfil.suporte.SuporteSacActivity
import java.text.NumberFormat
import java.util.Locale

class PerfilCuidadorFragment : Fragment() {

    private lateinit var viewModel: PerfilCuidadorViewModel
    private lateinit var fotoPerfil: ImageView
    private lateinit var nome: TextView
    private lateinit var especialidade: TextView
    private lateinit var resumo: TextView
    private lateinit var status: TextView
    private var ultimaFotoUrl: String = ""
    private var ultimoCuidadorId: String = ""

    private val escolherFoto = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            abrirAjusteFoto(uri)
        }
    }

    private val ajustarFoto = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val uri = result.data
            ?.getStringExtra(PhotoAdjustActivity.EXTRA_RESULT_URI)
            ?.let(Uri::parse)
        if (result.resultCode == android.app.Activity.RESULT_OK && uri != null) {
            fotoPerfil.setImageURI(uri)
            viewModel.salvarFoto(uri)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_cuidador_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[PerfilCuidadorViewModel::class.java]

        fotoPerfil = view.findViewById(R.id.imageFotoPerfil)
        nome = view.findViewById(R.id.textPerfilNome)
        especialidade = view.findViewById(R.id.textPerfilEspecialidade)
        resumo = view.findViewById(R.id.textPerfilResumo)
        status = view.findViewById(R.id.textPerfilStatus)

        view.findViewById<View>(R.id.buttonAlterarFotoPerfil).setOnClickListener {
            escolherFoto.launch("image/*")
        }
        view.findViewById<View>(R.id.rowDadosProfissionais).setOnClickListener { abrir(DadosProfissionaisActivity::class.java) }
        view.findViewById<View>(R.id.rowAvaliacoes).setOnClickListener { abrir(AvaliacoesActivity::class.java) }
        view.findViewById<View>(R.id.rowSuporte).setOnClickListener { abrir(SuporteSacActivity::class.java) }
        view.findViewById<View>(R.id.rowConfiguracoes).setOnClickListener { abrir(ConfiguracoesActivity::class.java) }
        view.findViewById<View>(R.id.rowReconhecimentoFacial).setOnClickListener { abrir(ReconhecimentoFacialActivity::class.java) }
        view.findViewById<View>(R.id.rowSairConta).setOnClickListener { confirmarSaida() }

        observarEstado()
        carregarPerfil()
    }

    private fun observarEstado() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            aplicarDados(state.dados)
            if (!state.mensagem.isNullOrBlank()) {
                Toast.makeText(requireContext(), state.mensagem, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun carregarPerfil() {
        viewModel.carregar(
            cuidadorId = requireActivity().intent.getStringExtra(HomeCuidadorActivity.EXTRA_ID).orEmpty(),
            fallbackNome = requireActivity().intent.getStringExtra(HomeCuidadorActivity.EXTRA_NOME).orEmpty(),
            fallbackEspecialidade = requireActivity().intent.getStringExtra(HomeCuidadorActivity.EXTRA_ESPECIALIDADE).orEmpty(),
            fallbackFotoUrl = requireActivity().intent.getStringExtra(HomeCuidadorActivity.EXTRA_FOTO_URL).orEmpty()
        )
    }

    private fun abrirAjusteFoto(uri: Uri) {
        ajustarFoto.launch(
            Intent(requireContext(), PhotoAdjustActivity::class.java)
                .putExtra(PhotoAdjustActivity.EXTRA_URI, uri.toString())
        )
    }

    private fun abrir(activityClass: Class<*>) {
        startActivity(
            Intent(requireContext(), activityClass)
                .putExtra(HomeCuidadorActivity.EXTRA_ID, ultimoCuidadorId.ifBlank {
                    requireActivity().intent.getStringExtra(HomeCuidadorActivity.EXTRA_ID).orEmpty()
                })
                .putExtra(HomeCuidadorActivity.EXTRA_NOME, nome.text.toString())
                .putExtra(HomeCuidadorActivity.EXTRA_ESPECIALIDADE, especialidade.text.toString())
                .putExtra(HomeCuidadorActivity.EXTRA_FOTO_URL, ultimaFotoUrl)
        )
    }

    private fun confirmarSaida() {
        AlertDialog.Builder(requireContext())
            .setTitle("Sair da conta")
            .setMessage("Deseja encerrar sua sessão neste aparelho?")
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Sair") { _, _ ->
                viewModel.sair()
                startActivity(
                    Intent(requireContext(), LoginActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
            }
            .show()
    }

    private fun aplicarDados(dados: PerfilCuidadorDados) {
        ultimoCuidadorId = dados.id
        nome.text = dados.nome
        especialidade.text = listOf(dados.especialidade, dados.cidade)
            .filter { it.isNotBlank() }
            .joinToString(" • ")
            .ifBlank { "Cuidadora profissional" }
        resumo.text = "${formatarNota(dados.avaliacao)} estrelas • ${dados.atendimentos} atendimentos"
        status.text = if (dados.ativo) {
            if (dados.reconhecimentoFacial) "● Perfil ativo e verificado" else "● Perfil ativo"
        } else {
            "● Perfil em análise"
        }

        if (dados.fotoUrl.isNotBlank() && dados.fotoUrl != ultimaFotoUrl) {
            ultimaFotoUrl = dados.fotoUrl
            carregarFoto(dados.fotoUrl)
        }
    }

    private fun carregarFoto(fotoUrl: String) {
        ImagemUrlLoader.carregar(fotoPerfil, fotoUrl)
    }

    private fun formatarNota(nota: Double): String {
        if (nota <= 0.0) return "0.0"
        return NumberFormat.getNumberInstance(Locale("pt", "BR")).apply {
            minimumFractionDigits = 1
            maximumFractionDigits = 1
        }.format(nota)
    }
}

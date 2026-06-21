package com.mesawa.cuidarproximocuidador.ui.perfil.dados

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mesawa.cuidarproximocuidador.R
import com.mesawa.cuidarproximocuidador.ui.HomeCuidadorActivity

class DadosProfissionaisFragment : Fragment() {
    private val certificados = mutableListOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.activity_perfil_dados_profissionais, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewModel = ViewModelProvider(this)[DadosProfissionaisViewModel::class.java]
        configurarFuncoes(view)
        view.findViewById<TextView>(R.id.buttonVoltarDados).setOnClickListener { requireActivity().finish() }
        view.findViewById<View>(R.id.buttonAdicionarCertificado).setOnClickListener { abrirModalCertificado(view) }
        view.findViewById<View>(R.id.buttonSalvarDadosProfissionais).setOnClickListener {
            viewModel.salvar(
                cuidadorId = requireActivity().intent.getStringExtra(HomeCuidadorActivity.EXTRA_ID).orEmpty(),
                form = coletar(view),
                onSuccess = { mensagem ->
                    Toast.makeText(requireContext(), mensagem, Toast.LENGTH_LONG).show()
                    requireActivity().finish()
                },
                onFailure = { mensagem -> Toast.makeText(requireContext(), mensagem, Toast.LENGTH_LONG).show() }
            )
        }
    }

    private fun coletar(view: View): DadosProfissionaisForm {
        return DadosProfissionaisForm(
            funcao = view.findViewById<Spinner>(R.id.spinnerFuncaoProfissional).selectedItem?.toString().orEmpty(),
            anoInicio = texto(view, R.id.editAnoInicio),
            anoFim = texto(view, R.id.editAnoFim),
            valorHora = texto(view, R.id.editDadosValorHora),
            especialidades = texto(view, R.id.editDadosEspecialidades),
            atendeAlzheimer = marcado(view, R.id.checkAlzheimer),
            atendeMobilidade = marcado(view, R.id.checkMobilidade),
            atendeMedicamentos = marcado(view, R.id.checkMedicamentos),
            atendeCompanhia = marcado(view, R.id.checkCompanhia),
            bio = texto(view, R.id.editDadosBio),
            sobreVoce = texto(view, R.id.editDadosSobreVoce),
            diasDisponiveis = diasSelecionados(view),
            horaInicio = texto(view, R.id.editHoraInicio),
            horaFim = texto(view, R.id.editHoraFim),
            certificados = certificados.toList()
        )
    }

    private fun configurarFuncoes(view: View) {
        val funcoes = listOf(
            "Cuidadora de idosos",
            "Cuidador de idosos",
            "Acompanhante de idosos",
            "Acompanhante hospitalar",
            "Técnico(a) em enfermagem",
            "Auxiliar de enfermagem",
            "Enfermeiro(a)",
            "Fisioterapeuta",
            "Terapeuta ocupacional",
            "Nutricionista",
            "Educador(a) físico para idosos"
        )
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, funcoes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        view.findViewById<Spinner>(R.id.spinnerFuncaoProfissional).adapter = adapter
    }

    private fun abrirModalCertificado(root: View) {
        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(42, 18, 42, 0)
        }
        val curso = EditText(requireContext()).apply { hint = "Nome do curso/certificado" }
        val instituicao = EditText(requireContext()).apply { hint = "Instituição onde fez" }
        val ano = EditText(requireContext()).apply {
            hint = "Ano"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }
        container.addView(curso)
        container.addView(instituicao)
        container.addView(ano)

        AlertDialog.Builder(requireContext())
            .setTitle("Adicionar curso")
            .setView(container)
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Salvar") { _, _ ->
                val item = listOf(curso.text.toString(), instituicao.text.toString(), ano.text.toString())
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                    .joinToString(" • ")
                if (item.isNotBlank()) {
                    certificados.add(item)
                    atualizarCertificados(root)
                }
            }
            .show()
    }

    private fun atualizarCertificados(view: View) {
        val texto = if (certificados.isEmpty()) {
            "Cursos e certificados\nNenhum certificado adicionado"
        } else {
            "Cursos e certificados\n${certificados.joinToString("\n") { "• $it" }}"
        }
        view.findViewById<TextView>(R.id.textCertificados).text = texto
    }

    private fun diasSelecionados(view: View): List<String> {
        return listOf(
            R.id.checkSegunda to "Segunda",
            R.id.checkTerca to "Terça",
            R.id.checkQuarta to "Quarta",
            R.id.checkQuinta to "Quinta",
            R.id.checkSexta to "Sexta",
            R.id.checkSabado to "Sábado",
            R.id.checkDomingo to "Domingo"
        ).filter { (id, _) -> marcado(view, id) }.map { (_, dia) -> dia }
    }

    private fun texto(view: View, id: Int): String = view.findViewById<EditText>(id).text.toString().trim()

    private fun marcado(view: View, id: Int): Boolean = view.findViewById<CheckBox>(id).isChecked
}

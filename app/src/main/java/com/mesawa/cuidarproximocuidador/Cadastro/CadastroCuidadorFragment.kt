package com.mesawa.cuidarproximocuidador.Cadastro

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.mesawa.cuidarproximocuidador.R

class CadastroCuidadorFragment : Fragment() {

    interface Callbacks {
        fun onEnviarCadastro(dados: CadastroCuidadorDados)
    }

    private lateinit var messageView: TextView
    private lateinit var progress: ProgressBar
    private lateinit var enviarButton: Button
    private lateinit var fotoPreview: ImageView
    private var fotoPerfilUri: Uri? = null

    private val escolherFoto = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            fotoPerfilUri = uri
            fotoPreview.setImageURI(uri)
            showMessage("Foto selecionada. Ela será enviada junto com seu cadastro.")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_cadastro_cuidador, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        messageView = view.findViewById(R.id.textCadastroMessage)
        progress = view.findViewById(R.id.progressCadastro)
        enviarButton = view.findViewById(R.id.buttonEnviarCadastro)
        fotoPreview = view.findViewById(R.id.imageFotoPerfilCadastro)
        view.findViewById<Button>(R.id.buttonEscolherFotoPerfil).setOnClickListener {
            escolherFoto.launch("image/*")
        }
        enviarButton.setOnClickListener { callbacks()?.onEnviarCadastro(coletarDados(view)) }
    }

    fun showMessage(message: String) {
        messageView.text = message
        messageView.visibility = View.VISIBLE
    }

    fun setLoading(loading: Boolean) {
        progress.visibility = if (loading) View.VISIBLE else View.GONE
        enviarButton.isEnabled = !loading
        enviarButton.text = if (loading) "Enviando..." else "Enviar cadastro para análise"
    }

    private fun coletarDados(view: View): CadastroCuidadorDados {
        return CadastroCuidadorDados(
            nomeCompleto = text(view, R.id.editNomeCompleto),
            cpf = text(view, R.id.editCpf),
            nascimento = text(view, R.id.editNascimento),
            telefone = text(view, R.id.editTelefone),
            email = text(view, R.id.editEmailCadastro),
            senha = text(view, R.id.editSenhaCadastro),
            fotoPerfilUri = fotoPerfilUri?.toString(),
            cidade = text(view, R.id.editCidade),
            uf = text(view, R.id.editUf),
            raioKm = text(view, R.id.editRaio),
            valorHora = text(view, R.id.editValorHora),
            disponibilidade = text(view, R.id.editDisponibilidade),
            especialidade = text(view, R.id.editEspecialidade),
            curso = text(view, R.id.editCurso),
            instituicao = text(view, R.id.editInstituicao),
            experiencia = text(view, R.id.editExperiencia),
            bio = text(view, R.id.editBio),
            experienciaMedicacao = checked(view, R.id.checkMedicacao),
            experienciaMobilidade = checked(view, R.id.checkMobilidade),
            experienciaAlzheimer = checked(view, R.id.checkAlzheimer),
            antecedentes = text(view, R.id.editAntecedentes),
            referenciaNome = text(view, R.id.editReferenciaNome),
            referenciaTelefone = text(view, R.id.editReferenciaTelefone),
            autorizouVerificacao = checked(view, R.id.checkVerificacao)
        )
    }

    private fun text(view: View, id: Int): String = view.findViewById<EditText>(id).text.toString().trim()

    private fun checked(view: View, id: Int): Boolean = view.findViewById<CheckBox>(id).isChecked

    private fun callbacks(): Callbacks? = activity as? Callbacks

    companion object {
        fun newInstance() = CadastroCuidadorFragment()
    }
}

package com.mesawa.cuidarproximocuidador.Cadastro

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.firebase.auth.FirebaseAuth

class CadastroCuidadorActivity : FragmentActivity(), CadastroCuidadorFragment.Callbacks {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val viewModel = CadastroCuidadorViewModel()
    private val repository = CadastroCuidadorRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, CadastroCuidadorFragment.newInstance())
                .commit()
        }
    }

    override fun onEnviarCadastro(dados: CadastroCuidadorDados) {
        val fragment = fragment()
        val erro = viewModel.validar(dados)
        if (erro != null) {
            fragment?.showMessage(erro)
            return
        }

        fragment?.setLoading(true)
        auth.createUserWithEmailAndPassword(dados.email.trim(), dados.senha.trim())
            .addOnSuccessListener {
                val uid = auth.currentUser?.uid
                if (uid.isNullOrBlank()) {
                    fragment?.setLoading(false)
                    fragment?.showMessage("Conta criada, mas não localizei o UID. Tente entrar novamente.")
                    return@addOnSuccessListener
                }
                repository.enviarFotoPerfil(
                    uid = uid,
                    fotoUri = dados.fotoPerfilUri,
                    onSuccess = { fotoUrl -> salvarCadastro(uid, dados, fotoUrl) },
                    onFailure = { mensagem ->
                        fragment?.setLoading(false)
                        fragment?.showMessage(mensagem)
                    }
                )
            }
            .addOnFailureListener {
                fragment?.setLoading(false)
                fragment?.showMessage("Não consegui criar a conta. Verifique email e senha.")
            }
    }

    private fun salvarCadastro(uid: String, dados: CadastroCuidadorDados, fotoUrl: String?) {
        val fragment = fragment()
        repository.salvar(
            uid = uid,
            dados = dados,
            fotoUrl = fotoUrl,
            onSuccess = {
                auth.signOut()
                Toast.makeText(this, "Cadastro enviado para análise.", Toast.LENGTH_LONG).show()
                finish()
            },
            onFailure = { mensagem ->
                fragment?.setLoading(false)
                fragment?.showMessage(mensagem)
            }
        )
    }

    private fun fragment(): CadastroCuidadorFragment? {
        return supportFragmentManager.findFragmentById(android.R.id.content) as? CadastroCuidadorFragment
    }
}

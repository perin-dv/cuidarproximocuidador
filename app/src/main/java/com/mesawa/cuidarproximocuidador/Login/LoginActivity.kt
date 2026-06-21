package com.mesawa.cuidarproximocuidador.Login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.mesawa.cuidarproximocuidador.Cadastro.CadastroCuidadorActivity
import com.mesawa.cuidarproximocuidador.R
import com.mesawa.cuidarproximocuidador.ui.HomeCuidadorActivity

class LoginActivity : FragmentActivity(), LoginFragment.Callbacks {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleClient: GoogleSignInClient
    private val repository = CuidadorRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        googleClient = GoogleSignIn.getClient(
            this,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        )

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, LoginFragment.newInstance())
                .commit()
        }
    }

    override fun onLoginEmail(email: String, senha: String) {
        val fragment = fragment()
        if (email.isBlank() || senha.isBlank()) {
            fragment?.showMessage("Preencha email e senha.")
            return
        }

        fragment?.setLoading(true)
        auth.signInWithEmailAndPassword(email.trim(), senha.trim())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    validarCuidadorLogado()
                } else {
                    fragment?.setLoading(false)
                    fragment?.showMessage("Login nao autorizado. Confira os dados.")
                }
            }
    }

    override fun onLoginGoogle() {
        startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
    }

    override fun onCadastroCuidador() {
        startActivity(Intent(this, CadastroCuidadorActivity::class.java))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != GOOGLE_SIGN_IN) return

        val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            val token = account.idToken
            if (token.isNullOrBlank()) {
                fragment()?.showMessage("Google nao retornou token de acesso.")
                return
            }

            fragment()?.setLoading(true)
            val credential = GoogleAuthProvider.getCredential(token, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { authTask ->
                    if (authTask.isSuccessful) {
                        validarCuidadorLogado()
                    } else {
                        fragment()?.setLoading(false)
                        fragment()?.showMessage("Falha na autenticacao com Google.")
                    }
                }
        } catch (e: ApiException) {
            fragment()?.showMessage("Google login falhou.")
        }
    }

    private fun validarCuidadorLogado() {
        val usuario = auth.currentUser
        if (usuario == null) {
            fragment()?.setLoading(false)
            fragment()?.showMessage("Sessao nao encontrada. Entre novamente.")
            return
        }

        repository.validarCuidador(
            usuario = usuario,
            onSuccess = { perfil -> abrirHome(perfil) },
            onBlocked = { mensagem ->
                auth.signOut()
                googleClient.signOut()
                fragment()?.setLoading(false)
                fragment()?.showMessage(mensagem)
            }
        )
    }

    private fun abrirHome(perfil: CuidadorPerfil) {
        startActivity(
            Intent(this, HomeCuidadorActivity::class.java)
                .putExtra(HomeCuidadorActivity.EXTRA_ID, perfil.id)
                .putExtra(HomeCuidadorActivity.EXTRA_NOME, perfil.nome)
                .putExtra(HomeCuidadorActivity.EXTRA_ESPECIALIDADE, perfil.especialidade)
                .putExtra(HomeCuidadorActivity.EXTRA_FOTO_URL, perfil.fotoUrl)
        )
        finish()
    }

    private fun fragment(): LoginFragment? {
        return supportFragmentManager.findFragmentById(android.R.id.content) as? LoginFragment
    }

    companion object {
        private const val GOOGLE_SIGN_IN = 9101
    }
}

package com.mesawa.cuidarproximocuidador.Login

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mesawa.cuidarproximocuidador.R

class LoginFragment : Fragment() {

    interface Callbacks {
        fun onLoginEmail(email: String, senha: String)
        fun onLoginGoogle()
        fun onCadastroCuidador()
    }

    private lateinit var emailInput: EditText
    private lateinit var senhaInput: EditText
    private lateinit var messageView: TextView
    private lateinit var loginButton: Button
    private lateinit var googleButton: Button
    private lateinit var toggleSenhaButton: ImageButton
    private lateinit var progress: ProgressBar
    private var senhaVisivel = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emailInput = view.findViewById(R.id.editTextEmail)
        senhaInput = view.findViewById(R.id.editTextSenha)
        messageView = view.findViewById(R.id.textLoginMessage)
        loginButton = view.findViewById(R.id.buttonLoginEmail)
        googleButton = view.findViewById(R.id.buttonLoginGoogle)
        toggleSenhaButton = view.findViewById(R.id.buttonToggleSenha)
        progress = view.findViewById(R.id.progressLogin)

        toggleSenhaButton.setOnClickListener { toggleSenha() }
        loginButton.setOnClickListener {
            callbacks()?.onLoginEmail(emailInput.text.toString(), senhaInput.text.toString())
        }
        googleButton.setOnClickListener { callbacks()?.onLoginGoogle() }
        view.findViewById<TextView>(R.id.textCadastroCuidador)
            .setOnClickListener { callbacks()?.onCadastroCuidador() }
    }

    fun showMessage(message: String) {
        if (!this::messageView.isInitialized) return
        messageView.text = message
        messageView.visibility = View.VISIBLE
    }

    fun setLoading(loading: Boolean) {
        if (!this::progress.isInitialized) return
        progress.visibility = if (loading) View.VISIBLE else View.GONE
        loginButton.isEnabled = !loading
        googleButton.isEnabled = !loading
        toggleSenhaButton.isEnabled = !loading
        loginButton.text = if (loading) "Validando..." else "Entrar"
    }

    private fun toggleSenha() {
        senhaVisivel = !senhaVisivel
        senhaInput.transformationMethod =
            if (senhaVisivel) HideReturnsTransformationMethod.getInstance() else PasswordTransformationMethod.getInstance()
        toggleSenhaButton.setImageResource(
            if (senhaVisivel) R.drawable.ic_eye_off_login else R.drawable.ic_eye_login
        )
        toggleSenhaButton.contentDescription = if (senhaVisivel) "Ocultar senha" else "Mostrar senha"
        senhaInput.setSelection(senhaInput.text.length)
    }

    private fun callbacks(): Callbacks? = activity as? Callbacks

    companion object {
        fun newInstance() = LoginFragment()
    }
}

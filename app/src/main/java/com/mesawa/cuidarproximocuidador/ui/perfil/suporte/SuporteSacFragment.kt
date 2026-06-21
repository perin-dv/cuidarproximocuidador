package com.mesawa.cuidarproximocuidador.ui.perfil.suporte

import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.mesawa.cuidarproximocuidador.R
import com.mesawa.cuidarproximocuidador.ui.HomeCuidadorActivity

class SuporteSacFragment : Fragment() {
    private lateinit var viewModel: SuporteSacViewModel
    private lateinit var chat: TextView
    private lateinit var input: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.activity_perfil_suporte, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[SuporteSacViewModel::class.java]
        chat = view.findViewById(R.id.textChatIa)
        input = view.findViewById(R.id.editPerguntaIa)
        view.findViewById<TextView>(R.id.buttonVoltarSuporte).setOnClickListener { requireActivity().finish() }
        view.findViewById<View>(R.id.cardEmailSac).setOnClickListener {
            registrarAcaoSac("email", "suporte@cuidarproximo.app")
            startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:suporte@cuidarproximo.app")))
        }
        view.findViewById<View>(R.id.cardTelefoneSac).setOnClickListener {
            registrarAcaoSac("telefone", "08000002026")
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:08000002026")))
        }
        view.findViewById<Button>(R.id.buttonEnviarIa).setOnClickListener {
            val uid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
            val cuidadorId = requireActivity().intent.getStringExtra(HomeCuidadorActivity.EXTRA_ID).orEmpty()
            viewModel.perguntar(uid, cuidadorId, input.text.toString().trim())
            input.setText("")
        }
        viewModel.chat.observe(viewLifecycleOwner) { chat.text = it }
    }

    private fun registrarAcaoSac(tipo: String, destino: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        val cuidadorId = requireActivity().intent.getStringExtra(HomeCuidadorActivity.EXTRA_ID).orEmpty()
        viewModel.registrarAcaoSac(uid, cuidadorId, tipo, destino)
    }
}

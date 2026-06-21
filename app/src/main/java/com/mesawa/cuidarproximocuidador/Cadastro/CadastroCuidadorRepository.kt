package com.mesawa.cuidarproximocuidador.Cadastro

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mesawa.cuidarproximocuidador.data.local.LocalSqlStore

class CadastroCuidadorRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance(),
    private val viewModel: CadastroCuidadorViewModel = CadastroCuidadorViewModel(),
    private val localSql: LocalSqlStore = LocalSqlStore.instance
) {

    fun enviarFotoPerfil(
        uid: String,
        fotoUri: String?,
        onSuccess: (String?) -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (fotoUri.isNullOrBlank()) {
            onSuccess(null)
            return
        }

        val uri = Uri.parse(fotoUri)
        val ref = storage.reference.child("cuidadores/$uid/perfil/foto_perfil.jpg")
        ref.putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                ref.downloadUrl
            }
            .addOnSuccessListener { downloadUri -> onSuccess(downloadUri.toString()) }
            .addOnFailureListener { onFailure("Não consegui enviar a foto. Tente escolher outra imagem.") }
    }

    fun salvar(
        uid: String,
        dados: CadastroCuidadorDados,
        fotoUrl: String?,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val payload = viewModel.toFirestore(uid, dados, fotoUrl)
        localSql.salvarRegistro(uid, "cadastro_cuidador", "dados_principais", payload)

        firestore.collection("cuidadores_cadastros")
            .document(uid)
            .set(payload)
            .addOnSuccessListener {
                localSql.salvarRegistro(uid, "cadastro_cuidador", "dados_principais", payload, sincronizado = true)
                onSuccess()
            }
            .addOnFailureListener { onFailure("Não consegui salvar o cadastro agora.") }
    }
}

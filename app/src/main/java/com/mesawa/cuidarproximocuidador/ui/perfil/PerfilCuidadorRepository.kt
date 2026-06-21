package com.mesawa.cuidarproximocuidador.ui.perfil

import android.net.Uri
import com.google.firebase.firestore.Source
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.mesawa.cuidarproximocuidador.data.local.LocalSqlStore
import java.util.Locale

class PerfilCuidadorRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance(),
    private val localSql: LocalSqlStore = LocalSqlStore.instance
) {

    val uidAtual: String
        get() = auth.currentUser?.uid.orEmpty()

    fun sair() {
        auth.signOut()
    }

    fun carregarPerfil(
        cuidadorId: String,
        fallbackNome: String,
        fallbackEspecialidade: String,
        fallbackFotoUrl: String,
        onSuccess: (PerfilCuidadorDados) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val uid = auth.currentUser?.uid.orEmpty()
        firestore.collection("cuidadores")
            .document("profissionais")
            .get(Source.SERVER)
            .addOnSuccessListener { doc ->
                val medicos = doc.get("medicos") as? Map<*, *>
                val entry = medicos?.entries?.firstOrNull { (id, value) ->
                    val dados = value as? Map<*, *> ?: return@firstOrNull false
                    id.toString() == cuidadorId || texto(dados["uid"]) == uid
                }

                val dadosMap = entry?.value as? Map<*, *>
                onSuccess(
                    PerfilCuidadorDados(
                        id = entry?.key?.toString().orEmpty().ifBlank { cuidadorId },
                        uid = uid,
                        nome = texto(dadosMap?.get("nome")).ifBlank { fallbackNome.ifBlank { "Cuidadora" } },
                        especialidade = texto(dadosMap?.get("especialidade")).ifBlank { fallbackEspecialidade.ifBlank { "Cuidadora profissional" } },
                        cidade = texto(dadosMap?.get("cidade")).ifBlank { texto(dadosMap?.get("localizacao")) },
                        avaliacao = numero(dadosMap?.get("avaliacao")),
                        atendimentos = inteiro(dadosMap?.get("atendimentos")),
                        faturamentoMes = numero(dadosMap?.get("faturamentoMes")).ifZero { calcularFaturamentoEstimado(dadosMap) },
                        fotoUrl = texto(dadosMap?.get("fotoUrl")).ifBlank { fallbackFotoUrl },
                        ativo = ativo(dadosMap?.get("ativo")),
                        reconhecimentoFacial = ativo(dadosMap?.get("reconhecimentoFacial"))
                    )
                )
            }
            .addOnFailureListener { onFailure("Não consegui carregar os dados do perfil agora.") }
    }

    fun salvarFotoPerfil(
        cuidadorId: String,
        fotoUri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val uid = auth.currentUser?.uid
        if (uid.isNullOrBlank()) {
            onFailure("Entre novamente para alterar a foto.")
            return
        }

        val ref = storage.reference.child("cuidadores/$uid/perfil/foto_perfil.jpg")
        ref.putFile(fotoUri)
            .continueWithTask { task ->
                if (!task.isSuccessful) task.exception?.let { throw it }
                ref.downloadUrl
            }
            .addOnSuccessListener { downloadUri ->
                val fotoUrl = downloadUri.toString()
                salvarFotoUrl(uid, cuidadorId, fotoUrl, onSuccess, onFailure)
            }
            .addOnFailureListener { error -> onFailure(mensagemStorage(error)) }
    }

    private fun salvarFotoUrl(
        uid: String,
        cuidadorId: String,
        fotoUrl: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val payload = mapOf(
            "uid" to uid,
            "cuidadorId" to cuidadorId,
            "fotoUrl" to fotoUrl
        )
        localSql.salvarRegistro(uid, "perfil", "foto", payload)

        firestore.collection("cuidadores_cadastros")
            .document(uid)
            .set(mapOf("fotoUrl" to fotoUrl), SetOptions.merge())

        val updates = mutableMapOf<String, Any>("fotoUrl" to fotoUrl)
        if (cuidadorId.isNotBlank()) {
            updates["medicos.$cuidadorId.fotoUrl"] = fotoUrl
        }

        firestore.collection("cuidadores")
            .document("profissionais")
            .update(updates)
            .addOnSuccessListener {
                localSql.salvarRegistro(uid, "perfil", "foto", payload, sincronizado = true)
                onSuccess(fotoUrl)
            }
            .addOnFailureListener { onFailure("Foto enviada, mas não consegui atualizar o perfil profissional.") }
    }

    private fun mensagemStorage(error: Exception): String {
        val storageError = error as? StorageException
        val motivo = when (storageError?.errorCode) {
            StorageException.ERROR_NOT_AUTHENTICATED -> "entre novamente na conta"
            StorageException.ERROR_NOT_AUTHORIZED -> "as regras do Storage bloquearam o envio"
            StorageException.ERROR_BUCKET_NOT_FOUND -> "o Firebase Storage ainda não está habilitado"
            StorageException.ERROR_QUOTA_EXCEEDED -> "a cota do Storage foi excedida"
            else -> error.localizedMessage ?: "erro desconhecido"
        }
        return "Não consegui enviar a foto: $motivo."
    }

    private fun calcularFaturamentoEstimado(dados: Map<*, *>?): Double {
        val atendimentos = inteiro(dados?.get("atendimentos"))
        val valorHora = numero(dados?.get("valorHora")).ifZero { numero(dados?.get("valor_hora")) }
        return atendimentos * valorHora
    }

    private fun texto(value: Any?): String = value?.toString()?.trim().orEmpty()

    private fun numero(value: Any?): Double {
        return when (value) {
            is Number -> value.toDouble()
            is String -> value.replace("R$", "", true)
                .replace(".", "")
                .replace(",", ".")
                .trim()
                .toDoubleOrNull() ?: 0.0
            else -> 0.0
        }
    }

    private fun inteiro(value: Any?): Int {
        return when (value) {
            is Number -> value.toInt()
            is String -> value.filter { it.isDigit() }.toIntOrNull() ?: 0
            else -> 0
        }
    }

    private fun ativo(value: Any?): Boolean {
        return when (value) {
            is Boolean -> value
            is Number -> value.toInt() == 1
            is String -> value.lowercase(Locale.ROOT) in listOf("true", "sim", "ativo", "aprovado", "aprovada")
            else -> false
        }
    }

    private fun Double.ifZero(block: () -> Double): Double = if (this == 0.0) block() else this
}

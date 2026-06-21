package com.mesawa.cuidarproximocuidador.ui.perfil.configuracoes

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mesawa.cuidarproximocuidador.data.local.LocalSqlStore

class ConfiguracoesRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val localSql: LocalSqlStore = LocalSqlStore.instance
) {
    fun salvarDadosConta(
        nome: String,
        telefone: String,
        cidade: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val uid = uidAtual(onFailure) ?: return
        val payload = mapOf(
            "dados_pessoais.nome_publico" to nome,
            "dados_pessoais.telefone" to telefone,
            "dados_pessoais.cidade" to cidade,
            "atualizado_em" to Timestamp.now()
        )
        localSql.salvarRegistro(uid, "configuracoes", "dados_conta", payload)
        firestore.collection("cuidadores_cadastros")
            .document(uid)
            .set(
                mapOf(
                    "dados_pessoais" to mapOf(
                        "nome_publico" to nome,
                        "telefone" to telefone,
                        "cidade" to cidade
                    ),
                    "atualizado_em" to Timestamp.now()
                ),
                SetOptions.merge()
            )
            .addOnSuccessListener {
                localSql.salvarRegistro(uid, "configuracoes", "dados_conta", payload, sincronizado = true)
                onSuccess()
            }
            .addOnFailureListener { onFailure("Nao consegui salvar os dados da conta agora.") }
    }

    fun salvarEndereco(
        endereco: String,
        cidade: String,
        raioKm: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val uid = uidAtual(onFailure) ?: return
        val payload = mapOf(
            "endereco_base" to endereco,
            "cidade" to cidade,
            "raio_km" to raioKm,
            "atualizado_em" to Timestamp.now()
        )
        localSql.salvarRegistro(uid, "configuracoes", "endereco_atendimento", payload)
        firestore.collection("cuidadores_cadastros")
            .document(uid)
            .set(
                mapOf(
                    "atendimento" to mapOf(
                        "endereco_base" to endereco,
                        "cidade" to cidade,
                        "raio_km" to raioKm
                    ),
                    "atualizado_em" to Timestamp.now()
                ),
                SetOptions.merge()
            )
            .addOnSuccessListener {
                localSql.salvarRegistro(uid, "configuracoes", "endereco_atendimento", payload, sincronizado = true)
                onSuccess()
            }
            .addOnFailureListener { onFailure("Nao consegui salvar endereco e regioes agora.") }
    }

    fun alterarSenha(novaSenha: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val user = auth.currentUser
        val uid = user?.uid
        if (user == null || uid.isNullOrBlank()) {
            onFailure("Entre novamente para trocar a senha.")
            return
        }
        val payload = mapOf("acao" to "troca_senha", "atualizado_em" to Timestamp.now())
        localSql.salvarEvento(uid, "seguranca_conta", payload)
        user.updatePassword(novaSenha)
            .addOnSuccessListener {
                registrarSolicitacao(uid, "senha_alterada", "Senha alterada pelo app")
                onSuccess()
            }
            .addOnFailureListener {
                onFailure("Nao consegui trocar a senha. Entre novamente e tente outra vez.")
            }
    }

    fun solicitarStatus(tipo: String, mensagem: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val uid = uidAtual(onFailure) ?: return
        val payload = mapOf(
            "tipo" to tipo,
            "mensagem" to mensagem,
            "status" to "solicitado",
            "criado_em" to Timestamp.now()
        )
        localSql.salvarEvento(uid, "solicitacao_conta", payload)
        firestore.collection("cuidadores_cadastros")
            .document(uid)
            .collection("solicitacoes_conta")
            .add(payload)
            .addOnSuccessListener {
                localSql.salvarRegistro(uid, "solicitacao_conta", it.id, payload, sincronizado = true)
                onSuccess()
            }
            .addOnFailureListener { onFailure("Nao consegui registrar a solicitacao agora.") }
    }

    private fun registrarSolicitacao(uid: String, tipo: String, mensagem: String) {
        val payload = mapOf(
            "tipo" to tipo,
            "mensagem" to mensagem,
            "status" to "concluido",
            "criado_em" to Timestamp.now()
        )
        localSql.salvarEvento(uid, "seguranca_conta", payload, sincronizado = true)
        firestore.collection("cuidadores_cadastros")
            .document(uid)
            .collection("seguranca_conta")
            .add(payload)
    }

    private fun uidAtual(onFailure: (String) -> Unit): String? {
        val uid = auth.currentUser?.uid
        if (uid.isNullOrBlank()) {
            onFailure("Entre novamente para salvar essa alteracao.")
            return null
        }
        return uid
    }
}

package com.mesawa.cuidarproximocuidador.Login

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class CuidadorRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    fun validarCuidador(
        usuario: FirebaseUser,
        onSuccess: (CuidadorPerfil) -> Unit,
        onBlocked: (String) -> Unit
    ) {
        val uid = usuario.uid
        val email = usuario.email.orEmpty().lowercase(Locale.ROOT)

        firestore.collection("cuidadores")
            .document("profissionais")
            .get()
            .addOnSuccessListener { doc ->
                val medicosMap = doc.get("medicos") as? Map<*, *>
                val perfil = medicosMap
                    ?.mapNotNull { (id, value) ->
                        val dados = value as? Map<*, *> ?: return@mapNotNull null
                        val uidCadastro = texto(dados["uid"])
                        val emailCadastro = (texto(dados["email"]).ifBlank { texto(dados["emailCuidador"]) })
                            .lowercase(Locale.ROOT)

                        if (uidCadastro == uid || (email.isNotBlank() && emailCadastro == email)) {
                            CuidadorPerfil(
                                id = id.toString(),
                                nome = texto(dados["nome"]).ifBlank { usuario.displayName ?: "Cuidador" },
                                especialidade = texto(dados["especialidade"]).ifBlank { "Cuidador profissional" },
                                ativo = estaAtivo(dados["ativo"]),
                                fotoUrl = texto(dados["fotoUrl"]).ifBlank { texto(dados["foto_url"]) }
                            )
                        } else {
                            null
                        }
                    }
                    ?.firstOrNull()

                when {
                    perfil == null -> onBlocked("Este login ainda nao esta vinculado a um cuidador cadastrado.")
                    !perfil.ativo -> onBlocked("Seu cadastro foi encontrado, mas ainda esta inativo.")
                    else -> onSuccess(perfil)
                }
            }
            .addOnFailureListener {
                onBlocked("Nao consegui consultar a base de cuidadores agora.")
            }
    }

    private fun texto(value: Any?): String = value?.toString()?.trim().orEmpty()

    private fun estaAtivo(value: Any?): Boolean {
        return when (value) {
            is Boolean -> value
            is String -> value.equals("true", true) || value.equals("ativo", true) || value.equals("sim", true)
            is Number -> value.toInt() == 1
            else -> false
        }
    }
}

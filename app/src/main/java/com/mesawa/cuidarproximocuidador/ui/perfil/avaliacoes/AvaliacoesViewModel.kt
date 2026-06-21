package com.mesawa.cuidarproximocuidador.ui.perfil.avaliacoes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AvaliacoesViewModel : ViewModel() {
    private val repository = AvaliacoesRepository()
    private val _titulo = MutableLiveData("5.0 estrelas")
    val titulo: LiveData<String> = _titulo
    private val _comentarios = MutableLiveData("Carregando avaliacoes...")
    val comentarios: LiveData<String> = _comentarios

    fun carregar(uid: String, cuidadorId: String) {
        repository.carregar(
            uid = uid,
            cuidadorId = cuidadorId,
            onSuccess = { avaliacoes ->
                val media = avaliacoes.map { it.estrelas }.average().takeIf { !it.isNaN() } ?: 5.0
                _titulo.value = String.format("%.1f estrelas", media)
                _comentarios.value = avaliacoes.joinToString(separator = "\n\n") { avaliacao ->
                    "${estrelasTexto(avaliacao.estrelas)}\n${avaliacao.cliente}\n${avaliacao.comentario}"
                }
            },
            onError = {
                _comentarios.value = "Nao consegui carregar as avaliacoes agora."
            }
        )
    }

    private fun estrelasTexto(estrelas: Double): String {
        val cheias = estrelas.toInt().coerceIn(1, 5)
        return List(cheias) { "*" }.joinToString(separator = " ")
    }
}

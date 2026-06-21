package com.mesawa.cuidarproximocuidador.ui.perfil

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.AndroidViewModel

class PerfilCuidadorViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = PerfilCuidadorRepository()
    private val cache = PerfilLocalCache(application)

    private val _state = MutableLiveData(PerfilUiState(loading = true))
    val state: LiveData<PerfilUiState> = _state

    fun carregar(
        cuidadorId: String,
        fallbackNome: String,
        fallbackEspecialidade: String,
        fallbackFotoUrl: String
    ) {
        val uid = repository.uidAtual
        val cacheLocal = cache.carregar(uid).ifNull { cache.carregar(cuidadorId) }
        if (cacheLocal != null) {
            _state.value = PerfilUiState(loading = false, dados = cacheLocal)
        } else {
            _state.value = _state.value?.copy(loading = true, mensagem = null) ?: PerfilUiState(loading = true)
        }

        repository.carregarPerfil(
            cuidadorId = cuidadorId,
            fallbackNome = fallbackNome,
            fallbackEspecialidade = fallbackEspecialidade,
            fallbackFotoUrl = fallbackFotoUrl,
            onSuccess = { dados ->
                cache.salvar(dados)
                _state.value = PerfilUiState(loading = false, dados = dados)
            },
            onFailure = { mensagem ->
                _state.value = (_state.value ?: PerfilUiState()).copy(loading = false, mensagem = mensagem)
            }
        )
    }

    fun salvarFoto(uri: Uri) {
        val atual = _state.value ?: PerfilUiState()
        val cuidadorId = atual.dados.id
        _state.value = atual.copy(salvandoFoto = true, mensagem = "Enviando foto...")
        repository.salvarFotoPerfil(
            cuidadorId = cuidadorId,
            fotoUri = uri,
            onSuccess = { fotoUrl ->
                val dados = (_state.value ?: PerfilUiState()).dados.copy(fotoUrl = fotoUrl)
                cache.salvar(dados)
                _state.value = PerfilUiState(dados = dados, mensagem = "Foto atualizada no perfil.")
            },
            onFailure = { mensagem ->
                _state.value = (_state.value ?: PerfilUiState()).copy(salvandoFoto = false, mensagem = mensagem)
            }
        )
    }

    fun sair() {
        repository.sair()
    }

    private fun <T> T?.ifNull(block: () -> T?): T? = this ?: block()
}

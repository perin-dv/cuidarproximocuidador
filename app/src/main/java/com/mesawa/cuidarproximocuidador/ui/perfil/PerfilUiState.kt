package com.mesawa.cuidarproximocuidador.ui.perfil

data class PerfilUiState(
    val loading: Boolean = false,
    val salvandoFoto: Boolean = false,
    val dados: PerfilCuidadorDados = PerfilCuidadorDados(),
    val mensagem: String? = null
)

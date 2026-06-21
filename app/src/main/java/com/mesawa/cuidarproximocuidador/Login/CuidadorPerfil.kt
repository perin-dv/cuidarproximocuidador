package com.mesawa.cuidarproximocuidador.Login

data class CuidadorPerfil(
    val id: String,
    val nome: String,
    val especialidade: String,
    val ativo: Boolean,
    val fotoUrl: String
)

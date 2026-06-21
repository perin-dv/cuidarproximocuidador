package com.mesawa.cuidarproximocuidador.ui.perfil

data class PerfilCuidadorDados(
    val id: String = "",
    val uid: String = "",
    val nome: String = "Cuidadora",
    val especialidade: String = "Cuidadora profissional",
    val cidade: String = "",
    val avaliacao: Double = 0.0,
    val atendimentos: Int = 0,
    val faturamentoMes: Double = 0.0,
    val fotoUrl: String = "",
    val ativo: Boolean = false,
    val reconhecimentoFacial: Boolean = false
)

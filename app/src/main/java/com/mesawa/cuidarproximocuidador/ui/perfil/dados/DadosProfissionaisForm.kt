package com.mesawa.cuidarproximocuidador.ui.perfil.dados

data class DadosProfissionaisForm(
    val funcao: String,
    val anoInicio: String,
    val anoFim: String,
    val valorHora: String,
    val especialidades: String,
    val atendeAlzheimer: Boolean,
    val atendeMobilidade: Boolean,
    val atendeMedicamentos: Boolean,
    val atendeCompanhia: Boolean,
    val bio: String,
    val sobreVoce: String,
    val diasDisponiveis: List<String>,
    val horaInicio: String,
    val horaFim: String,
    val certificados: List<String>
)

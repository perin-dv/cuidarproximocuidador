package com.mesawa.cuidarproximocuidador.ui.perfil

import android.content.Context

class PerfilLocalCache(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences("perfil_cuidador_cache", Context.MODE_PRIVATE)

    fun carregar(uidOuId: String): PerfilCuidadorDados? {
        val prefix = chave(uidOuId)
        val nome = prefs.getString("${prefix}_nome", null) ?: return null
        return PerfilCuidadorDados(
            id = prefs.getString("${prefix}_id", uidOuId).orEmpty(),
            uid = prefs.getString("${prefix}_uid", "").orEmpty(),
            nome = nome,
            especialidade = prefs.getString("${prefix}_especialidade", "Cuidadora profissional").orEmpty(),
            cidade = prefs.getString("${prefix}_cidade", "").orEmpty(),
            avaliacao = Double.fromBits(prefs.getLong("${prefix}_avaliacao", 0L)),
            atendimentos = prefs.getInt("${prefix}_atendimentos", 0),
            faturamentoMes = Double.fromBits(prefs.getLong("${prefix}_faturamento", 0L)),
            fotoUrl = prefs.getString("${prefix}_fotoUrl", "").orEmpty(),
            ativo = prefs.getBoolean("${prefix}_ativo", false),
            reconhecimentoFacial = prefs.getBoolean("${prefix}_reconhecimento", false)
        )
    }

    fun salvar(dados: PerfilCuidadorDados) {
        val primary = dados.uid.ifBlank { dados.id }
        if (primary.isBlank()) return
        salvarComChave(primary, dados)
        if (dados.id.isNotBlank() && dados.id != primary) salvarComChave(dados.id, dados)
    }

    private fun salvarComChave(uidOuId: String, dados: PerfilCuidadorDados) {
        val prefix = chave(uidOuId)
        prefs.edit()
            .putString("${prefix}_id", dados.id)
            .putString("${prefix}_uid", dados.uid)
            .putString("${prefix}_nome", dados.nome)
            .putString("${prefix}_especialidade", dados.especialidade)
            .putString("${prefix}_cidade", dados.cidade)
            .putLong("${prefix}_avaliacao", dados.avaliacao.toBits())
            .putInt("${prefix}_atendimentos", dados.atendimentos)
            .putLong("${prefix}_faturamento", dados.faturamentoMes.toBits())
            .putString("${prefix}_fotoUrl", dados.fotoUrl)
            .putBoolean("${prefix}_ativo", dados.ativo)
            .putBoolean("${prefix}_reconhecimento", dados.reconhecimentoFacial)
            .apply()
    }

    private fun chave(uidOuId: String): String {
        return uidOuId.ifBlank { "ultimo" }.replace(Regex("[^A-Za-z0-9_]"), "_")
    }
}

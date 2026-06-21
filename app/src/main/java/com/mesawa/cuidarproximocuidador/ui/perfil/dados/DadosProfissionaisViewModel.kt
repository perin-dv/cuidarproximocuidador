package com.mesawa.cuidarproximocuidador.ui.perfil.dados

import androidx.lifecycle.ViewModel

class DadosProfissionaisViewModel(
    private val repository: DadosProfissionaisRepository = DadosProfissionaisRepository()
) : ViewModel() {
    fun salvar(
        cuidadorId: String,
        form: DadosProfissionaisForm,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (form.funcao.isBlank()) {
            onFailure("Escolha sua função principal.")
            return
        }
        if (form.valorHora.replace(",", ".").toDoubleOrNull() == null) {
            onFailure("Informe um valor por hora válido.")
            return
        }
        repository.salvar(
            cuidadorId = cuidadorId,
            form = form,
            onSuccess = { onSuccess("Currículo atualizado no Firestore.") },
            onFailure = onFailure
        )
    }
}

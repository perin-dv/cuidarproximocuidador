package com.mesawa.cuidarproximocuidador.ui.perfil.configuracoes

import androidx.lifecycle.ViewModel

class ConfiguracoesViewModel(
    private val repository: ConfiguracoesRepository = ConfiguracoesRepository()
) : ViewModel() {
    fun salvarDadosConta(nome: String, telefone: String, cidade: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        repository.salvarDadosConta(nome, telefone, cidade, { onSuccess("Dados da conta salvos.") }, onFailure)
    }

    fun salvarEndereco(endereco: String, cidade: String, raioKm: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        repository.salvarEndereco(endereco, cidade, raioKm, { onSuccess("Endereco e regioes salvos.") }, onFailure)
    }

    fun alterarSenha(novaSenha: String, confirmacao: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        if (novaSenha.length < 6) {
            onFailure("A senha precisa ter pelo menos 6 caracteres.")
            return
        }
        if (novaSenha != confirmacao) {
            onFailure("A confirmacao da senha nao confere.")
            return
        }
        repository.alterarSenha(novaSenha, { onSuccess("Senha alterada com seguranca.") }, onFailure)
    }

    fun solicitarStatus(tipo: String, mensagem: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        repository.solicitarStatus(tipo, mensagem, { onSuccess("Solicitacao registrada.") }, onFailure)
    }
}

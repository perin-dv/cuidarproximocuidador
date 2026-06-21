package com.mesawa.cuidarproximocuidador.ui.perfil.suporte

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Locale

class SuporteSacViewModel : ViewModel() {
    private val repository = SuporteSacRepository()
    private val _chat = MutableLiveData("IA Cuidar Próximo: Olá! Posso ajudar com dúvidas básicas sobre perfil, foto, propostas, avaliações e conta.")
    val chat: LiveData<String> = _chat

    fun perguntar(uid: String, cuidadorId: String, pergunta: String) {
        if (pergunta.isBlank()) return
        val resposta = respostaBasica(pergunta)
        _chat.value = "${_chat.value}\n\nVocê: $pergunta\n\nIA Cuidar Próximo: $resposta"
        repository.salvarMensagem(uid, cuidadorId, pergunta, resposta)
    }

    fun registrarAcaoSac(uid: String, cuidadorId: String, tipo: String, destino: String) {
        repository.salvarAcaoSac(uid, cuidadorId, tipo, destino)
    }

    private fun respostaBasica(pergunta: String): String {
        val p = pergunta.lowercase(Locale.ROOT)
        return when {
            "foto" in p -> "Para trocar a foto, volte ao Perfil e toque no avatar com o sinal de +. Depois ajuste o rosto no círculo e salve."
            "proposta" in p -> "As propostas ficam na aba Propostas. Antes de aceitar, confira endereço, horário, perfil do idoso e tarefas do cuidado."
            "pagamento" in p || "carteira" in p -> "A Carteira ainda está em breve. Por enquanto ela aparece bloqueada até liberarmos saldo, repasses e extrato."
            "avali" in p || "estrela" in p -> "As avaliações aparecem depois que famílias concluem atendimentos e deixam comentários sobre seu serviço."
            "senha" in p || "login" in p -> "Se tiver problema de acesso, confira email e senha. Em breve teremos recuperação de senha dentro do app."
            "documento" in p || "reconhecimento" in p -> "O reconhecimento facial vai comparar selfie e documento para confirmar sua identidade profissional."
            else -> "Entendi. Para esse caso, recomendo falar com o SAC humano. Em breve vamos ligar este chat a um atendimento real."
        }
    }
}

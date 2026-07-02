# Cuidar Proximo - App do Cuidador

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![Google Play](https://img.shields.io/badge/Play_Services-4285F4?style=for-the-badge&logo=googleplay&logoColor=white)

Aplicativo Android para cuidadores gerenciarem sua presenca profissional dentro do ecossistema **Cuidar Proximo**. O app permite cadastro/login, perfil profissional, propostas de cuidado, avaliacoes, ganhos, suporte e configuracoes.

O projeto demonstra desenvolvimento Android com Kotlin, Firebase, arquitetura baseada em telas/features e persistencia local para apoiar experiencia de usuario.

## Funcionalidades

- Cadastro de cuidador com dados profissionais.
- Login e fluxo inicial do app.
- Home do cuidador com visao de propostas e atividades.
- Perfil profissional com foto, dados, avaliacoes e configuracoes.
- Ajuste/corte de foto de perfil.
- Propostas de cuidado e detalhe de proposta.
- Tela de atendimento em andamento.
- Ganhos e analise de desempenho.
- Suporte/SAC e configuracoes.
- Cache local de dados de perfil.
- Coordenador de atualizacao do app via Play App Update.

## Tecnologias

- Kotlin
- Android Views/XML
- Fragments e Activities
- ViewModel e LiveData
- Firebase Auth
- Cloud Firestore
- Firebase Storage
- Google Sign-In
- Play App Update
- SQLite/local store
- Gradle Kotlin DSL

## Estrutura

```txt
app/src/main/java/com/mesawa/cuidarproximocuidador/
  Cadastro/
  Login/
  atualizacao/
  data/
    firestore/
    local/
  ui/
    analise/
    andamento/
    ganhos/
    home/
    perfil/
    propostas/
```

## Como executar

### Pre-requisitos

- Android Studio
- JDK 21
- Projeto Firebase

### Build local

```bash
git clone https://github.com/perin-dv/cuidarproximocuidador.git
cd cuidarproximocuidador
./gradlew assembleDebug
```

No Windows:

```powershell
.\gradlew.bat assembleDebug
```

### Configuracao Firebase

O app usa Firebase e espera o arquivo local `app/google-services.json`, que nao fica versionado por seguranca. O package name do aplicativo e `com.mesawa.cuidarproximocuidador`.

## Configuracao local

Este repositorio nao versiona arquivos gerados, logs locais ou credenciais de ambiente. Para executar em uma nova maquina, e necessario configurar um projeto Firebase proprio com Auth, Firestore e Storage.

## Aprendizados

- Criacao de um app Android voltado para uma persona especifica.
- Organizacao de telas por dominio: login, cadastro, perfil, propostas e ganhos.
- Uso de ViewModel/Repository para separar UI e dados.
- Integracao com Firebase Auth, Firestore e Storage.
- Uso de cache local para melhorar experiencia e reduzir dependencia de rede.
- Fluxos profissionais como avaliacoes, configuracoes, suporte e propostas.

## Evolucao do projeto

O projeto segue em evolucao enquanto aprofundo meus estudos em Android, Firebase, organizacao de codigo, testes e arquitetura de aplicativos mobile.

## Autor

Desenvolvido por [Perin](https://github.com/perin-dv) como projeto de portfolio para Desenvolvimento Android Junior e Engenharia de Software.

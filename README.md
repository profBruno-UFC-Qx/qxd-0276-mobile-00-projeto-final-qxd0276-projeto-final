# 🔗 [Acesse o Código Fonte do Projeto Aqui](https://github.com/josiasdev/mural-talentos-app)


# Proposta de aplicativo

## Equipe
* Francisco Josias da Silva Batista - 542167
* Cristiano Mendes da Silva - 558382 - Projeto integrado em Engenharia de Software 3
* Paulo Victor Costa Ferreira - 557331
* Guilherme Damasceno Nobre - 511329

---

## Título do Projeto
Mural de Talentos

## Descrição do Projeto
O **Mural de Talentos** é uma solução mobile projetada para modernizar e centralizar o processo de contratação na cidade de Quixadá. Atualmente, a busca e divulgação de vagas na região dependem de canais informais e descentralizados, como grupos de WhatsApp e redes sociais, resultando em desorganização, baixa visibilidade e dificuldade na conexão entre oferta e demanda de trabalho.

O projeto resolve este problema oferecendo um aplicativo Android de fácil uso, que serve como um ponto de encontro digital entre empresas locais e profissionais da região. O público-alvo são as empresas de Quixadá que buscam um canal eficiente para anunciar suas vagas e os candidatos locais que precisam de um espaço confiável para cadastrar suas informações e encontrar oportunidades.

Ao contrário de grandes plataformas como LinkedIn e InfoJobs, que têm baixa penetração em cidades menores, nossa solução foca exclusivamente no ecossistema de emprego regional. O Mural de Talentos visa fortalecer a economia local, otimizar o tempo de recrutamento e aumentar a empregabilidade, tudo através de uma interface simples, acessível e intuitiva.

---

## Funcionalidades Principais

#### Para Empresas
- [ ] **HU-1:** Cadastrar e publicar vagas de emprego.
- [ ] **HU-3:** Pesquisar perfis de candidatos cadastrados na plataforma.
- [ ] **HU-10:** Gerenciar as candidaturas recebidas para uma vaga (visualizar, aprovar, rejeitar).
- [ ] **HU-11:** Marcar e registrar entrevistas com os candidatos selecionados.

#### Para Candidatos

- [ ] **HU-2:** Criar e gerenciar um perfil profissional com informações pessoais, experiências e habilidades.
- [ ] **HU-4:** Visualizar e se candidatar às vagas publicadas pelas empresas.
- [ ] **HU-9:** Filtrar vagas por categoria, tipo de emprego ou setor.
- [ ] **HU-7:** Visualizar vagas próximas à sua localização em um mapa (via Google Maps).
- [ ] **HU-8:** Receber notificações sobre o status de suas candidaturas.

#### Gerais
- [ ] **HU-6:** Suporte aos modos de visualização claro (Light Mode) e escuro (Dark Mode).
- [ ] **(Autenticação):** Permitir login e cadastro de forma simples, incluindo opções com redes sociais.


---

##  Tecnologias: 
A arquitetura do projeto é baseada em um aplicativo nativo Android consumindo serviços de Backend as a Service (BaaS) do Firebase.

| Tecnologia | Descrição |
| --- | --- |
| **Kotlin** | Linguagem principal utilizada para o desenvolvimento mobile nativo Android. |
| **Jetpack Compose** | Framework moderno para construção de interfaces reativas e declarativas. |
| **Firebase Authentication** | Utilizado para autenticação segura de usuários (Email, Google, etc.). |
| **Firebase Firestore** | Banco de dados NoSQL em tempo real para armazenamento e sincronização de dados (vagas, perfis, candidaturas). |
| **Firebase Cloud Messaging** | Para o envio de notificações push sobre o status das candidaturas. |
| **Room Database** | Solução local de persistência de dados, garantindo operação offline (cache de vagas, perfil). |
| **ViewModel (Jetpack)** | Gerenciamento de estado e do ciclo de vida da UI, seguindo a arquitetura recomendada pelo Google. |
| **Kotlin Flows (StateFlow)** | Utilizado para fluxos de dados assíncronos e reativos entre o ViewModel e a UI. |
| **Google Maps API** | Integração para a funcionalidade de visualização de vagas no mapa. |
| **Material Design 3** | Interface moderna e consistente com as diretrizes do Google. |
---

## Instruções para Execução
### Pré-requisitos

* [Android Studio](https://developer.android.com/studio) (Versão Iguana ou superior)
* JDK 17 ou superior
* Um dispositivo Android (Físico ou Emulador)
* Conta no [Firebase](https://firebase.google.com/)

### Passos para Execução

1.  **Clone o repositório:**
    ```bash
    git clone git@github.com:josiasdev/mural-talentos-app.git
    cd mural-talentos-app
    ```

2.  **Configure o Firebase:**
    * Acesse o console do Firebase e crie um novo projeto.
    * Adicione um aplicativo Android ao seu projeto Firebase (o `package name` deve ser o mesmo do projeto clonado).
    * Baixe o arquivo `google-services.json` gerado pelo Firebase.
    * Mova o arquivo `google-services.json` para o diretório `app/` do seu projeto Android.
    * Ative os serviços necessários no console do Firebase: **Authentication**, **Firestore Database** e **Cloud Messaging**.

3.  **Abra no Android Studio:**
    * Inicie o Android Studio e selecione "Open an existing project".
    * Navegue até a pasta onde você clonou o repositório e abra-a.

4.  **Sincronize e Execute:**
    * Aguarde o Android Studio sincronizar as dependências do Gradle.
    * Selecione um dispositivo (emulador ou físico).
    * Clique no botão "Run" (▶️) para compilar e instalar o aplicativo.


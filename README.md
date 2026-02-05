[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/AR7CADm8)
[![Open in Codespaces](https://classroom.github.com/assets/launch-codespace-2972f46106e565e64193e422d61a12cf1da4916b45550586e14ef0a7c637dd04.svg)](https://classroom.github.com/open-in-codespaces?assignment_repo_id=21621904)
# Proposta de aplicativo

## Equipe
* **Nome do Aluno(a):** Andressa Lima Colares - 471151

---

## Título do Projeto
BookKeeper - Seu organizador de livros

## Descrição do Projeto

O BookKeeper é uma proposta de aplicativo voltado para auxiliar leitores na organização de seus hábitos de leitura. A ideia surgiu a partir de uma dificuldade comum entre pessoas que possuem o hábito de ler com frequência: gerenciar seus livros e manter o controle do que já foi lido, do que está em andamento e do que ainda se deseja ler.

O aplicativo funciona como uma estante virtual e catálogo pessoal, permitindo que o usuário registre seus livros e os organize em prateleiras personalizadas, como “Lido”, “Lendo” e “Quero Ler”. Para facilitar o cadastro, o BookKeeper possibilita o escaneamento do código de barras (ISBN) de livros físicos, utilizando os dados obtidos na Google Books API para preencher automaticamente informações como título, autor, capa e descrição.

Todos os livros são armazenados localmente por meio do Room, o que garante acesso ao acervo mesmo sem conexão com a internet. Dessa forma, o aplicativo busca tornar o processo de gerenciamento de leituras mais prático, centralizado e acessível, evitando a perda de informações e promovendo uma experiência mais organizada para o leitor.

---

## Funcionalidades Principais

* [x] **Navegação Multitela:** Implementação de múltiplas telas (Estante, Detalhes do Livro, Scanner).
* [x] **Persistência Local (Room):** Criação da entidade `Livro` e configuração do banco de dados Room para salvar a estante do usuário, permitindo acesso offline.
* [x] **CRUD de Livros:** O usuário pode adicionar (Create), ler (Read), atualizar  mudar status de leitura) e excluir (Delete) livros da sua estante.
* [x] **Integração com Câmera (Scanner de ISBN):** Uso da câmera para escanear o código de barras (ISBN) de um livro físico.
* [x] **Integração com API Externa (Google Books):** Após o scan, consultar a Google Books API para buscar e preencher automaticamente os dados do livro.
* [x] **Tema (Modo Claro/Escuro):** Implementação correta do `MaterialTheme`  para que o app se adapte automaticamente ao tema do sistema (light/dark).
* [x] **Organização (Prateleiras):** Exibição dos livros na tela principal, separados por status (ex: "Lendo", "Lidos", "Quero Ler").
* [x] **Cadastro Manual:** Permitir que o usuário adicione um livro manualmente caso o scan falhe ou o livro não tenha ISBN.

---

## 🛠️ Tecnologias e Bibliotecas

O projeto utiliza as tecnologias mais modernas do ecossistema Android para garantir performance, persistência de dados e uma experiência de usuário fluida:

- **Jetpack Compose**  
  Framework moderno para construção de UI declarativa e reativa.

- **Kotlin Coroutines & Flow**  
  Para gerenciamento de chamadas assíncronas e fluxo de dados em tempo real entre o banco de dados e a UI.

- **Room Database**  
  Abstração sobre o SQLite para persistência local de livros, notas e sessões de leitura.

- **Coil (Compose)**  
  Biblioteca para carregamento de imagens de perfil e capas de livros via URL.

- **Retrofit & OkHttp**  
  Para consumo da API do Google Books para busca de livros por ISBN.

- **CameraX**  
  Utilizada para a implementação nativa da câmera e visualização do scanner.

- **Google ML Kit (Barcode Scanning)**  
  API de inteligência artificial para detecção e leitura ultra-rápida de códigos de barras (ISBN).

- **ViewModel & StateFlow**  
  Arquitetura MVVM para separação de lógica de negócio e estado da interface.

---

## 🚀 Instruções para Execução

Para rodar o projeto localmente, siga os passos usuais de clonagem e build no Android Studio.  
No entanto, devido ao uso de hardware (Câmera) e armazenamento, atente-se aos seguintes pontos:

---

### 1. 🔐 Permissões Necessárias

O aplicativo solicitará permissões em tempo de execução.  
Certifique-se de aceitá-las para o funcionamento total:

- **CAMERA**  
  Necessária para o scanner de livros e captura de foto de perfil.

- **INTERNET**  
  Para busca de capas e informações na API do Google.

---

### 2. ⚙️ Configuração de Hardware

- **Scanner**  
  O scanner funciona melhor em dispositivos físicos devido à necessidade de foco automático da lente.  
  Se estiver usando um Emulador, certifique-se de habilitar a **Camera** nas configurações do AVD (Virtual Device) e aponte para um QR Code ou código de barras exibido na tela do computador.

- **Armazenamento de Capas**  
  As fotos tiradas pela câmera são salvas no diretório interno do aplicativo (`filesDir`), garantindo que as imagens persistam mesmo após fechar o app.

---

### 3. ▶️ Executando o Projeto

```bash
# 1. Clone o repositório
git clone https://github.com/profBruno-UFC-Qx/classroom-mobile-final-andressa-lima-colares.git

# 2. Abra o projeto no Android Studio
# (Versão Ladybug ou superior recomendada)

# 3. Aguarde o Gradle Sync completar
# (isso baixará as bibliotecas ML Kit e CameraX)

# 4. Conecte um dispositivo físico via USB ou Wi-fi com dispositivo Android 11 ou +
# ou use um Emulador com suporte a Camera

# 5. Execute o projeto
# Clique em 'Run' ou pressione Shift + F10


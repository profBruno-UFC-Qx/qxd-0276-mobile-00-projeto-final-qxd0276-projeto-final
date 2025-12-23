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

* [ ] **Navegação Multitela:** Implementação de múltiplas telas (Estante, Detalhes do Livro, Scanner).
* [ ] **Persistência Local (Room):** Criação da entidade `Livro` e configuração do banco de dados Room para salvar a estante do usuário, permitindo acesso offline.
* [ ] **CRUD de Livros:** O usuário pode adicionar (Create), ler (Read), atualizar  mudar status de leitura) e excluir (Delete) livros da sua estante.
* [ ] **Integração com Câmera (Scanner de ISBN):** Uso da câmera para escanear o código de barras (ISBN) de um livro físico.
* [ ] **Integração com API Externa (Google Books):** Após o scan, consultar a Google Books API para buscar e preencher automaticamente os dados do livro.
* [ ] **Tema (Modo Claro/Escuro):** Implementação correta do `MaterialTheme`  para que o app se adapte automaticamente ao tema do sistema (light/dark).
* [ ] **Organização (Prateleiras):** Exibição dos livros na tela principal, separados por status (ex: "Lendo", "Lidos", "Quero Ler").
* [ ] **Cadastro Manual:** Permitir que o usuário adicione um livro manualmente caso o scan falhe ou o livro não tenha ISBN.

---


> [!WARNING]
> Daqui em diante o README.md só deve ser preenchido no momento da entrega final.

##  Tecnologias: 
Liste aqui as tecnologias e bibliotecas que foram utilizadas no projeto.

---

## Instruções para Execução
[Inclua instruções claras sobre como rodar o projeto localmente. Isso é crucial para que você possa testá-lo nas próximas entregas. **Somente caso haja alguma coisa diferente do usual**

```bash
# Clone o repositório
git clone [https://docs.github.com/pt/repositories/creating-and-managing-repositories/about-repositories](https://docs.github.com/pt/repositories/creating-and-managing-repositories/about-repositories)

# Navegue para o diretório
cd [nome-do-repositorio]

# Siga as instruções específicas para a sua tecnologia...

[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/AR7CADm8)
[![Open in Codespaces](https://classroom.github.com/assets/launch-codespace-2972f46106e565e64193e422d61a12cf1da4916b45550586e14ef0a7c637dd04.svg)](https://classroom.github.com/open-in-codespaces?assignment_repo_id=20885053)

# Proposta de aplicativo

## Equipe

* **Antonio Rewelli Oliveira dos Santos** – 554047
* **Francisco Kauan Pereira Cavalcante** – 554089
* **Julian César Pereira Cardoso** – 553782
* **Lucas Ferreira Nobre** – 554590
* **Luis Eduardo Vieira de Oliveira** – 552375
* **Miqueias Bento da Silva** – 553972

---

## Título do Projeto

**Livo – Sistema de Acompanhamento de Leitura**

---

## Descrição do Projeto

O **Livo** é um aplicativo voltado para o acompanhamento de leitura e organização de bibliotecas pessoais. Ele busca oferecer uma forma simples e intuitiva de registrar o progresso de leitura, adicionar e classificar livros, além de permitir reflexões e resenhas sobre as obras.

O projeto surge da necessidade de leitores que desejam centralizar o controle de suas leituras em um ambiente agradável, sem distrações sociais, permitindo acompanhar o progresso, definir metas e manter o histórico organizado.

O público-alvo são **leitores que buscam autonomia e praticidade** para acompanhar suas leituras, sejam estudantes, profissionais ou entusiastas da leitura, valorizando a experiência individual em vez da interação social entre usuários.

---

## Repositórios
Possui a versão mais recente do projeto.  
- **Aplicativo mobile**: [RewelliOliveira/livoAppOfBooks](https://github.com/RewelliOliveira/livoAppOfBooks).
- **API backend**: [kauanper/livo-api](https://github.com/kauanper/livo-api).

---

## Funcionalidades Principais

* [x] **Gerenciar biblioteca pessoal:** adicionar, remover, visualizar e listar livros.
* [x] **Organizar livros por status:** “Quero ler”, “Lendo”, “Lido” e “Abandonado”.
* [x] **Cadastrar e gerenciar prateleiras personalizadas:** criar, editar e excluir categorias.
* [x] **Associar livros a prateleiras específicas.**
* [x] **Registrar leitura:** informar progresso de páginas e adicionar resenhas.
* [x] **Avaliar livros concluídos com notas.**
* [x] **Pesquisar livros no catálogo global (Google Books API).**
* [x] **Gerenciar conta do usuário:** cadastro, login, edição e exclusão de perfil.

---

## Tecnologias

* **Frontend:**
    - Figma (prototipagem)
    - Kotlin
    - Jetpack Compose
* **Backend:**
    - Spring Boot (Java)
    - Docker
* **Banco de Dados:**
    - PostgreSQL (Para persistência)
    - H2 (Para teste)
* **Infraestrutura:** Railway
* **APIs Externas:** Google Books API

---

## Guia de Inicialização do Projeto Livo
Descrição com os passos necessários para rodar o projeto completo (Backend + Frontend) localmente para testes.

### 1. Configuração do Backend (livo-api)

O backend é composto por microserviços que rodam em containers Docker. Para facilitar, existem scripts prontos para o ambiente de staging (testes).
  
**Passos**:
1.  Certifique-se de ter o **Docker** e o **Docker Desktop** instalados e rodando.
2.  Navegue até a pasta raiz do backend: `livo-api`.
3.  Execute o comando para subir o ambiente de **staging** (onde os microserviços e bancos de dados são iniciados):
    ```bash
    docker-compose -f docker/docker-compose.staging.yaml --env-file .env up --build
    ```
    *Nota: Este processo pode demorar alguns minutos na primeira vez, pois irá baixar imagens e construir os containers.*

4.  **Verificação**:
    - Após subir, você pode conferir o status dos serviços através do **Discovery Server** (Netflix Eureka) em: [http://localhost:8761/](http://localhost:8761/).
    - Verifique se os serviços `API-GATEWAY`, `AUTH-SERVICE`, `BOOK-SERVICE`, `USER-SERVICE` e `LIBRARY-SERVICE` aparecem com status **UP**.

---

### 2. Configuração do App Mobile (livoAppOfBooks)

Para que o aplicativo Android consiga se comunicar com o backend rodando localmente, é necessário ajustar a URL base da API.

**Passos**:
1.  Abra o projeto `livoAppOfBooks` no Android Studio.
2.  Localize o arquivo de configuração de rede (normalmente em `RetrofitInstance.kt` ou conforme indicado na navegação do app).
    - **Caminho**: `app/src/main/java/com/example/livoappofbooks/data/remote/RetrofitInstance.kt`
3.  Modifique a constante `BASE_URL` para apontar para o seu gateway local na porta **8080**.
    - Se estiver usando o **Emulador Android**, utilize o IP especial:
      ```kotlin
      private const val BASE_URL = "http://10.0.2.2:8080/"
      ```
    - Se estiver usando um **dispositivo físico**, utilize o IP da sua máquina na rede local (ex: `http://192.168.1.5:8080/`).

---

### Observações Gerais
- O gateway do backend centraliza todas as chamadas na porta **8080**.
- Caso precise limpar o ambiente docker ou liberar memória, consulte o arquivo `livo-api/docker/comandos.md` para comandos úteis de `prune`.

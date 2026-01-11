[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/AR7CADm8)
[![Open in Codespaces](https://classroom.github.com/assets/launch-codespace-2972f46106e565e64193e422d61a12cf1da4916b45550586e14ef0a7c637dd04.svg)](https://classroom.github.com/open-in-codespaces?assignment_repo_id=21021004)
# Proposta de aplicativo

## Equipe
* **Nome do Aluno(a) 1:** Rogério Girão de Castro Filho - 569533
* **Nome do Aluno(a) 2:** [Seu nome e matrícula]
* **Nome do Aluno(a) 3:** [Seu nome e matrícula]
* ...

---

## Título do Projeto
TrustCam

## Descrição do Projeto
TrustCam é um aplicativo de câmera FOSS (Free and Open Source Software), desenvolvido para usuários que buscam uma solução simples, eficiente e com foco em privacidade. O aplicativo permite capturar fotos, armazená-las localmente no dispositivo, sem qualquer coleta ou rastreamento de dados pessoais.
TrustCam oferece uma galeria integrada para visualizar, editar informações e excluir imagens facilmente, sem depender de serviços em nuvem ou de terceiros.
O aplicativo possui suporte a modo claro/escuro, integração opcional com APIs open source para geolocalização e timestamp, grid (linha de enquadramento) na câmera, processamento local de imagens e marcação d'água personalizada.

---

## Funcionalidades Principais
[Liste as principais funcionalidades do projeto. Use caixas de seleção para que a equipe possa marcar as concluídas nas próximas etapas.]

- [x] Captura de fotos:    Captura de fotos usando a câmera do dispositivo com CameraX
- [x] Armazenamento local: Armazenamento local das imagens e metadados usando Room
- [x] Galeria integrada:   Galeria integrada para visualização, edição (título, tags) e exclusão das fotos
- [x] Geolocalização:      Integração com API de geolocalização para registrar e exibir o local onde a foto foi tirada

---

> [!WARNING]
> Daqui em diante o README.md só deve ser preenchido no momento da entrega final.

##  Tecnologias:

- **Kotlin:** Linguagem de programação utilizada.
- **Arquitetura MVVM:** Padrão arquitetural adotado.
- **Jetpack Compose:** Criação da interface.
- **CameraX:** Biblioteca do Jetpack para captura de fotos.
- **Room:** Persistência local de dados.
- **Navigation Compose:** Navegação entre telas.
- **MediaStore**: Armazenamento e acesso seguro às fotos no dispositivo.
- **Coil**: Carregamento e exibição de imagens.
- **Zoomable**: Biblioteca open-source para Compose que permite zoom.
- **FileProvider**: Compartilhamento de arquivos entre aplicativos.
- **LocationManager**: API nativa para obtenção da localização do dispositivo.

## Instruções para Execução
[Inclua instruções claras sobre como rodar o projeto localmente. Isso é crucial para que você possa testá-lo nas próximas entregas. **Somente caso haja alguma coisa diferente do usual**

```bash
# Clone o repositório
git clone [https://docs.github.com/pt/repositories/creating-and-managing-repositories/about-repositories](https://docs.github.com/pt/repositories/creating-and-managing-repositories/about-repositories)

# Navegue para o diretório
cd [nome-do-repositorio]

# Siga as instruções específicas para a sua tecnologia...

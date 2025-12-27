[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/AR7CADm8)
[![Open in Codespaces](https://classroom.github.com/assets/launch-codespace-2972f46106e565e64193e422d61a12cf1da4916b45550586e14ef0a7c637dd04.svg)](https://classroom.github.com/open-in-codespaces?assignment_repo_id=21013826)
# Proposta de aplicativo

## Equipe
* **Nome do Aluno(a) 1:** Sammy Diogenes Ferreira - 553747
* **Nome do Aluno(a) 2:** Andre Lucas de Melo Bezerra - 554216
* ...

---

## Título do Projeto
DexGo.

## Descrição do Projeto
O Pokédex é um aplicativo mobile que permite aos usuários colecionar, gerenciar e compartilhar cartinhas de Pokémon. Além de manter uma coleção personalizada, o usuário pode ganhar novas cartas diariamente, interagir com amigos e trocar ou compartilhar cartas por meio de um sistema de QR Codes exclusivo. O objetivo do sistema é proporcionar uma experiência divertida e social, combinando elementos de colecionismo com interação entre jogadores. O armazenamento local será utilizado para manter informações do usuário (como sessão ativa, cartas coletadas e preferências) de forma offline, garantindo que o aplicativo continue funcional mesmo sem conexão com a internet.
Além disso, será integrada uma API externa da PokéAPI (https://pokeapi.co/) para obter dados oficiais dos Pokémon, como nome, tipo, imagem e descrição, que servirão de base para as cartas do jogo.

---

## Funcionalidades Principais
[Liste as principais funcionalidades do projeto. Use caixas de seleção para que a equipe possa marcar as concluídas nas próximas etapas.]

- [ ] Cadastro de Conta: Permite que novos usuários criem uma conta no aplicativo. Inclui informações básicas como nome, e-mail, senha e avatar. Após o cadastro, o usuário tem acesso à sua coleção e às demais funcionalidades do sistema.
- [ ] Login e Autenticação: O usuário pode acessar sua conta inserindo e-mail e senha. Validação de credenciais e tratamento de erros (senha incorreta, conta inexistente, etc.).
- [ ] Gerenciamento de Perfil: Edição de informações do perfil (nome, avatar, biografia, etc.). Exclusão da conta, removendo permanentemente todos os dados do usuário e suas cartas.
- [ ] Painel de Cartinhas: Exibe todas as cartas do usuário, com filtros e categorias: Por região (Kanto, Johto, Hoenn, etc.), Por favoritas, Por nível de poder (mais fortes, raras, etc.). Permite visualizar detalhes de cada carta, como tipo, poder e descrição.
- [ ] Sistema de Recompensas Diárias: Todos os dias o usuário pode ganhar uma nova carta por meio de um sorteio aleatório. Caso o usuário já possua a carta sorteada, uma nova carta diferente será concedida automaticamente.
- [ ] Compartilhamento de Cartas: O usuário pode compartilhar uma carta específica com até uma pessoa. Cada carta compartilhada gera um QR Code. O QR Code pode ser escaneado, permitindo que o amigo receba a carta compartilhada. Caso o destinatário já possua a carta, ele receberá uma notificação e não haverá duplicação.

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

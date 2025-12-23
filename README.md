[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/AR7CADm8)
[![Open in Codespaces](https://classroom.github.com/assets/launch-codespace-2972f46106e565e64193e422d61a12cf1da4916b45550586e14ef0a7c637dd04.svg)](https://classroom.github.com/open-in-codespaces?assignment_repo_id=20867420)
# Proposta de aplicativo

## Equipe
* **Nome do Aluno(a) 1:** Guilherme Barros Vieira de Araujo - 509873
* **Nome do Aluno(a) 2:** Francisco Edinaldo dos Santos Silva - 586043
* **Nome do Aluno(a) 3:** José Adrian Nascimento Silva - 475594
* **Nome do Aluno(a) 4:** Petrucio de Carvalho Neves Filho - 469854

---

## Título do Projeto
Pegaí

## Descrição do Projeto
O Pegaí é uma plataforma mobile *peer-to-peer* (P2P) de aluguel de materiais acadêmicos, projetada para reduzir custos estudantis desnecessários. Diferente de um simples classificado, o aplicativo gerencia todo o **ciclo de vida da transação**, garantindo segurança através de processos de validação visual e reputação.

Para garantir robustez técnica e atender aos requisitos de complexidade, definimos a seguinte arquitetura:
1.  **APIs Externas:** Utilizaremos **Google Maps/Mapbox** para a geolocalização dos itens, **Firebase Auth** para gestão segura de identidades, **Firebase Storage** para o upload das imagens de vistoria e **Firebase Cloud Messaging (FCM)** para notificações push em tempo real. 
2.  **Persistência Local e Cache:** Adotaremos uma estratégia de otimização onde os dados críticos (usuários, negociações e catálogo) residem na API para evitar conflitos, enquanto o banco de dados local (**Room/SQLite**) armazenará cache de itens visualizados recentemente, histórico de buscas e preferências do usuário, garantindo agilidade na navegação e economia de dados.

A lógica de negócio central foca em um sistema de avaliação bilateral e gamificação, calculando scores de confiabilidade para locadores e locatários, promovendo um ambiente seguro e moderado pela própria comunidade.

---

## Funcionalidades Principais

- [ ] **Vistoria Digital (Check-in com Câmera):** Fluxo obrigatório no momento da entrega, onde o locador utiliza a câmera nativa para registrar o estado do objeto. As fotos são enviadas para o **Firebase Storage** servindo de evidência imutável.
- [ ] **Busca Georreferenciada:** Integração com **Google Maps API** para permitir que o usuário filtre materiais disponíveis em um raio de proximidade.
- [ ] **Chat em Tempo Real:** Canal de mensagens integrado para que locador e locatário combinem o local de encontro e a devolução, mantendo a privacidade dos dados de contato pessoais.
- [ ] **Notificações Push (FCM):** Sistema de alertas para avisar o usuário sobre aprovação de pedidos, novas mensagens no chat ou data de devolução próxima.
- [ ] **Sistema de Reputação e Score:** Algoritmo de avaliação bilateral (locador e locatário se avaliam) pós-devolução. O sistema calcula uma nota de confiabilidade pública baseada em pontualidade e cuidado com o item.
- [ ] **Simulação de Gateway de Pagamento:** Implementação de um módulo de pagamento simulado (Mock) que reproduz os estados de uma transação real (Processando, Aprovado, Recusado). O sistema valida dados de cartão de crédito ou gera códigos PIX fictícios para demonstrar o fluxo financeiro completo e a mudança de status dos pedidos no banco de dados.
- [ ] **Persistência de Cache:** Utilização de banco de dados local para salvar histórico de buscas e itens recentes, melhorando a experiência de navegação.

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

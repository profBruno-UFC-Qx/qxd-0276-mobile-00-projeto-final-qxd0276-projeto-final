# Guia de Especificação Funcional - Pegaí

**Status:** Versão Atualizada (Fluxo Chat-Centric)
**Objetivo:** Definir as regras de negócio, dados e telas para garantir que todos desenvolvam na mesma direção.

---

## 1. Visão Geral e Glossário
Para evitar confusão durante as conversas da equipe:
* **Locador (Dono):** O aluno que empresta o material (Ex: Maria).
* **Locatário (Cliente):** O aluno que precisa do objeto (Ex: João).
* **Objeto/Item:** O material acadêmico sendo negociado.
* **Vistoria:** O ato de tirar fotos para comprovar o estado do item.

---

## 2. Estrutura de Dados (Banco de Dados Conceitual)
Aqui definimos quais informações precisamos salvar.

### Coleção: Usuários (Users)
Informações do perfil do aluno.
* **Nome Completo:** Texto.
* **Matrícula/Curso:** Texto (para validação institucional).
* **Reputação Locador:** Objeto contendo Nota Média (0-5) e Lista de Comentários recebidos.
* **Reputação Locatário:** Objeto contendo Nota Média (0-5) e Lista de Comentários recebidos.
* **Saldo/Carteira:** Valor numérico.
* **Métodos de Pagamento:** Lista de cartões salvos (tokens).
* **Histórico:** Lista de IDs dos aluguéis passados.

### Coleção: Produtos (Products)
Os itens disponíveis no feed.
* **Título:** Texto (ex: "Calculadora HP 12c").
* **Descrição:** Texto detalhado sobre o estado.
* **Preço Diária:** Valor monetário.
* **CEP:** Texto (Localização aproximada).
* **Fotos:** Lista de imagens (URLs).
* **Localização:** Latitude/Longitude.
* **Dono:** ID do Usuário que postou.
* **Status do Item:** "Disponível", "Alugado" ou "Inativo".

### Coleção: Pedidos de Aluguel (Rentals)
A tabela que liga quem alugou o quê.
* **Datas Agendadas:** Início e Fim solicitados.
* **Local de Entrega:** Coordenadas definidas no chat.
* **Status do Pedido:** (Ver Máquina de Estados abaixo).
* **Check-in (Entrega):** Fotos tiradas pelo Locador na hora da entrega.
* **Check-out (Devolução):** Fotos tiradas e Avaliação final.

---

## 3. Fluxo de Telas (Sitemap)

### A. Autenticação (Entrada)
1.  **Tela de Login**
2.  **Tela de Cadastro**

### B. Navegação Principal (Bottom Bar)
**1. Início (Home):** Feed de produtos, Busca e Filtros.
**2. Aluguéis:** Listagem de contratos ativos/pendentes.
**3. Anunciar:** Botão central para cadastro de itens.
**4. Chat (Inbox):** Lista de conversas ativas. Central de negociação.
**5. Perfil:** Dados do usuário, Reputação e Pagamentos.

---

## 4. Jornada do Usuário (Fluxo Chat-Centric)

O fluxo de aluguel acontece predominantemente **dentro da tela de Chat**, utilizando mensagens de sistema interativas (Action Bubbles).

### Fase 1: Contato Inicial e Solicitação
1.  **Dúvidas Prévias:** Na tela do produto, o usuário pode clicar em "Contatar Dono". Isso abre o chat imediatamente, permitindo tirar dúvidas antes de formalizar qualquer coisa.
2.  **Solicitação Formal:** O locatário clica em "Solicitar Aluguel" (na tela do produto ou opção no chat).
3.  **Notificação no Chat:** Uma mensagem de sistema aparece na conversa para ambos:
    * *"🔄 Solicitação de Aluguel Enviada: 3 dias (Total R$ 15,00)"*

### Fase 2: Negociação e Aceite (No Chat)
1.  **Ação do Dono:** O Locador vê a solicitação no chat com botões **[Aceitar]** ou **[Recusar]**.
2.  **Feedback:** Se aceitar, aparece a mensagem: *"Solicitação Aceita. Combinem o local de entrega."*
3.  **Definição de Local:**
    * O sistema exibe um botão para o Locador: **[Definir Local de Entrega]**.
    * O Locador escolhe no mapa (ex: Praça do Leão Centro).
    * O sistema envia no chat: *" Local definido: Praça do Leão, Centro"*
4.  **Aceite do Local:**
    * O Locatário vê o mapa e clica em **[Concordar com Local]**.
    * **Status muda para:** `AGUARDANDO_ENTREGA`.

### Fase 3: O Encontro e Pagamento (Presencial)
*Esta fase ocorre quando eles se encontram fisicamente.*

1.  **Vistoria (Check-in):**
    * O Locador clica na opção **[Finalizar Entrega / Vistoria]**.
    * O app abre a câmera, ele tira fotos do produto e envia.
2.  **Gatilho de Pagamento:**
    * Assim que as fotos são enviadas, o status muda para `AGUARDANDO_PAGAMENTO`.
    * O chat exibe para o Locatário: *"Vistoria recebida. Realize o pagamento para liberar o item."*
3.  **Pagamento Real-Time:**
    * O Locatário clica em **[Pagar Agora]** dentro do chat.
    * Escolhe: **PIX** ou **Cartão**.
    * **Confirmação:** O sistema processa e avisa no chat: *" Pagamento Confirmado! Aluguel iniciado."*
4.  **Conclusão:**
    * O status muda para `EM_CURSO`.
    * O app exibe a data de devolução.

---

## 5. Regras de Negócio Específicas

### Pagamento no Ato (Just-in-Time)
Para evitar que o usuário pague por dias que não usou (caso demore a buscar o item):
* O pagamento só é liberado **após** a vistoria presencial.
* Isso garante que o "relógio" do aluguel e o pagamento estão sincronizados com a posse física do objeto.

### Chat Híbrido
O chat suporta dois tipos de mensagens:
1.  **Texto Livre:** Para combinar detalhes ("Estou de camisa azul").
2.  **Action Bubbles (Sistema):** Widgets interativos dentro da conversa (Solicitação, Mapa, Botão de Pagar) que controlam a máquina de estados do aluguel.

### Vistoria Obrigatória
O fluxo de pagamento é bloqueado até que o Locador envie as fotos da vistoria. Isso protege o Locatário de pagar por algo que não recebeu ou que está quebrado.

# Proposta de aplicativo

## Equipe

* **Anaildo do Nascimento Silva** – 552836
* **Camile Isidorio Araújo** – 555251
* **Debora Silva Viana** – 557337
* **Francisco Werley da Silva** – 553948
* **Wendel Rodrigues Viana** – 548323

---
## Repositório Principal de desenvolvimento:
[Roomiê Repositório](https://github.com/WendelRodriguesz/Roomie_Android)

## Título do Projeto

**Roomiê — Match de colegas de quarto e vagas em moradias compartilhadas**

## Descrição do Projeto

O **Roomiê** é um app Android (Kotlin + Jetpack Compose) que conecta quem **tem vaga** em casa/apê com quem **procura dividir moradia**. Ele combina a experiência de **match** (compatibilidade entre perfis) com descoberta de **anúncios completos** (fotos, valores, regras) e **chat** para negociar visitas. Público-alvo: **universitários e jovens trabalhadores** em cidades acadêmicas/urbanas.

O problema hoje é a busca fragmentada (grupos e classificados), pouca informação sobre **convivência** e **custo real por pessoa** e risco de incompatibilidade. O Roomiê resolve com **filtros finos**, **índice de compatibilidade**, **endereço aproximado** antes do match (privacidade) e **notificações** para manter o fluxo ativo. Para usos futuros, o app prevê **gestão simples de despesas** (registro e divisão de contas), mantendo o foco em parceria e segurança. Para o setup técnico, adotamos **Compose BOM** (gerência de versões), **Version Catalog** (`libs.versions.toml`) e **plugin de segredos** (sem vazar chaves). ([Android Developers][1])

---

## Funcionalidades Principais

> (✓ = concluído; vazio = a fazer. Entre parênteses, **Sprint**)

* [ ] **Cadastro e Login** (A001–A003) — e-mail/senha; recuperar senha. **[S4–S5]**
* [ ] **Perfil Interessado** (UI001–UI002) — hábitos, pets, horários, orçamento. **[S5–S6]**
* [ ] **Anúncio do Ofertante** (UO001–UO003) — fotos, bairro/raio, aluguel + contas, regras; pausar/reativar. **[S6–S7]**
* [ ] **Lista de Vagas + Filtros** (VIC001–VIC002) — localização, preço, nº moradores, pets etc. **[S8]**
* [ ] **Interesse / Aceite** (VIC003–VIC004) e **Detalhe da Vaga** (VIC005). **[S9]**
* [ ] **Match & Recomendações** (MC001–MC003) — índice de compatibilidade e ordenação. **[S10–S11]**
* [ ] **Chat interno + Notificações** (CI001–CI003, N001–N003). **[S12–S13]**
* [ ] **Segurança/Moderação** — bloquear/denunciar perfis. **[S12]**
* [ ] **(Opcional) Gestão de Despesas** — registrar/ dividir contas, lembretes. **[pós-MVP]**

> Requisitos não-funcionais alvo: P95 feed ≤ 2,5s, disponibilidade ≥ 99,5%, LGPD (endereço **aproximado** antes do match), acessibilidade AA. **[S14]**

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

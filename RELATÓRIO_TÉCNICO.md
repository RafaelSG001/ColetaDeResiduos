# Relatório Técnico — Modernização de Sistema Legado (EcoGestão Resíduos)

**Instituição:** Escola Superior de Engenharia e Gestão (ESEG)  
**Curso:** Engenharia de Computação  
**Disciplina:** Programação Web
**Professor:** Israel Florentino Dos Santos
**Aluno:**  Roberto Rafael Lima Nunes Silva - RA: 42872
**Data:** 20/11/2026
**Artefato:** Relatório Técnico e Inventário de Decisões Arquiteturais (Seções 5.2.d e 5.4 do Edital)

---

## 1. Diagnóstico e Problemas Identificados na Estrutura Legada

A base de dados original (`11_gestao_residuos.xlsx`, composta por 4 planilhas totalizando 6.450 registros) apresentava deficiências estruturais, operacionais e de segurança que impediam seu uso como aplicação corporativa:

1. **Quebra de Rastreabilidade (Tabela Órfã):** A tabela `Clientes` (1.200 registros) não possuía chave estrangeira para `Empresas` (10 registros) nem ligação com `Coletas_Residuos` (5.000 registros). Era impossível identificar qual gerador solicitou determinada coleta ou emitir o Manifesto de Transporte de Resíduos (MTR).
2. **Desnormalização e Redundância (Violação de 2FN e 3FN):** Atributos textuais como `tipo_residuo`, `destino_final`, `status_coleta`, `segmento`, `cidade` e `estado` eram repetidos milhares de vezes como `VARCHAR` livre, gerando anomalias de atualização e degradação de busca.
3. **Ausência de Chave Primária:** A tabela `Indicadores_Mensais` (240 registros) não possuía identificador único, dificultando o mapeamento relacional em JPA/Hibernate.
4. **Inconsistência de Estados (`status_coleta`):** O campo de status era uma string sem validação de ciclo de vida, permitindo transições inválidas (como reabrir ou concluir uma coleta já cancelada) e receita financeira em pedidos cancelados.
5. **Ausência de Segurança e Controle de Acesso:** Os dados ficavam expostos sem autenticação, sem hash de senhas e sem segregação entre operadores ambientais, clientes geradores e administradores.

---

## 2. Soluções Adotadas e Justificativas Técnicas

### 2.1. Evolução do Modelo Relacional e Orientado a Objetos (3FN)
A base foi reestruturada em **9 tabelas relacionais** no schema `APP` (`USUARIO`, `TIPO_EDIFICACAO`, `ENDERECO`, `CLIENTE`, `EMPRESA`, `INDICADOR_MENSAL`, `SERVICO`, `PEDIDO` e `ITEM_PEDIDO`) e **2 enumerações** (`Perfil` e `StatusPedido`):

* **Chaves Substitutas e Restrições de Unicidade:** Adoção de chaves primárias numéricas (`BIGINT IDENTITY`) para entidades de negócio e `UUID` para `Usuario`, combinadas com `@UniqueConstraint` em campos naturais (`login_email`/`username`, `cnpj`, `nome_empresa`, `codigo_protocolo`, `tipo_residuo`, `numero_mtr_certificado`).
* **Indexação Estratégica (`@Index`):** Criação de índices explícitos nas chaves estrangeiras e colunas mais consultadas (`IDX_PEDIDO_CLIENTE`, `IDX_PEDIDO_EMPRESA`, `IDX_PEDIDO_STATUS`, `IDX_ITEM_SERVICO`, `IDX_ENDERECO_CIDADE_UF`), reduzindo buscas de $O(N)$ (*Full Table Scan*) para $O(\log N)$ (*B-Tree Index Seek*).
* **Resolução de Relacionamento N:N via Classe Associativa (`ItemPedido`):** Um `Pedido` pode conter múltiplos tipos de resíduos (`Servico`), e cada item possui atributos próprios (`pesoToneladas`, `destinoFinal`, `custoItem`, `receitaReciclagemItem`, `numeroMtrCertificado`) além de compor o `Endereco` físico da retirada.
* **Lado Proprietário (*Owning Side*) no JPA:** Nas associações `@ManyToOne`, o lado proprietário é a entidade que detém a coluna de chave estrangeira (`@JoinColumn`), garantindo integridade referencial no banco (`Pedido` referencia `Cliente` e `Empresa`; `ItemPedido` referencia `Pedido`, `Servico` e `Endereco`; `Endereco` referencia `TipoEdificacao`).

### 2.2. Máquina de Estados Estrita (`StatusPedido`)
Para prevenir estados inconsistentes, implementou-se o método `podeTransitarPara(StatusPedido novo)` no enum `StatusPedido` e a validação de negócio em `Pedido.avancarStatus(StatusPedido novo)`:

* **Fluxo válido:** `PENDENTE` $\rightarrow$ `AGENDADO` $\rightarrow$ `EM_EXECUCAO` $\rightarrow$ `CONCLUIDO` (ou `CANCELADO` a partir de estados não finais).
* **Bloqueios:** Proibido retroceder de `AGENDADO` para `PENDENTE` ou alterar pedidos nos estados finais `CONCLUIDO` e `CANCELADO` (lança `IllegalStateException`).
* **Regra Financeira:** Ao transitar para `CANCELADO`, a `receitaReciclagemTotal` é automaticamente zerada.

### 2.3. Autenticação, Autorização e Integridade Administrativa (Spring Security 6)
* **Proteção no Servidor (`SecurityConfig`):** Regras granulares por método HTTP e perfil (`ADMIN`, `OPERADOR`, `CLIENTE`), com `BCryptPasswordEncoder`, proteção CSRF habilitada por padrão e página dedicada `/acesso-negado` (HTTP 403).
* **Revalidação Contínua de Sessão (`EstadoContaFilter`):** Filtro `OncePerRequestFilter` executado antes do `AuthorizationFilter` que verifica a conta no banco H2 a cada requisição; caso o usuário seja desativado ou tenha seu perfil alterado, a sessão é imediatamente invalidada (`/login?expired`).
* **Proteção contra Perda do Último Administrador:** Uso de bloqueio pessimista (`@Lock(LockModeType.PESSIMISTIC_WRITE)`) em `UsuarioRepository.bloquearAdministradoresAtivos()` no `UsuarioService`, impedindo que o sistema fique sem ao menos um `ADMIN` ativo mesmo sob concorrência.
* **Integração com Thymeleaf:** Renderização condicional de menus e botões via `sec:authorize` e exibição da identidade autenticada via `sec:authentication="name"`.

### 2.4. Estratégia de Migração e Preservação de Dados
O componente `CargaAutomatica` executa na inicialização (idempotente, apenas quando as tabelas estão vazias), migrando integralmente os 6.450 registros legados para a estrutura normalizada 3FN, preservando os indicadores mensais de $\text{CO}_2$ evitado e taxa de reciclagem.

---

## 3. Resumo da Matriz de Rastreabilidade

| Requisito / Lacuna | Solução Implementada | Evidência / Teste |
| :--- | :--- | :--- |
| **RF01 — Rastreabilidade e Múltiplos Itens** | Entidades `Pedido` + classe associativa `ItemPedido` com certificado MTR e `Endereco`. | CRUD `/pedidos` e `/itens-pedido` persistindo no H2 + `PedidoRegrasNegocioTest`. |
| **RF02 — Controle de Transição de Status** | Enum `StatusPedido` e método `Pedido.avancarStatus()` validado no `PedidoService`. | Testes unitários validando fluxo completo e bloqueio de `CANCELADO -> CONCLUIDO`. |
| **RF03 — Normalização de Domínios** | Tabelas próprias `SERVICO`, `TIPO_EDIFICACAO` e `ENDERECO` com `UNIQUE` e FKs. | Esquema `APP` inspecionável no H2 Console com 9 tabelas íntegras. |
| **RF04 — Autenticação e Perfis Distintos** | `SecurityConfig`, `PrincipalAmbiental`, `EstadoContaFilter`, `/login` e `/acesso-negado`. | `SecurityAccessTest` comprovando redirecionamento para `/login` e HTTP 403 para `CLIENTE`. |
| **RF05 — Preservação de Indicadores ESG** | Entidade `IndicadorMensal` ligada a `Empresa` e totalizadores no `DashboardService`. | Painel `/` exibindo métricas consolidadas de peso, receita e $\text{CO}_2$ evitado. |
| **RNF01 — Usabilidade e Responsividade** | Templates Thymeleaf + Bootstrap 5.3 + tema corporativo `EcoGestão Resíduos`. | Navegação responsiva em desktop/mobile com paginação via `Pageable`. |

---

## 4. Testes Realizados e Resultados Obtidos

Os testes automatizados foram estruturados com **JUnit 5** e **Spring Security Test (`MockMvc`)**, cobrindo cenários válidos, inválidos e casos-limite:

### 4.1. Testes Unitários (`PedidoRegrasNegocioTest`)
* **Transições válidas de status:** `PENDENTE` $\rightarrow$ `AGENDADO` $\rightarrow$ `EM_EXECUCAO` $\rightarrow$ `CONCLUIDO` — **Aprovado**.
* **Bloqueio de transição a partir de estado final:** tentativa de alterar `CANCELADO` para `CONCLUIDO` lançando `IllegalStateException` — **Aprovado**.
* **Bloqueio de retrocesso de status:** tentativa de retornar `AGENDADO` para `PENDENTE` — **Aprovado**.
* **Zeramento de receita no cancelamento:** confirmação de que `receitaReciclagemTotal` passa para `0` ao cancelar — **Aprovado**.
* **Cálculo financeiro:** validação de `Pedido.calcularSaldoLiquido()` e `ItemPedido.calcularSubtotalLiquido()` — **Aprovado**.
* **Caso-limite (valores nulos):** validação de `Cliente.calcularVolumeTotalContrato()` retornando `BigDecimal.ZERO` quando atributos ainda não foram preenchidos — **Aprovado**.

### 4.2. Testes de Segurança e Controle de Acesso (`SecurityAccessTest`)
* **Usuário não autenticado em `/pedidos`:** redirecionamento HTTP 302 para `/login` — **Aprovado**.
* **Perfil `CLIENTE` acessando `/usuarios`:** bloqueio no servidor com HTTP 403 (*Forbidden*) — **Aprovado**.
* **Perfil `CLIENTE` enviando `POST /pedidos/1/excluir` com CSRF:** bloqueio no servidor com HTTP 403 (*Forbidden*) — **Aprovado**.
* **Perfil `ADMIN` acessando `/usuarios`:** acesso permitido com HTTP 200 (*OK*) — **Aprovado**.

### 4.3. Funcionalidades Verificadas Manualmente (Não Cobertas por Testes Automatizados)
* Comportamento responsivo dos templates Bootstrap em diferentes larguras de tela (DevTools).
* Carga completa em lote dos 6.450 registros legados na inicialização limpa do banco H2.
* Renderização visual da identidade corporativa (`EcoGestão Resíduos`) e da logomarca nas telas de login e menu.

---

## 5. Limitações Conhecidas, Riscos Residuais e Melhorias Futuras

* **Limitações Conhecidas:**
  * Persistência configurada em banco H2 em arquivo local (`./database/faculdade`), adequado para reprodução acadêmica e testes locais, porém não recomendado para alta concorrência em produção.
  * Credenciais padrão de laboratório criadas automaticamente pelo `CargaAutomatica` para facilitar a avaliação.
* **Riscos Residuais:**
  * Caso a aplicação esteja em execução simultânea ao comando `mvnw test`, o arquivo `.mv.db` do H2 bloqueia o acesso concorrente (mitigado documentando o encerramento prévio do servidor no `README.md`).
* **Melhorias Futuras:**
  * Migração da camada de persistência para **PostgreSQL** via Docker Compose com versionamento de schema via **Flyway**.
  * Geração de comprovante em **PDF** do Manifesto de Transporte de Resíduos (MTR) por `ItemPedido`.
  * Restrição de escopo de visualização para que usuários `ROLE_CLIENTE` visualizem exclusivamente os pedidos vinculados ao seu próprio CNPJ.
  * Inclusão de gráficos interativos no Dashboard ESG para acompanhamento temporal da redução de emissões de $\text{CO}_2$.
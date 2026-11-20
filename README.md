# EcoGestão Resíduos — Sistema Web de Gestão Ambiental e Coleta de Resíduos

Aplicação web full stack desenvolvida como **Projeto Integrador Individual** para a disciplina de **Programação Web** (Engenharia de Computação — ESEG, 2º Semestre de 2026).

O sistema moderniza uma base de dados legada de gestão de resíduos (`11_gestao_residuos.xlsx`, com 6.450 registros desnormalizados) em uma plataforma corporativa aderente à **3ª Forma Normal (3FN)**, com rastreabilidade ponta a ponta de coletas e certificados MTR, máquina de estados para controle operacional e autenticação/autorização baseada em perfis.

---

## 1. Tecnologias Utilizadas

| Camada | Tecnologias |
| :--- | :--- |
| **Linguagem e Framework** | Java 21, Spring Boot 3.5.7 |
| **Web e Templates** | Spring MVC, Thymeleaf, `thymeleaf-extras-springsecurity6` |
| **Frontend** | HTML5, CSS3, Bootstrap 5.3 (layout responsivo e identidade visual *EcoGestão Resíduos*) |
| **Persistência e Banco** | Spring Data JPA, Hibernate ORM, H2 Database (arquivo local `./database/faculdade`, schema `APP`) |
| **Segurança** | Spring Security 6, `BCryptPasswordEncoder`, proteção CSRF, `OncePerRequestFilter` (`EstadoContaFilter`) |
| **Testes Automatizados** | JUnit 5, Spring Boot Test, Spring Security Test (`MockMvc`) |

---

## 2. Arquitetura e Organização do Projeto

O projeto segue o padrão arquitetural em camadas do Spring MVC (`Controller -> Service -> Repository -> Model/Entity`):

```text
ColetaDeResiduos/
├── pom.xml
├── README.md
├── RELATORIO_TECNICO.md
└── src/
    ├── main/
    │   ├── java/br/garbage/ColetaDeResiduos/
    │   │   ├── ColetaDeResiduosApplication.java
    │   │   ├── carga/            # Migração automática da base legada (CargaAutomatica)
    │   │   ├── config/           # SecurityConfig e H2ConsoleSecurityConfig (perfil dev)
    │   │   ├── controller/
    │   │   │   ├── rest/         # Endpoints JSON (@RestController)
    │   │   │   └── web/          # Controllers MVC Thymeleaf (@Controller)
    │   │   ├── model/            # 9 Entidades JPA + Enums Perfil e StatusPedido
    │   │   ├── repository/       # Interfaces Spring Data JPA + queries e locks
    │   │   ├── security/         # PrincipalAmbiental (UserDetails) e EstadoContaFilter
    │   │   └── service/          # Regras de negócio, Dashboard e transações
    │   └── resources/
    │       ├── application.properties
    │       ├── arquivos/         # Dados legados (.txt) e schema.sql
    │       ├── static/           # CSS corporativo e ícone (images/logo-eco)
    │       └── templates/        # Views Thymeleaf (login, acesso-negado, fragmentos e CRUDs)
    └── test/
        └── java/br/garbage/ColetaDeResiduos/
            ├── ColetaDeResiduosApplicationTests.java
            ├── PedidoRegrasNegocioTest.java   # Testes unitários de domínio e status
            └── SecurityAccessTest.java        # Testes de autenticação e HTTP 403
```

---

## 3. Pré-requisitos e Instruções de Execução

### Pré-requisitos
* **JDK 21** (ou superior compatível)
* **Git** e **Maven** (ou o Maven Wrapper `mvnw` / `mvnw.cmd` incluso no repositório)

### Executando via Terminal (Maven Wrapper)
1. Clone o repositório e acesse a pasta do projeto:
   ```bash
   git clone https://github.com/RafaelSG001/ColetaDeResiduos.git
   cd ColetaDeResiduos
   ```
2. Inicie a aplicação:
   * **Windows (PowerShell / CMD):**
     ```bash
     .\mvnw.cmd spring-boot:run
     ```
   * **Linux / macOS:**
     ```bash
     ./mvnw spring-boot:run
     ```

### Executando via IntelliJ IDEA
1. Abra a pasta `ColetaDeResiduos` como projeto Maven.
2. Aguarde a sincronização das dependências (`Reload All Maven Projects`).
3. Execute a classe principal `br.garbage.ColetaDeResiduos.ColetaDeResiduosApplication`.

### Endereços de Acesso
* **Aplicação Web:** `http://localhost:8082`
* **Console H2 (ativo no perfil `dev`):** `http://localhost:8082/h2-console`
  * **JDBC URL:** `jdbc:h2:file:./database/faculdade;DB_CLOSE_ON_EXIT=FALSE;INIT=CREATE SCHEMA IF NOT EXISTS APP\;SET SCHEMA APP`
  * **User Name:** `sa`
  * **Password:** *(em branco)*

---

## 4. Perfis de Acesso (Ambiente de Laboratório)

Na primeira inicialização, o componente `CargaAutomatica` cria as contas de laboratório com senhas criptografadas via **BCrypt** e realiza a migração integral dos dados legados para as tabelas normalizadas:

| Perfil | Usuário | Senha | Permissões no Servidor (`SecurityConfig`) |
| :--- | :--- | :--- | :--- |
| **ADMIN** | `admin` | `admin123` | Acesso total ao sistema, gestão de contas (`/usuarios/**`) e exclusão de registros (`POST /*/*/excluir`). |
| **OPERADOR** | `operador` | `operador123` | Consulta, cadastro e edição de empresas, clientes, serviços, endereços, tipos de edificação, indicadores e pedidos. Bloqueado (HTTP 403) em exclusões e gestão de usuários. |
| **CLIENTE** | `cliente` | `cliente123` | Consulta de painéis e abertura/edição de pedidos e itens de coleta. Bloqueado (HTTP 403) em cadastros estruturais, exclusões e `/usuarios`. |

---

## 5. Principais Rotas e Funcionalidades

* **Autenticação:** `/login`, `/logout` (POST com CSRF), `/acesso-negado` (HTTP 403)
* **Painel Executivo (Dashboard):** `/` (KPIs de empresas, clientes, pedidos, peso total coletado, receita de reciclagem e $\text{CO}_2$ evitado)
* **Módulos CRUD (Thymeleaf):**
  * `/pedidos` — Gestão de pedidos de coleta com filtros multicritério, paginação e máquina de estados
  * `/itens-pedido` — Classe associativa com peso em toneladas, destino final, endereço e certificado MTR
  * `/clientes` — Cadastro de geradores de resíduos vinculados ao segmento/tipo de edificação
  * `/empresas` — Operadoras ambientais com certificação ISO 14001 e dados geográficos
  * `/servicos` — Catálogo de resíduos, periculosidade e tarifa por tonelada
  * `/enderecos` e `/tipos-edificacao` — Locais físicos de coleta e normas aplicáveis
  * `/indicadores` — Histórico mensal ESG e emissão de $\text{CO}_2$ evitada
  * `/usuarios` — Administração de contas e perfis (exclusivo `ROLE_ADMIN`)
* **API REST (JSON):** `/api/pedidos/{id}` e `/api/empresas/{id}`

---

## 6. Testes Automatizados

> **Importante:** Pare a aplicação antes de executar a suíte de testes, pois o banco H2 em arquivo mantém bloqueio exclusivo durante a execução do servidor.

Para rodar todos os testes unitários e de integração/segurança:
```bash
.\mvnw.cmd test
```
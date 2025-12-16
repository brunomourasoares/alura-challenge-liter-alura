# LiterAlura

Catálogo de livros que roda no console (CLI) e integra com a API pública Gutendex para buscar, salvar e consultar obras e autores. Construído com Spring Boot 3, Java 17 e JPA/Hibernate, usando H2 (dev) e PostgreSQL (prod).

## Sumário
- [Visão geral](#visão-geral)
- [Funcionalidades](#funcionalidades)
- [Arquitetura e tecnologias](#arquitetura-e-tecnologias)
- [Requisitos](#requisitos)
- [Configuração](#configuração)
  - [Perfis (dev e prod)](#perfis-dev-e-prod)
  - [Banco de dados](#banco-de-dados)
- [Como executar](#como-executar)
  - [Ambiente de desenvolvimento (H2)](#ambiente-de-desenvolvimento-h2)
  - [Ambiente de produção (PostgreSQL)](#ambiente-de-produção-postgresql)
  - [Executar o JAR](#executar-o-jar)
- [Como usar (menu CLI)](#como-usar-menu-cli)
- [Modelo de dados](#modelo-de-dados)
- [APIs externas](#apis-externas)
- [Testes](#testes)
- [Solução de problemas](#solução-de-problemas)
- [Roadmap (ideias)](#roadmap-ideias)

---

## Visão geral
O LiterAlura oferece uma interface textual (via console) com, no mínimo, 5 operações para interação com o catálogo. Ao buscar um título que não existe no banco local, a aplicação consulta a API do Gutendex, converte o JSON em entidades, e persiste no banco.

## Funcionalidades
- Buscar livro pelo título (local; se não existir, consulta Gutendex e salva)
- Listar livros registrados
- Listar autores registrados
- Listar autores vivos em um determinado ano
- Listar livros por idioma (ex.: `en`, `pt`)
- Estatísticas agregadas (quantidade, min/máx/média/soma de downloads)
- Top 10 livros mais baixados
- Buscar autor por nome

## Arquitetura e tecnologias
- Spring Boot 3.2.3 (starter-data-jpa, starter-web)
- Java 17
- JPA/Hibernate
- H2 (dev) e PostgreSQL (prod)
- Jackson (serialização/deserialização)
- HTTP Client Java 11+ para consumo da API Gutendex
- Console Runner via `CommandLineRunner`

Principais componentes:
- `LiterAluraApplication` (ponto de entrada) inicia o menu CLI (`MenuPrincipal`).
- `LivroService` orquestra chamadas ao repositório, consumo da API e conversão de dados.
- `ConsumoApi` faz o HTTP GET na Gutendex.
- `ConverteDados`/`IConverteDados` usam Jackson para mapear JSON em DTOs/entidades.
- `LivroRepository` expõe consultas (por título, idioma, autor, top10 downloads, etc.).
- Entidades JPA: `Livro`, `Autor`, `FormatosMedia` (+ coleções `@ElementCollection`).

## Requisitos
- JDK 17+
- Maven (o projeto inclui Maven Wrapper: `mvnw`/`mvnw.cmd`)
- (Prod) PostgreSQL 13+ local ou em container

## Configuração
A aplicação usa perfis do Spring para separar configurações.

### Perfis (dev e prod)
Arquivo base: `src/main/resources/application.properties`
- `spring.profiles.active=dev` (padrão)

#### Dev (`application-dev.properties`)
- H2 em memória (`jdbc:h2:mem:literalura`)
- Console H2 habilitado em `/h2-console`
- `ddl-auto=create-drop`

#### Prod (`application-prod.properties`)
- PostgreSQL com variáveis de ambiente:
  - `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`
- `ddl-auto=update`

### Banco de dados
- Dev: H2 em memória (zero setup).
- Prod: PostgreSQL. Exemplo rápido com Docker:

```powershell
# Sobe um PostgreSQL local na porta 5432
docker run --name literalura-postgres -e POSTGRES_DB=literalura -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres:16
```

## Como executar
Todas as instruções abaixo consideram Windows PowerShell.

### Ambiente de desenvolvimento (H2)
```powershell
# Opcional: explicitar o perfil
echo $env:SPRING_PROFILES_ACTIVE = "dev"

# Rodar direto com o Maven Wrapper
./mvnw.cmd spring-boot:run
```
Após iniciar, o menu do console será exibido no terminal. O servidor embutido também sobe (por conta do starter Web), permitindo acessar o console do H2 em:
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:literalura`
- User: `sa`
- Password: (vazio)

### Ambiente de produção (PostgreSQL)
Configure as variáveis e execute:
```powershell
$env:SPRING_PROFILES_ACTIVE = "prod"
$env:DB_HOST = "localhost"
$env:DB_PORT = "5432"
$env:DB_NAME = "literalura"
$env:DB_USER = "postgres"
$env:DB_PASSWORD = "postgres"

./mvnw.cmd spring-boot:run
```

### Executar o JAR
```powershell
# Gera o JAR
./mvnw.cmd clean package

# Executa (usa o perfil ativo nas variáveis ou no application.properties)
java -jar ./target/LiterAlura-0.0.1-SNAPSHOT.jar
```

Dica: você pode trocar o perfil em runtime adicionando `-Dspring.profiles.active=prod` na linha de comando do Maven/Java.

## Como usar (menu CLI)
Ao iniciar, você verá algo similar a:
```
----- MENU PRINCIPAL -----
1- Buscar livro pelo titulo
2- Listar livros registrados
3- Listar autores registrados
4- Listar autores vivos em um determinado ano
5- Listar livros em um determinado idioma
6- Gerar estatísticas (extra)
7- Top 10 livros mais baixados (extra)
8- Buscar autor por nome (extra)
0- Sair
```
- A busca por título verifica primeiro o banco local; se vazio, consulta a Gutendex e salva os resultados (com proteção contra duplicidade simples por título).
- Idiomas devem ser informados no formato ISO curto (ex.: `en`, `pt`).
- A listagem por autores vivos considera o autor vivo no ano X se `nascimento <= X <= falecimento`.

## Modelo de dados
- `Livro`
  - Muitos-para-muitos com `Autor` (autores e tradutores)
  - Muitos-para-um com `FormatosMedia`
  - Várias coleções (`resumos`, `editores`, `assuntos`, `estantes`, `idioma`)
  - Campo `totalDeDownloads` para ranking
- `Autor`
  - Campos: `nome`, `anoDeNascimento`, `anoDeFalecimento`
- `FormatosMedia`
  - Mapeia chaves dinâmicas de formatos (HTML, EPUB, MOBI, etc.) via Jackson; extras caem em `additionalFormats`

Observações:
- O salvamento em lote está configurado (tamanho 32) para reduzir consumo de memória e limpar o contexto.
- A deduplicação é feita por título (pode haver títulos iguais de autores diferentes; ajuste se desejar granularidade por autor+titulo).

## APIs externas
- [Gutendex](https://gutendex.com/)
  - A aplicação consulta `https://gutendex.com/books/?search=<termo>`
  - O JSON é convertido com Jackson para `RespostaAPI` e depois para entidades `Livro`/`Autor`.

## Solução de problemas
- Porta 8080 ocupada: altere `server.port` ou libere a porta.
- Console H2 não abre: confirme que o perfil `dev` está ativo e acesse `http://localhost:8080/h2-console`.
- Erro de conexão PostgreSQL (prod): verifique variáveis `DB_*`, rede/porta e credenciais.
- Falha ao consultar Gutendex: cheque conectividade de rede/proxy. A exceção será propagada como `RuntimeException`.

---
Projeto baseado no desafio Alura (LiterAlura).

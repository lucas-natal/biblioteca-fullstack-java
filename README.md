# 📚 Sistema de Gestão de Biblioteca

Aplicação web de biblioteca feita com **Java 21, Spring Boot, JPA/Hibernate e H2**, com front-end em **HTML, CSS e JavaScript puro**. Funcionários cadastram livros e exemplares; usuários consultam o acervo e fazem empréstimos e devoluções.

Projeto acadêmico de Programação de Sistemas II (Universidade Presbiteriana Mackenzie).

## O que o sistema faz

**Área pública (sem login)**
- Lista os livros do acervo em cartões.
- Busca livros por título.

**Usuário**
- Cria conta (registro, nome, data de nascimento, telefone e senha). O e-mail é gerado automaticamente no formato `registro@biblioteca.com`.
- Faz login e empresta um exemplar informando o código dele. A devolução prevista é de **15 dias** após o empréstimo.
- Vê o histórico de empréstimos com o status de cada um: **ativo**, **atrasado** ou **devolvido**.
- Devolve um exemplar emprestado.

**Funcionário**
- Faz login com o ID funcional.
- Cadastra livros (código, título, autor e ano de publicação).
- Cadastra exemplares de um livro.
- Consulta um exemplar pelo código, vê seu status e pode removê-lo.
- Lista os usuários cadastrados.

**Perfil**
- Botão no cabeçalho, para quem está logado, com nome, registro/ID, e-mail, telefone e data de nascimento, além do botão de sair.

## Tecnologias

Java 21 · Spring Boot 4 (Web MVC, Data JPA, Validation) · Hibernate · H2 em memória · Maven · HTML/CSS/JS

## Como rodar

Requisito: JDK 21 ou superior.

```bash
git clone <url-do-repositorio>
cd gestaobiblioteca
./mvnw spring-boot:run        # no Windows: .\mvnw.cmd spring-boot:run
```

Abra <http://localhost:8080>.

O banco é em memória e é recriado, com dados de exemplo, a cada execução. As consultas SQL são impressas no terminal (`spring.jpa.show-sql=true`).

### Contas de exemplo

| Tipo | Login | Senha |
|---|---|---|
| Usuário | registro `1` | `lapetina` |
| Usuário | registro `2` | `mabel` |
| Usuário | registro `3` | `gil` |
| Funcionário | ID `1` ou `2` | `func123` |

O acervo de exemplo traz 4 livros: *Dom Casmurro*, *O Cortiço*, *Capitães da Areia* e *Vidas Secas*, com seus exemplares.

## API REST

| Método | Rota | O que faz |
|---|---|---|
| GET / POST | `/api/livros` | Lista / cadastra livros |
| GET / PUT / DELETE | `/api/livros/{codigo}` | Busca / atualiza / remove um livro |
| GET | `/api/livros/busca?titulo=` | Busca por parte do título |
| POST | `/api/livros/{codigo}/exemplares` | Cadastra exemplar de um livro |
| GET | `/api/livros/{codigo}/exemplares/disponiveis` | Lista exemplares disponíveis |
| GET / DELETE | `/api/exemplares/{codigo}` | Busca / remove um exemplar |
| GET / POST | `/api/usuarios` | Lista / cadastra usuários |
| GET / PUT / DELETE | `/api/usuarios/{registro}` | Busca / atualiza / remove um usuário |
| GET | `/api/usuarios/{registro}/informacoes` | Texto de `exibirInformacoes()` |
| GET / POST | `/api/funcionarios` | Lista / cadastra funcionários |
| GET / PUT / DELETE | `/api/funcionarios/{id}` | Busca / atualiza / remove um funcionário |
| GET | `/api/funcionarios/{id}/informacoes` | Texto de `exibirInformacoes()` |
| GET / POST | `/api/emprestimos` | Lista / registra empréstimos |
| GET | `/api/emprestimos/{id}` | Busca um empréstimo |
| GET | `/api/emprestimos/usuario/{registro}` | Empréstimos de um usuário |
| PUT | `/api/emprestimos/{id}/devolucao` | Registra a devolução |
| POST | `/api/login` | Login de usuário |
| POST | `/api/login/funcionario` | Login de funcionário |

Erros voltam com o status HTTP adequado: **400** dados inválidos, **404** não encontrado e **409** conflito (código duplicado ou regra de negócio).

## Estrutura

```
src/main/java/com/biblioteca/gestaobiblioteca/
├── GestaobibliotecaApplication.java   # classe principal
├── config/       # DadosIniciais (dados de exemplo)
├── controller/   # endpoints REST
├── dto/          # objetos de entrada (records)
├── exception/    # exceções e tratamento global
├── model/        # entidades JPA
└── repository/   # interfaces Spring Data
src/main/resources/static/   # index.html, style.css, app.js
```

## Conceitos de POO aplicados

- **Herança:** `Usuario` e `Funcionario` estendem `Pessoa`, que é `@MappedSuperclass`.
- **Polimorfismo e sobrescrita:** `exibirInformacoes()` é sobrescrito nas subclasses.
- **Sobrecarga:** `adicionarExemplar(Exemplar)` / `adicionarExemplar(int)` e os dois construtores de `Emprestimo`.
- **Encapsulamento:** atributos privados, alterados só por construtores e métodos da própria classe.
- **Associação:** `Livro` 1→N `Exemplar`, `Usuario` 1→N `Emprestimo`, `Emprestimo` N→1 `Exemplar`.

## Limitações conhecidas

- O login protege apenas as **telas**: a API não exige autenticação, então qualquer cliente HTTP consegue chamar os endpoints. Proteger de verdade exigiria Spring Security, com tokens ou sessão.
- As senhas ficam em texto puro no banco, o que é aceitável só para fins didáticos. Em produção, usar hash (por exemplo, BCrypt).
- O banco H2 é em memória: os dados somem ao reiniciar.

## Autores

Lucas Natal, Giovana Nunes Schumacher, Pedro Guimarães, Isabella Gil, Maria Isabel e Felipe Lapetina.

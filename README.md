# Factory API

 - API desenvolvida utilizando Java 8 e Spring boot.

# Testes

 - Testes criados com Rest Assured e JUnit 5

# Rotas
VERBO | ROTA | DESCRIÇÃO |
------------ | ------------- | -------------
| GET| /pessoa | Carrega uma lista de pessoas 
| GET | /pessoa/{id} | Carrega uma pessoa baseada no ID
| POST| /pessoa | Persiste uma pessoa
| PUT| /pessoa/{id} | Atualiza uma pessoa, a pessoa deve ser informada no BODY
| DELETE| /pessoa/{id} | Deleta uma pessoa

# Banco de dados

 - Fora utilizado o banco de Dados H2 em modo arquivo
 
 - Será criado um banco de dados para Produção e um banco de dados para Testes
 
 # Features
 
 - Retorno de Recurso criado no Header Ex: _http://localhost/pessoa/1
 - Desenvolvido um componente/anotation para validar a data de nascimento
 - Toda infra do banco é gerenciado pelo Flyway
 - Separação do banco de dados de Produção e Testes
 - Utilização de uma classe para realizar a limpeza do banco a cada teste


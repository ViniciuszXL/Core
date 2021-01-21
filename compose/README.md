# dust-compose-api

Este projeto é um projeto criado em Docker Compose para a criação da API em NodeJS para consumo de servidores de minecrafts próprios.

O intuito de criar este projeto, é ter mais estabilidade, segurança, além de ter mais aprendizado em contâiners.

Também há o fato de ter um consumo menor de recursos por parte do dedicado/vps, na questão de banco de dados.

## Frameworks utilizadas ##

* NodeJS
* Cors
* ExpressJS
* HTTP
* Mongoose
* MongoDB
* MariaDB
* Redis

## Como funciona? ##

A parte principal deste projeto é a API. Ela que faz a conexão somente com o banco de dados, onde, quando ela recebe um requisito HTTP, ela verifica qual requisit é, e qual informação é pedida. Logo, ela fará conexão somente com o que for pedido, pegará o resultado e retornará em formato `JSON`.

Há duas redes criadas para os containers, onde `banco` é a rede responsável pelos serviços `redis, mongo e maria`, e a rede `web`, onde ela é responsável pelo serviço `front`. 

O contâiner `API`, tem acesso total à rede `banco e web`, onde, ela que é a intermediadora das conexões e requisições. 

### NGINX ###

A parte do front (nginx), é utilizando a tecnologia nginx para o proxy reverso, para que não haja quaisquer problemas de conexões que pode acontecer durante a execução da aplicação.

Dito iso, antes, seria necessário especificar a porta 8080 da API para que seja possível a requisição de informações. Mas, com o Proxy Reverso do nginx, só é necessário acessar `http://localhost/api` para que a utilização da API seja funcional, além de ter uma URL amigável.
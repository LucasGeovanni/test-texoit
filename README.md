
# test-texoit





## Sobre

A partir de um arquivo csv, obtem o produtor com maior intervalo entre dois prêmios consecutivos, e o que
obteve dois prêmios mais rápido.
## Requisitos de execução

Requisitos para a execução do serviço é necessario o Java JDK 11 e Maven


## Como executar:

#### mvn clean install java 
#### -jar target/goldenRaspberryAwards-0.0.1-SNAPSHOT.jar
## Documentação

(Documentação Swagger http://localhost:8080/swagger-ui.html)


## Teste

Com o arquivo movielist.csv na pasta principal do repositório, executar a consulta no end-point - [/v1/movie-information/raspberryAwards](/v1/movie-information/raspberryAwards)

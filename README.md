Certifique-se que tem instalados o [Maven](https://maven.apache.org/download.cgi), o [Java JDK 17](https://www.oracle.com/java/technologies/downloads/?er=221886) e o [MySQL](https://www.mysql.com/downloads/) em seu dispositivo.

Para rodar:

No MySQL:

```
CREATE SCHEMA `projeto` ;
```

edite em 

``
ProjetoSpringFullStack/src/main/resources/application.properties
`` 

Para adicionar os dados de seu banco

```
spring.datasource.username=root
spring.datasource.password=todo1233#@S
```

Em seguida

Digite o seguinte comando no terminal para rodar o projeto:

```
mvn spring-boot:run
```

# Spring-boot-library

Spring Boot base Library App

## Installation

### Run a database, eg. MariaDB

1. `cd /var/lib`
1. `sudo mkdir -p /var/lib/mariadb/data`
1. `sudo chown -R 999:999 mariadb`
1. `docker run -d --name mariadb -p 3306:3306 -e MARIADB_ROOT_PASSWORD=root -v /var/lib/mariadb/data:/var/lib/mysql mariadb:11.4`
1. create the schema with [mariadb-create-schema.sql](src/main/resources/mariadb-create-schema.sql)

NB To stop, start, and restart the database use `docker start`, `docker stop`, and `docker restart`.

### Create a directory for the Lucene index

1. `cd /var/lib`
1. `sudo mkdir -p /var/lib/lucene/index`
1. `sudo chown -R <my-username>:<my-group> lucene`

### Run app

`java -jar target/spring-boot-library-0.0.1-SNAPSHOT.jar`

### API specs

- [Swagger](http://localhost:8080/swagger-ui/index.html)
- [OpenAPI spec in JSON](http://localhost:8080/v3/api-docs)
- [OpenAPI spec in YAML](http://localhost:8080/v3/api-docs.yaml)

### Smoke Test qqqq

- generate create tables scipt met ut
- load data.sql

use swagger or curl (NB swagger also generates the curl commands):
- get version
- get token as user
- find book


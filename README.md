## Описание решения

### Account Service

Сервис позволяет запрашивать или изменять баланс по id. Кэширует данные в памяти, а так же сохраняет их в БД PostgreSQL.

Сохраняет статистику по общему количеству запросов от клиентов, так же количество запросов в секунду(обновляется ежесекундно). Доступ по http запросу 

В качестве транспортного слоя - протокол HTTP

#### Используемые технологии

java 11, spring boot(web, jdbc), в качестве in-memory кэша используется hazelcast.

#### Для запуска
1. Необходимо предоставить следующие properties для подключения к БД

>spring.datasource.url
>
>spring.datasource.username
>
>spring.datasource.password

(Опционально) Максимальный размер кэша (по умолчанию, 100000)

>eviction.size

2. Сервис взаимодействует с следующей таблицей:

>CREATE TABLE accounts(
>
>    id INTEGER PRIMARY KEY,
>    
>    balance bigint NOT NULL
>    
>)

3. Добавить аргументы для запуска(для корректной работы hazelcast)

>java --add-modules java.se --add-exports java.base/jdk.internal.ref=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/sun.nio.ch=ALL-UNNAMED --add-opens java.management/sun.management=ALL-UNNAMED --add-opens jdk.management/com.ibm.lang.management.internal=ALL-UNNAMED --add-opens jdk.management/com.sun.management.internal=ALL-UNNAMED

#### Api endpoints
>GET /accounts/{id}    get balance of specific id
>
> response body example
> 
> {
> 'amount' : 100
> }

>PUT /accounts/{id}    add amount to balance of specific id
>
> request body example
> 
> {
> 'amount' : 100 
> }

>GET /statistics/add    get information about addAmount requests
>
> response body example
> 
>{
>"total": 0,
>"current": 0 
>}

>GET /statistics/get    get information about getAmount requests
>
> response body example
> 
>{
>"total": 0,
>"current": 0
>}

>GET /statistics/reset    reset all statistics on server

### Клиент

Тестовый клиент располагается в пакете test-client. Клиент может запускать несколько потоков (для каждого писателя или читателя создается отдельный поток).

Для обработки параметров через командную строку используется библиотека picocli

#### Main class: com.pavel.client.Client

#### Аргументы для запуска

> -u = url account service'a
> 
> -rCount = количество читателей вызывающих метод getAmount(id)
> 
>-wCount = количество читателей вызывающих метод addAmount(id, value)
>
>id1 id2 id3 ... список ключей, которые будут использоваться для тестирования

Пример

> -u=http://localhost:8080 -rCount=2 -wCount=2 1 2 



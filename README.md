# Complaint Service

REST API for managing complaints.

## How to run

```bash
mvn compile jib:dockerBuild
docker-compose up
```

## Swagger

API Documentation and examples.  
Go to SWAGGER: `http://localhost:8080/swagger-ui.html`
Credentials: admin/admin


## Client Remote IP discovery

The "Client Remote IP discovery" feature depends on deployment setup.
Please check `pl/tzason/complaint/util/RequestUtil.java` implementation for details.

**Setup A.**

If this application is deployed in the standalone mode without any proxy/load balancer,
then IP address is read directly from request `request.getRemoteAddr()` (default). 

**Setup B.**

Otherwise, when application is deployed with Load Balancer/ Reverse Proxy, remote address is read from HEADER `X-Forwarded-For`


### How to test?  

### 1. Local environment
If you want to verify country discovery by IP using your local environment, please add IP to the header `X-Real-IP: 84.234.119.249`. Below 2 examples (PL and DE):

* CURL PL IP address:

```bash
curl  -u admin:admin  -X 'POST' \
'http://localhost:8080/api/complaints' \
-H 'accept: */*' \
-H 'Content-Type: application/json' \
-H "X-Real-IP: 84.234.119.249" \
-d '{
"productId": "Product ",
"content": "Does not work",
"reporter": "tomasz.zason@test.pl"
}'
```


* CURL for DE IP address:

```bash
curl  -u admin:admin  -X 'POST' \
'http://localhost:8080/api/complaints' \
-H 'accept: */*' \
-H 'Content-Type: application/json' \
-H "X-Real-IP: 85.214.132.117" \
-d '{
"productId": "Produkt",
"content": "Funktioniert nicht",
"reporter": "tomasz.zason@test.de"
}'
```

### 2. Access from another device 

Then just run without header.

```bash
curl  -u admin:admin  -X 'POST' \
'http://localhost:8080/api/complaints' \
-H 'accept: */*' \
-H 'Content-Type: application/json' \
-d '{
"productId": "Produkt",
"content": "Funktioniert nicht",
"reporter": "tomasz.zason@test.de"
}'
```
# Spring Reference Implementation for Azure Service Bus - Queue

### This project is a reference implementation for Azure Service Bus - Queue with using Spring framework. 

Need docker and docker-compose to run on local system.
The docker-compose will create a tomcat container and will deploy the project on tomcat.
The tomcat container will be running on 9081 port.

This project uses JAX-RS for input/output. Postman script is provided.


1. Build the project using
```bash
mvn clean install
```

2. Start the network.
```bash
docker-compose up -d
```

3. See tomcat logs
```bash
docker logs -f tomcat.fspt.walmart.com
```

4. Stop the containers.
```bash
docker-compose down
```
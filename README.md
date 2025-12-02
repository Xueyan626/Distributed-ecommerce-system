# Tutorial12-Group-08  
Xueyan Yang

# pgAdmin #
Open pgAdmin 4  
Create a server  
Create these databases under the server:  
**'storeapplication'**  
**'bank'**  
**'delivery'**    

For each application’s src/main/resources/application.properties, update the settings as shown below.
**spring.datasource.url=jdbc:postgresql://localhost:5432/{{database}}**   
**spring.datasource.username={{your user name}}**  
**spring.datasource.password={{your password}}**  

# How to start front-end:  #  
docker compose up  frontend  
docker compose down 

# How to start/shut down Rabbit:  #  
docker compose up rabbit  
docker compose down 

**You can start Rabbit and Frontend together using 'docker compose up --build'**  

# How to start applications:  # (after start rabbit)
Each service can be started from your IDE  
Run DeliverycoApplication  
Run StoreApplication  
Run BankApplication  
Run EmailApplication  

## Configurations ##

# JDK #  
We use JDK17 for this program  
# Shared RabbitMQ:  #
spring.rabbitmq.host=localhost  
spring.rabbitmq.port=5672  
spring.rabbitmq.username=guest  
spring.rabbitmq.password=guest  
RabbitMQ UI: http://localhost:15672  (user/pass: guest/guest)  

# Port:  #
Store: 8080  
DeliveryCo: 8081  
Bank: 8082  
Email: 8083  




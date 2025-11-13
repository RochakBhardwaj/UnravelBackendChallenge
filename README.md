# Unravel Backend Challenge

## Prerequisites

To run this project you need the following 

- [Oracle JDK 21](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html) 
- [Maven](https://maven.apache.org/install.html) for building and managing the project dependencies.
- MySQL (Installation Step Given Below)
- [IntelliJ](https://www.jetbrains.com/idea/download)


#### Seting Up the MySQL Server

```bash
# macOS
brew install mysql

brew services start mysql
```

Windows:

Download SQL via : [SQL Installation](https://dev.mysql.com/downloads/mysql/)

Start the service using 

```bash
net start MySQL80
```

Inside the MySQL shell, run:

```sql
mysql -u root -p
CREATE USER 'dbuser'@'localhost' IDENTIFIED BY 'dbpassword';
GRANT CREATE, SELECT, INSERT, UPDATE, DELETE ON *.* TO 'dbuser'@'localhost';
CREATE DATABASE mydb;
FLUSH PRIVILEGES;
EXIT;
```

#### Verify Connection

```bash
mysql -u dbuser -p
```

---

## Project Setup

### 1. Configure Database in `application.properties`

Edit the file at `src/main/resources/application.properties`:

```properties
spring.application.name=unravel_backend

# DB Connection Settings
spring.datasource.url=jdbc:mysql://localhost:3306/mydb
spring.datasource.username=dbuser
spring.datasource.password=dbpassword

# HikariCP Configuration
spring.datasource.hikari.maximum-pool-size=100
spring.datasource.hikari.minimum-idle=10
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=60000
spring.datasource.hikari.max-lifetime=1800000
```

---


## Testing and Running Problems:

### Part1 : Session Management

**Pin-pointed tests in the test folder are created to replicate the initial problem and to see how the improved implementation solves the problem. Please run the code through the test package for the first problem**

### Part2 : Memory Management

**Tests are given in test folder for this problem, you can choose different strategies to look for memory management under different caching strategies.**

### Part3 : 

**The simulation of this problem is provided in the LogProcessingApp Class in the main package, please run it from there. **

### Part4 : 

**You can see the safe execution through the DeadlockSafeExecutor class in the main package as well as through the tests provided in the test package. The Test Package provides comparison between the safe and unsafe executions**

### Part5 : 

**The connection simulation scenarios are executed automatically on application startup via `ConnectionSimulator`.**  

You can check this problem by running the Spring Boot application (`UnravelBackendChallengeApplication`) through run icon in IntelliJ or through the command

```bash
mvnw spring-boot:run
```


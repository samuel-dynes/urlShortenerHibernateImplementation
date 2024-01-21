# URL Shortener Application

## Table of Contents

- [Introduction](#introduction)
- [Getting Started](#getting-started)
- [API Docs](#API Documentation)
- [Next Steps](#next-steps)
- [Architecture](#architecture)
   - [Benefits Of This Approach](#benefits-of-this-approach)
   - [Areas Of Improvement](#areas-of-improvement)

## Introduction
The URL Shortener application is designed to provide URL shortening and retrieval services through a RESTful API. It leverages Java technologies, including JAX-RS for API endpoints, Hibernate for database interaction, and NettyJaxrsServer for launching the web application.

Im using JAX-RS, Resteasy and Hibernate since yous didnt want Spring (which is the main framework I had experience with), so decided I may as well see what I could learn in 1 day with sections of your tech stack.

## Getting Started
### Prerequisites
- [Docker](https://www.docker.com/get-started)
- [Maven](https://maven.apache.org/download.cgi) (for building the project)
- JDK 21

### Starting application Locally
- Clone the repository: git clone <repository-url>
- Configure the database connection details in the hibernate.cfg.xml file.
- Configure the database connection details in local-postgress-docker-compose.yml to match the hibernate.cfg.xml file.
- To start the postgress database locally, navigate to the resources folder and run command
```bash
docker compose -f local-postgress-docker-compose.yml up -d
``` 
- To start the web application, run the WebappMain class. The application will be accessible at http://127.0.0.1:8080/api.

### Access the Application
After the containers are up and running, you can access the URL Shortener application at http://localhost:8080.

To add a url to the db and receive the link to expand it use the following command
```bash
curl -X POST -H "Content-Type: text/plain" -d "<URL DATA>" http://localhost:8080/urls/
```

To retrieve the lengthened URL you can use the following command
```bash
curl http://127.0.0.1:8080/api/urls/<SHORTENED_URL>
```

## API Docs
### API Endpoints

#### Shorten a URL

**Endpoint:**
```http request
POST /urls
```

**Request:**
```http request
http://localhost:8080/api/urls/
```

**Request Body:**
```
Long URL to be shortened
```

**Response:**
```
Shortened URL
```

#### Retrieve the Original URL
   
**Endpoint:**
```http request
GET /urls/{shortenedUrl}
```

**Request:**
```http request
http://localhost:8080/api/urls/{shortenedUrl}
```

Response:
```http request
Original URL corresponding to the shortened URL
```
### Error Handling
#### **400 Bad Request:** 
Returned when the provided URL for shortening does not match the expected format.

#### **404 Not Found:** 
Returned when data is not found in the database for the given shortened URL.

#### **500 Internal Server Error:**
Returned when there is an error communicating with the database during the URL shortening or retrieval process.


## Next Steps

### Cloud deployment
Given that im a single dev in this scenario and im looking to capitalise on a time sensitive issue, I wouldn't be
implementing a full pipeline for the first deployment to capitalise on the sceanrio and then backill with my deployment infrastructure. 
I'd be deploying the MVP manually and using the data I get from that to inform the build pipeline
but my next steps would be


#### Deploy Postgress DB

1. Host postgres database instance on RDS:
   - Ensure that security groups allows access to the db from a future EC2 instance
   - Update hibernate.cfg.xml to reflect the RDS database URL, username, password and ports

#### Deploy Java Application

1. Get a domain from goDaddy, lets run with "http://shorturl.com/"

2. Host the Java on an EC2 instance:
   - Probably use a m7g.medium due to the mixed resource requirements + to judge demand
   - Will need to install java on the instance + ensure that 8080 is allowed in the security group
   - Copy built jar to EC2 Instance and run it, confirm that database connections are possible.

3. Ensure http://shorturl.com/ directs to the public IP of the java application instance


### Add basic UI
Adding a basic templated site will be a fine start to begin with, I know JAX-RS can be used to generate a basic HTML page
and serve it, we could take input via a basic form with two fields and buttons one for lengthening, one for shortening

### Improve Webserver
At the minute we are running through a NettyJaxrsServer, however we could move to something more known and configurable such as jboss or Tomcat
# Nothing past this point would be in an actual readme.
But considering there was an architectural element I thought may as well give high level reasoning, known issues and next steps

## Architecture

### Components
1. **Hibernate Configuration** (HibernateConfiguration.java)
   The HibernateConfiguration class is responsible for initializing and providing a Hibernate SessionFactory. It follows good design choices such as the Singleton pattern and static initialization.
   - **Singleton Pattern**: The class follows the singleton pattern, ensuring that only one instance of the SessionFactory is created, promoting efficiency and consistency.
   - **Static Initialization**: The static initialization of the SessionFactory guarantees its creation at the start of the application, minimizing overhead during runtime.
   - **Instantiation Restriction**: The private constructor ensures that the class cannot be instantiated, enforcing adherence to the singleton pattern.
   

2. **URL Data Entity** (UrlDataEntity.java)
   The UrlDataEntity class represents URL data in the database, serving as an entity for the Shortener application. It includes attributes for the unique identifier, original URL, and shortened URL.
   - **JPA Compatibility**: This class is designed for use with JPA, ensuring compatibility with various database systems.
   - **Auto-Generated ID**: The id field is automatically generated, making it suitable for use as a primary key.
   - **Constructors**: Provides both a parameterized constructor for convenient object creation and a default constructor required by JPA.
   - **Encapsulation**: Access to fields is controlled through getter methods, promoting encapsulation.


3. **URL Data Entity DAO** (UrlDataEntityDAO.java)
   The UrlDataEntityDAO class is a Data Access Object (DAO) for performing operations on UrlDataEntity in the database. It provides methods for saving and retrieving entities based on shortened and original URLs.
   - **Transaction Management**: Utilizes transactions to ensure the atomicity of save operations, maintaining data consistency.
   - **Parameterized Queries**: Employs parameterized HQL queries to safely retrieve entities based on specific criteria.


4. **Session Open Exception** (SessionOpenException.java)
   The SessionOpenException class is a custom exception for handling errors related to opening a Hibernate session. It provides constructors for specifying the error message and cause.
   - **Custom Exception**: Creating a custom exception class (SessionOpenException) allows for more specific handling of exceptions related to Hibernate session opening. This practice improves code organization and makes error handling more meaningful (Although is likely overkill in this application)


5. **URL Resource** (UrlResource.java)
   The UrlResource class is a JAX-RS resource handling URL shortening and retrieval operations. It includes endpoints for shortening and retrieving URLs, along with appropriate error handling and validation.
   - **Configuration Separation**: The BEGINNING_LINK is defined as a constant, but the class acknowledges that it should ideally be extracted to an external configuration file. This practice allows for easy configuration changes without modifying the code.
   - **Input Validation**: The class performs input validation using a regular expression to ensure that the inputted data for URL shortening matches the structure of a URL. This helps prevent invalid data from reaching the underlying service.
   - **Resource Statelessness**: The class methods do not maintain any internal state between requests. Each method is designed to handle a specific request independently, adhering to the statelessness principle of REST.
   - **Separation of Concerns**: The class focuses on handling HTTP requests and delegating the actual business logic to the UrlShortenService. This separation of concerns makes the code more modular and maintainable.


6. **URL Shorten Service** (UrlShortenService.java)
   The UrlShortenService class is a service responsible for URL shortening and retrieval operations. It interacts with the database through the UrlDataEntityDAO and utilizes Hibernate for session management. It includes a method for generating random short URLs.
   - **Unique Short URL Handling**: class incorporates a naive mechanism to ensure the uniqueness of shortened URLs. While it's not the most efficient solution, it provides a simple and effective way to avoid collisions
   - **Resource Management**: The Session objects are managed using a try-with-resources block, ensuring proper resource closure and preventing resource leaks.
   - **Transactional Operations**: The shortenUrl method is wrapped in a transaction, ensuring that operations are atomic. If an exception occurs, the transaction is rolled back to maintain database consistency.


7. **Application Configuration** (ApplicationConfig.java)
   The ApplicationConfig class configures the JAX-RS application and sets the base path for the REST API. It specifies the resource classes to be registered in the application.


8. **Web Application Launcher** (WebappMain.java)
   The WebappMain class serves as the main entry point to launch the web application using NettyJaxrsServer. It configures the server by setting the root resource path, port, security domain, and registering the ApplicationConfig class.


9. **Hibernate Configuration File** (hibernate.cfg.xml)
   The hibernate.cfg.xml file is a crucial configuration file for setting up Hibernate and defining the database connection details, credentials and connection pool parameters.


10. **Web Configuration File** (web.xml)
    The web.xml file is used to configure the web application, and in this case, it's tailored for the Shortener application. It defines parameters for RESTEasy and servlet mapping.


11. **Local PostgreSQL Docker Compose Configuration** (local-postgres-docker-compose.yml)
    The local-postgres-docker-compose.yml file defines a Docker Compose configuration to set up a local PostgreSQL database for the Shortener application. This configuration uses the latest PostgreSQL image, sets up a database, and configures user credentials.

### Benefits of this approach
In general this is designed to emphasise a couple of key architectural concerns:

**Modularity**: Each component is organized into separate classes, promoting code modularity and maintainability.

**Ease of Extension**: The modular design allows easy extension of functionality without impacting existing code.

**Database Abstraction**: The use of Hibernate provides a convenient and abstracted way to interact with the database. I might not know hibernate at all but JPA is JPA and I definitely prefer that over a mishmash of direct SQL

**Focused Responsibility and Separation of Concerns**: The UrlResource class focuses on handling HTTP requests, while the actual URL shortening logic is encapsulated in the UrlShortenService class to give one example. This separation of concerns simplifies code maintenance and enhances readability.

### Areas of improvement
1. **External Configuration for Constants**:
   Externalize constants like BEGINNING_LINK and PERMITTED_CHARS to external configuration files. This allows for runtime configuration changes without modifying code, enhancing flexibility.
2. **Security Considerations**:
   Avoid hardcoding credentials in configuration files, and consider using secure credential management solutions. Properly configure our server to 
3. **Improve Transaction Management**: 
   I have a feeling that the transaction management in the UrlDataEntityDAO class could be improved, at the minute opens a new session for saving the URL entity in saveUrl method, but if I had more time/wasn't just learning about this I feel like the existing session could be used.

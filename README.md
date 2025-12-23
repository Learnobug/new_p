# Reverse Proxy with Caching

This project is a simple reverse proxy server built with Spring Boot. It forwards incoming requests to a configurable origin server and caches the responses to improve performance and reduce the load on the origin.

## Features

*   **Request Forwarding**: Forwards all incoming requests to a specified upstream server.
*   **Response Caching**: Caches responses from the origin server to provide faster responses for subsequent identical requests.
*   **In-Memory and Disk Cache**: Utilizes both an in-memory cache for quick access and a disk-based cache for persistence.
*   **LRU Cache Eviction**: The in-memory cache uses a Least Recently Used (LRU) eviction policy to manage its size.
*   **Cache-Status Header**: Adds an `X-Cache` header to the response, indicating a cache `HIT` or `MISS`.
*   **Configurable Origin**: The upstream origin URL can be easily configured in the application properties.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

*   Java 17 or newer
*   Maven 3.2+

### Running the Application

1.  **Clone the repository:**
    ```sh
    git clone <repository-url>
    cd new_p
    ```

2.  **Package the application:**
    Build the project and create the executable JAR file:
    ```sh
    mvn package
    ```

3.  **Run the application:**
    Run the application from the created JAR file. You can specify the origin server URL as a command-line argument.
    ```sh
    java -jar target/new_p-0.0.1-SNAPSHOT.jar --origin.url=http://localhost:3000
    ```
    The proxy server will start on `http://localhost:8080` and forward requests to `http://localhost:3000`.

## Endpoints

All requests sent to the proxy server will be forwarded to the configured origin URL.

*   **URL**: `/**`
*   **Method**: All HTTP methods are supported.

For example, a request to `http://localhost:8080/users/1` will be forwarded to `https://api.example.com/users/1`.

## Built With

*   [Spring Boot](https://spring.io/projects/spring-boot) - The framework used to create the application.
*   [Spring WebFlux](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html) - Used for making non-blocking requests to the origin server.
*   [Maven](https://maven.apache.org/) - Dependency management.


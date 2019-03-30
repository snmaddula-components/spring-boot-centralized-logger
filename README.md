# Centralized Logging in Spring Boot Microservices
Centralized logger implementation for spring boot microservices


### Plug in this logger as a dependency in your microservice pom.xml
    <dependency>
      <groupId>snmaddula.components.logger</groupId>
      <artifactId>spring-boot-centralized-logger</artifactId>
    </dependency>

### Configure the paths that you would like to exclude from being filtered / logged.
In `application.yml` add the below section, to exclude swagger endpoints.
```yml
app:
  log:
    exluded-paths:
      - /swagger-ui.html
      - /webjars/**
      - /swagger-resources
      - /swagger-resources/**
      - /v2/api-docs
      - /actuator
      - /actuator/**
```

### Add `@EnableCentralizedLogging` annotation to your main application class.
```java
import snmaddula.components.logger.EnableCentralizedLogging;

@SpringBootApplication
@EnableCentralizedLogging
public class ProductCrudApplication {

  public static void main(String[] args) {
    SpringApplication.run(ProductCrudApplication.class, args);
  }
  
}
```


### Logs Output
When you run the application, you will get logs like below.
```
s.q.crud.controller.ProductController    : Started: ProductEntity snmaddula.quickpoc.crud.controller.ProductController.create(ProductEntity)[ product=ProductEntity(id=null, title=Product-B, description=product B description, price=10.0) ]
s.quickpoc.crud.service.ProductService   : Started: ProductEntity snmaddula.quickpoc.crud.service.ProductService.create(ProductEntity)[ product=ProductEntity(id=null, title=Product-B, description=product B description, price=10.0) ]
s.quickpoc.crud.service.ProductService   : Finished: ProductEntity snmaddula.quickpoc.crud.service.ProductService.create(ProductEntity)[product=ProductEntity(id=1, title=Product-B, description=product B description, price=10.0)], returned: ProductEntity(id=1, title=Product-B, description=product B description, price=10.0) in 28 ms
s.q.crud.controller.ProductController    : Finished: ProductEntity snmaddula.quickpoc.crud.controller.ProductController.create(ProductEntity)[product=ProductEntity(id=1, title=Product-B, description=product B description, price=10.0)], returned: ProductEntity(id=1, title=Product-B, description=product B description, price=10.0) in 28 ms
s.components.logger.AppLoggingConfig     : Request POST : /products completed in 77 ms

s.q.crud.controller.ProductController    : Started: List snmaddula.quickpoc.crud.controller.ProductController.getAll()[  ]
s.q.crud.controller.ProductController    : GET ALL called...
s.quickpoc.crud.service.ProductService   : Started: List snmaddula.quickpoc.crud.service.ProductService.getAll()[  ]
s.quickpoc.crud.service.ProductService   : Finished: List snmaddula.quickpoc.crud.service.ProductService.getAll()[], returned: [ProductEntity(id=1, title=Product-B, description=product B description, price=10.0)] in 94 ms
s.q.crud.controller.ProductController    : Finished: List snmaddula.quickpoc.crud.controller.ProductController.getAll()[], returned: [ProductEntity(id=1, title=Product-B, description=product B description, price=10.0)] in 94 ms
s.components.logger.AppLoggingConfig     : Request GET : /products completed in 105 ms
```

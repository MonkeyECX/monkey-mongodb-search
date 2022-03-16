# monkey-mongodb-search

This project was created to help everyone to create a searchable Rest API :)

### How to use:

Add a search parameter on your URL like this:

```javascript
?search=car.field:foo AND car.field2!bar OR car.field3:200
```

#### Aggregators: 
AND

OR

#### Operations:

EQUAL: car.field:foo

NOT: car.field!foo

GREATER_THAN_EQUAL: car.field>200

LESS_THAN_EQUAL: car.field>2021-12-23

CONTAINS: car.field:FO*, car.field:*OO

NOT_CONTAINS: car.field!FO*, car.field!*OO



### Configuration

First step: Add the last version of monkey-mongodb-search in your pom:

```xml

<dependency>
    <groupId>br.com.monkey.ecx</groupId>
    <artifactId>monkey-mongodb-search</artifactId>
    <version>${version}</version>
</dependency>
```

Second step: you need annotate your domain configuration class or your main class like this:

```java
@EnableMongoRepositories(basePackages = "br.com.monkey.ecx", repositoryBaseClass = ResourceRepositoryImpl.class)
``` 

Third step: your repository needs to extends ResourceRepository like this:

```java
interface MyRepository
        extends MongoRepository<WebHookDelivery, String>, ResourceRepository<WebHookDelivery, String> {
}
```

Last step: Add search parameter on your controller class:

```java

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("v1/cars")
class WebHookDeliveryRestService {

    private final MyRepository repository;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    Page<WebHookDelivery> findAll(@SearchParameter Query search,
                                  @PageableDefault Pageable pageable) {
        return repository.findAll(search, pageable);
    }

}
```



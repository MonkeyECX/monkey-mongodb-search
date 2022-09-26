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

**First step:** Add the last version of monkey-mongodb-search in your pom:

```xml

<dependency>
    <groupId>br.com.monkey.ecx</groupId>
    <artifactId>monkey-mongodb-search</artifactId>
    <version>${version}</version>
</dependency>
```

**Second step:** you need annotate your domain configuration class or your main class like this:

```java
@EnableMongoRepositories(basePackages = "br.com.monkey.ecx", repositoryBaseClass = ResourceRepositoryImpl.class)
``` 

**Third step:** Define the configuration class, at DomainConfiguration:

Here you can configure:

The prohibited fields, and this causes error when someone tries to use that field on the search parameter.

If search will use program in all queries and the name of the program field to be used. If you do not specify the program field will be program.

```java
@PostConstruct
void started() {
    MongoDBSearchConfiguration.configure(Collections.singletonList("companyId"), true);
}
``` 

Version 2:

We added two new configurations, to configure alias keys and monetary values. Look at the details:

**1. Alias keys** – You can use an alias to find multiple fields using alias for example we can create a ```governmentId``` alias with the following keys ```requestPayalod.governmentId```, ```requestPayalod.sellerGovernmentId```, ```requestPayalod.buyerGovernmentId```.

Configure:

```java
@Configuration
class MonkeyMongoSearchConfiguration {

	@PostConstruct
	public void start() {
		//@formatter:off
	MongoDBSearchConfiguration.configure(List.of("companyId", "program"), true)

        // alias for governmentId
        .addAlias(Alias.of("governmentId", "requestPayload.sponsorGovernmentId",
                asList(CombinedKey.of("requestPayload.buyerGovernmentId"),
                CombinedKey.of("requestPayload.supplierGovernmentId"),
                CombinedKey.of("requestPayload.sellerGovernmentId"),
                CombinedKey.of("requestPayload.governmentId"))));
	}

}
```

Using as search parameter:

```javascript
?search=governmentId:62144175000120
```
And the generated query will follow the example:
```javascript
{ "$or" : [{ "requestPayload.supplierGovernmentId" : "62144175000120"}, { "requestPayload.sponsorGovernmentId" : "62144175000120"}, { "requestPayload.governmentId" : "62144175000120"}, { "requestPayload.buyerGovernmentId" : "62144175000120"}, { "requestPayload.sellerGovernmentId" : "62144175000120"}]}
```

*Limitation: You can't use AND with alias, all the alias will be in a or clausule.*

**2. Monetary value** - To search monetary values you need to indicate this on the search query, for example if you use $ as monetary identification you need use this on your search:

```javascript
?search=requestPayload.netPaymentValue>$17395.69
``` 

Configure:

```java
MongoDBSearchConfiguration.getInstance()..withMonetaryIdentification("R$");
```

It is possible to change the monetary identification using this configuration:

**Fourth step:** your repository needs to extends ResourceRepository like this:

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



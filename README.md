# Trip Planning API Documentation

## Task 3 + 4: API Endpoint Testing Results

Below are the test results from our dev.http file:

### 1. Get All Trips
```http
GET http://localhost:7070/api/trips
Accept: application/json

HTTP/1.1 200 OK
Content-Type: application/json
[
  {
    "id": 1,
    "startTime": "2024-11-05T09:00:44.268Z",
    "endTime": "2024-11-05T11:00:44.268Z",
    "longitude": 12.5683,
    "latitude": 55.6761,
    "name": "Copenhagen City Walk",
    "price": 299.99,
    "category": "CITY",
    "guide": {
      "id": 1,
      "firstName": "John",
      "lastName": "Smith",
      "email": "john.smith@guides.com",
      "phone": "+45 12345678",
      "yearsOfExperience": 5
    }
  },
  {
    "id": 2,
    "startTime": "2024-11-06T13:00:44.268Z",
    "endTime": "2024-11-06T16:00:44.268Z",
    "longitude": 12.6347,
    "latitude": 55.6582,
    "name": "Amager Beach Experience",
    "price": 399.99,
    "category": "BEACH",
    "guide": {
      "id": 1,
      "firstName": "John",
      "lastName": "Smith",
      "email": "john.smith@guides.com",
      "phone": "+45 12345678",
      "yearsOfExperience": 5
    }
  },
  {
    "id": 3,
    "startTime": "2024-11-07T08:00:44.268Z",
    "endTime": "2024-11-07T12:00:44.268Z",
    "longitude": 12.5693,
    "latitude": 55.7832,
    "name": "Dyrehaven Forest Tour",
    "price": 449.99,
    "category": "FOREST",
    "guide": {
      "id": 2,
      "firstName": "Sarah",
      "lastName": "Johnson",
      "email": "sarah.j@guides.com",
      "phone": "+45 87654321",
      "yearsOfExperience": 8
    }
  },
  {
    "id": 4,
    "startTime": "2024-11-08T10:00:44.268Z",
    "endTime": "2024-11-08T14:00:44.268Z",
    "longitude": 12.6298,
    "latitude": 55.7069,
    "name": "Øresund Sea Adventure",
    "price": 599.99,
    "category": "SEA",
    "guide": {
      "id": 2,
      "firstName": "Sarah",
      "lastName": "Johnson",
      "email": "sarah.j@guides.com",
      "phone": "+45 87654321",
      "yearsOfExperience": 8
    }
  }
]
```

### 2. Get Trip by ID
```http
GET http://localhost:7070/api/trips/1
Accept: application/json

HTTP/1.1 200 OK
Content-Type: application/json
{
  "id": 1,
  "startTime": "2024-11-05T10:00:00.000Z",
  "endTime": "2024-11-05T12:00:00.000Z",
  "longitude": 12.5683,
  "latitude": 55.6761,
  "name": "Copenhagen City Walk",
  "price": 299.99,
  "category": "CITY",
  "guide": {
    "id": 1,
    "firstName": "John",
    "lastName": "Smith",
    "email": "john.smith@guides.com",
    "phone": "+45 12345678",
    "yearsOfExperience": 5
  }
}
```

### 3. Create New Trip (POST)
```http
POST http://localhost:7070/api/trips
Content-Type: application/json
Authorization: Bearer {{jwt_token}}

REQUEST:
{
  "name": "Copenhagen City Walk",
  "startTime": "2024-11-05T10:00:00.000Z",
  "endTime": "2024-11-05T12:00:00.000Z",
  "longitude": 12.5683,
  "latitude": 55.6761,
  "price": 299.99,
  "category": "CITY"
}

HTTP/1.1 201 Created
Content-Type: application/json
{
  "id": 5,
  "startTime": "2024-11-05T10:00:00.000Z",
  "endTime": "2024-11-05T12:00:00.000Z",
  "longitude": 12.5683,
  "latitude": 55.6761,
  "name": "Copenhagen City Walk",
  "price": 299.99,
  "category": "CITY"
}
```

### 4. Update Trip (PUT)
```http
PUT http://localhost:7070/api/trips/1
Content-Type: application/json
Authorization: Bearer {{jwt_token}}

REQUEST:
{
  "name": "Updated Copenhagen City Walk",
  "startTime": "2024-11-05T10:00:00.000Z",
  "endTime": "2024-11-05T12:00:00.000Z",
  "longitude": 12.5683,
  "latitude": 55.6761,
  "price": 349.99,
  "category": "CITY"
}

HTTP/1.1 200 OK
Content-Type: application/json
{
  "id": 1,
  "startTime": "2024-11-05T10:00:00.000Z",
  "endTime": "2024-11-05T12:00:00.000Z",
  "longitude": 12.5683,
  "latitude": 55.6761,
  "name": "Updated Copenhagen City Walk",
  "price": 349.99,
  "category": "CITY",
  "guide": {
    "id": 1,
    "firstName": "John",
    "lastName": "Smith",
    "email": "john.smith@guides.com",
    "phone": "+45 12345678",
    "yearsOfExperience": 5
  }
}
```

### 5. Add Guide to Trip
```http
PUT http://localhost:7070/api/trips/1/guides/1
Authorization: Bearer {{jwt_token}}

HTTP/1.1 200 OK
```

### 6. Delete Trip
```http
DELETE http://localhost:7070/api/trips/1
Authorization: Bearer {{jwt_token}}

HTTP/1.1 204 No Content
```

### Error Test Results

#### 1. Attempt to Get Non-existent Trip
```http
GET http://localhost:7070/api/trips/999
Accept: application/json

HTTP/1.1 404 Not Found
Content-Type: application/json
{
  "error": "Trip not found with id: 999 - /api/trips/999",
  "status": "404 Not Found",
  "timestamp": "2024-11-04 11:02:06.036"
}
```

#### 2. Attempt to Create Invalid Trip
```http
POST http://localhost:7070/api/trips
Content-Type: application/json
Authorization: Bearer {{jwt_token}}

REQUEST:
{
  "name": "",
  "price": -100
}

HTTP/1.1 400 Bad Request
Content-Type: application/json
{
  "error": "Trip name is required, Start time is required, End time is required, Longitude is required, Latitude is required, Valid price is required (must be greater than 0), Category is required",
  "status": "400 Bad Request",
  "timestamp": "2024-11-04 11:00:53.924"
}
```

#### 3. Attempt to delete non-existent Trip
```http
DELETE http://localhost:7070/api/trips/999
Authorization: Bearer {{jwt_token}}

HTTP/1.1 404 Not Found
Content-Type: application/json
{
  "error": "Trip not found with id: 999 - /api/trips/999",
  "status": "404 Not Found",
  "timestamp": "2024-11-04 11:02:06.036"
}
```

### Why PUT for Adding a Guide?

The choice to use PUT instead of POST for adding a guide to a trip is based on the idempotent nature of the operation. Idempotency means that making the same request multiple times should have the same effect as making it once.

In this case:
- A trip can only have one guide at a time
- Adding the same guide to a trip multiple times results in the same end state
- The operation is more about updating the trip's guide property rather than creating a new resource

This aligns with PUT's semantics of updating an existing resource, whereas POST is typically used for creating new resources or when the operation might have different results if repeated.

### Available Endpoints

| Method | Endpoint                          | Role        | Description                    |
|--------|----------------------------------|-------------|--------------------------------|
| GET    | /api/trips                       | ANYONE      | Get all trips                  |
| GET    | /api/trips/{id}                  | ANYONE      | Get a specific trip            |
| POST   | /api/trips                       | USER, ADMIN | Create a new trip              |
| PUT    | /api/trips/{id}                  | USER, ADMIN | Update an existing trip        |
| DELETE | /api/trips/{id}                  | ADMIN       | Delete a trip                  |
| PUT    | /api/trips/{tripId}/guides/{guideId} | USER, ADMIN | Add a guide to a trip      |
| POST   | /api/trips/populate              | ADMIN       | Populate database with test data|


## Task 5:

### Get Trips by Category

```http
GET http://localhost:7070/api/trips?category=SEA
Accept: application/json

HTTP/1.1 200 OK
Content-Type: application/json
[
  {
    "id": 4,
    "startTime": "2024-11-08T10:00:30.812Z",
    "endTime": "2024-11-08T14:00:30.812Z",
    "longitude": 12.6298,
    "latitude": 55.7069,
    "name": "Øresund Sea Adventure",
    "price": 599.99,
    "category": "SEA",
    "guide": {
      "id": 2,
      "firstName": "Sarah",
      "lastName": "Johnson",
      "email": "sarah.j@guides.com",
      "phone": "+45 87654321",
      "yearsOfExperience": 8
    }
  }
]
```

### Get Guides with Total Price

```http
GET http://localhost:7070/api/trips/guides/totalprice
Accept: application/json

HTTP/1.1 200 OK
Content-Type: application/json
[
  {
    "totalPrice": 699.98,
    "guideId": 1
  },
  {
    "totalPrice": 1049.98,
    "guideId": 2
  }
]
```

## Task 6:

### Get Packing Items for a Trip

```http
GET http://localhost:7070/api/trips/4
Accept: application/json

HTTP/1.1 200 OK
Content-Type: application/json
{
  "id": 4,
  "startTime": "2024-11-08T10:00:30.812Z",
  "endTime": "2024-11-08T14:00:30.812Z",
  "longitude": 12.6298,
  "latitude": 55.7069,
  "name": "Øresund Sea Adventure",
  "price": 599.99,
  "category": "SEA",
  "guide": {
    "id": 2,
    "firstName": "Sarah",
    "lastName": "Johnson",
    "email": "sarah.j@guides.com",
    "phone": "+45 87654321",
    "yearsOfExperience": 8
  },
  "packingItems": [
    {
      "name": "Sea Kayak",
      "weightInGrams": 10000.0,
      "quantity": 1,
      "description": "Inflatable kayak suitable for Sea adventures."
    },
    {
      "name": "Sea Snorkeling Kit",
      "weightInGrams": 500.0,
      "quantity": 1,
      "description": "Mask and fins set for sea snorkeling."
    },
    {
      "name": "Ocean Hat",
      "weightInGrams": 150.0,
      "quantity": 1,
      "description": "Wide-brim hat for sun protection in sea conditions."
    },
    {
      "name": "Sea Goggles",
      "weightInGrams": 100.0,
      "quantity": 1,
      "description": "Comfortable goggles for sea swimming."
    },
    {
      "name": "Snorkel",
      "weightInGrams": 150.0,
      "quantity": 1,
      "description": "Snorkel for underwater sea exploration."
    },
    {
      "name": "Waterproof Bag",
      "weightInGrams": 200.0,
      "quantity": 1,
      "description": "Bag for keeping belongings dry at sea."
    }
  ]
}
```

### Get Sum of Packing Items Weight for a Trip

```http 
GET http://localhost:7070/api/trips/4/weight
Accept: application/json

HTTP/1.1 200 OK
Content-Type: application/json
{
  "totalWeightInGrams": 11100.0,
  "tripId": 4
}
```

## Task 8:

### 8.3 Ensuring Tests Pass with Security Implementation

To ensure that the tests pass without issues after adding security roles, the implementation implemented the following steps:

1. **Obtain Authentication Tokens in Tests**:
   - During the setup (`@BeforeEach`), the implementation obtain JWT tokens for both `userDTO` and `adminDTO`. These tokens are then used in subsequent API requests during tests.
   - This ensures that all protected endpoints are accessed with valid tokens, simulating authenticated access.

    ```java
    try {
        UserDTO verifiedUser = securityDAO.getVerifiedUser(userDTO.getEmail(), userDTO.getPassword());
        UserDTO verifiedAdmin = securityDAO.getVerifiedUser(adminDTO.getEmail(), adminDTO.getPassword());

        userToken = "Bearer " + securityController.createToken(verifiedUser);
        adminToken = "Bearer " + securityController.createToken(verifiedAdmin);
    } catch (Exception e) {
        throw new RuntimeException("Failed to setup security tokens", e);
    }
    ```

2. **Include Authorization Headers**:
   - For endpoints that require a certain role, the corresponding Rest Assured test was updated to include the `Authorization` header with a valid token (`userToken` or `adminToken`).
   - For example, the test for creating a new trip was updated as follows:

    ```java
    TripDTO createdTrip = given()
        .contentType("application/json")
        .header("Authorization", userToken)
        .body(newTrip)
        .when()
        .post(BASE_URL + "/trips")
        .then()
        .statusCode(201)
        .extract()
        .as(TripDTO.class);
    ```
   - The `updateTrip`, `deleteTrip`, and other endpoints that require secure access were also updated similarly.

3. **Unauthorized Access Tests**:
   - Tests were added to verify that endpoints return `401 Unauthorized` if the user tries to access a protected route without a token or with an invalid token.
   - Example:

    ```java
    given()
        .contentType("application/json")
        .body(newTrip)
        .when()
        .post(BASE_URL + "/trips")
        .then()
        .statusCode(401);
    ```

4. **Role Validation**:
   - For endpoints that require `ADMIN` access, the `adminToken` was used instead of `userToken` to ensure the tests verify proper role-based permissions.
   - Example:

    ```java
    given()
        .contentType("application/json")
        .header("Authorization", adminToken)
        .when()
        .delete(BASE_URL + "/trips/" + trip1.getId())
        .then()
        .statusCode(204);
    ```


# Technical Implementation Decisions and Workflow

## General Workflow

### Initial Planning

I began by thoroughly reading through the entire task description to understand the requirements and identify areas where additional features, such as security, could be beneficial. I noticed that security was a potential requirement, and since I had already set up the basic security configuration in my starter code, I decided to implement it right away. By doing this, I ensured that I wouldn't need to refactor my code later to add security features, saving time and avoiding potential issues down the line.

### Branching Strategy with Git

To manage my workflow effectively, I used Git to create separate branches for each task. By working on different branches, I could focus on individual tasks without affecting the main codebase. Once a task was completed, I merged the branch into the main branch. This branching strategy helped maintain a clean and organized project history, making it easier to track changes and roll back if necessary.

### Use of Logger for Debugging and Monitoring

Throughout the project, I made use of the `Logger` from the `SLF4J` library to provide real-time debugging and monitoring information. This included logging key events such as:

- Starting and committing database transactions.
- Creating entities like guides and trips.
- Errors and exceptions during CRUD operations.

The benefits of using a logger are significant:

- **Debugging**: It provides a clear insight into the flow of the application, which is extremely helpful for identifying where things might go wrong during execution.
- **Error Tracking**: By logging errors and exceptions, it becomes easier to troubleshoot issues in production environments, as the logs provide detailed information about the failure, including stack traces and custom error messages.
- **Audit Trail**: It helps in maintaining an audit trail of operations performed, which is useful for understanding the application's behavior over time, such as tracking changes to the database.

For example, in the `Populator` class, I used `LOGGER.info()` to inform when the database was being populated and `LOGGER.debug()` to detail specific entity creation. Similarly, in the `TripDAO` class, I logged important CRUD operations and any errors that occurred during the process, ensuring that all critical actions were recorded for easier monitoring and debugging.

### Database Reset During Population

When populating the database, I chose to delete all existing data and reset the sequences to start from `0`. This decision was made for several reasons:
- **Data Consistency**: By starting with a clean slate, I ensured that there were no leftover or inconsistent data from previous runs that could affect the correctness of the current data population. This helps in maintaining a predictable state of the database.
- **Primary Key Management**: Resetting ID sequences ensures that primary key values start from `1` each time, making it easier to track entity creation and maintain a clear, sequential order of records. This is particularly helpful when verifying logs or debugging issues related to specific entities.

The reset process involved deleting all records from the `Trip` and `Guide` tables, followed by resetting their respective ID sequences to start from `1`. This approach allowed for a clean and consistent environment, making it straightforward to populate the database without unexpected conflicts or duplicates.

I implemented this approach before moving on to Task 7, which involved testing the endpoints. At that point, I also chose to create a test-specific populator to ensure that testing could be carried out effectively with controlled data.


## Use of JPA (JPQL) vs Java Streams in Task 5

In implementing the guide total price calculation (`/trips/guides/totalprice`), I chose to use JPQL (Java Persistence Query Language) instead of Java Streams. This decision was based on several critical factors:

### Performance Optimization
- By using JPQL, the aggregation (i.e., the `SUM` of trip prices) is performed directly at the database level. Database systems are highly optimized for such aggregation operations, allowing them to execute the `SUM` function efficiently while grouping the results.
- If Java Streams were used, all guide and trip data would need to be loaded into the application memory first, followed by in-memory processing to compute the sums. This would lead to unnecessary processing overhead, particularly for large datasets.

### Memory Efficiency
- JPQL allows us to execute the aggregation at the source and retrieve only the final results. This means that only the relevant aggregated data is transferred from the database, minimizing memory consumption.
- Using Streams would involve loading the entire set of guide and trip entities into memory before performing the aggregation, which could result in higher memory usage, especially with larger databases.

### Network Optimization
- By performing the aggregation in JPQL, we limit the volume of data being transferred from the database to the application. Only the aggregated results are transferred, minimizing network bandwidth usage.
- If Java Streams were used, all records for guides and their trips would need to be retrieved from the database first, leading to significantly larger data transfers and potential network performance bottlenecks.

### Readability and Conciseness
- The JPQL approach results in more concise and readable code, which directly expresses the intent of the query—calculating the total price for each guide. In contrast, using Java Streams would require additional code to collect, filter, and aggregate the data, which could be more cumbersome to read and maintain.

The JPQL query used is:
```sql
SELECT NEW map(
    g.id as guideId,
    COALESCE(SUM(t.price), 0.0) as totalPrice
)
FROM Guide g 
LEFT JOIN g.trips t 
GROUP BY g.id
```
This query ensures that each guide is listed, even those without any trips, thanks to the `LEFT JOIN`. The `COALESCE` function is used to handle cases where a guide has no trips, ensuring a total price of `0.0` is returned in such scenarios.

## Use of `@SuppressWarnings("unchecked")`

In the DAO implementation, `@SuppressWarnings("unchecked")` is used for the JPQL query result. This choice has a specific purpose:

### Type Safety vs. Runtime Safety
- The JPQL query uses the `NEW map` constructor to create a map representing the results, which cannot be statically typed at compile time. The query returns a raw `List` that needs to be cast to `List<Map<String, Object>>`.
- Although the compiler cannot verify the type safety of this cast, we are confident about the structure of our query results, which makes this suppression safe.

### Cleaner Code
- Java's type system requires us to cast the raw list returned by the JPQL query, and without `@SuppressWarnings`, the compiler will generate warnings about potential type safety issues.
- Using `@SuppressWarnings` allows us to avoid these warnings for this specific, well-understood use case, keeping the code cleaner and free from unnecessary noise.

## Summary

This implementation leverages JPQL to provide optimal performance, memory efficiency, and reduced network overhead while ensuring clear and concise code. The use of `@SuppressWarnings("unchecked")` is a pragmatic choice to maintain type safety where the compiler's warnings are not applicable. Together, these decisions contribute to an efficient, maintainable, and scalable solution.

## Use of @JsonInclude(JsonInclude.Include.NON_NULL)

In our DTOs (Data Transfer Objects), we use the `@JsonInclude(JsonInclude.Include.NON_NULL)` annotation for several important reasons:

1. **Bandwidth Optimization**
    - Only non-null fields are serialized to JSON
    - Reduces response payload size
    - Particularly important when dealing with mobile clients or limited bandwidth

2. **Clean API Responses**
    - Prevents cluttering of responses with null values
    - Makes responses easier to read and understand
    - Example without annotation:
      ```json
      {
        "id": 1,
        "name": "Copenhagen City Walk",
        "guide": null,
        "packingItems": null,
        "price": 299.99
      }
      ```
    - Example with annotation:
      ```json
      {
        "id": 1,
        "name": "Copenhagen City Walk",
        "price": 299.99
      }
      ```

3. **Flexible Object Usage**
    - Same DTO can be used for different endpoints with varying data requirements
    - Optional fields (like packingItems) only appear when actually populated
    - Reduces need for multiple specialized DTOs

4. **Error Prevention**
    - Clients are less likely to process null values incorrectly
    - Clearer contract about which data is actually available
    - Helps prevent NullPointerExceptions in client applications

This annotation is particularly useful for our packing items integration, where the data might not always be available (for example, if the external API is temporarily unavailable).

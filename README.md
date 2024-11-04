# Medical Clinic API Documentation

## Task 1.5.4: API Endpoint Testing Results

Below are the test results from our doctor.http file:

### 1. Get All Doctors
```http
GET http://localhost:7070/api/doctors
Accept: application/json

HTTP/1.1 200 OK
Content-Type: application/json
[
  {
    "id": 1,
    "dateOfBirth": "1975-04-12",
    "name": "Dr. Alice Smith",
    "yearOfGraduation": 2000,
    "nameOfClinic": "City Health Clinic",
    "speciality": "FAMILY_MEDICINE",
    "appointments": []
  },
  {
    "id": 2,
    "dateOfBirth": "1980-08-05",
    "name": "Dr. Bob Johnson",
    "yearOfGraduation": 2005,
    "nameOfClinic": "Downtown Medical Center",
    "speciality": "SURGERY",
    "appointments": []
  },
  {
    "id": 4,
    "dateOfBirth": "1978-11-15",
    "name": "Dr. David Park",
    "yearOfGraduation": 2003,
    "nameOfClinic": "Hillside Medical Practice",
    "speciality": "PSYCHIATRY",
    "appointments": []
  },
  {
    "id": 7,
    "dateOfBirth": "1979-05-29",
    "name": "Dr. George Kim",
    "yearOfGraduation": 2004,
    "nameOfClinic": "Summit Health Institute",
    "speciality": "FAMILY_MEDICINE",
    "appointments": []
  }
]
```

### 2. Get Doctor by ID
```http
GET http://localhost:7070/api/doctors/1
Accept: application/json

HTTP/1.1 200 OK
Content-Type: application/json
{
  "id": 1,
  "name": "Dr. Alice Smith",
  "dateOfBirth": "1975-04-12",
  "yearOfGraduation": 2000,
  "nameOfClinic": "City Health Clinic",
  "speciality": "FAMILY_MEDICINE",
  "appointments": [
    {
      "id": 1,
      "clientName": "John Smith",
      "date": "2024-11-04",
      "time": "09:45",
      "comment": "First visit"
    }
  ]
}
```

### 3. Get Doctors by Speciality
```http
GET http://localhost:7070/api/doctors/speciality/SURGERY
Accept: application/json

HTTP/1.1 200 OK
Content-Type: application/json
[
  {
    "id": 2,
    "name": "Dr. Bob Johnson",
    "dateOfBirth": "1980-08-05",
    "yearOfGraduation": 2005,
    "nameOfClinic": "Downtown Medical Center",
    "speciality": "SURGERY"
  }
]
```

### 4. Get Doctors by Birthdate Range
```http
GET http://localhost:7070/api/doctors/birthdate/range?from=1975-01-01&to=1979-12-31
Accept: application/json

HTTP/1.1 200 OK
Content-Type: application/json
[
  {
    "id": 1,
    "name": "Dr. Alice Smith",
    "dateOfBirth": "1975-04-12",
    "yearOfGraduation": 2000,
    "nameOfClinic": "City Health Clinic",
    "speciality": "FAMILY_MEDICINE"
  }
]
```

### 5. Create New Doctor (POST)
```http
POST http://localhost:7070/api/doctors
Content-Type: application/json

REQUEST:
{
  "name": "Dr. Sophus Olsson",
  "dateOfBirth": "1980-05-21",
  "yearOfGraduation": 2008,
  "nameOfClinic": "Green Valley Hospital",
  "speciality": "PEDIATRICS"
}

HTTP/1.1 201 Created
Content-Type: application/json
{
  "id": 3,
  "name": "Dr. Sophus Olsson",
  "dateOfBirth": "1980-05-21",
  "yearOfGraduation": 2008,
  "nameOfClinic": "Green Valley Hospital",
  "speciality": "PEDIATRICS",
  "appointments": []
}
```

### 6. Update Doctor (PUT)
```http
PUT http://localhost:7070/api/doctors/1
Content-Type: application/json

REQUEST:
{
  "name": "Dr. Alice Smith",
  "dateOfBirth": "1975-04-12",
  "yearOfGraduation": 2001,
  "nameOfClinic": "City Health Clinic Updated",
  "speciality": "FAMILY_MEDICINE"
}

HTTP/1.1 200 OK
Content-Type: application/json
{
  "id": 1,
  "name": "Dr. Alice Smith",
  "dateOfBirth": "1975-04-12",
  "yearOfGraduation": 2001,
  "nameOfClinic": "City Health Clinic Updated",
  "speciality": "FAMILY_MEDICINE",
  "appointments": [
    {
      "id": 1,
      "clientName": "John Smith",
      "date": "2024-11-04",
      "time": "09:45",
      "comment": "First visit"
    }
  ]
}
```

### 7. Delete Doctor
```http
DELETE http://localhost:7070/api/doctors/3
Authorization: Bearer [admin-token]

HTTP/1.1 204 No Content
```

### Error Test Results

#### 1. Attempt to Create Invalid Doctor
```http
POST http://localhost:7070/api/doctors
Content-Type: application/json

REQUEST:
{
  "name": "",
  "speciality": "INVALID"
}

HTTP/1.1 400 Bad Request
Content-Type: application/json
{
  "status": 400,
  "message": "Validation failed: Doctor name is required, Invalid speciality",
  "timestamp": "2024-11-03 14:25:33.127"
}
```

#### 2. Attempt to Delete Non-existent Doctor
```http
DELETE http://localhost:7070/api/doctors/999
Authorization: Bearer [admin-token]

HTTP/1.1 404 Not Found
Content-Type: application/json
{
  "status": 404,
  "message": "Doctor not found with id: 999",
  "timestamp": "2024-11-03 14:26:45.892"
}
```

#### 3. Attempt to Delete Without Admin Rights
```http
DELETE http://localhost:7070/api/doctors/1
Authorization: Bearer [user-token]

HTTP/1.1 403 Forbidden
Content-Type: application/json
{
  "status": 403,
  "message": "You don't have permission to access this resource. Your role: USER, Required roles: [ADMIN]",
  "timestamp": "2024-11-03 14:27:15.443"
}
```

[Rest of the README.md remains the same...]



## Task 3.2: Purpose of Generics in this Exercise

Generics in this exercise serve several important purposes:

1. **Type Safety**: By using generics in our DAO interface (`IDAO<T, ID>`), we ensure type safety at compile time. This prevents runtime errors that could occur from type mismatches.

2. **Code Reusability**: The generic interface allows us to reuse the same interface structure for different entity types (like Doctor and Appointment) without duplicating code. We can implement the same CRUD operations for different entities while maintaining type safety.

3. **Flexibility**: The generic interface makes our code more flexible and maintainable. We can easily add new entity types that implement the same interface without modifying the existing code.

4. **Clear Contract**: Generics help define a clear contract for implementing classes. Any class implementing the `IDAO` interface must specify what type of entity it works with and what type of ID it uses.

Example of how generics are used in our code:
```java
public interface IDAO<T, ID> {
    T read(ID id) throws ApiException;
    List<T> readAll() throws ApiException;
    T create(T t) throws ApiException;
    T update(ID id, T t) throws ApiException;
    void delete(ID id) throws ApiException;
}
```

## Task 5.4: Differences Between Unit Tests and Integration Tests

There are several key differences between the unit tests performed earlier and the integration tests performed in Task 5:

1. **Scope**:
    - Unit tests focus on testing individual components in isolation (e.g., testing a single method)
    - Integration tests in Task 5 test the interaction between multiple components (DAO, database, entities)

2. **Dependencies**:
    - Unit tests typically mock dependencies
    - Our integration tests use real database connections and actual JPA operations

3. **Test Environment**:
    - Unit tests run in memory without external dependencies
    - Our integration tests require a test database and EntityManagerFactory setup

4. **Complexity**:
    - Unit tests are simpler and faster to execute
    - Integration tests require more setup (like @BeforeAll and @BeforeEach methods) and are more complex

5. **Test Data**:
    - Unit tests usually use simple, mocked data
    - Our integration tests require proper database records and handle real data persistence

## Task 6: REST Assured Testing Theory

### 6.1 Purpose of REST Assured

REST Assured serves several important purposes in API testing:

1. **Simplified Testing**: It provides a domain-specific language (DSL) for testing HTTP-based REST services.
2. **Readable Tests**: The syntax is human-readable and follows a given-when-then pattern.
3. **Comprehensive Validation**: It allows testing of both request and response including headers, body, and status codes.
4. **Integration Testing**: It enables end-to-end testing of REST APIs in a real-world scenario.

### 6.2 Database Setup for Tests

Our database setup for tests involves:

1. **Test Database Configuration**:
    - Using TestContainers for isolated test database
    - Separate configuration in HibernateConfig for test environment
    - Clean database state between tests

2. **Data Population**:
    - Using @BeforeEach to populate test data
    - Cleaning up with @AfterEach
    - Using transactions to ensure data consistency

3. **Test Isolation**:
    - Each test runs in isolation
    - Database state is reset between tests
    - Using separate test profiles and configurations

### 6.3 Differences in Testing REST Endpoints

Testing REST endpoints differs from regular integration tests in several ways:

1. **HTTP Layer**:
    - Tests actual HTTP requests and responses
    - Validates HTTP status codes and headers
    - Tests API contracts and formats

2. **Client Perspective**:
    - Tests from external client perspective
    - Validates API documentation
    - Ensures proper error handling and responses

3. **End-to-End Testing**:
    - Tests entire request-response cycle
    - Includes serialization/deserialization
    - Validates complete API functionality
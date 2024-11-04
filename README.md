# Trip Planning API Documentation

## Task 3: API Endpoint Testing Results

Below are the test results from our trip.http file:

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
Authorization: Bearer [user-token]

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
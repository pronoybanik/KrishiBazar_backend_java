 # KrishiBazar API

All API responses use this format:

```json
{
	"success": true,
	"statusCode": 200,
	"message": "Message for the client",
	"data": {}
}
```

## Authorization header

Login returns a JWT access token. Send that token on every protected API:

```text
Authorization: Bearer <token-from-login-response>
```

For local testing, make one registered user an admin directly in the database:

```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'admin@example.com';
```

## Authentication

### Register

```text
POST /api/v1/auth/register
```

```json
{
	"name": "Pronoy",
	"email": "pronoy@example.com",
	"password": "12345678"
}
```

### Login

```text
POST /api/v1/auth/login
```

```json
{
	"email": "pronoy@example.com",
	"password": "12345678"
}
```

The login response contains the user's id, role, and JWT token. The token payload includes:

```json
{
	"sub": "user-uuid",
	"role": "USER",
	"iat": 1760000000,
	"exp": 1760086400
}
```

## Farmer application and profile

## Admin creation

This endpoint does not require an authentication header.

```text
POST /api/v1/admin/users
```

```json
{
	"name": "New Admin",
	"email": "newadmin@example.com",
	"password": "12345678"
}
```

### 1. User applies to become a farmer

The user sends this request with the bearer token from login. The application starts with `PENDING` status.

```text
POST /api/v1/farmers/application
Authorization: Bearer <token>
```

```json
{
	"farmName": "Green Valley Farm",
	"farmAddress": "Village Road, Rangpur",
	"phoneNumber": "01700000000",
	"description": "Vegetable and rice farm"
}
```

### Check application status

```text
GET /api/v1/farmers/application
Authorization: Bearer <token>
```

### 2. Admin views applications

The header must belong to a user whose database role is `ADMIN`.

```text
GET /api/v1/admin/farmer-applications
Authorization: Bearer <admin-token>
```

### Admin approves an application

Approval changes the user's role from `USER` to `FARMER` and creates the farmer profile.

```text
POST /api/v1/admin/farmer-applications/{applicationId}/approve
Authorization: Bearer <admin-token>
```

### Admin rejects an application

```text
POST /api/v1/admin/farmer-applications/{applicationId}/reject
Authorization: Bearer <admin-token>
```

### Get farmer profile

Only an approved farmer can use this endpoint.

```text
GET /api/v1/farmers/profile
Authorization: Bearer <farmer-token>
```

### Update farmer profile and farm information

```text
PUT /api/v1/farmers/profile
Authorization: Bearer <farmer-token>
```

```json
{
	"farmName": "Updated Green Valley Farm",
	"farmAddress": "New Village Road, Rangpur",
	"phoneNumber": "01700000000",
	"description": "Organic vegetables and rice"
}
```

## Products

### Get all products

This endpoint is public and returns products newest first.

```text
GET /api/v1/products
```

### 3. Approved farmer adds a product

Only a user with role `FARMER` can add products.

```text
POST /api/v1/products
Authorization: Bearer <farmer-token>
```

```json
{
	"name": "Tomato",
	"description": "Fresh farm tomatoes",
	"price": 80.00,
	"quantity": 50,
	"unit": "kg",
	"imageUrl": "https://example.com/tomato.jpg"
}
```

### Update a product

Only the farmer who owns the product can update it.

```text
PUT /api/v1/products/{productId}
Authorization: Bearer <farmer-token>
```

Use the same JSON body as the add product request.

### Delete a product

Only the owner can delete the product.

```text
DELETE /api/v1/products/{productId}
Authorization: Bearer <farmer-token>
```

## Existing users endpoint

```text
GET /api/v1/users
```


 # KrishiBazar API

 <!-- Run commend -->
<!-- (.\mvnw.cmd spring-boot:run) -->

All API responses use this format:

```json
{
	"success": true,
	"statusCode": 200,
	"message": "Message for the client",
	"data": {}
}
```

Protected endpoints require an `Authorization: Bearer <token>` header. If the
header is missing or the token is invalid, the API returns `401 Unauthorized`
using the same response format with the message
`Authorization is required to access this endpoint`.

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

Copy the `data.token` value from the login response and send it in the
`Authorization` header. Opening the protected URL directly in a browser will
return `401` because the browser request has no JWT header.

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

Example with `curl`:

```bash
curl -X POST http://localhost:8080/api/v1/farmers/application \
	-H "Authorization: Bearer <token-from-login-response>" \
	-H "Content-Type: application/json" \
	-d '{
		"farmName": "Green Valley Farm",
		"address": {
			"district": "Rangpur",
			"zilla": "Rangpur",
			"detailsAddress": "Village Road, Mithapukur"
		},
		"phoneNumber": "01700000000",
		"description": "Vegetable and rice farm"
	}'
```

```json
{
	"farmName": "Green Valley Farm",
	"address": {
		"district": "Rangpur",
		"zilla": "Rangpur",
		"detailsAddress": "Village Road, Mithapukur"
	},
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

Any authenticated user can use this endpoint. For users without a farmer
profile, farmer-specific fields are returned as `null`. The `role` field
contains the current user's role: `USER`, `FARMER`, or `ADMIN`.

```text
GET /api/v1/farmers/profile
Authorization: Bearer <token>
```

Example response for a user without a farmer profile:

```json
{
	"success": true,
	"statusCode": 200,
	"message": "User profile fetched successfully",
	"data": {
		"id": null,
		"userId": "dc1782f7-5046-441b-b44b-5b467b7fd023",
		"farmerName": "Pronoy",
		"email": "pronoy@example.com",
		"role": "USER",
		"farmName": null,
		"address": null,
		"phoneNumber": null,
		"description": null,
		"createdAt": "2026-09-21T12:00:00",
		"updatedAt": "2026-09-21T12:00:00"
	}
}
```

### Update farmer profile and farm information

```text
PUT /api/v1/farmers/profile
Authorization: Bearer <farmer-token>
```

```json
{
	"farmName": "Updated Green Valley Farm",
	"address": {
		"district": "Rangpur",
		"zilla": "Rangpur",
		"detailsAddress": "New Village Road, Mithapukur"
	},
	"phoneNumber": "01700000000",
	"description": "Organic vegetables and rice"
}
```

## Categories

Category reads are public. A category with `parentCategoryId: null` is a top-level category. Set `parentCategoryId` to a top-level category ID to create a subcategory.

### Get all categories

```text
GET /api/v1/categories
```

### Get all subcategories for a parent category

Use the top-level category ID as `parentCategoryId`. This endpoint is public
and returns an empty `data` array when the parent category has no subcategories.

```text
GET /api/v1/categories/{parentCategoryId}/subcategories
```

Example:

```text
GET /api/v1/categories/aaaa99c0-ec8b-4e93-aa88-9491804220b4/subcategories
```

### Get one category

```text
GET /api/v1/categories/{categoryId}
```

### Create a category or subcategory

Only an `ADMIN` can create categories.

```text
POST /api/v1/categories
Authorization: Bearer <admin-token>
```

```json
{
	"name": "Vegetables",
	"parentCategoryId": null
}
```

For a subcategory, replace `parentCategoryId` with the ID of a top-level category.

### Update a category

```text
PUT /api/v1/categories/{categoryId}
Authorization: Bearer <admin-token>
```

Use the same JSON body as the create request.

### Delete a category

A category cannot be deleted while it has subcategories or products assigned to it.

```text
DELETE /api/v1/categories/{categoryId}
Authorization: Bearer <admin-token>
```

## Products

### Get all products

This endpoint is public and returns products newest first.

```text
GET /api/v1/products
```

### Get all products for a farmer

This endpoint is public and returns the specified farmer's products newest first.

```text
GET /api/v1/products/farmer/{farmerId}
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
	"imageUrl": "https://example.com/tomato.jpg",
	"categoryId": "550e8400-e29b-41d4-a716-446655440000"
}
```

Use the actual `id` returned by `GET /api/v1/categories`. Numeric values such as
`"1232"` are not valid because category IDs are UUIDs.

### Update a product

Only the farmer who owns the product can update it.

```text
PUT /api/v1/products/{productId}
Authorization: Bearer <farmer-token>
```

```json
{
	"name": "Updated Tomato",
	"description": "Fresh red farm tomatoes",
	"price": 90.00,
	"quantity": 40,
	"unit": "kg",
	"imageUrl": "https://example.com/updated-tomato.jpg",
	"categoryId": "550e8400-e29b-41d4-a716-446655440000"
}
```

The authenticated farmer must own the product. Use the actual `categoryId`
returned by `GET /api/v1/categories`.

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


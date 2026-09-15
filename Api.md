1. Get All user APi
GET http://localhost:8080/api/v1/users

2. Register user Api
POST http://localhost:8080/api/v1/auth/register

//body
{
"name": "Pronoy",
"email": "pronoy@gmail.com",
"password": "12345678"
}

3. login APi
POST http://localhost:8080/api/v1/auth/login

//body
{

"email": "pronoy@gmail.com",
"password": "12345678"
}

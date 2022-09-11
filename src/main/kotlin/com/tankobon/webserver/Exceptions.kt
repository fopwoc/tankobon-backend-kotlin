package com.tankobon.webserver

class InternalServerError : RuntimeException()
class BadRequestError : RuntimeException()
class AuthenticationException : RuntimeException()
class CredentialsException : RuntimeException()
class AdminAuthenticationException : RuntimeException()
class UserExistException : RuntimeException()

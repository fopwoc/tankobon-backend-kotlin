package com.tankobon.api

class InternalServerError(override val message: String) : RuntimeException()
class BadRequestError : RuntimeException()
class AuthenticationException : RuntimeException()
class CredentialsException : RuntimeException()
class AdminAuthenticationException : RuntimeException()
class UserExistException : RuntimeException()
class UserDisabledException : RuntimeException()

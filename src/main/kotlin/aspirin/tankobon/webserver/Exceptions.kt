package aspirin.tankobon.webserver

class InternalServerError : RuntimeException()
class BadRequestError : RuntimeException()
class AuthenticationException : RuntimeException()
class AdminAuthenticationException : RuntimeException()
class UserExistException : RuntimeException()

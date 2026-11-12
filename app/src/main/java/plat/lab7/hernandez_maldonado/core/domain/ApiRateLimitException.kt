package plat.lab7.hernandez_maldonado.core.domain

class ApiRateLimitException(
    cause: Throwable
) : Exception(
    "El servidor limitó temporalmente las solicitudes.",
    cause
)
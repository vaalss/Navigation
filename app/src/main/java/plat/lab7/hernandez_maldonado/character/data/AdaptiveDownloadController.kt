package plat.lab7.hernandez_maldonado.core.data

import android.os.SystemClock
import android.util.Log
import java.io.IOException
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import plat.lab7.hernandez_maldonado.core.domain.ApiRateLimitException

class AdaptiveDownloadController {

    private val semaphore = Semaphore(2)
    private val mutex = Mutex()

    private var intervalMillis = 300L
    private var nextRequestAt = 0L
    private var blockedUntil = 0L

    suspend fun <T> execute(request: suspend () -> T): T {
        return semaphore.withPermit {
            executeWithRetries(request)
        }
    }

    private suspend fun <T> executeWithRetries(request: suspend () -> T): T {
        var retries = 0

        while (true) {
            awaitRequestSlot()

            try {
                return request()
            } catch (exception: ApiRateLimitException) {
                val waitMillis = exception.retryAfterMillis
                    ?: (15_000L * (1L shl retries))

                // No mantenemos el login esperando indefinidamente.
                // Si Retry-After es largo, terminamos sin reintentar antes.
                if (retries >= 2 || waitMillis > 60_000L) {
                    throw exception
                }

                mutex.withLock {
                    intervalMillis = (intervalMillis * 2).coerceAtMost(2_000L)
                    blockedUntil = maxOf(
                        blockedUntil,
                        SystemClock.elapsedRealtime() + waitMillis
                    )
                }

                retries++
                Log.w(
                    "LoginSync",
                    "Límite del servidor: pausa compartida de ${waitMillis}ms. " +
                            "Reintento $retries/2."
                )
            } catch (exception: IOException) {
                if (retries >= 2) {
                    throw exception
                }

                retries++
                Log.w(
                    "LoginSync",
                    "Fallo de conexión. Reintento $retries/2.",
                    exception
                )
                delay(retries * 2_000L)
            }
        }
    }

    private suspend fun awaitRequestSlot() {
        while (true) {
            val waitMillis = mutex.withLock {
                val now = SystemClock.elapsedRealtime()
                val allowedAt = maxOf(nextRequestAt, blockedUntil)

                if (now >= allowedAt) {
                    nextRequestAt = now + intervalMillis
                    0L
                } else {
                    allowedAt - now
                }
            }

            if (waitMillis == 0L) {
                return
            }

            // Esperar fuera del mutex permite extender la pausa si otra
            // solicitud recibe un 429 mientras esta está esperando.
            delay(waitMillis)
        }
    }
}

package ru.rian.dynamics.utils

import mu.KLogging
import org.slf4j.Marker
import org.slf4j.MarkerFactory

open class KLoggerWrap(any: Any) {

    private val kLogging: KLogging = KLogging()
    private val callerClass: Any = any

    private fun marker(): Marker {
        return MarkerFactory.getMarker(callerClass.javaClass.simpleName)
    }

    fun log_e(err: Throwable) {
        kLogging.logger.error {
            marker()
            err
        }
    }

    fun log_w(message: String) {
        kLogging.logger.warn {
            marker()
            message
        }
    }

    fun log_d(message: String) {
        kLogging.logger.debug {
            marker()
            message
        }
    }
}

/* fun <T : Any> T.getClass(): KClass<T> {
        return javaClass.kotlin
    }*/
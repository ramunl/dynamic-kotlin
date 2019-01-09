package ru.rian.dynamics.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.Marker
import org.slf4j.MarkerFactory
import kotlin.reflect.KClass

open class KLoggerWrap(clazz: KClass<*>) {

    private val logger: Logger = LoggerFactory.getLogger(clazz.java.simpleName)
    private val marker: Marker = MarkerFactory.getMarker(clazz.java.simpleName)

    init {

    }

    fun kError(err: Throwable) {
        logger.error( err.message)
    }

    fun kWarn(message: String) {
        logger.warn(message)
    }

    fun kDebug(message: String) {
        logger.debug( message)
    }
}

/* fun <T : Any> T.getClass(): KClass<T> {
        return javaClass.kotlin
    }*/
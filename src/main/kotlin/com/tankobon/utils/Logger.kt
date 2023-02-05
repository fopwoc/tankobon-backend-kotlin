package com.tankobon.utils

import kotlin.reflect.full.companionObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun logger(title: String): Logger {
    return LoggerFactory.getLogger(title)
}

fun <R : Any> R.injectLogger(): Lazy<Logger> {
    return lazyOf(logger(this.javaClass))
}

private fun <T : Any> logger(forClass: Class<T>): Logger {
    return logger(unwrapCompanionClass(forClass).name)
}

private fun <T : Any> unwrapCompanionClass(ofClass: Class<T>): Class<*> {
    return ofClass.enclosingClass?.takeIf {
        ofClass.enclosingClass.kotlin.companionObject?.java == ofClass
    } ?: ofClass
}

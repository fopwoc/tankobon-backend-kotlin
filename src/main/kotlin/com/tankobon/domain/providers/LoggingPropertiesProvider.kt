package com.tankobon.domain.providers

import ch.qos.logback.core.PropertyDefinerBase

class LoggingPropertiesProvider : PropertyDefinerBase() {
    private var propertyLookupKey: String? = null

    fun setPropertyLookupKey(propertyLookupKey: String?) {
        this.propertyLookupKey = propertyLookupKey
    }

    override fun getPropertyValue(): String {
        return properties[propertyLookupKey]!!
    }

    companion object {
        private val properties: MutableMap<String?, String> = HashMap()
        private val configProvider = ConfigProvider.get()

        init {
            properties["logFile"] = configProvider.server.logFile
            properties["logLevel"] = configProvider.server.logLevel
        }
    }
}

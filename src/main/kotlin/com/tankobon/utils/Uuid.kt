package com.tankobon.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.UUID
import java.util.regex.Pattern

private val uuidRegexPattern: Pattern =
    Pattern.compile("^[{]?[\\da-fA-F]{8}-([\\da-fA-F]{4}-){3}[\\da-fA-F]{12}[}]?$")

fun isValidUUID(str: String?): Boolean {
    return if (str == null) {
        false
    } else {
        uuidRegexPattern.matcher(str).matches()
    }
}

fun uuidFromString(str: String?): UUID? {
    return if (str == null) {
        null
    } else {
        if (uuidRegexPattern.matcher(str).matches()) {
            UUID.fromString(str)
        } else {
            null
        }
    }
}

object UUIDSerializer : KSerializer<UUID> {
    override val descriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): UUID {
        return UUID.fromString(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(value.toString())
    }
}

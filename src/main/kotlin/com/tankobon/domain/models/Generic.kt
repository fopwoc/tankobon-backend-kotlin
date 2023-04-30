package com.tankobon.domain.models

interface IdEntity<T> {
    val id: T
}

interface DateEntity<T> {
    val creation: T
    val modified: T
}

interface ContentEntity<T> {
    val content: List<T>
}

interface ImageMeta {
    val hash: String
}

interface FilterEntity {
    val offset: Long?
    val limit: Int?
    val search: String?
}

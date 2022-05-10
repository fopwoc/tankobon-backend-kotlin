package aspirin.tankobon.database.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.UUIDTable

object MangaModel : UUIDTable() {
    val title = varchar("title", 255)
    val description = text("description")
    val cover = text("cover")
    val volume = text("volume")
}

@Serializable
data class Manga(
    val id: String,
    val title: String,
    val description: String,
    val cover: String,
    val volume: List<Int>,
)

@Serializable
data class MangaUpdate(
    val id: String,
    val title: String?,
    val volume: List<Int>,
)


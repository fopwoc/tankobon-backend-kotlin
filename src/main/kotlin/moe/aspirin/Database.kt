package moe.aspirin

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*


object Utils : Table() {
    val secret = varchar("secret", 36)
}

class UtilDB {

    fun init() {
        Database.connect("jdbc:sqlite:./data.db", driver = "org.sqlite.JDBC")
        transaction {
            //addLogger(StdOutSqlLogger)
            SchemaUtils.createDatabase("data")
            SchemaUtils.create(Utils)
            SchemaUtils.create(Users)
            SchemaUtils.create(Manga)

            utilInitSecret()
            UsersDB().addUser(System.getenv("tkbn_username") ?: "user", System.getenv("tkbn_password") ?: "password")
        }
    }

    private fun utilInitSecret() {
        val secretUUID = UUID.randomUUID().toString()
        if (Utils.select { Utils.secret eq secretUUID }.toList().isEmpty())
            Utils.insert {
                it[secret] = secretUUID
            }
    }

    fun utilGetSecret(): String {
       return transaction { Utils.selectAll().first()[Utils.secret] }
    }
}

object Users : Table() {
    val username = varchar("username", 64)
    val password = varchar("password", 64)
}

class UsersDB {

    fun addUser(username: String, password: String) {
        if (Users.select { Users.username eq username }.toList().isEmpty())
            Users.insert {
                it[Users.username] = username
                it[Users.password] = password
            }
    }

    fun authUser(username: String, password: String): Boolean {
        val userList = transaction {
            Users.select(Users.username.eq(username) and Users.password.eq(password)).toList()
        }
        print(userList)
        return userList.isNotEmpty()
    }
}

@Serializable
data class MangaModel(val uuid: String, val fileName: String, val volume: List<Int>)

object Manga : UUIDTable() {
    val fileName = varchar("fileName", 256)
    val volume = text("volume")
}

class MangaDao (uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    companion object : UUIDEntityClass<MangaDao>(Manga)

    var fileName by Manga.fileName
    var volume by Manga.volume

    fun toModel():MangaModel{
        return MangaModel(id.toString(), fileName, Json.decodeFromString(volume))
    }
}


class MangaDB {

    fun getMangaList(): List<MangaModel> {
        return transaction { MangaDao.all().map { e -> e.toModel() } }
    }

    fun createMangaList(name: String, volumeDate: String) {
        transaction {
            if (MangaDao.find { Manga.fileName eq name }.toList().isEmpty())
                MangaDao.new {
                    fileName = name
                    volume = volumeDate
                }
        }
    }

    //TODO clear deleted manga from db

    fun getPathByUUID(uuid: UUID): String {
        return transaction { MangaDao.find { Manga.id eq uuid }.first().fileName }
    }

}
package aspirin.tankobon.database

import aspirin.tankobon.database.model.MangaModel
import aspirin.tankobon.database.model.UserModel
import aspirin.tankobon.database.model.UtilsModel
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.util.UUID

object DatabaseFactory {

    fun init(): Database {
        val serviceDB = Database.connect(hikariPersist())
        transaction(serviceDB) {

            // TODO refactor to use UserService.addUser
            if (!UserModel.exists()) {
                create(UserModel)
                UserModel.insert {
                    it[username] = System.getenv("tkbn_username") ?: "user"
                    it[password] = BCrypt.hashpw(
                        System.getenv("tkbn_password") ?: "password",
                        BCrypt.gensalt(),
                    )
                    it[registerDate] = System.currentTimeMillis()
                    it[active] = true
                    it[admin] = true
                }
            }

            if (!UtilsModel.exists()) {
                create(UtilsModel)
                UtilsModel.insert {
                    it[secret] = UUID.randomUUID().toString()
                    it[creationDate] = System.currentTimeMillis()
                }
            }

            if (!MangaModel.exists()) {
                create(MangaModel)
            }
        }
        return serviceDB
    }

    private fun hikariPersist(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = "org.h2.Driver"
        config.jdbcUrl = "jdbc:h2:file:./data/serviceDB"
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()
        return HikariDataSource(config)
    }
}

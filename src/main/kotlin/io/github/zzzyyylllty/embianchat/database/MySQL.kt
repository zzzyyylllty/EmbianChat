package io.github.zzzyyylllty.embianchat.database

import org.bukkit.entity.Player
import taboolib.module.database.ColumnOptionSQL
import taboolib.module.database.ColumnTypeSQL
import taboolib.module.database.Table
import taboolib.module.database.getHost
import java.util.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import taboolib.common.platform.function.info
import kotlin.text.get

val jsonUtils = Json {
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys = true
    coerceInputValues = true
    encodeDefaults = true
    allowStructuredMapKeys = true
    allowSpecialFloatingPointValues = true
}

open class SQLDataBase {

    val host by lazy { config.getHost("database") }
    val dataSource by lazy { host.createDataSource() }

    val userTable = Table("user_table", host) {
        add { id() }
        add("uuid") { // Player UUID
            type(ColumnTypeSQL.VARCHAR, 36) {
                options(
                    ColumnOptionSQL.NOTNULL, ColumnOptionSQL.UNIQUE_KEY)
            }
        }
        add("data") {
            type(ColumnTypeSQL.JSON)
        }
    }

    val indexTable = Table("index_table", host) {
        add { id() }
        add("uuid") { // Player UUID
            type(ColumnTypeSQL.VARCHAR, 36) {
                options(
                    ColumnOptionSQL.NOTNULL, ColumnOptionSQL.UNIQUE_KEY)
            }
        }
        add("name") { // Player Name
            type(ColumnTypeSQL.VARCHAR, 36) {
                options(
                    ColumnOptionSQL.NOTNULL, ColumnOptionSQL.UNIQUE_KEY)


            }
        }
        add("userid") { // Player Long Id
            type(ColumnTypeSQL.VARCHAR, 36) {
                options(
                    ColumnOptionSQL.NOTNULL, ColumnOptionSQL.UNIQUE_KEY)
            }
        }
    }


    fun saveInDatabase(user: PlayerData?) {
        if (user == null) return
        val json = jsonUtils.encodeToString(user)
        if (getUserInDatabasebyUUID(user.playerUUID) == null) {
            userTable.insert(dataSource, "uuid", "data") {
                value(user.playerUUID,  json)
            }
        } else {
            userTable.update(dataSource) {
                set("uuid", user.playerUUID)
                set("data", json)
            }
        }
        if (!getIDExists(user.userId)) {
            indexTable.insert(dataSource, "uuid", "name", "userid") {
                value(user.playerUUID, user.playerName, user.userId)
            }
        } else {
            indexTable.update(dataSource) {
                set("uuid", user.playerUUID)
                set("userid", user.userId)
            }
        }
    }
    fun saveInDatabase(user: Player) {
        saveInDatabase(dataMap[user.uniqueId.toString()])
    }

    fun getUserInDatabase(player: Player?): PlayerData? {
        if (player == null) return null else return getUserInDatabasebyUUID(player.uniqueId.toString())
    }

    fun getUserInDatabase(name: String): PlayerData? {
        val uuid = indexTable.select(dataSource) {
            rows("uuid")
            where("name" eq name)
            limit(1)
        }.firstOrNull {
            getString("uuid")
        }
        if (uuid == null) return null else {
            return getUserInDatabasebyUUID(uuid)
            // Expansion fun import kotlinx.serialization.encodeToString required.
        }
    }

    fun getUserInDatabaseByUserID(uid: String): PlayerData? {
        val uuid = indexTable.select(dataSource) {
            rows("uuid")
            where("userid" eq uid)
            limit(1)
        }.firstOrNull {
            getString("uuid")
        }
        if (uuid == null) return null else {
            return getUserInDatabasebyUUID(uuid)
            // Expansion fun import kotlinx.serialization.encodeToString required.
        }
    }
    fun getIDExists(uid: String): Boolean {
        val uuid = indexTable.select(dataSource) {
            rows("uuid")
            where("userid" eq uid)
            limit(1)
        }.firstOrNull {
            getString("uuid")
        }
        info("UID: $uid, UUID: $uuid")
        return (uuid != null)
    }
    fun getUUIDExists(uuid: String): Boolean {
        val uid = indexTable.select(dataSource) {
            rows("userid")
            where("uuid" eq uuid)
            limit(1)
        }.firstOrNull {
            getString("uuid")
        }
        info("UID: $uid, UUID: $uuid")
        return (uid != null)
    }

    fun getUserInDatabasebyUUID(uuid: String?): PlayerData? {
        val string = userTable.select(dataSource) {
            rows("data")
            where("uuid" eq uuid)
            limit(1)
        }.firstOrNull {
            getString("data")
        }
        if (string == null) return null else {
            return jsonUtils.decodeFromString<PlayerData>(string)
            // Expansion fun import kotlinx.serialization.encodeToString required.
        }
    }

    init {
        userTable.createTable(dataSource)
        indexTable.createTable(dataSource)
    }
}
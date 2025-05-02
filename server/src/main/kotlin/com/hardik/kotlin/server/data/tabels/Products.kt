package com.hardik.kotlin.server.data.tabels

import org.jetbrains.exposed.sql.Table

object Products : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 128)
    val price = double("price")

    override val primaryKey = PrimaryKey(id)
}
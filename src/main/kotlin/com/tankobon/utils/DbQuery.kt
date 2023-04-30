package com.tankobon.utils

import com.tankobon.domain.providers.DatabaseProvider
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

suspend fun <T> dbQuery(block: suspend Transaction.() -> T): T =
    newSuspendedTransaction(
        context = Dispatchers.IO,
        db = DatabaseProvider.get()
    ) { block() }

package com.lemonlab.all_in_one.model

import java.sql.Timestamp
import java.util.*

data class Message(
    var text: String, var userUid: String, var username: String,
    var timestamp: Date
) {
    constructor() : this("", "", "", Timestamp(System.currentTimeMillis()))
}
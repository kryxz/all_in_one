package com.lemonlab.all_in_one.model

import java.sql.Timestamp

data class Message(
    var text: String, var userUid: String, var username: String,
    var timestamp: Timestamp
)
package com.lemonlab.all_in_one.model

import java.sql.Timestamp

data class UserStatusImage(var url: String, var timestamp: Timestamp?, var userId: String) {

    constructor() : this("", null, "")
}
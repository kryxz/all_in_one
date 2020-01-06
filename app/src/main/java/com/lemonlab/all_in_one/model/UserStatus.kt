package com.lemonlab.all_in_one.model

import java.sql.Timestamp

enum class UserStatusType{
    TextStatus, ImageStatus
}
data class UserStatus(var userId:String, var statusType:UserStatusType, var text:String
    , var imageUrl:String? = null, var timestamp: Timestamp)
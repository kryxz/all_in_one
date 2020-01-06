package com.lemonlab.all_in_one.model

import com.lemonlab.all_in_one.items.Category
import java.sql.Timestamp

enum class StatusColor{
    Red, Green, Blue
}

data class UserStatus(var userId:String, var text:String, var category: Category,
                      var statusColor: StatusColor,
                      var likesIds:List<String>? = null, var imageUrl:String? = null,
                      var timestamp: Timestamp)
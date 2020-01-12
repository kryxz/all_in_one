package com.lemonlab.all_in_one.model

data class User(val name: String, val email: String, var online: Boolean) {
    constructor() : this(
        "", "", false
    )

}
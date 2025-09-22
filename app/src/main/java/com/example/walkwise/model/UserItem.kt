package com.example.walkwise.model

data class UserItem(var username: String, var email: String) {

    var id: Long = 0L

    constructor(): this ("", "")
}
package com.example.gsapp

object SessionManager {
    var currentUserType: UserType? = null
    var currentUserId: Int? = null
    
    fun logout() {
        currentUserType = null
        currentUserId = null
    }
}

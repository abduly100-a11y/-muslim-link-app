package com.mosque.community.data.models

enum class UserRole(val label: String) {
    GUEST("Guest"),
    CONGREGANT("Community Member"),
    MOSQUE_ADMIN("Mosque Administrator")
}

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val adminMosqueId: String? = null,
    val adminMosqueName: String? = null
) {
    val isMosqueAdmin: Boolean get() = role == UserRole.MOSQUE_ADMIN
    val isCongregant: Boolean get() = role == UserRole.CONGREGANT
    val isGuest: Boolean get() = role == UserRole.GUEST

    companion object {
        val Guest = User(
            id = "guest",
            name = "Guest User",
            email = "",
            role = UserRole.GUEST
        )

        val SampleCongregant = User(
            id = "user_bilal",
            name = "Bilal Siddiqui",
            email = "bilal.s@example.com",
            role = UserRole.CONGREGANT
        )

        val SampleAdmin = User(
            id = "admin_tariq",
            name = "Imam Tariq (Admin)",
            email = "admin@alnoorcenter.org",
            role = UserRole.MOSQUE_ADMIN,
            adminMosqueId = "m1",
            adminMosqueName = "Al-Noor Islamic Cultural Center"
        )
    }
}

package technopark_db.models.local

data class UserLocal(
        var nickname: String,
        val email: String,
        val fullname: String,
        val about: String)
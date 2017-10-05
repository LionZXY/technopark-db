package technopark_db.models.local

data class UserLocal(
        var nickname: String,
        val localId: Int,
        val email: String,
        val fullname: String)
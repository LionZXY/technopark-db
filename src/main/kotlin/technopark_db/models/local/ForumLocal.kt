package technopark_db.models.local

data class ForumLocal(
        val slug: String,
        var title: String,
        var postCount: Int = 0,
        var threads: Int = 0 /* Веток */,
        var user: UserLocal)
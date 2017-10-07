package technopark_db.models.local

data class ForumThreadLocal(var localId: Int,
                            var authornick: String,
                            var created: Long,
                            var messagetext: String,
                            var slug: String,
                            var title: String)
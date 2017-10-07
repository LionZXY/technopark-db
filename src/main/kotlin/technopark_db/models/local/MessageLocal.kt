package technopark_db.models.local

data class MessageLocal(
        var localId: Int,
        var created: Long,
        var isEdited: Boolean,
        var message: String
)
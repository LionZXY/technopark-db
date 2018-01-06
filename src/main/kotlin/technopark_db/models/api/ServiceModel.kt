package technopark_db.models.api

class ServiceModel(public var forum: Int,
                   public var post: Int,
                   public var thread: Int,
                   public var user: Int) {
    constructor() : this(0, 0, 0, 0)
}
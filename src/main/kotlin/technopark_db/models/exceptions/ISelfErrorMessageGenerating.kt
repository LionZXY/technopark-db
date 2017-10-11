package technopark_db.models.exceptions


interface ISelfErrorMessageGenerating {
    fun generate(): Any
}
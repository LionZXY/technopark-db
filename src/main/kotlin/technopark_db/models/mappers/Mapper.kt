package technopark_db.models.mappers

interface Mapper<IN, OUT> {
    fun map(input: IN): OUT

    fun mapList(input: List<IN>): List<OUT> {
        val list = ArrayList<OUT>(input.size)
        input.forEach({ list.add(map(it)) })
        return list
    }
}
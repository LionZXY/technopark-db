package technopark_db.models.api

import technopark_db.models.utils.FlatSort
import technopark_db.models.utils.PTreeSort
import technopark_db.models.utils.SortSqlGeneration
import technopark_db.models.utils.TreeSort

enum class SortType(val sortSqlGeneration: SortSqlGeneration) {
    FLAT(FlatSort()),
    TREE(TreeSort()),
    PARENT_TREE(PTreeSort());

    companion object {
        fun valueOf(sort: String?): SortType {
            return when (sort) {
                "tree" -> TREE
                "parent_tree" -> PARENT_TREE
                else -> FLAT
            }
        }
    }
}
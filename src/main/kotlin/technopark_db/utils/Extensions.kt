package technopark_db.utils

fun String.isSlug() = Constants.slugPatter.matcher(this).matches()
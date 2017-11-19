package technopark_db.utils

fun String.isSlug() = Constants.slugPatter.matcher(this).matches()

fun String.isNumeric() = this.toCharArray().none { it !in '0'..'9' }

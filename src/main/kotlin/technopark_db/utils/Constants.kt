package technopark_db.utils

import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

class Constants {
    companion object {
        public val pattern = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        public val slugPatter = Pattern.compile("^(\\d|\\w|-|_)*(\\w|-|_)(\\d|\\w|-|_)*\$")
    }
}
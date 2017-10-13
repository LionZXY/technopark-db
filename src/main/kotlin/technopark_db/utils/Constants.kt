package technopark_db.utils

import java.text.SimpleDateFormat
import java.util.regex.Pattern

class Constants {
    companion object {
        public val pattern = SimpleDateFormat("yyyy-MM-ddTHH:mm:ss.SSSz")
        public val slugPatter = Pattern.compile("^(\\d|\\w|-|_)*(\\w|-|_)(\\d|\\w|-|_)*\$")
    }
}
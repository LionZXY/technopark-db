package technopark_db.models.mappers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import technopark_db.utils.Constants
import java.sql.Date


class DateAdapterSeriliazation : JsonSerializer<Date>() {
    override fun serialize(value: Date, gen: JsonGenerator, serializers: SerializerProvider?) {
        gen.writeString(Constants.pattern.format(value))
    }
}
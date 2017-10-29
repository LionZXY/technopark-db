package technopark_db.models.mappers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import technopark_db.utils.Constants
import java.sql.Date
import java.sql.Time
import java.sql.Timestamp
import java.time.OffsetDateTime


class DateAdapterSeriliazation : JsonSerializer<Timestamp>() {
    override fun serialize(value: Timestamp, gen: JsonGenerator, serializers: SerializerProvider?) {
        gen.writeString(Constants.pattern.format(value))
    }
}
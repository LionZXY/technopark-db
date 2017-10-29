package technopark_db.models.mappers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import technopark_db.utils.Constants
import java.sql.Date
import java.sql.Time
import java.sql.Timestamp
import java.time.OffsetDateTime

class DateAdapterDeseriliazation : JsonDeserializer<Timestamp>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): Timestamp {
        return Timestamp(Constants.pattern.parse(p.valueAsString).time)
    }
}
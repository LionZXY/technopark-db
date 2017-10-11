package technopark_db.models.mappers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import technopark_db.utils.Constants
import java.sql.Date

class DateAdapterDeseriliazation : JsonDeserializer<Date>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): Date {
        return Date(Constants.pattern.parse(p.valueAsString).time)
    }
}
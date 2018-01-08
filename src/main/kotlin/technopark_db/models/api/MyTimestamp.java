package technopark_db.models.api;

import technopark_db.utils.Constants;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public class MyTimestamp {
    private Timestamp timestamp;

    public MyTimestamp(String timestamp) throws Exception {
        final OffsetDateTime offsetDateTime = OffsetDateTime.parse(timestamp);
        this.timestamp = Timestamp.valueOf(offsetDateTime.atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime());
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}

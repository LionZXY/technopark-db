package technopark_db.models.api;

import technopark_db.utils.Constants;

import java.sql.Timestamp;

public class MyTimestamp {
    private Timestamp timestamp;

    public MyTimestamp(String timestamp) throws Exception {
        this.timestamp = new Timestamp(Constants.Companion.getPattern().parse(timestamp).getTime());
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}

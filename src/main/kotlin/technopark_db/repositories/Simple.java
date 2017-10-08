package technopark_db.repositories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Solovyev on 09/10/2017.
 */
@Component
public class Simple {
    private static final Logger logger = LoggerFactory.getLogger(Simple.class);


    private final DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    public Simple(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void postConstruct2() throws SQLException {
        String result = jdbcTemplate.query("select rolname from pg_roles;", (rs) -> {
            StringBuilder sb = new StringBuilder();
            while(rs.next()) {
                sb.append(rs.getString(1));
                sb.append("\n");
            }
            return sb.toString();
        }
               );
        logger.info(String.format("Got result: %s", result));
    }
}

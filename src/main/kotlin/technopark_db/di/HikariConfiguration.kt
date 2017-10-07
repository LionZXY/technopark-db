package technopark_db.di

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import java.sql.SQLException
import java.util.*
import javax.sql.DataSource


@Configuration
@ConfigurationProperties(prefix = "params.datasource")
open class HikariConfiguration {

    @Bean
    @Throws(SQLException::class)
    open fun dataSource(): DataSource {
        val res = ClassPathResource("hikari.properties")
        val prop = Properties()

        res.inputStream.use {
            prop.load(it)
        }

        return HikariDataSource(HikariConfig(prop))
    }

}
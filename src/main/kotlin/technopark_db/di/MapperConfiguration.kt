package technopark_db.di

import org.flywaydb.core.Flyway
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDataSourceConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import technopark_db.models.api.User
import technopark_db.models.local.UserLocal
import technopark_db.models.mappers.Mapper
import technopark_db.models.mappers.UserMapper
import javax.sql.DataSource


@Configuration
open class MapperConfiguration {
    @Bean
    open fun provideUserMapper(): Mapper<UserLocal, User> = UserMapper()
}
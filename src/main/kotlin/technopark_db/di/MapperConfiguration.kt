package technopark_db.di

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import technopark_db.models.api.User
import technopark_db.models.local.UserLocal
import technopark_db.models.mappers.Mapper
import technopark_db.models.mappers.UserMapper
import java.text.SimpleDateFormat

@Configuration
open class MapperConfiguration {
    @Bean
    open fun provideUserMapper(): Mapper<UserLocal, User> = UserMapper()
}
package technopark_db.models.mappers

import org.springframework.stereotype.Component
import technopark_db.models.api.User
import technopark_db.models.local.UserLocal

@Component
class UserMapper : Mapper<UserLocal, User> {
    override fun map(input: UserLocal) = User(input.email,
            input.fullname,
            input.about,
            input.nickname)
}
package technopark_db.repositories

import org.springframework.stereotype.Component
import technopark_db.data.ServiceDao
import technopark_db.models.api.ServiceModel

@Component
class ServiceRepository(private val serviceDao: ServiceDao) {
    fun info(): ServiceModel {
        return serviceDao.info()
    }

    fun clear() {
        serviceDao.clear()
    }
}
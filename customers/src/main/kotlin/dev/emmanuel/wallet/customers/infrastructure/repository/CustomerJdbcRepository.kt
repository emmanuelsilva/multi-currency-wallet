package dev.emmanuel.wallet.customers.infrastructure.repository

import dev.emmanuel.wallet.customers.domain.entity.Email
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CustomerJdbcRepository : CrudRepository<CustomerEntity, UUID> {

    fun findByEmail(email: Email): CustomerEntity?

}
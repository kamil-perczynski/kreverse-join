package io.github.kamilperczynski.kreversejoin.repo

import io.github.kamilperczynski.kreversejoin.db.tables.TAddress.ADDRESS
import io.github.kamilperczynski.kreversejoin.db.tables.TCustomer.CUSTOMER
import io.github.kamilperczynski.kreversejoin.db.tables.records.CustomerRecord
import io.github.kamilperczynski.kreversejoin.dto.CustomerDto
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import kotlin.random.Random

@Repository
class CustomerRepository(private val dslContext: DSLContext) {

    fun save(customer: CustomerDto) {
        val rec = CustomerRecord().also {
            it.id = customer.id ?: Random.Default.nextLong(0L, Long.MAX_VALUE)
            it.customerName = customer.name
            it.primaryAddressId = customer.primaryAddressId
            it.secondaryAddressId = customer.secondaryAddressId
            it.lastModified = OffsetDateTime.now()
        }

        dslContext
            .insertInto(CUSTOMER)
            .set(rec)
            .onConflict(CUSTOMER.ID)
            .doUpdate()
            .set(rec)
            .execute()
    }

    fun findByAnyAddresses(addressId: Long): List<Long> {
        return dslContext
            .selectDistinct(CUSTOMER.ID)
            .from(CUSTOMER)
            .where(
                CUSTOMER.PRIMARY_ADDRESS_ID.eq(addressId)
                    .or(CUSTOMER.SECONDARY_ADDRESS_ID.eq(addressId))
            )
            .fetch { it.value1() }
    }

    fun findEnrichedCustomer(customerIds: Set<Long>): List<RichCustomerRecord> {
        val primaryAddress = ADDRESS.`as`("a1")
        val secondaryAddress = ADDRESS.`as`("a2")

        return dslContext
            .select(
                CUSTOMER,
                primaryAddress,
                secondaryAddress
            )
            .from(
                CUSTOMER
                    .leftJoin(primaryAddress).on(CUSTOMER.PRIMARY_ADDRESS_ID.eq(primaryAddress.ID))
                    .leftJoin(secondaryAddress).on(CUSTOMER.SECONDARY_ADDRESS_ID.eq(secondaryAddress.ID))
            )
            .fetch {
                RichCustomerRecord(it.value1(), it.value2(), it.value3())
            }
    }

}


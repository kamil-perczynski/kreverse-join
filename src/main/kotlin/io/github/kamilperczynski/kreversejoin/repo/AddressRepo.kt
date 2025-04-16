package io.github.kamilperczynski.kreversejoin.repo

import io.github.kamilperczynski.kreversejoin.db.tables.TAddress.ADDRESS
import io.github.kamilperczynski.kreversejoin.db.tables.records.AddressRecord
import io.github.kamilperczynski.kreversejoin.dto.AddressDto
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

@Repository
class AddressRepo(private val dslContext: DSLContext) {

    fun upsert(addressDto: AddressDto) {
        val rec = AddressRecord().also {
            it.id = addressDto.id
            it.street = addressDto.street
            it.city = addressDto.city
            it.state = addressDto.state
            it.zipCode = addressDto.zipCode
            it.lastModified = OffsetDateTime.now()
        }

        dslContext.insertInto(ADDRESS)
            .set(rec)
            .onConflict(ADDRESS.ID)
            .doUpdate()
            .set(rec)
            .execute()
    }

}

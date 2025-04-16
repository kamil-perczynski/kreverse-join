package io.github.kamilperczynski.kreversejoin.repo

import io.github.kamilperczynski.kreversejoin.db.tables.records.AddressRecord
import io.github.kamilperczynski.kreversejoin.db.tables.records.CustomerRecord

data class RichCustomerRecord(
    val customer: CustomerRecord,
    val primaryAddress: AddressRecord?,
    val secondaryAddress: AddressRecord?
)

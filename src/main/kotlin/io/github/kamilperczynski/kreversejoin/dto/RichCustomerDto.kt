package io.github.kamilperczynski.kreversejoin.dto

class RichCustomerDto(
    val id: Long,
    val name: String,
    val primaryAddress: AddressDto?,
    val secondaryAddress: AddressDto?
)

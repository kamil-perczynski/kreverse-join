package io.github.kamilperczynski.kreversejoin.dto

data class AddressDto(
    val id: Long,
    val street: String,
    val city: String,
    val state: String,
    val zipCode: String
)

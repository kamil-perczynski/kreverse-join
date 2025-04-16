package io.github.kamilperczynski.kreversejoin.dto

class CustomerDto(
    val id: Long,
    val name: String,
    val primaryAddressId: Long,
    val secondaryAddressId: Long
)

/*
Use those addresses to test the application with akhq,
you can post multiple messages at once specifying the separator in akhq console

__ { "id": 1, "name": "John Doe", "primaryAddressId": 1, "secondaryAddressId": 2 }
__ { "id": 2, "name": "Jane Smith", "primaryAddressId": 3, "secondaryAddressId": 4 }
__ { "id": 3, "name": "Alice Johnson", "primaryAddressId": 5, "secondaryAddressId": 6 }
__ { "id": 4, "name": "Bob Brown", "primaryAddressId": 7, "secondaryAddressId": 8 }
__ { "id": 5, "name": "Charlie Davis", "primaryAddressId": 9, "secondaryAddressId": 10 }
 */

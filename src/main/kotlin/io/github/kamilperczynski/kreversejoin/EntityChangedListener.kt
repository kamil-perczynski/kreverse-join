package io.github.kamilperczynski.kreversejoin

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.kamilperczynski.kreversejoin.dto.EntityChangedDto
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class EntityChangedListener(
    private val customerListener: CustomerListener,
    private val objectMapper: ObjectMapper
) {

    @KafkaListener(topics = ["private.entity_changed"])
    fun onEntityChanged(msg: ConsumerRecord<String, JsonNode>) {
        val dto = objectMapper.convertValue(msg.value(), EntityChangedDto::class.java)!!

        when (dto.entityName) {
            "AddressDto" -> {
                customerListener.onAddressChanged(dto)
            }
            "CustomerDto" -> {
                customerListener.onCustomerChange(dto)
            }
            else -> throw IllegalArgumentException("Unknown entity name: ${dto.entityName}")
        }

    }

}

/*
__ { "id": 1, "street": "123 Main St", "city": "Springfield", "state": "IL", "zipCode": "62701" }
__ { "id": 2, "street": "456 Elm St", "city": "Chicago", "state": "IL", "zipCode": "60601" }
__ { "id": 3, "street": "789 Oak St", "city": "Naperville", "state": "IL", "zipCode": "60540" }
__ { "id": 4, "street": "101 Maple Ave", "city": "Peoria", "state": "IL", "zipCode": "61602" }
__ { "id": 5, "street": "202 Pine St", "city": "Rockford", "state": "IL", "zipCode": "61101" }
__ { "id": 6, "street": "303 Cedar St", "city": "Aurora", "state": "IL", "zipCode": "60505" }
__ { "id": 7, "street": "404 Birch St", "city": "Evanston", "state": "IL", "zipCode": "60201" }
__ { "id": 8, "street": "505 Walnut St", "city": "Decatur", "state": "IL", "zipCode": "62521" }
__ { "id": 9, "street": "606 Chestnut St", "city": "Champaign", "state": "IL", "zipCode": "61820" }
__ { "id": 10, "street": "707 Ash St", "city": "Bloomington", "state": "IL", "zipCode": "61701" }
__ { "id": 11, "street": "808 Spruce St", "city": "Carbondale", "state": "IL", "zipCode": "62901" }
__ { "id": 12, "street": "909 Poplar St", "city": "Quincy", "state": "IL", "zipCode": "62301" }
__ { "id": 13, "street": "111 Willow St", "city": "Joliet", "state": "IL", "zipCode": "60431" }
__ { "id": 14, "street": "222 Sycamore St", "city": "Waukegan", "state": "IL", "zipCode": "60085" }
__ { "id": 15, "street": "333 Magnolia St", "city": "Elgin", "state": "IL", "zipCode": "60120" }
__ { "id": 16, "street": "444 Dogwood St", "city": "Belleville", "state": "IL", "zipCode": "62220" }
__ { "id": 17, "street": "555 Hickory St", "city": "Moline", "state": "IL", "zipCode": "61265" }
__ { "id": 18, "street": "666 Redwood St", "city": "Galesburg", "state": "IL", "zipCode": "61401" }
__ { "id": 19, "street": "777 Cypress St", "city": "Mattoon", "state": "IL", "zipCode": "61938" }
__ { "id": 20, "street": "888 Palm St", "city": "Danville", "state": "IL", "zipCode": "61832" }
 */

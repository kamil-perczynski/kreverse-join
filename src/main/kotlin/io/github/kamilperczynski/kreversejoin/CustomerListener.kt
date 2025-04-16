package io.github.kamilperczynski.kreversejoin

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.kamilperczynski.kreversejoin.db.tables.records.AddressRecord
import io.github.kamilperczynski.kreversejoin.dto.*
import io.github.kamilperczynski.kreversejoin.repo.CustomerRepository
import io.github.kamilperczynski.kreversejoin.repo.RichCustomerRecord
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaOperations
import org.springframework.stereotype.Component

@Component
class CustomerListener(
    private val kafkaOperations: KafkaOperations<Any, Any>,
    private val objectMapper: ObjectMapper,
    private val customerRepository: CustomerRepository
) {

    companion object : Sl4fj()

    @KafkaListener(topics = ["public.customers"])
    fun onCustomerUpdate(msg: ConsumerRecord<String, JsonNode>) {
        log.debug("Received customer: {}", msg.value())
        val customerDto = objectMapper.convertValue(msg.value(), CustomerDto::class.java)

        customerRepository.save(customerDto)
        val event = EntityChangedDto(CustomerDto::class.java.simpleName, customerDto.id.toString())
        kafkaOperations.send("private.entity_changed", event.toEntityChangedKey(), event)
    }

    fun onCustomerChange(event: EntityChangedDto) {
        log.debug("Received entity changed event: {}", event)

        kafkaOperations.send("private.entity_publish", event)
    }

    fun onAddressChanged(dto: EntityChangedDto) {
        val customerIds = customerRepository.findByAnyAddresses(dto.id.toLong())
        log.debug("Propagating address change to customers: {}", customerIds)

        for (customerId in customerIds) {
            val event = EntityChangedDto(CustomerDto::class.java.simpleName, customerId.toString())
            kafkaOperations.send("private.entity_changed", event.toEntityChangedKey(), event)
        }
    }

    fun onCustomerPublish(incomingEvents: List<EntityChangedDto>) {
        val customerIds = incomingEvents
            .map { it.id.toLong() }
            .toSet()

        val enrichedCustomers = customerRepository
            .findEnrichedCustomer(customerIds)
            .map { it.toRichCustomer() }

        log.info("Publishing {} enriched customers", enrichedCustomers.size)

        for (enrichedCustomer in enrichedCustomers) {
            kafkaOperations.send(
                "private.enriched_customers",
                enrichedCustomer.id.toString(),
                enrichedCustomer
            )
        }

    }

}

fun RichCustomerRecord.toRichCustomer(): RichCustomerDto {
    val customer = this.customer
    val primaryAddress = this.primaryAddress
    val secondaryAddress = this.secondaryAddress
    return RichCustomerDto(
        id = customer.id,
        name = customer.customerName,
        primaryAddress = primaryAddress?.toAddressDto(),
        secondaryAddress = secondaryAddress?.toAddressDto()
    )
}

fun AddressRecord.toAddressDto(): AddressDto {
    return AddressDto(
        id = this.id,
        street = this.street,
        city = this.city,
        state = this.state,
        zipCode = this.zipCode
    )
}

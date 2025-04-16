package io.github.kamilperczynski.kreversejoin

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.kamilperczynski.kreversejoin.dto.AddressDto
import io.github.kamilperczynski.kreversejoin.dto.EntityChangedDto
import io.github.kamilperczynski.kreversejoin.dto.toEntityChangedKey
import io.github.kamilperczynski.kreversejoin.repo.AddressRepo
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaOperations
import org.springframework.stereotype.Component

@Component
class AddressesListener(
    private val objectMapper: ObjectMapper,
    private val addressRepo: AddressRepo,
    private val kafkaOperations: KafkaOperations<Any, Any>
) {

    companion object : Sl4fj()

    @KafkaListener(topics = ["public.addresses"])
    fun onAddressChange(msg: ConsumerRecord<String, JsonNode>) {
        log.debug("Received address: {}", msg.value())

        val addressDto = objectMapper.convertValue(msg.value(), AddressDto::class.java)
        addressRepo.upsert(addressDto)

        val event = EntityChangedDto(AddressDto::class.java.simpleName, addressDto.id.toString())
        kafkaOperations.send(
            "private.entity_changed",
            event.toEntityChangedKey(),
            event
        )
    }

}

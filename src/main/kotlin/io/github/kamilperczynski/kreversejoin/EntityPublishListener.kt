package io.github.kamilperczynski.kreversejoin

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.kamilperczynski.kreversejoin.dto.EntityChangedDto
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class EntityPublishListener(
    private val customerListener: CustomerListener,
    private val objectMapper: ObjectMapper
) {

    companion object : Sl4fj()

    /**
     * This listener is throttled so that it runs
     * every 10s and processes 500 messages at a time.
     */
    @KafkaListener(
        topics = ["private.entity_publish"],
        batch = "true",
        properties = [
            "max.poll.interval.ms=30000",
            "max.poll.records=500",
            "fetch.min.bytes=100000",
            "fetch.max.wait.ms=10000",
        ]
    )
    fun onEntityPublish(msgs: List<ConsumerRecord<String, JsonNode>>) {
        val groups = msgs
            .map { objectMapper.convertValue(it.value(), EntityChangedDto::class.java) }
            .groupBy { it.entityName }

        for (entry in groups.entries) {
            when (entry.key) {
                "CustomerDto" -> {
                    customerListener.onCustomerPublish(entry.value)
                }

                else -> log.warn("Unknown entity name: {}", entry.key)
            }
        }

    }

}

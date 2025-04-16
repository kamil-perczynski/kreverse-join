package io.github.kamilperczynski.kreversejoin

import org.apache.kafka.clients.admin.NewTopic
import org.jooq.conf.RenderQuotedNames
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jooq.DefaultConfigurationCustomizer
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.kafka.annotation.EnableKafka


@SpringBootApplication
@EnableKafka
class KreversejoinApplication {

    companion object : Sl4fj()

    @Bean
    fun customersTopic(): NewTopic {
        return NewTopic("public.customers", 1, 1.toShort())
    }

    @Bean
    fun addressesTopic(): NewTopic {
        return NewTopic("public.addresses", 1, 1.toShort())
    }

    @Bean
    fun entityChangedTopic(): NewTopic {
        return NewTopic("private.entity_changed", 1, 1.toShort())
    }

    @Bean
    fun entityPublishTopic(): NewTopic {
        return NewTopic("private.entity_publish", 1, 1.toShort())
    }

    @Bean
    fun noQuotesCustomizer(): DefaultConfigurationCustomizer {
        return DefaultConfigurationCustomizer {
            it.settings().withRenderQuotedNames(RenderQuotedNames.NEVER)
        }
    }

}

fun main(args: Array<String>) {
    runApplication<KreversejoinApplication>(*args)
}

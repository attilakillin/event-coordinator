package hu.attilakillin.coordinatoreventsbackend.config

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

/**
 * Configure WebSocker and STOMP connection information.
 */
@Configuration
@EnableWebSocketMessageBroker
class StompConfiguration : WebSocketMessageBrokerConfigurer {

    /**
     * Configure message broker.
     */
    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableSimpleBroker("/topic")
    }

    /**
     * Register application endpoints that will be intercepted by STOMP.
     */
    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/websocket-checkin")
    }
}

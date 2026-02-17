package kr.or.ddit.spring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableWebSocketMessageBroker
@Configuration
@Controller
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	// STOMP 엔드포인트 등록 및 SockJS 지원 활성화
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp")
                .setAllowedOrigins("/wss")
                .withSockJS();
    }

    // 메시지 브로커 관련 설정
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
    	registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic");
    }


}

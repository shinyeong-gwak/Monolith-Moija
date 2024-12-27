package com.example.monolithmoija.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
//@EnableWebSocket    // 웹소켓 서버 사용
@EnableWebSocketMessageBroker   // STOMP 사용
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    //private final StompHandler stompHandler;

    @Autowired
    Environment env;
    @Override
    public void configureMessageBroker(final MessageBrokerRegistry registry) {
        registry.setPathMatcher(new AntPathMatcher("."));//왜하는지 찾아볼것
        //registry.enableSimpleBroker("/sub");
        registry.setApplicationDestinationPrefixes("/pub");
        registry.enableStompBrokerRelay("/queue", "/topic", "/exchange", "/amq/queue")
                .setVirtualHost("/")
                .setRelayHost(env.getProperty("spring.rabbitmq.host"))
                .setRelayPort(61613)
                .setSystemLogin(env.getProperty("spring.rabbitmq.username"))
                .setSystemPasscode(env.getProperty("spring.rabbitmq.password"))
                .setClientLogin(env.getProperty("spring.rabbitmq.username"))
                .setClientPasscode(env.getProperty("spring.rabbitmq.password"));
    }

    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
        registry
                .addEndpoint("/stomp/chat")// 엔드포인트
                .setAllowedOriginPatterns("*").withSockJS()//왜 http로 해놨냐.. 당연히 안되지.
                ;
        registry
                .addEndpoint("/stomp/ws")
                .setAllowedOriginPatterns("*");
    }
//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(stompHandler);
//    }

}
package com.example.monolithmoija.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableRabbit
public class RabbitConfig {
    private static final String FANOUT_EXCHANGE_NAME = "notify.fanout";
    @Autowired
    Environment env;
    private static final String CHAT_QUEUE_NAME = "chat.queue";
    private static final String CHAT_EXCHANGE_NAME = "chat.exchange";
    private static final String NOTIFY_EXCHANGE_NAME = "notify.exchange";

    private static final String NOTIFY_QUEUE_NAME = "notify.queue";

    private static final String CHAT_ROUTING_KEY = "room.*";
    private static final String NOTIFY_ROUTING_KEY = "user.*";

    //Queue 등록
    @Bean("chatQueue")
    public Queue chatQueue(){ return new Queue(CHAT_QUEUE_NAME, true); }
    @Bean("notifyQueue")
    public Queue notifyQueue() { return
            new Queue(NOTIFY_QUEUE_NAME, true); }

     //Exchange 등록
    @Bean("chatExchange")
    public TopicExchange chatExchange(){ return new TopicExchange(CHAT_EXCHANGE_NAME); }
    @Bean("notifyExchange")
    public DirectExchange notifyExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("alternate-exchange", FANOUT_EXCHANGE_NAME);
        return new DirectExchange(NOTIFY_EXCHANGE_NAME,true,false,args); }
    @Bean("notifyFanoutExchange")
    public FanoutExchange notifyFanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE_NAME); }

    @Bean("chatBinding")
    public Binding chatBinding(Queue chatQueue, TopicExchange chatExchange) {
        return BindingBuilder.bind(chatQueue).to(chatExchange).with(CHAT_ROUTING_KEY);
    }
    @Bean("notifyBinding")
    public Binding notifyBinding(Queue notifyQueue, DirectExchange notifyExchange) {
        return BindingBuilder.bind(notifyQueue).to(notifyExchange).with(NOTIFY_ROUTING_KEY);
    }



    /* messageConverter를 커스터마이징 하기 위해 Bean 새로 등록 */
    @Bean("chatRabbitTemplate")
    public RabbitTemplate chatRabbitTemplate(){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        rabbitTemplate.setRoutingKey(CHAT_QUEUE_NAME);
        return rabbitTemplate;
    }

    @Bean("notifyRabbitTemplate")
    public RabbitTemplate notifyRabbitTemplate(){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        rabbitTemplate.setRoutingKey(NOTIFY_QUEUE_NAME);
        return rabbitTemplate;
    }




    //controller에 있는거랑 겹치는 것 rabbitListener로 리스너를 설정하든가 / bean으로 설정하든가
//    @Bean
//    public SimpleMessageListenerContainer container(MessageListenerAdapter listenerAdapter){
//        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory());
//        container.setQueueNames(CHAT_QUEUE_NAME);
//        container.setMessageListener(listenerAdapter);
//        return container;
//    }

    //Spring에서 자동생성해주는 ConnectionFactory는 SimpleConnectionFactory인가? 그건데
    //여기서 사용하는 건 CachingConnectionFacotry라 새로 등록해줌
    @Bean
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost(env.getProperty("spring.rabbitmq.host"));
        factory.setPort(5672);
        factory.setVirtualHost("/");
        factory.setUsername(env.getProperty("spring.rabbitmq.username"));
        factory.setPassword(env.getProperty("spring.rabbitmq.password"));

        return factory;
    }

    //시간 포맷을 읽어들여 변한하기 위한 컨버터를 사용한다.
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter(){
        ObjectMapper objectMapper = new ObjectMapper();
        // previous
        // objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);

        // 타임스탬프로 쓰지 않도록 설정
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.registerModule(dateTimeModule());

        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);

        return converter;
    }

    @Bean
    public JavaTimeModule dateTimeModule(){
        // JavaTimeModule을 등록하여 Java 8 날짜/시간 API 지원
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
        ZonedDateTimeSerializer zonedDateTimeSerializer = new ZonedDateTimeSerializer(dateTimeFormatter);
        javaTimeModule.addSerializer(ZonedDateTime.class, zonedDateTimeSerializer);
        return javaTimeModule;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

}

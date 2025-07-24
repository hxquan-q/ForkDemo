package com.hxq.common.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置类
 */
@Configuration
public class RabbitMQConfig {

    /**
     * 订单队列名称
     */
    public static final String ORDER_QUEUE = "order.queue";

    /**
     * 订单交换机名称
     */
    public static final String ORDER_EXCHANGE = "order.exchange";

    /**
     * 订单路由键
     */
    public static final String ORDER_ROUTING_KEY = "order.routing.key";

    /**
     * 订单延迟队列名称（用于处理超时未支付订单）
     */
    public static final String ORDER_DELAY_QUEUE = "order.delay.queue";

    /**
     * 订单延迟交换机名称
     */
    public static final String ORDER_DELAY_EXCHANGE = "order.delay.exchange";

    /**
     * 订单延迟路由键
     */
    public static final String ORDER_DELAY_ROUTING_KEY = "order.delay.routing.key";

    /**
     * 声明消息转换器
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 自定义RabbitTemplate，设置消息转换器
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

    /**
     * 声明订单队列
     */
    @Bean
    public Queue orderQueue() {
        return new Queue(ORDER_QUEUE, true);
    }

    /**
     * 声明订单交换机
     */
    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(ORDER_EXCHANGE);
    }

    /**
     * 将订单队列和订单交换机绑定
     */
    @Bean
    public Binding bindingOrder() {
        return BindingBuilder.bind(orderQueue()).to(orderExchange()).with(ORDER_ROUTING_KEY);
    }

    /**
     * 声明订单延迟队列
     */
    @Bean
    public Queue orderDelayQueue() {
        return new Queue(ORDER_DELAY_QUEUE, true);
    }

    /**
     * 声明订单延迟交换机
     */
    @Bean
    public DirectExchange orderDelayExchange() {
        return new DirectExchange(ORDER_DELAY_EXCHANGE);
    }

    /**
     * 将订单延迟队列和订单延迟交换机绑定
     */
    @Bean
    public Binding bindingOrderDelay() {
        return BindingBuilder.bind(orderDelayQueue()).to(orderDelayExchange()).with(ORDER_DELAY_ROUTING_KEY);
    }
}
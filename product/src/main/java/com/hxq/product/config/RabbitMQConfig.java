package com.hxq.product.config;

import com.hxq.product.constant.RabbitConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置类
 */
@Configuration
@Slf4j
public class RabbitMQConfig {

    // 直接引用本地常量
    public static final String ORDER_EXCHANGE = RabbitConstants.ORDER_EXCHANGE;
    public static final String ORDER_ROUTING_KEY = RabbitConstants.ORDER_ROUTING_KEY;

    /**
     * 配置自定义RabbitTemplate
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        // 使用JSON消息转换器
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());

        // 消息发送到交换机的回调
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                if (ack) {
                    log.info("消息发送到交换机成功: correlationData={}", correlationData);
                } else {
                    log.error("消息发送到交换机失败: correlationData={}, cause={}", correlationData, cause);
                }
            }
        });

        // 消息从交换机发送到队列失败的回调
        rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {
            @Override
            public void returnedMessage(ReturnedMessage returned) {
                log.error("消息从交换机发送到队列失败: exchange={}, routingKey={}, message={}, replyCode={}, replyText={}",
                        returned.getExchange(), returned.getRoutingKey(), new String(returned.getMessage().getBody()),
                        returned.getReplyCode(), returned.getReplyText());
            }
        });

        return rabbitTemplate;
    }
}
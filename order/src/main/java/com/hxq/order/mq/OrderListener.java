package com.hxq.order.mq;

import com.hxq.order.config.RabbitMQConfig;
import com.hxq.order.dto.SeckillMessage;
import com.hxq.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * 订单消息监听器
 */
@Component
@Slf4j
public class OrderListener {

    @Resource
    private OrderService orderService;

    /**
     * 监听订单消息
     */
    @RabbitListener(queues = RabbitMQConfig.ORDER_QUEUE)
    public void handleOrderMessage(SeckillMessage message, Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        log.info("收到订单消息: {}", message);

        try {
            // 创建订单
            Long orderId = orderService.createOrder(message);

            if (orderId != null) {
                // 确认消息
                channel.basicAck(tag, false);
                log.info("订单创建成功，确认消息, orderId: {}", orderId);
            } else {
                // 拒绝消息，不重新入队
                channel.basicReject(tag, false);
                log.error("订单创建失败，拒绝消息");
            }
        } catch (Exception e) {
            log.error("处理订单消息异常", e);
            try {
                // 拒绝消息，重新入队
                channel.basicNack(tag, false, true);
            } catch (IOException ex) {
                log.error("拒绝消息失败", ex);
            }
        }
    }
}
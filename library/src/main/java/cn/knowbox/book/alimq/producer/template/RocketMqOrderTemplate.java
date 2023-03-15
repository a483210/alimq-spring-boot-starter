package cn.knowbox.book.alimq.producer.template;

import cn.knowbox.book.alimq.properties.RocketMqProperties;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.bean.OrderProducerBean;

import cn.knowbox.book.alimq.message.IMessageEvent;
import cn.knowbox.book.alimq.message.RocketMqMessage;
import cn.knowbox.book.alimq.parser.MqParser;
import cn.knowbox.book.alimq.producer.RocketMqOrderType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 顺序消息生产者
 *
 * @author Created by gold on 2019/10/4 15:27
 */
@Slf4j
@AllArgsConstructor
public class RocketMqOrderTemplate {

    private final MqParser mqParser;
    private final OrderProducerBean orderProducer;
    private final RocketMqProperties properties;

    /**
     * 同步发送全局顺序消息
     *
     * @param event  event事件
     * @param domain 对象
     */
    public SendResult send(IMessageEvent event, Object domain) {
        return send(event, domain, RocketMqOrderType.GLOBAL);
    }

    /**
     * 同步发送顺序消息
     *
     * @param event  event事件
     * @param domain 对象
     */
    public SendResult send(IMessageEvent event, Object domain, RocketMqOrderType orderType) {
        return send(new RocketMqMessage(event, properties.getTopicSuffix(), properties.getTagSuffix(), format(domain)), orderType);
    }

    /**
     * 同步发送全局顺序消息
     *
     * @param event 事件
     */
    public SendResult send(RocketMqMessage event) {
        return send(event, RocketMqOrderType.GLOBAL);
    }

    /**
     * 同步发送顺序消息
     *
     * @param event     事件
     * @param orderType 类型
     */
    public SendResult send(RocketMqMessage event, RocketMqOrderType orderType) {
        String sharding;
        switch (orderType) {
            case TOPIC:
                sharding = "#" + event.getTopic() + "#";
                break;
            case TAG:
                sharding = "#" + event.getTopic() + "#" + event.getTag() + "#";
                break;
            case GLOBAL:
            default:
                sharding = "#global#";
                break;
        }
        return send(event, sharding);
    }

    /**
     * 同步发送顺序消息
     *
     * @param event  event事件
     * @param domain 对象
     */
    public SendResult send(IMessageEvent event, Object domain, String sharding) {
        return send(new RocketMqMessage(event, properties.getTopicSuffix(), properties.getTagSuffix(), format(domain)), sharding);
    }

    /**
     * 同步发送顺序消息
     *
     * @param event    事件
     * @param sharding 分区顺序消息中区分不同分区的关键字段，sharding key 于普通消息的 key 是完全不同的概念。
     */
    public SendResult send(RocketMqMessage event, String sharding) {
        if (properties.getProducer().isLogging()) {
            log.info("sendOrder [message：{}, sharding：{}]", event, sharding);
        }

        return orderProducer.send(RocketMqTemplate.createMessage(orderProducer.getProperties(), event), sharding);
    }

    private String format(Object domain) {
        return RocketMqTemplate.format(mqParser, domain);
    }
}

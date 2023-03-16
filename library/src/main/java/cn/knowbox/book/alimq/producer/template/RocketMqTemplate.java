package cn.knowbox.book.alimq.producer.template;

import cn.knowbox.book.alimq.consts.RocketMqConstants;
import cn.knowbox.book.alimq.error.RocketMqException;
import cn.knowbox.book.alimq.message.IMessageEvent;
import cn.knowbox.book.alimq.message.RocketMqMessage;
import cn.knowbox.book.alimq.parser.MqParser;
import cn.knowbox.book.alimq.producer.RocketMqClusterType;
import cn.knowbox.book.alimq.properties.RocketMqProperties;
import com.aliyun.openservices.ons.api.*;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.util.SerializationUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

/**
 * 普通消息、定时消息、延迟消息生产者
 *
 * @author Created by gold on 2019/10/4 15:27
 */
@Slf4j
@AllArgsConstructor
public class RocketMqTemplate {

    private static final long DELAY_DAY_7 = 7 * 24 * 60 * 60 * 1000L;

    private final MqParser mqParser;
    private final ProducerBean producer;
    private final RocketMqProperties properties;

    /**
     * 单向发送
     *
     * @param topic  topic
     * @param domain 对象
     */
    public void sendOneway(String topic, String tag, Object domain) {
        sendOneway(new RocketMqMessage(topic, tag, format(domain)));
    }

    /**
     * 单向发送
     *
     * @param event  event事件
     * @param domain 对象
     */
    public void sendOneway(IMessageEvent event, Object domain) {
        sendOneway(new RocketMqMessage(event, properties.getTopicSuffix(), properties.getTagSuffix(), format(domain)));
    }

    /**
     * 单向发送，不等待回执
     * 适用于某些耗时非常短，但对可靠性要求并不高的场景，例如日志收集
     *
     * @param event 事件
     */
    public void sendOneway(RocketMqMessage event) {
        Message message = createMessage(producer.getProperties(), event);

        producer.sendOneway(message);
    }

    /**
     * 同步发送
     *
     * @param topic  topic
     * @param domain 对象
     */
    public SendResult send(String topic, String tag, Object domain) {
        return send(new RocketMqMessage(topic, tag, format(domain)));
    }

    /**
     * 同步发送
     *
     * @param topic  topic
     * @param domain 对象
     */
    public SendResult send(String topic, String tag, Object domain, long delay) {
        return send(new RocketMqMessage(topic, tag, format(domain)), delay);
    }

    /**
     * 同步发送
     *
     * @param event  event事件
     * @param domain 对象
     */
    public SendResult send(IMessageEvent event, Object domain) {
        return send(new RocketMqMessage(event, properties.getTopicSuffix(), properties.getTagSuffix(), format(domain)));
    }

    /**
     * 同步发送
     *
     * @param event  event事件
     * @param domain 对象
     * @param delay  延迟
     */
    public SendResult send(IMessageEvent event, Object domain, long delay) {
        return send(new RocketMqMessage(event, properties.getTopicSuffix(), properties.getTagSuffix(), format(domain)), delay);
    }

    /**
     * 同步发送
     *
     * @param event 事件
     */
    public SendResult send(RocketMqMessage event) {
        return sendTime(event, 0L);
    }

    /**
     * 同步发送，延迟发送
     *
     * @param event 事件
     * @param delay 延迟时间
     */
    public SendResult send(RocketMqMessage event, long delay) {
        if (delay <= 0) {
            return sendTime(event, 0L);
        }

        return sendTime(event, System.currentTimeMillis() + delay);
    }

    /**
     * 同步发送，定时发送
     *
     * @param event 事件
     * @param date  发送时间
     */
    public SendResult send(RocketMqMessage event, Date date) {
        return sendTime(event, date.getTime());
    }

    /**
     * 同步发送，定时发送
     *
     * @param event 事件
     * @param date  发送时间
     */
    public SendResult send(RocketMqMessage event, LocalDateTime date) {
        long startTime = date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        return sendTime(event, startTime);
    }

    /**
     * 同步发送，延迟发送
     *
     * @param event     事件
     * @param startTime 发送的时间点
     */
    public SendResult sendTime(RocketMqMessage event, long startTime) {
        checkStartTime(startTime);

        if (properties.getProducer().isLogging()) {
            log.info("send [message：{}]", event);
        }

        Message message = createMessage(producer.getProperties(), event);

        if (startTime > 0) {
            message.setStartDeliverTime(startTime);
        }

        return producer.send(message);
    }

    /**
     * 异步发送
     *
     * @param topic  topic
     * @param domain 对象
     */
    public CompletableFuture<SendResult> sendAsync(String topic, String tag, Object domain) {
        return sendAsync(new RocketMqMessage(topic, tag, format(domain)));
    }

    /**
     * 异步发送
     *
     * @param topic  topic
     * @param domain 对象
     * @param delay  延迟时间
     */
    public CompletableFuture<SendResult> sendAsync(String topic, String tag, Object domain, long delay) {
        return sendAsync(new RocketMqMessage(topic, tag, format(domain)), delay);
    }

    /**
     * 异步发送
     *
     * @param event  event事件
     * @param domain 对象
     */
    public CompletableFuture<SendResult> sendAsync(IMessageEvent event, Object domain) {
        return sendAsync(new RocketMqMessage(event, properties.getTopicSuffix(), properties.getTagSuffix(), format(domain)));
    }

    /**
     * 异步发送
     *
     * @param event  event事件
     * @param domain 对象
     * @param delay  延迟时间
     */
    public CompletableFuture<SendResult> sendAsync(IMessageEvent event, Object domain, long delay) {
        return sendAsync(new RocketMqMessage(event, properties.getTopicSuffix(), properties.getTagSuffix(), format(domain)), delay);
    }

    /**
     * 异步发送
     *
     * @param event 事件
     */
    public CompletableFuture<SendResult> sendAsync(RocketMqMessage event) {
        return sendAsyncTime(event, 0L);
    }

    /**
     * 异步发送，延迟发送
     *
     * @param event 事件
     * @param delay 延迟时间
     */
    public CompletableFuture<SendResult> sendAsync(RocketMqMessage event, long delay) {
        if (delay <= 0) {
            return sendAsyncTime(event, 0L);
        }

        return sendAsyncTime(event, System.currentTimeMillis() + delay);
    }

    /**
     * 异步发送，定时发送
     *
     * @param event 事件
     * @param date  发送时间
     */
    public CompletableFuture<SendResult> sendAsync(RocketMqMessage event, Date date) {
        return sendAsyncTime(event, date.getTime());
    }

    /**
     * 异步发送，定时发送
     *
     * @param event 事件
     * @param date  发送时间
     */
    public CompletableFuture<SendResult> sendAsync(RocketMqMessage event, LocalDateTime date) {
        long startTime = date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        return sendAsyncTime(event, startTime);
    }

    /**
     * 异步发送，延迟发送
     *
     * @param event     事件
     * @param startTime 发送的时间点
     */
    public CompletableFuture<SendResult> sendAsyncTime(RocketMqMessage event, long startTime) {
        checkStartTime(startTime);

        if (properties.getProducer().isLogging()) {
            log.info("sendAsync [message：{}]", event);
        }

        CompletableFuture<SendResult> future = new CompletableFuture<>();

        Message message = createMessage(producer.getProperties(), event);

        if (startTime > 0) {
            message.setStartDeliverTime(startTime);
        }

        producer.sendAsync(message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                future.complete(sendResult);
            }

            @Override
            public void onException(OnExceptionContext context) {
                future.completeExceptionally(new RocketMqException(context));
            }
        });

        return future;
    }

    private String format(Object domain) {
        return format(mqParser, domain);
    }

    static Message createMessage(Properties properties, RocketMqMessage event) {
        if (event == null) {
            throw new RocketMqException("事件不允许为空！");
        }
        if (ObjectUtils.isEmpty(event.getTopic())) {
            throw new RocketMqException("Topic不允许为空！");
        }
        if (event.getDomain() == null) {
            throw new RocketMqException("Domain不允许为空！");
        }

        int clusterType = (int) properties.getOrDefault(RocketMqConstants.CLUSTER_TYPE, RocketMqClusterType.ALIYUN.getType());

        Message message;
        if (RocketMqClusterType.ALIYUN.getType() == clusterType) {
            message = new Message(event.getTopic(), event.getTag(), SerializationUtils.serialize(event));
        } else {
            message = new ApacheMessage(event.getTopic(), event.getTag(), SerializationUtils.serialize(event));
        }

        message.setKey(event.generateTxId());

        return message;
    }

    static String format(MqParser mqParser, Object domain) {
        if (domain == null) {
            throw new RocketMqException("domain不能为空！");
        }

        if (domain instanceof String) {
            return (String) domain;
        }

        String json = mqParser.format(domain);
        if (ObjectUtils.isEmpty(json)) {
            throw new RocketMqException("domain不是Json！");
        }

        return json;
    }

    private static void checkStartTime(long startTime) {
        if (startTime == 0) {
            return;
        }
        long nowTime = System.currentTimeMillis();
        if (startTime <= nowTime) {
            throw new RocketMqException("发送事件不能小于当前时间！");
        } else if (startTime > nowTime + DELAY_DAY_7) {
            throw new RocketMqException("发送事件不能大于7天时间！");
        }
    }
}

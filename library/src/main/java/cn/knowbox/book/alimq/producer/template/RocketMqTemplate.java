package cn.knowbox.book.alimq.producer.template;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.OnExceptionContext;
import com.aliyun.openservices.ons.api.SendCallback;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.bean.ProducerBean;

import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

import cn.knowbox.book.alimq.error.RocketMqException;
import cn.knowbox.book.alimq.message.IMessageEvent;
import cn.knowbox.book.alimq.message.RocketMqMessage;
import cn.knowbox.book.alimq.parser.MqParser;
import lombok.extern.slf4j.Slf4j;

/**
 * 普通消息、定时消息、延迟消息生产者
 *
 * @author Created by gold on 2019/10/4 15:27
 */
@Slf4j
public class RocketMqTemplate {

    private static final long DELAY_DAY_7 = 7 * 24 * 60 * 60 * 1000;

    private MqParser mqParser;
    private ProducerBean producer;

    public RocketMqTemplate(MqParser mqParser, ProducerBean producer) {
        this.mqParser = mqParser;
        this.producer = producer;
    }

    /**
     * 单向发送
     *
     * @param topic  topic
     * @param domain 对象
     */
    public void sendOneway(String topic, Object domain) {
        sendOneway(new RocketMqMessage(topic, format(domain)));
    }

    /**
     * 单向发送
     *
     * @param event  event事件
     * @param domain 对象
     */
    public void sendOneway(IMessageEvent event, Object domain) {
        sendOneway(new RocketMqMessage(event, format(domain)));
    }

    /**
     * 单向发送，不等待回执
     * 适用于某些耗时非常短，但对可靠性要求并不高的场景，例如日志收集
     *
     * @param event 事件
     */
    public void sendOneway(RocketMqMessage event) {
        Message message = createMessage(event);

        producer.sendOneway(message);
    }

    /**
     * 同步发送
     *
     * @param topic  topic
     * @param domain 对象
     */
    public SendResult send(String topic, Object domain) {
        return send(new RocketMqMessage(topic, format(domain)));
    }

    /**
     * 同步发送
     *
     * @param event  event事件
     * @param domain 对象
     */
    public SendResult send(IMessageEvent event, Object domain) {
        return send(new RocketMqMessage(event, format(domain)));
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
     * @param event 事件
     * @param delay 延迟时间
     */
    public SendResult send(RocketMqMessage event, long delay) {
        return sendTime(event, System.currentTimeMillis() + delay);
    }

    /**
     * 同步发送，延迟发送
     *
     * @param event     事件
     * @param startTime 延迟时间
     */
    public SendResult sendTime(RocketMqMessage event, long startTime) {
        checkStartTime(startTime);

        log.info("send [message：{}]", event.toString());

        Message message = createMessage(event);

        if (startTime > 0) {
            message.setStartDeliverTime(System.currentTimeMillis() + startTime);
        }

        return producer.send(message);
    }

    /**
     * 异步发送
     *
     * @param topic  topic
     * @param domain 对象
     */
    public CompletableFuture<SendResult> sendAsync(String topic, Object domain) {
        return sendAsync(new RocketMqMessage(topic, format(domain)));
    }

    /**
     * 异步发送
     *
     * @param event  event事件
     * @param domain 对象
     */
    public CompletableFuture<SendResult> sendAsync(IMessageEvent event, Object domain) {
        return sendAsync(new RocketMqMessage(event, format(domain)));
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
     * @param event 事件
     * @param delay 延迟时间
     */
    public CompletableFuture<SendResult> sendAsync(RocketMqMessage event, long delay) {
        return sendAsyncTime(event, System.currentTimeMillis() + delay);
    }

    /**
     * 异步发送，延迟发送
     *
     * @param event     事件
     * @param startTime 发送时间
     */
    public CompletableFuture<SendResult> sendAsyncTime(RocketMqMessage event, long startTime) {
        checkStartTime(startTime);

        log.info("sendAsync [message：{}]", event.toString());

        CompletableFuture<SendResult> future = new CompletableFuture<>();

        Message message = createMessage(event);

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

    static Message createMessage(RocketMqMessage event) {
        if (event == null) {
            throw new RocketMqException("事件不允许为空！");
        }
        if (StringUtils.isEmpty(event.getTopic())) {
            throw new RocketMqException("Topic不允许为空！");
        }
        if (event.getDomain() == null) {
            throw new RocketMqException("Domain不允许为空！");
        }

        Message message = new Message(event.getTopic(), event.getTag(), SerializationUtils.serialize(event));
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
        if (StringUtils.isEmpty(json)) {
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

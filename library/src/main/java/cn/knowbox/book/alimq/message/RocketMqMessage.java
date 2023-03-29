package cn.knowbox.book.alimq.message;

import cn.knowbox.book.alimq.utils.RocketMqUtils;
import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.UUID;

/**
 * MQ统一事件对象，用在跨业务整合中
 *
 * @author Created by gold on 2019/10/4 15:26
 */
@Data
public class RocketMqMessage implements Serializable {

    /**
     * 创建消息
     *
     * @param topic  topic
     * @param domain 消息
     * @return message
     */
    public static RocketMqMessage create(String topic, String tag, String domain) {
        return new RocketMqMessage(topic, tag, domain);
    }

    /**
     * 创建消息
     *
     * @param event  事件
     * @param domain 消息
     * @return message
     */
    public static RocketMqMessage create(IMessageEvent event, String topicSuffix, String tagSuffix, String domain) {
        return new RocketMqMessage(event, topicSuffix, tagSuffix, domain);
    }

    public RocketMqMessage() {
    }

    public RocketMqMessage(String topic, String tag, String domain) {
        this.topic = topic;
        this.tag = tag;
        this.domain = domain;
    }

    public RocketMqMessage(IMessageEvent event, String topicSuffix, String tagSuffix, String domain) {
        this(RocketMqUtils.generateTopic(event.getTopic(), topicSuffix),
                RocketMqUtils.generateTag(event.getTags(), tagSuffix),
                domain);
    }

    /**
     * 事件序列ID
     */
    private String txId;

    /**
     * topic name
     */
    private String topic;

    /**
     * topic tag
     */
    private String tag;

    /**
     * 对象
     */
    private String domain;

    /**
     * 传递的领域对象的唯一标识,用来构建消息的唯一标识,不检测重复,可以为空,不影响消息收发
     */
    private String domainKey;

    /**
     * 事件创建时间
     */
    private long createdDate = System.currentTimeMillis();

    /**
     * 生成TxId
     */
    public String generateTxId() {
        if (txId == null) {
            txId = getTopic() + ":" + getTag();
            if (ObjectUtils.isEmpty(domainKey)) {
                txId = txId + getCreatedDate() + ":" + UUID.randomUUID();
            } else {
                txId = txId + domainKey;
            }
        }
        return txId;
    }
}

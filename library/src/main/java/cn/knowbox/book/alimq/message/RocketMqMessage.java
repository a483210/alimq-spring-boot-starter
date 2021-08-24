package cn.knowbox.book.alimq.message;

import cn.knowbox.book.alimq.utils.RocketMqUtil;
import com.aliyun.openservices.shade.io.netty.util.internal.StringUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

/**
 * MQ统一事件对象，用在跨业务整合中
 *
 * @author Created by gold on 2019/10/4 15:26
 */
@Data
public class RocketMqMessage implements Serializable {

    private static final long serialVersionUID = -2624253925403159396L;

    /**
     * 创建消息
     *
     * @param topic  topic
     * @param domain 消息
     * @return message
     */
    public static RocketMqMessage create(String topic, String domain) {
        return new RocketMqMessage(topic, domain);
    }

    /**
     * 创建消息
     *
     * @param event  事件
     * @param domain 消息
     * @return message
     */
    public static RocketMqMessage create(IMessageEvent event, String domain) {
        return new RocketMqMessage(event, domain);
    }

    public RocketMqMessage() {
    }

    public RocketMqMessage(String topic, String domain) {
        this.topic = topic;
        this.domain = domain;
    }

    public RocketMqMessage(IMessageEvent event, String domain) {
        this(event.getTopic(), domain);
        this.tag = RocketMqUtil.generateTag(event.getTags());
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
            txId = String.format("%s:%s:", getTopic(), getTag());
            if (StringUtil.isNullOrEmpty(domainKey)) {
                txId = String.format("%s%s:%s", txId, getCreatedDate(), UUID.randomUUID().toString());
            } else {
                txId = txId + domainKey;
            }
        }
        return txId;
    }
}

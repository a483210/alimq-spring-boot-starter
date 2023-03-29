package cn.knowbox.book.alimq.message;

import lombok.Data;

import java.io.Serializable;

/**
 * 事务处理类型
 *
 * @author Created by gold on 2019/10/5 16:39
 */
@Data
public class TransactionMessage<T> implements Serializable {

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
    private T domain;

    /**
     * 传递的领域对象的唯一标识,用来构建消息的唯一标识,不检测重复,可以为空,不影响消息收发
     */
    private String domainKey;

    /**
     * 事件创建时间
     */
    private long createdDate;

    /**
     * hash
     */
    private long hashId;

    /**
     * 值
     */
    private Object arg;

    public TransactionMessage(RocketMqMessage rocketMqMessage, T domain, long hashId, Object arg) {
        this.txId = rocketMqMessage.generateTxId();
        this.topic = rocketMqMessage.getTopic();
        this.tag = rocketMqMessage.getTag();
        this.domain = domain;
        this.domainKey = rocketMqMessage.getDomainKey();
        this.createdDate = rocketMqMessage.getCreatedDate();
        this.hashId = hashId;
        this.arg = arg;
    }
}

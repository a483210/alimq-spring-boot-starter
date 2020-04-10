package cn.knowbox.book.alimq.message;

import cn.knowbox.book.alimq.utils.RocketMqUtil;
import lombok.Data;

/**
 * 事务处理类型
 *
 * @author Created by gold on 2019/10/5 16:39
 */
@Data
public class TransactionMessage<T> {

    private String txId;
    private String topic;
    private String tag;
    private T domain;
    private String domainKey;
    private long createdDate;
    private long hashId;
    private Object arg;

    public TransactionMessage(RocketMqMessage rocketMqMessage, T domain, long hashId, Object arg) {
        this.txId = rocketMqMessage.getTxId();
        this.topic = rocketMqMessage.getTopic();
        this.tag = rocketMqMessage.getTag();
        this.domain = domain;
        this.domainKey = rocketMqMessage.getDomainKey();
        this.createdDate = rocketMqMessage.getCreatedDate();
        this.hashId = hashId;
        this.arg = arg;
    }

}

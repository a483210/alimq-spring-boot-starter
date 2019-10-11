package cn.knowbox.book.alimq.message;

import com.aliyun.openservices.shade.io.netty.util.internal.StringUtil;

import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.UUID;

import cn.knowbox.book.alimq.error.RocketMqException;
import cn.knowbox.book.alimq.utils.RocketMqUtil;
import lombok.Data;

/**
 * MQ统一事件对象，用在跨业务整合中
 *
 * @author Created by gold on 2019/10/4 15:26
 */
@Data
public class RocketMqMessage implements Serializable {

    private static final long serialVersionUID = -2624253925403159396L;

    public RocketMqMessage() {
    }

    public RocketMqMessage(String topic, Object domain) {
        this.topic = topic;
        if (domain instanceof String) {
            this.domain = (String) domain;
        } else {
            this.domain = RocketMqUtil.toJson(domain);
            if (StringUtils.isEmpty(domain)) {
                throw new RocketMqException("domain不是Json！");
            }
        }
    }

    public RocketMqMessage(IMessageEvent event, Object domain) {
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

package cn.knowbox.book.alimq.mq;

import cn.knowbox.book.alimq.consts.Constants;
import cn.knowbox.book.alimq.message.IMessageEvent;
import lombok.Getter;

/**
 * 消息topic枚举
 *
 * @author Created by gold on 2019/10/4 21:55
 */
@Getter
public enum MessageEvent implements IMessageEvent {
    SINGLE_MESSAGE(Constants.TOPIC_SINGLE, "v1"),
    SINGLE_MESSAGE_LIST(Constants.TOPIC_SINGLE, "list"),
    ;

    private final String topic;
    private final String[] tags;

    MessageEvent(String topic) {
        this.topic = topic;
        this.tags = null;
    }

    MessageEvent(String topic, String... tags) {
        this.topic = topic;
        this.tags = tags;
    }
}

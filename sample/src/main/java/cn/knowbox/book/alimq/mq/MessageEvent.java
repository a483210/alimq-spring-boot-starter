package cn.knowbox.book.alimq.mq;

import cn.knowbox.book.alimq.message.IMessageEvent;
import lombok.Getter;

/**
 * 消息topic枚举
 *
 * @author Created by gold on 2019/10/4 21:55
 */
@Getter
public enum MessageEvent implements IMessageEvent {
    SINGLE_MESSAGE("singleMessage", "v1"),
    SINGLE_MESSAGE_LIST("singleMessage", "list"),
    ;

    private String topic;
    private String[] tags;

    MessageEvent(String topic) {
        this.topic = topic;
    }

    MessageEvent(String topic, String... tags) {
        this.topic = topic;
        this.tags = tags;
    }
}

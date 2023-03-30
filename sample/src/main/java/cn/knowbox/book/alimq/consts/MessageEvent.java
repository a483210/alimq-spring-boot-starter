package cn.knowbox.book.alimq.consts;

import cn.knowbox.book.alimq.message.IMessageEvent;
import lombok.Getter;

/**
 * 消息topic枚举
 *
 * @author Created by gold on 2019/10/4 21:55
 */
@Getter
public enum MessageEvent implements IMessageEvent {

    /**
     * 私聊消息
     */
    SINGLE_MESSAGE(Constants.Topic.SINGLE, Constants.Tag.SINGLE),
    /**
     * 列表消息
     */
    SINGLE_LIST_MESSAGE(Constants.Topic.SINGLE, Constants.Tag.LIST),
    /**
     * 延迟消息
     */
    DELAY_MESSAGE(Constants.Topic.DELAY, Constants.Tag.DELAY),
    /**
     * 顺序消息
     */
    ORDER_MESSAGE(Constants.Topic.ORDER, Constants.Tag.ORDER),
    /**
     * 事务消息
     */
    TRANSACTION_MESSAGE(Constants.Topic.TRANSACTION, Constants.Tag.TRANSACTION),
    ;

    private final String topic;
    private final String[] tags;

    MessageEvent(String topic, String... tags) {
        this.topic = topic;
        this.tags = tags;
    }
}

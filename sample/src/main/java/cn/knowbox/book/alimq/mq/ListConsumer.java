package cn.knowbox.book.alimq.mq;

import java.util.List;

import cn.knowbox.book.alimq.annotation.RocketMqConsume;
import cn.knowbox.book.alimq.consumer.RocketMqListener;
import cn.knowbox.book.alimq.model.SingleMessage;
import lombok.extern.slf4j.Slf4j;

/**
 * 私聊消费者
 *
 * @author Created by gold on 2019/10/5 14:34
 */
@Slf4j
@RocketMqConsume(groupId = "GID_app_user", topic = "singleMessage", tag = "list", reconsumeFor = IllegalArgumentException.class)
public class ListConsumer implements RocketMqListener<List<SingleMessage>> {

    @Override
    public void onMessage(List<SingleMessage> messages) {
        log.info("list {}", messages);
    }

}
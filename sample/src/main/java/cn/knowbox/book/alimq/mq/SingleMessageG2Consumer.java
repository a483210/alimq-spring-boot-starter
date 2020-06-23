package cn.knowbox.book.alimq.mq;

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
@RocketMqConsume(groupId = "GID_app_message", topic = "singleMessage", tag = "v2", reconsumeFor = IllegalArgumentException.class)
public class SingleMessageG2Consumer implements RocketMqListener<SingleMessage> {

    @Override
    public void onMessage(SingleMessage message) {
        log.info("g2 {}", message);
    }

}
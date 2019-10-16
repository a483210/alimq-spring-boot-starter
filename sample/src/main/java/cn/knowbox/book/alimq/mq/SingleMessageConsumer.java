package cn.knowbox.book.alimq.mq;

import cn.knowbox.book.alimq.annotation.RocketMqConsume;
import cn.knowbox.book.alimq.consumer.RocketMqListener;
import cn.knowbox.book.alimq.model.SingleMessage;
import lombok.extern.log4j.Log4j2;

/**
 * 私聊消费者
 *
 * @author Created by gold on 2019/10/5 14:34
 */
@Log4j2
@RocketMqConsume(topic = "singleMessage", tag = "v1", reconsumeFor = IllegalArgumentException.class)
public class SingleMessageConsumer implements RocketMqListener<SingleMessage> {

    @Override
    public void onMessage(SingleMessage message) {
        log.info(message.toString());
    }

}
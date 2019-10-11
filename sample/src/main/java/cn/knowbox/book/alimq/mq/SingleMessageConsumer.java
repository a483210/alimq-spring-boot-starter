package cn.knowbox.book.alimq.mq;

import cn.knowbox.book.alimq.annotation.RocketMqConsume;
import cn.knowbox.book.alimq.consumer.RocketMqListener;
import cn.knowbox.book.alimq.model.SingleMessage;

/**
 * 私聊消费者
 *
 * @author Created by gold on 2019/10/5 14:34
 */
@RocketMqConsume(topic = "singleMessage", tag = "v1", reconsumeFor = IllegalArgumentException.class)
public class SingleMessageConsumer implements RocketMqListener<SingleMessage> {

    @Override
    public void onMessage(SingleMessage message) {
        System.out.println(message.toString());
    }

}
package cn.knowbox.book.alimq.mq;

import cn.knowbox.book.alimq.annotation.RocketMqConsume;
import cn.knowbox.book.alimq.consts.Constants;
import cn.knowbox.book.alimq.consumer.ConsumerListener;
import cn.knowbox.book.alimq.model.SingleMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

/**
 * 私聊消费者
 *
 * @author Created by gold on 2019/10/5 14:34
 */
@Slf4j
@RocketMqConsume(groupId = Constants.GROUP_ID_LIVE, topic = Constants.TOPIC_SINGLE, tag = "v1", reconsumeFor = IllegalArgumentException.class)
public class SingleMessageConsumer implements ConsumerListener<SingleMessage> {

    @Override
    public void onMessage(@NonNull SingleMessage message) {
        log.info("{}", message);

        if (message.getContent().toLowerCase().endsWith("reject")) {
            throw new NullPointerException("reject message");
        }
    }
}
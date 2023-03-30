package cn.knowbox.book.alimq.mq;

import cn.knowbox.book.alimq.annotation.RocketMqConsume;
import cn.knowbox.book.alimq.consts.Constants;
import cn.knowbox.book.alimq.consumer.ConsumerListener;
import cn.knowbox.book.alimq.model.SingleMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

/**
 * 延迟消息消费者
 *
 * @author Created by gold on 2023/3/29 18:09
 * @since 1.0.0
 */
@Slf4j
@RocketMqConsume(groupId = Constants.GroupId.DELAY,
        topic = Constants.Topic.DELAY,
        tag = Constants.Tag.DELAY,
        reconsumeFor = IllegalArgumentException.class)
public class DelayMessageConsumer implements ConsumerListener<SingleMessage> {

    @Override
    public void onMessage(@NonNull SingleMessage message) {
        log.info("delay {}", message);
    }
}
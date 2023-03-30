package cn.knowbox.book.alimq.mq;

import cn.knowbox.book.alimq.annotation.RocketMqConsume;
import cn.knowbox.book.alimq.consts.Constants;
import cn.knowbox.book.alimq.consumer.ConsumerListener;
import cn.knowbox.book.alimq.model.SingleMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

/**
 * 顺序消息消费者
 *
 * @author Created by gold on 2023/3/30 10:36
 * @since 1.0.0
 */
@Slf4j
@RocketMqConsume(groupId = Constants.GroupId.ORDER,
        topic = Constants.Topic.ORDER,
        tag = Constants.Tag.ORDER,
        reconsumeFor = IllegalArgumentException.class)
public class OrderMessageConsumer implements ConsumerListener<SingleMessage> {

    @Override
    public void onMessage(@NonNull SingleMessage message) {
        log.info("order {}", message);
    }
}
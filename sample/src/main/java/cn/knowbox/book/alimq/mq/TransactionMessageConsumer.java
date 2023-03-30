package cn.knowbox.book.alimq.mq;

import cn.knowbox.book.alimq.annotation.RocketMqConsume;
import cn.knowbox.book.alimq.consts.Constants;
import cn.knowbox.book.alimq.consumer.ConsumerListener;
import cn.knowbox.book.alimq.model.SingleMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

/**
 * 事务消息消费者
 *
 * @author Created by gold on 2023/3/30 10:37
 * @since 1.0.0
 */
@Slf4j
@RocketMqConsume(groupId = Constants.GroupId.TRANSACTION,
        topic = Constants.Topic.TRANSACTION,
        tag = Constants.Tag.TRANSACTION,
        reconsumeFor = IllegalArgumentException.class)
public class TransactionMessageConsumer implements ConsumerListener<SingleMessage> {

    @Override
    public void onMessage(@NonNull SingleMessage message) {
        log.info("transaction {}", message);

        if (message.getContent().toLowerCase().endsWith("reject")) {
            throw new NullPointerException("reject message");
        }
    }
}
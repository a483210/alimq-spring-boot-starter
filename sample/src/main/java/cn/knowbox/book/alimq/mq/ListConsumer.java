package cn.knowbox.book.alimq.mq;

import cn.knowbox.book.alimq.annotation.RocketMqConsume;
import cn.knowbox.book.alimq.consts.Constants;
import cn.knowbox.book.alimq.consumer.RocketMqListener;
import cn.knowbox.book.alimq.model.SingleMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import java.util.List;

/**
 * 私聊消费者
 *
 * @author Created by gold on 2019/10/5 14:34
 */
@Slf4j
@RocketMqConsume(groupId = Constants.GROUP_ID_USER, topic = Constants.TOPIC_SINGLE, tag = "list", reconsumeFor = IllegalArgumentException.class)
public class ListConsumer implements RocketMqListener<List<SingleMessage>> {

    @Override
    public void onMessage(@NonNull List<SingleMessage> messages) {
        log.info("list {}", messages);
    }
}
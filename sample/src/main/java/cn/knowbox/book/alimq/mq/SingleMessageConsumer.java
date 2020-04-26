package cn.knowbox.book.alimq.mq;

import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

import cn.knowbox.book.alimq.annotation.RocketMqConsume;
import cn.knowbox.book.alimq.consumer.RocketMqListener;
import cn.knowbox.book.alimq.model.SingleMessage;
import lombok.extern.log4j.Log4j2;

/**
 * 私聊消费者
 *
 * @author Created by gold on 2019/10/5 14:34
 */
@Validated
@Log4j2
@RocketMqConsume(groupId = "GID_app_live", topic = "singleMessage", tag = "v1", reconsumeFor = IllegalArgumentException.class)
public class SingleMessageConsumer implements RocketMqListener<SingleMessage> {

    @Override
    public void onMessage(@Valid @NonNull SingleMessage message) {
        log.info("{}", message);
    }

}
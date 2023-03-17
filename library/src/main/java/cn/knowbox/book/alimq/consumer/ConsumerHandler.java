package cn.knowbox.book.alimq.consumer;

import cn.knowbox.book.alimq.annotation.RocketMqConsume;
import cn.knowbox.book.alimq.error.RocketMqException;
import cn.knowbox.book.alimq.message.RocketMqMessage;
import cn.knowbox.book.alimq.parser.MqParser;
import cn.knowbox.book.alimq.utils.RocketMqUtils;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.SerializationUtils;

import java.lang.reflect.Type;

/**
 * mq消费处理器，消息消费逻辑处理(如果抛出异常，则重新入队列)
 *
 * @author Created by gold on 2019/10/4 15:52
 */
@Slf4j
public class ConsumerHandler<T> implements MessageListener {

    private final MqParser mqParser;

    private final Type type;
    private final ConsumerListener<T> consumerListener;
    private final Class<? extends Throwable>[] reconsumeFor;

    ConsumerHandler(MqParser mqParser, ConsumerListener<T> consumerListener, RocketMqConsume rocketMqConsume) {
        Class<?> listenerCls = AopUtils.getTargetClass(consumerListener);

        this.type = RocketMqUtils.parseType(listenerCls, ConsumerListener.class);
        if (type == null) {
            throw new RocketMqException(String.format("%s缺少泛型！", listenerCls.getSimpleName()));
        }

        this.mqParser = mqParser;
        this.consumerListener = consumerListener;
        this.reconsumeFor = rocketMqConsume.reconsumeFor();
    }

    @Override
    public Action consume(Message message, ConsumeContext context) {
        log.info("consume [message：(topic={}, tag={}, msgId={}, startDeliverTime={})]",
                message.getTopic(), message.getTag(), message.getMsgID(), message.getStartDeliverTime());

        try {
            RocketMqMessage rocketMqMessage = (RocketMqMessage) SerializationUtils.deserialize(message.getBody());
            if (rocketMqMessage == null) {
                throw new NullPointerException("rocketMqMessage null");
            }
            String domain = rocketMqMessage.getDomain();
            if (ObjectUtils.isEmpty(domain)) {
                throw new NullPointerException("domain null");
            }
            T value = mqParser.parse(domain, type);
            if (value == null) {
                throw new NullPointerException("value null");
            }

            consumerListener.onMessage(value);
            return Action.CommitMessage;
        } catch (Throwable throwable) {
            log.warn("consume message error msgId =" + message.getMsgID(), throwable);

            if (reconsumeFor.length > 0) {
                Class<?> throwableClazz = throwable.getClass();
                for (Class<? extends Throwable> cls : reconsumeFor) {
                    if (cls.isAssignableFrom(throwableClazz)) {
                        return Action.CommitMessage;
                    }
                }
            }

            return Action.ReconsumeLater;
        }
    }

}

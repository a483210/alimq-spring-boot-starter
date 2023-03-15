package cn.knowbox.book.alimq.producer.template;

import cn.knowbox.book.alimq.properties.RocketMqProperties;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.bean.TransactionProducerBean;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionExecuter;

import cn.knowbox.book.alimq.error.RocketMqException;
import cn.knowbox.book.alimq.message.IMessageEvent;
import cn.knowbox.book.alimq.message.RocketMqMessage;
import cn.knowbox.book.alimq.parser.MqParser;
import cn.knowbox.book.alimq.producer.impl.LocalTransactionCheckerImpl;
import cn.knowbox.book.alimq.producer.impl.LocalTransactionExecutorImpl;
import cn.knowbox.book.alimq.producer.intefaces.TransactionExecutor;
import cn.knowbox.book.alimq.utils.RocketMqUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 事务消息生产者
 *
 * @author Created by gold on 2019/10/4 15:38
 */
@Slf4j
@AllArgsConstructor
public class RocketMqTransactionTemplate {

    private final MqParser mqParser;
    private final TransactionProducerBean transactionProducer;
    private final RocketMqProperties properties;

    /**
     * 同步发送事务消息
     *
     * @param event    event事件
     * @param domain   对象
     * @param executor 执行器
     */
    public <T> SendResult send(IMessageEvent event, T domain, TransactionExecutor<T> executor) {
        return send(event, domain, executor, null);
    }

    /**
     * 同步发送事务消息
     *
     * @param event    event事件
     * @param domain   对象
     * @param executor 执行器
     * @param arg      额外参数
     */
    @SuppressWarnings("unchecked")
    public <T> SendResult send(IMessageEvent event, T domain, TransactionExecutor<T> executor, Object arg) {
        return send(new RocketMqMessage(event, properties.getTopicSuffix(), properties.getTagSuffix(), format(domain)),
                new LocalTransactionExecutorImpl<>(mqParser, executor, (Class<T>) domain.getClass()), arg);
    }

    /**
     * 同步发送事务消息
     *
     * @param event    事件
     * @param executor 执行器
     */
    public <T> SendResult send(RocketMqMessage event, TransactionExecutor<T> executor, Class<T> cls) {
        return send(event, executor, cls, null);
    }

    /**
     * 同步发送事务消息
     *
     * @param event    事件
     * @param executor 执行器
     * @param arg      额外参数
     */
    public <T> SendResult send(RocketMqMessage event, TransactionExecutor<T> executor, Class<T> cls, Object arg) {
        return send(event, new LocalTransactionExecutorImpl<>(mqParser, executor, cls), arg);
    }

    /**
     * 同步发送事务消息
     *
     * @param event    event事件
     * @param domain   对象
     * @param executor 执行器
     */
    public SendResult send(IMessageEvent event, Object domain, LocalTransactionExecuter executor) {
        return send(event, domain, executor, null);
    }

    /**
     * 同步发送事务消息
     *
     * @param event    event事件
     * @param domain   对象
     * @param executor 执行器
     * @param arg      额外参数
     */
    public SendResult send(IMessageEvent event, Object domain, LocalTransactionExecuter executor, Object arg) {
        return send(new RocketMqMessage(event, properties.getTopicSuffix(), properties.getTagSuffix(), format(domain)), executor, arg);
    }

    /**
     * 同步发送事务消息
     *
     * @param event    事件
     * @param executor 执行器
     */
    public SendResult send(RocketMqMessage event, LocalTransactionExecuter executor) {
        return send(event, executor, null);
    }

    /**
     * 同步发送事务消息
     *
     * @param event    事件
     * @param executor 执行器
     * @param arg      额外参数
     */
    public SendResult send(RocketMqMessage event, LocalTransactionExecuter executor, Object arg) {
        LocalTransactionCheckerImpl checkerImpl = (LocalTransactionCheckerImpl) transactionProducer.getLocalTransactionChecker();

        String checkerKey = RocketMqUtil.generateCheckerKey(event.getTopic(), event.getTag());
        if (!checkerImpl.contains(checkerKey)) {
            throw new RocketMqException(String.format("TransactionChecker[%s]未初始化！", checkerKey));
        }

        if (properties.getProducer().isLogging()) {
            log.info("sendTransaction [message：{}]", event);
        }

        return transactionProducer.send(RocketMqTemplate.createMessage(transactionProducer.getProperties(), event), executor, arg);
    }

    private String format(Object domain) {
        return RocketMqTemplate.format(mqParser, domain);
    }
}

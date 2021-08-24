package cn.knowbox.book.alimq.producer.template;

import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.bean.TransactionProducerBean;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionExecuter;

import cn.knowbox.book.alimq.error.RocketMqException;
import cn.knowbox.book.alimq.message.IMessageEvent;
import cn.knowbox.book.alimq.message.RocketMqMessage;
import cn.knowbox.book.alimq.parser.MqParser;
import cn.knowbox.book.alimq.producer.impl.LocalTransactionCheckerImpl;
import cn.knowbox.book.alimq.producer.impl.LocalTransactionExecuterImpl;
import cn.knowbox.book.alimq.producer.intefaces.TransactionExecuter;
import cn.knowbox.book.alimq.utils.RocketMqUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 事务消息生产者
 *
 * @author Created by gold on 2019/10/4 15:38
 */
@Slf4j
public class RocketMqTransactionTemplate {

    private final MqParser mqParser;
    private final TransactionProducerBean transactionProducer;

    public RocketMqTransactionTemplate(MqParser mqParser, TransactionProducerBean transactionProducer) {
        this.mqParser = mqParser;
        this.transactionProducer = transactionProducer;
    }

    /**
     * 同步发送事务消息
     *
     * @param event    event事件
     * @param domain   对象
     * @param executer 执行器
     */
    @SuppressWarnings("unchecked")
    public <T> SendResult send(IMessageEvent event, T domain, TransactionExecuter<T> executer) {
        return send(new RocketMqMessage(event, format(domain)),
                new LocalTransactionExecuterImpl<>(mqParser, executer, (Class<T>) domain.getClass()), null);
    }

    /**
     * 同步发送事务消息
     *
     * @param event    event事件
     * @param domain   对象
     * @param executer 执行器
     * @param arg      额外参数
     */
    @SuppressWarnings("unchecked")
    public <T> SendResult send(IMessageEvent event, T domain, TransactionExecuter<T> executer, Object arg) {
        return send(new RocketMqMessage(event, format(domain)),
                new LocalTransactionExecuterImpl<>(mqParser, executer, (Class<T>) domain.getClass()), arg);
    }

    /**
     * 同步发送事务消息
     *
     * @param event    事件
     * @param executer 执行器
     */
    public <T> SendResult send(RocketMqMessage event, TransactionExecuter<T> executer, Class<T> cls) {
        return send(event, new LocalTransactionExecuterImpl<>(mqParser, executer, cls), null);
    }

    /**
     * 同步发送事务消息
     *
     * @param event    事件
     * @param executer 执行器
     * @param arg      额外参数
     */
    public <T> SendResult send(RocketMqMessage event, TransactionExecuter<T> executer, Class<T> cls, Object arg) {
        return send(event, new LocalTransactionExecuterImpl<>(mqParser, executer, cls), arg);
    }

    /**
     * 同步发送事务消息
     *
     * @param event    event事件
     * @param domain   对象
     * @param executer 执行器
     */
    public SendResult send(IMessageEvent event, Object domain, LocalTransactionExecuter executer) {
        return send(new RocketMqMessage(event, format(domain)), executer, null);
    }

    /**
     * 同步发送事务消息
     *
     * @param event    event事件
     * @param domain   对象
     * @param executer 执行器
     * @param arg      额外参数
     */
    public SendResult send(IMessageEvent event, Object domain, LocalTransactionExecuter executer, Object arg) {
        return send(new RocketMqMessage(event, format(domain)), executer, arg);
    }

    /**
     * 同步发送事务消息
     *
     * @param event    事件
     * @param executer 执行器
     */
    public SendResult send(RocketMqMessage event, LocalTransactionExecuter executer) {
        return send(event, executer, null);
    }

    /**
     * 同步发送事务消息
     *
     * @param event    事件
     * @param executer 执行器
     * @param arg      额外参数
     */
    public SendResult send(RocketMqMessage event, LocalTransactionExecuter executer, Object arg) {
        LocalTransactionCheckerImpl checkerImpl = (LocalTransactionCheckerImpl) transactionProducer.getLocalTransactionChecker();

        String checkerKey = RocketMqUtil.generateCheckerKey(event.getTopic(), event.getTag());
        if (!checkerImpl.contains(checkerKey)) {
            throw new RocketMqException(String.format("TransactionChecker[%s]未初始化！", checkerKey));
        }

        log.info("sendTransaction [message：{}]", event.toString());

        return transactionProducer.send(RocketMqTemplate.createMessage(event), executer, arg);
    }

    private String format(Object domain) {
        return RocketMqTemplate.format(mqParser, domain);
    }
}

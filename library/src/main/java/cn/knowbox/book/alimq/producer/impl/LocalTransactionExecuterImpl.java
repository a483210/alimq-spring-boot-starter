package cn.knowbox.book.alimq.producer.impl;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionExecuter;
import com.aliyun.openservices.ons.api.transaction.TransactionStatus;

import org.springframework.util.SerializationUtils;

import cn.knowbox.book.alimq.message.RocketMqMessage;
import cn.knowbox.book.alimq.message.TransactionMessage;
import cn.knowbox.book.alimq.producer.intefaces.TransactionExecuter;
import cn.knowbox.book.alimq.utils.RocketMqUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 默认的本地事务执行器实现
 *
 * @author Created by gold on 2019/10/4 15:27
 */
@Slf4j
public class LocalTransactionExecuterImpl<T> implements LocalTransactionExecuter {

    private TransactionExecuter<T> transactionExecuter;
    private Class<T> clsType;

    public LocalTransactionExecuterImpl(TransactionExecuter<T> transactionExecuter, Class<T> clsType) {
        this.transactionExecuter = transactionExecuter;
        this.clsType = clsType;
    }

    @Override
    public TransactionStatus execute(Message msg, Object arg) {
        String msgId = msg.getMsgID();
        long crc32Id = RocketMqUtil.crc32Code(msg.getBody());
        TransactionStatus transactionStatus = null;

        try {
            RocketMqMessage message = (RocketMqMessage) SerializationUtils.deserialize(msg.getBody());
            if (message == null) {
                throw new NullPointerException("rocketMqMessage null");
            }

            transactionStatus = transactionExecuter.execute(new TransactionMessage<>(message, clsType, crc32Id, arg));

            log.info("TransactionExecuter success [msgId：{}, transactionStatus：{}]", msgId, transactionStatus.name());
        } catch (Throwable throwable) {
            log.error("TransactionExecuter error msgId = " + msg.getMsgID(), throwable);
        }

        if (transactionStatus == null) {
            return TransactionStatus.Unknow;
        }

        return transactionStatus;
    }

}

package cn.knowbox.book.alimq.producer.impl;

import cn.knowbox.book.alimq.error.RocketMqException;
import cn.knowbox.book.alimq.message.RocketMqMessage;
import cn.knowbox.book.alimq.message.TransactionMessage;
import cn.knowbox.book.alimq.parser.MqParser;
import cn.knowbox.book.alimq.producer.intefaces.DelegateTransactionChecker;
import cn.knowbox.book.alimq.producer.intefaces.TransactionChecker;
import cn.knowbox.book.alimq.utils.RocketMqUtils;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.transaction.TransactionStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.SerializationUtils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * 默认的本地事务状态检测器实现
 *
 * @author Created by gold on 2019/10/4 15:27
 */
@Slf4j
public class DelegateTransactionCheckerImpl implements DelegateTransactionChecker {

    private final MqParser mqParser;

    private final Map<String, TransactionChecker<?>> transactionChecks;
    private final Map<String, Type> checkTypes;

    public DelegateTransactionCheckerImpl(MqParser mqParser) {
        this.mqParser = mqParser;

        this.transactionChecks = new HashMap<>();
        this.checkTypes = new HashMap<>();
    }

    @Override
    public void put(String checkerKey, TransactionChecker<?> transactionChecker) {
        transactionChecks.put(checkerKey, transactionChecker);

        Type type = RocketMqUtils.parseType(transactionChecker.getClass(), TransactionChecker.class);
        if (type == null) {
            throw new RocketMqException(String.format("%s is missing a generic type.", transactionChecker.getClass().getSimpleName()));
        }

        checkTypes.put(checkerKey, type);
    }

    @Override
    public boolean contains(String checkerKey) {
        return transactionChecks.containsKey(checkerKey);
    }

    @Override
    public TransactionStatus check(Message msg) {
        String msgId = msg.getMsgID();
        long crc32Id = RocketMqUtils.crc32Code(msg.getBody());

        TransactionStatus transactionStatus = null;
        try {
            RocketMqMessage message = (RocketMqMessage) SerializationUtils.deserialize(msg.getBody());
            if (message == null) {
                throw new NullPointerException("rocketMqMessage null");
            }

            String checkerKey = RocketMqUtils.generateCheckerKey(message.getTopic(), message.getTag());
            TransactionChecker<?> transactionChecker = transactionChecks.get(checkerKey);
            if (transactionChecker == null) {//这里理论上不可能发生
                throw new RocketMqException(String.format("TransactionChecker[%s]未初始化！", checkerKey));
            }

            Type checkType = checkTypes.get(checkerKey);
            if (checkType == null) {
                throw new NullPointerException("rocketMqMessage checkType null");
            }
            Object value = mqParser.parse(message.getDomain(), checkType);
            if (value == null) {
                throw new NullPointerException("rocketMqMessage value null");
            }

            transactionStatus = transactionChecker.check(new TransactionMessage(message, value, crc32Id, null));

            log.info("TransactionChecker success [msgId：{}, transactionStatus：{}]", msgId, transactionStatus.name());
        } catch (Exception e) {
            log.error("TransactionChecker error msgId = " + msgId, e);
        }

        if (transactionStatus == null) {
            return TransactionStatus.Unknow;
        }

        return transactionStatus;
    }

}

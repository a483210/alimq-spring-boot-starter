package cn.knowbox.book.alimq.producer.impl;

import cn.knowbox.book.alimq.error.RocketMqException;
import cn.knowbox.book.alimq.message.RocketMqMessage;
import cn.knowbox.book.alimq.message.TransactionMessage;
import cn.knowbox.book.alimq.parser.MqParser;
import cn.knowbox.book.alimq.producer.intefaces.TransactionChecker;
import cn.knowbox.book.alimq.utils.RocketMqUtil;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionChecker;
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
public class LocalTransactionCheckerImpl implements LocalTransactionChecker {

    private final MqParser mqParser;

    private final Map<String, TransactionChecker<?>> transactionChecks;
    private final Map<String, Type> checkTypes;

    public LocalTransactionCheckerImpl(MqParser mqParser) {
        this.mqParser = mqParser;

        this.transactionChecks = new HashMap<>();
        this.checkTypes = new HashMap<>();
    }

    public void put(String checkerKey, TransactionChecker<?> transactionCheck) {
        transactionChecks.put(checkerKey, transactionCheck);

        Type type = RocketMqUtil.parseType(transactionCheck.getClass(), TransactionChecker.class);
        if (type == null) {
            throw new RocketMqException(String.format("%s缺少泛型！", transactionCheck.getClass().getSimpleName()));
        }

        checkTypes.put(checkerKey, type);
    }

    public boolean contains(String checkerKey) {
        return transactionChecks.containsKey(checkerKey);
    }

    @Override
    @SuppressWarnings("unchecked")
    public TransactionStatus check(Message msg) {
        String msgId = msg.getMsgID();
        long crc32Id = RocketMqUtil.crc32Code(msg.getBody());

        TransactionStatus transactionStatus = null;
        try {
            RocketMqMessage message = (RocketMqMessage) SerializationUtils.deserialize(msg.getBody());
            if (message == null) {
                throw new NullPointerException("rocketMqMessage null");
            }

            String checkerKey = RocketMqUtil.generateCheckerKey(message.getTopic(), message.getTag());
            TransactionChecker<?> transactionChecker = transactionChecks.get(checkerKey);
            if (transactionChecker == null) {//这里理论上不可能发生
                throw new RocketMqException(String.format("TransactionChecker[%s]未初始化！", checkerKey));
            }

            Object value = mqParser.parse(message.getDomain(), checkTypes.get(checkerKey));
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

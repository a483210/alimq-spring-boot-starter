package cn.knowbox.book.alimq.mq.checker;

import com.aliyun.openservices.ons.api.transaction.TransactionStatus;

import cn.knowbox.book.alimq.annotation.RocketMqChecker;
import cn.knowbox.book.alimq.message.TransactionMessage;
import cn.knowbox.book.alimq.model.SingleMessage;
import cn.knowbox.book.alimq.producer.intefaces.TransactionChecker;

/**
 * 单聊消息事务检查
 *
 * @author Created by gold on 2019/10/5 17:11
 */
@RocketMqChecker(topic = "singleMessage", tag = "v1")
public class SingleMessageChecker implements TransactionChecker<SingleMessage> {

    @Override
    public TransactionStatus check(TransactionMessage<SingleMessage> transactionMessage) {
        return TransactionStatus.CommitTransaction;
    }

}

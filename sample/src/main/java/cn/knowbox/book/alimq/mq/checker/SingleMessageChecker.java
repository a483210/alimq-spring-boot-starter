package cn.knowbox.book.alimq.mq.checker;

import com.aliyun.openservices.ons.api.transaction.TransactionStatus;

import cn.knowbox.book.alimq.annotation.RocketMqChecker;
import cn.knowbox.book.alimq.message.TransactionMessage;
import cn.knowbox.book.alimq.model.SingleMessage;
import cn.knowbox.book.alimq.producer.intefaces.TransactionChecker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

/**
 * 单聊消息事务检查
 *
 * @author Created by gold on 2019/10/5 17:11
 */
@Slf4j
@RocketMqChecker(topic = "singleMessage", tag = "v1")
public class SingleMessageChecker implements TransactionChecker<SingleMessage> {

    @Override
    public TransactionStatus check(@NonNull TransactionMessage<SingleMessage> transactionMessage) {
        log.info("checkTransaction {}", transactionMessage);

        return TransactionStatus.CommitTransaction;
    }

}

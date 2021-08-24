package cn.knowbox.book.alimq.producer.intefaces;

import com.aliyun.openservices.ons.api.transaction.TransactionStatus;

import org.springframework.lang.NonNull;

import cn.knowbox.book.alimq.message.TransactionMessage;

/**
 * 用作LocalTransactionExecuterImpl的构造器参数,用于执行本地事务操作
 *
 * @author Created by gold on 2019/10/4 16:00
 */
@FunctionalInterface
public interface TransactionExecuter<T> {

    /**
     * 执行
     *
     * @param message 消息
     * @return 状态
     */
    TransactionStatus execute(@NonNull TransactionMessage<T> message);

}

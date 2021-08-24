package cn.knowbox.book.alimq.producer.intefaces;

import com.aliyun.openservices.ons.api.transaction.TransactionStatus;

import org.springframework.lang.NonNull;

import cn.knowbox.book.alimq.message.TransactionMessage;

/**
 * 用作LocalTransactionExecutorImpl的构造器参数,用于执行本地事务操作
 *
 * @author Created by gold on 2019/10/4 15:28
 */
@FunctionalInterface
public interface TransactionChecker<T> {

    /**
     * 检查
     *
     * @param message 事务消息
     * @return 状态
     */
    TransactionStatus check(@NonNull TransactionMessage<T> message);

}

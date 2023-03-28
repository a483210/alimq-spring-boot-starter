package cn.knowbox.book.alimq.producer.intefaces;

import com.aliyun.openservices.ons.api.transaction.LocalTransactionChecker;

/**
 * DelegateTransactionChecker
 *
 * @author Created by gold on 2023/3/24 17:27
 * @since 1.0.0
 */
public interface DelegateTransactionChecker extends LocalTransactionChecker {

    /**
     * 添加checker
     *
     * @param checkerKey         key
     * @param transactionChecker checker
     */
    void put(String checkerKey, TransactionChecker<?> transactionChecker);

    /**
     * 判断checker是否存在
     *
     * @param checkerKey key
     * @return bool
     */
    boolean contains(String checkerKey);

}

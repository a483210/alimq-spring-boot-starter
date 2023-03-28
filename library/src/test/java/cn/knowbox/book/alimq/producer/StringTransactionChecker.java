package cn.knowbox.book.alimq.producer;

import cn.knowbox.book.alimq.message.TransactionMessage;
import cn.knowbox.book.alimq.producer.intefaces.TransactionChecker;
import com.aliyun.openservices.ons.api.transaction.TransactionStatus;
import org.springframework.lang.NonNull;

/**
 * StringTransactionChecker
 *
 * @author Created by gold on 2023/3/24 17:19
 * @since 1.0.0
 */
public class StringTransactionChecker implements TransactionChecker<String> {

    @Override
    public TransactionStatus check(@NonNull TransactionMessage<String> message) {
        return TransactionStatus.CommitTransaction;
    }
}

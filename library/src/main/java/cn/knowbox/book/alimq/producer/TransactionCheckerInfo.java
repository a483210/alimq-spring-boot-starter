package cn.knowbox.book.alimq.producer;

import cn.knowbox.book.alimq.annotation.RocketMqChecker;
import cn.knowbox.book.alimq.producer.intefaces.TransactionChecker;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * TransactionCheckerInfo
 *
 * @author Created by gold on 2023/3/6 17:46
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
public class TransactionCheckerInfo {

    /**
     * 目标
     */
    private TransactionChecker<?> target;

    /**
     * 注解
     */
    private RocketMqChecker annotation;

}

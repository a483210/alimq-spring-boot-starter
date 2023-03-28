package cn.knowbox.book.alimq.producer;

import cn.knowbox.book.alimq.annotation.RocketMqChecker;
import cn.knowbox.book.alimq.producer.intefaces.DelegateTransactionChecker;
import cn.knowbox.book.alimq.properties.RocketMqProperties;
import cn.knowbox.book.alimq.utils.RocketMqUtils;
import com.aliyun.openservices.ons.api.bean.TransactionProducerBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * 事务检查处理
 *
 * @author Created by gold on 2019/10/5 17:14
 */
public class TransactionCheckerInitializingProcessor implements InitializingBean {

    private final DelegateTransactionChecker checker;
    private final RocketMqProperties properties;
    private final TransactionCheckerPostProcessor processor;

    public TransactionCheckerInitializingProcessor(TransactionProducerBean transactionProducer,
                                                   RocketMqProperties properties,
                                                   TransactionCheckerPostProcessor processor) {
        this.checker = (DelegateTransactionChecker) transactionProducer.getLocalTransactionChecker();
        this.properties = properties;
        this.processor = processor;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        processor.checkers.forEach(info -> {
            RocketMqChecker annotation = info.getAnnotation();

            String topic = RocketMqUtils.generateTopic(annotation.topic(), properties.getTopicSuffix());
            String tag = RocketMqUtils.generateTag(annotation.tag(), properties.getTagSuffix());
            String checkerKey = RocketMqUtils.generateCheckerKey(topic, tag);

            checker.put(checkerKey, info.getTarget());
        });
    }
}

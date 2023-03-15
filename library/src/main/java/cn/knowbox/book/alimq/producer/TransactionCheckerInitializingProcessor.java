package cn.knowbox.book.alimq.producer;

import cn.knowbox.book.alimq.annotation.RocketMqChecker;
import cn.knowbox.book.alimq.properties.RocketMqProperties;
import cn.knowbox.book.alimq.producer.impl.LocalTransactionCheckerImpl;
import cn.knowbox.book.alimq.utils.RocketMqUtil;
import com.aliyun.openservices.ons.api.bean.TransactionProducerBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * 事务检查处理
 *
 * @author Created by gold on 2019/10/5 17:14
 */
public class TransactionCheckerInitializingProcessor implements InitializingBean {

    private final LocalTransactionCheckerImpl checkerImpl;
    private final RocketMqProperties properties;
    private final TransactionCheckerPostProcessor processor;

    public TransactionCheckerInitializingProcessor(TransactionProducerBean transactionProducer,
                                                   RocketMqProperties properties,
                                                   TransactionCheckerPostProcessor processor) {
        this.checkerImpl = (LocalTransactionCheckerImpl) transactionProducer.getLocalTransactionChecker();
        this.properties = properties;
        this.processor = processor;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        processor.checkers.forEach(info -> {
            RocketMqChecker annotation = info.getAnnotation();

            String topic = RocketMqUtil.generateTopic(annotation.topic(), properties.getTopicSuffix());
            String tag = RocketMqUtil.generateTag(annotation.tag(), properties.getTagSuffix());
            String checkerKey = RocketMqUtil.generateCheckerKey(topic, tag);

            checkerImpl.put(checkerKey, info.getTarget());
        });
    }
}

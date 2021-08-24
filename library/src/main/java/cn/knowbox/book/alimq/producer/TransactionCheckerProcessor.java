package cn.knowbox.book.alimq.producer;

import com.aliyun.openservices.ons.api.bean.TransactionProducerBean;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import cn.knowbox.book.alimq.annotation.RocketMqChecker;
import cn.knowbox.book.alimq.producer.impl.LocalTransactionCheckerImpl;
import cn.knowbox.book.alimq.producer.intefaces.TransactionChecker;
import cn.knowbox.book.alimq.utils.RocketMqUtil;

/**
 * 事务检查处理
 *
 * @author Created by gold on 2019/10/5 17:14
 */
public class TransactionCheckerProcessor implements ApplicationContextAware {

    private final LocalTransactionCheckerImpl checkerImpl;

    public TransactionCheckerProcessor(TransactionProducerBean transactionProducer) {
        checkerImpl = (LocalTransactionCheckerImpl) transactionProducer.getLocalTransactionChecker();
    }

    /**
     * 获取所有事务检查
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        for (String beanName : applicationContext.getBeanDefinitionNames()) {
            Object bean = applicationContext.getBean(beanName);
            Class<?> cls = AopUtils.getTargetClass(bean);

            RocketMqChecker annotation = cls.getAnnotation(RocketMqChecker.class);
            if (annotation != null) {
                TransactionChecker<?> listener = (TransactionChecker) bean;

                String tag = RocketMqUtil.generateTag(annotation.tag());
                String checkerKey = RocketMqUtil.generateCheckerKey(annotation.topic(), tag);

                checkerImpl.put(checkerKey, listener);
            }
        }
    }
}

package cn.knowbox.book.alimq.producer;

import cn.knowbox.book.alimq.annotation.RocketMqChecker;
import cn.knowbox.book.alimq.producer.intefaces.TransactionChecker;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 事务检查预处理
 *
 * @author Created by gold on 2019/10/5 17:14
 */
public class TransactionCheckerPostProcessor implements BeanPostProcessor {

    protected List<TransactionCheckerInfo> checkers = new ArrayList<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = AopUtils.getTargetClass(bean);
        RocketMqChecker annotation = AnnotationUtils.getAnnotation(clazz, RocketMqChecker.class);
        if (annotation != null) {
            if (!(bean instanceof TransactionChecker<?>)) {
                throw new IllegalStateException("classes annotated with RocketMqChecker must extend TransactionChecker.");
            }

            checkers.add(new TransactionCheckerInfo((TransactionChecker<?>) bean, annotation));
        }

        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }
}

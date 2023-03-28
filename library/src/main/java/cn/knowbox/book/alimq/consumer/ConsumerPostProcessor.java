package cn.knowbox.book.alimq.consumer;

import cn.knowbox.book.alimq.annotation.RocketMqConsume;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 消费者预处理
 *
 * @author Created by gold on 2019/10/4 15:25
 */
public class ConsumerPostProcessor implements BeanPostProcessor {

    final List<ConsumerInfo> consumers = new ArrayList<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetType = bean.getClass();

        RocketMqConsume annotation = AnnotationUtils.getAnnotation(targetType, RocketMqConsume.class);
        if (annotation != null) {
            if (!(bean instanceof ConsumerListener<?>)) {
                throw new IllegalStateException("classes annotated with RocketMqConsume must extend ConsumerListener.");
            }

            consumers.add(new ConsumerInfo((ConsumerListener<?>) bean, annotation));
        }

        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }
}

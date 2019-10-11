package cn.knowbox.book.alimq.consumer;

import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.Properties;

import cn.knowbox.book.alimq.annotation.RocketMqConsume;
import cn.knowbox.book.alimq.error.RocketMqException;
import cn.knowbox.book.alimq.utils.RocketMqUtil;

/**
 * 消费者
 *
 * @author Created by gold on 2019/10/4 15:25
 */
public class ConsumerProcessor implements BeanPostProcessor {

    private Properties properties;
    private Consumer consumer;

    public ConsumerProcessor(Properties properties) {
        if (properties == null || properties.get(PropertyKeyConst.GROUP_ID) == null
                || properties.get(PropertyKeyConst.AccessKey) == null
                || properties.get(PropertyKeyConst.SecretKey) == null
                || properties.get(PropertyKeyConst.NAMESRV_ADDR) == null) {
            throw new RocketMqException("Consumer初始化失败，配置错误！");
        }

        this.properties = properties;
    }

    public void start() {
        consumer = ONSFactory.createConsumer(properties);
        consumer.start();
    }

    public void shutdown() {
        if (consumer != null) {
            consumer.shutdown();
        }
    }

    /**
     * 获取所有消费者订阅内容(Topic 、 Tag)
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = AopUtils.getTargetClass(bean);
        RocketMqConsume annotation = clazz.getAnnotation(RocketMqConsume.class);
        if (annotation != null) {
            RocketMqListener<?> listener = (RocketMqListener) bean;

            String tag = RocketMqUtil.generateTag(annotation.tag());
            consumer.subscribe(annotation.topic(), tag, new ConsumerConverter<>(listener, annotation));
        }
        return bean;
    }
}

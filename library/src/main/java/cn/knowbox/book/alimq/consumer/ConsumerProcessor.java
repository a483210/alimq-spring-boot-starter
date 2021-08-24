package cn.knowbox.book.alimq.consumer;

import cn.knowbox.book.alimq.annotation.RocketMqConsume;
import cn.knowbox.book.alimq.config.RocketMqProperties;
import cn.knowbox.book.alimq.error.RocketMqException;
import cn.knowbox.book.alimq.parser.MqParser;
import cn.knowbox.book.alimq.utils.RocketMqUtil;
import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 消费者
 *
 * @author Created by gold on 2019/10/4 15:25
 */
public class ConsumerProcessor implements ApplicationContextAware {

    private final MqParser mqParser;
    private final RocketMqProperties properties;

    private final Map<String, Consumer> consumers;

    public ConsumerProcessor(MqParser mqParser, RocketMqProperties properties) {
        this.mqParser = mqParser;
        this.properties = properties;

        this.consumers = new LinkedHashMap<>();
    }

    /**
     * 获取所有消费者订阅内容(Topic 、 Tag)
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        for (String beanName : applicationContext.getBeanDefinitionNames()) {
            Object bean = applicationContext.getBean(beanName);
            Class<?> cls = AopUtils.getTargetClass(bean);

            RocketMqConsume annotation = cls.getAnnotation(RocketMqConsume.class);
            if (annotation != null) {
                String groupId = annotation.groupId();

                RocketMqListener<?> listener = (RocketMqListener) bean;
                String tag = RocketMqUtil.generateTag(annotation.tag());

                getConsumer(groupId).subscribe(annotation.topic(), tag, new ConsumerConverter<>(mqParser, listener, annotation));
            }
        }

    }

    private Consumer getConsumer(String groupId) {
        Consumer consumer = consumers.get(groupId);
        if (consumer != null) {
            return consumer;
        }

        Properties p = new Properties();

        p.setProperty(PropertyKeyConst.GROUP_ID, groupId);
        p.setProperty(PropertyKeyConst.AccessKey, properties.getAccessKey());
        p.setProperty(PropertyKeyConst.SecretKey, properties.getSecretKey());
        p.setProperty(PropertyKeyConst.NAMESRV_ADDR, properties.getAddress());

        consumer = create(p);

        consumers.put(groupId, consumer);

        return consumer;
    }

    private Consumer create(Properties properties) {
        if (!RocketMqUtil.checkProperties(properties)) {
            throw new RocketMqException("Consumer初始化失败，配置错误！");
        }

        Consumer consumer = ONSFactory.createConsumer(properties);
        consumer.start();

        return consumer;
    }

    private void shutdown() {
        if (CollectionUtils.isEmpty(consumers)) {
            return;
        }

        consumers.values()
                .forEach(consumer -> {
                    if (!consumer.isClosed()) {
                        consumer.shutdown();
                    }
                });

        consumers.clear();
    }

    /**
     * 是否全部成功订阅的
     *
     * @return bool
     */
    public boolean isStarted() {
        return consumers.values()
                .stream()
                .allMatch(Consumer::isStarted);
    }

    /**
     * 获取消费者状态
     *
     * @return Map<GroupId, 是否成功订阅>
     */
    public Map<String, Boolean> getStatus() {
        if (CollectionUtils.isEmpty(consumers)) {
            return new HashMap<>();
        }

        Map<String, Boolean> map = new HashMap<>();

        consumers.forEach((name, consumer) ->
                map.put(name, consumer.isStarted()));

        return map;
    }
}

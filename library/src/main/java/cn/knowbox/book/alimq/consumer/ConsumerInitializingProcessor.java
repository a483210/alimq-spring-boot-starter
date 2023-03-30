package cn.knowbox.book.alimq.consumer;

import cn.knowbox.book.alimq.annotation.RocketMqConsume;
import cn.knowbox.book.alimq.properties.RocketMqProperties;
import cn.knowbox.book.alimq.error.RocketMqException;
import cn.knowbox.book.alimq.parser.MqParser;
import cn.knowbox.book.alimq.utils.RocketMqUtils;
import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 消费者后缀处理
 *
 * @author Created by gold on 2023/3/6 13:45
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class ConsumerInitializingProcessor implements InitializingBean {

    private final MqParser mqParser;
    private final RocketMqProperties properties;
    private final ConsumerPostProcessor processor;

    private final Map<String, Consumer> consumers = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        processor.consumers.forEach(consumerInfo -> {
            RocketMqConsume annotation = consumerInfo.getAnnotation();

            String topic = RocketMqUtils.generateTopic(annotation.topic(), properties.getTopicSuffix());
            String groupId = RocketMqUtils.generateGroupId(annotation.groupId(), properties.getGroupSuffix());
            String tag = RocketMqUtils.generateTag(annotation.tag(), properties.getTagSuffix());

            getConsumer(annotation, groupId)
                    .subscribe(topic,
                            tag,
                            new ConsumerHandler<>(mqParser,
                                    consumerInfo.getTarget(),
                                    annotation,
                                    properties.getConsumer().isLogging()));

            log.info("consumer subscribe: topic={}, groupId={}, tag={}", tag, groupId, tag);
        });
    }

    private Consumer getConsumer(RocketMqConsume annotation, String groupId) {
        return consumers.computeIfAbsent(groupId, it -> {
            Properties p = new Properties();

            p.setProperty(PropertyKeyConst.GROUP_ID, it);
            p.setProperty(PropertyKeyConst.AccessKey, properties.getAccessKey());
            p.setProperty(PropertyKeyConst.SecretKey, properties.getSecretKey());
            p.setProperty(PropertyKeyConst.NAMESRV_ADDR, properties.getAddress());

            p.setProperty(PropertyKeyConst.MaxReconsumeTimes, String.valueOf(Math.max(properties.getConsumer().getMaxReconsumeTimes(), 1)));

            p.setProperty(PropertyKeyConst.MessageModel, properties.getConsumer().getMessageModel());
            p.setProperty(PropertyKeyConst.MsgTraceSwitch, String.valueOf(properties.getConsumer().isTraceSwitch()));

            int threadNums;
            if (annotation.threadNums() > 0) {
                threadNums = annotation.threadNums();
            } else {
                threadNums = properties.getConsumer().getThreadNums();
            }
            if (threadNums > 0) {
                p.setProperty(PropertyKeyConst.ConsumeThreadNums, String.valueOf(threadNums));
            }

            return create(p);
        });
    }

    private Consumer create(Properties properties) {
        if (!RocketMqUtils.checkProperties(properties)) {
            throw new RocketMqException("Consumer初始化失败，配置错误！");
        }

        Consumer consumer = ONSFactory.createConsumer(properties);
        consumer.start();

        return consumer;
    }

    public void shutdown() {
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
     * @return Map[GroupId, 是否成功订阅]
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

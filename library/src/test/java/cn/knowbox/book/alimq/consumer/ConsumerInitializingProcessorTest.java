package cn.knowbox.book.alimq.consumer;

import cn.knowbox.book.alimq.annotation.RocketMqConsume;
import cn.knowbox.book.alimq.parser.MqParser;
import cn.knowbox.book.alimq.properties.RocketMqProperties;
import cn.knowbox.book.alimq.tools.ReflectUtils;
import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.ONSFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

/**
 * ConsumerInitializingProcessorTest
 *
 * @author Created by gold on 2023/3/17 15:25
 * @since 1.0.0
 */
public class ConsumerInitializingProcessorTest {

    private MqParser parser;
    private ConsumerPostProcessor postProcessor;
    private RocketMqProperties properties;

    private MockedStatic<ONSFactory> onsFactoryMock;

    @BeforeEach
    public void setUp() {
        this.onsFactoryMock = mockStatic(ONSFactory.class);

        this.parser = mock(MqParser.class);
        this.postProcessor = spy(ConsumerPostProcessor.class);

        this.properties = new RocketMqProperties();

        properties.setAddress("http://localhost:30076");
        properties.setAccessKey("nil");
        properties.setSecretKey("nil");
        properties.setTopicSuffix("-t");
        properties.setGroupSuffix("-g");
    }

    @AfterEach
    public void tearDown() {
        onsFactoryMock.close();
    }

    @Test
    public void testInit() throws Exception {
        whenConsumerInfo();

        Consumer consumer = mock(Consumer.class);
        onsFactoryMock.when(() -> ONSFactory.createConsumer(any()))
                .thenReturn(consumer);

        ConsumerInitializingProcessor processor = new ConsumerInitializingProcessor(parser, properties, postProcessor);

        processor.afterPropertiesSet();

        Map<String, Consumer> consumers = retrieveReadInfo(processor);

        assertThat(consumers)
                .hasSize(2)
                .containsOnlyKeys("group1-g", "group2-g");

        verify(consumer, times(2)).start();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Consumer> retrieveReadInfo(ConsumerInitializingProcessor processor) {
        return (Map<String, Consumer>) ReflectUtils.getAccessibleObject(processor, "consumers");
    }

    private void whenConsumerInfo() {
        ConsumerListener<String> listener1 = mock(StringConsumerListener.class);
        RocketMqConsume annotation1 = mock(RocketMqConsume.class);
        when(annotation1.topic()).thenReturn("topic1");
        when(annotation1.groupId()).thenReturn("group1");

        ConsumerListener<String> listener2 = mock(StringConsumerListener.class);
        RocketMqConsume annotation2 = mock(RocketMqConsume.class);
        when(annotation2.topic()).thenReturn("topic2");
        when(annotation2.groupId()).thenReturn("group2");

        List<ConsumerInfo> consumerInfos = new ArrayList<>();
        consumerInfos.add(new ConsumerInfo(listener1, annotation1));
        consumerInfos.add(new ConsumerInfo(listener2, annotation2));

        postProcessor.consumers.addAll(consumerInfos);
    }
}

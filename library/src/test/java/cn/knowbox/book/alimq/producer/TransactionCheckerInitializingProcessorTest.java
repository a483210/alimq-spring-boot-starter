package cn.knowbox.book.alimq.producer;

import cn.knowbox.book.alimq.annotation.RocketMqChecker;
import cn.knowbox.book.alimq.producer.intefaces.DelegateTransactionChecker;
import cn.knowbox.book.alimq.producer.intefaces.TransactionChecker;
import cn.knowbox.book.alimq.properties.RocketMqProperties;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.bean.TransactionProducerBean;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * TransactionCheckerInitializingProcessorTest
 *
 * @author Created by gold on 2023/3/24 17:14
 * @since 1.0.0
 */
public class TransactionCheckerInitializingProcessorTest {

    private TransactionProducerBean transactionproducerbean;
    private DelegateTransactionChecker delegateTransactionChecker;
    private TransactionCheckerPostProcessor postProcessor;
    private RocketMqProperties properties;

    private MockedStatic<ONSFactory> onsFactoryMock;

    @BeforeEach
    public void setUp() {
        this.onsFactoryMock = mockStatic(ONSFactory.class);

        this.transactionproducerbean = mock(TransactionProducerBean.class);
        this.delegateTransactionChecker = mock(DelegateTransactionChecker.class);
        this.postProcessor = spy(TransactionCheckerPostProcessor.class);

        this.properties = new RocketMqProperties();

        when(transactionproducerbean.getLocalTransactionChecker()).thenReturn(delegateTransactionChecker);

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
        whenTransactionCheckerInfo();

        TransactionCheckerInitializingProcessor processor = new TransactionCheckerInitializingProcessor(transactionproducerbean, properties, postProcessor);

        processor.afterPropertiesSet();

        verify(delegateTransactionChecker).put(eq("topic1-t/*"), any());
        verify(delegateTransactionChecker).put(eq("topic2-t/tag1 || tag2"), any());
    }

    private void whenTransactionCheckerInfo() {
        TransactionChecker<String> listener1 = mock(StringTransactionChecker.class);
        RocketMqChecker annotation1 = mock(RocketMqChecker.class);
        when(annotation1.topic()).thenReturn("topic1");
        when(annotation1.tag()).thenReturn(new String[]{"*"});

        TransactionChecker<String> listener2 = mock(StringTransactionChecker.class);
        RocketMqChecker annotation2 = mock(RocketMqChecker.class);
        when(annotation2.topic()).thenReturn("topic2");
        when(annotation2.tag()).thenReturn(new String[]{"tag1", "tag2"});

        List<TransactionCheckerInfo> consumerInfos = new ArrayList<>();
        consumerInfos.add(new TransactionCheckerInfo(listener1, annotation1));
        consumerInfos.add(new TransactionCheckerInfo(listener2, annotation2));

        postProcessor.checkers.addAll(consumerInfos);
    }
}

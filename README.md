# alimq-spring-boot-starter

- [spring-boot-starter-alimq](https://github.com/jibaole/spring-boot-starter-alimq)的Gradle版本
- 调整了Template的封装使用、修改domain使用json交互、添加RocketMqChecker注解简化事务使用

## 使用

消息消费

    @Log4j2
    @RocketMqConsume(topic = "singleMessage")
    public class SingleMessageConsumer implements RocketMqListener<SingleMessage> {

        @Override
        public void onMessage(SingleMessage message) {
            log.info(message.toString());
        }
    }
    
发送普通事件

    @Service
    public class MqServiceImpl extends MqService {
    
        @Autowired
        private RocketMqTemplate rocketMqTemplate;

        @Override
        public void sendMessage(String content){
            SingleMessage singleMessage = new SingleMessage();
            singleMessage.setMsgId(UUID.randomUUID().toString());
            singleMessage.setContent(content);

            rocketMqTemplate.send("singleMessage", singleMessage);
        }
        
        @Override
        public void sendAsyncMessage(String content){
            SingleMessage singleMessage = new SingleMessage();
            singleMessage.setMsgId(UUID.randomUUID().toString());
            singleMessage.setContent(content);

            rocketMqTemplate.sendAsync("singleMessage", singleMessage);
        }
    }
    
发送顺序事件

    @Service
    public class MqServiceImpl extends MqService {
    
        @Autowired
        private RocketMqOrderTemplate rocketMqOrderTemplate;

        @Override
        public void sendMessage(String content){
            SingleMessage singleMessage = new SingleMessage();
            singleMessage.setMsgId(UUID.randomUUID().toString());
            singleMessage.setContent(content);

            rocketMqOrderTemplate.send("singleMessage", singleMessage);
        }
    }
    
发送事务事件

    @Service
    public class MqServiceImpl extends MqService {
    
        @Autowired
        private RocketMqTransactionTemplate rocketMqTransactionTemplate;

        @Override
        public void sendMessage(String content){
            SingleMessage singleMessage = new SingleMessage();
            singleMessage.setMsgId(UUID.randomUUID().toString());
            singleMessage.setContent(content);

            rocketMqTransactionTemplate.send("singleMessage", singleMessage,
                (message) -> {
                    //你的业务
                    return TransactionStatus.CommitTransaction;
                });
        }
    }
    
    //必须实现事务检查部分 
    @RocketMqChecker(topic = "singleMessage")
    public class SingleMessageChecker implements TransactionChecker<SingleMessage> {

        @Override
        public TransactionStatus check(TransactionMessage<SingleMessage> transactionMessage) {
            //检查本地事务
            return TransactionStatus.CommitTransaction;
        }

    }
## 配置说明

| 配置名称 | 配置说明 | 备注
| --- | --- | ---
| aliyun.mq.address | mq公网接入点 | 从控制台的实例获取
| aliyun.mq.access-key | 当前账号的accessKey | RAM访问控制获取
| aliyun.mq.secret-key | 当前账号的secretKey | RAM访问控制获取
| aliyun.mq.tag-suffix | 标识后缀 | 未启用
| aliyun.mq.consumer.enabled | 是否启用消费者 | 
| aliyun.mq.consumer.group-id | GroupName | 从Group管理获取
| aliyun.mq.producer.enabled | 是否开启普通生产者 | 
| aliyun.mq.producer.order-enabled | 是否开启顺序生产者 | 
| aliyun.mq.producer.transaction-enabled | 是否开启事务生产者 | 
| aliyun.mq.producer.group-id | GroupName | 从Group管理获取

## 注解说明
- **RocketMqConsume** 消息消费，默认消费失败会重新消费，如果指定reconsumeFor后，出现相同错误不会重新消费
- **RocketMqChecker** 事务检查

## Template说明
- **RocketMqTemplate** 普通消息、定时消息、延迟消息
- **RocketMqOrderTemplate** 顺序消息
  - **RocketMqOrderType** 消息分区
- **RocketMqTransactionTemplate** 事务消息

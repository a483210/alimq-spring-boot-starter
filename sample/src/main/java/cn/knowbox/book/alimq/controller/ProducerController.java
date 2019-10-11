package cn.knowbox.book.alimq.controller;

import com.aliyun.openservices.ons.api.transaction.TransactionStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import cn.knowbox.book.alimq.model.SingleMessage;
import cn.knowbox.book.alimq.mq.MessageEvent;
import cn.knowbox.book.alimq.producer.template.RocketMqOrderTemplate;
import cn.knowbox.book.alimq.producer.template.RocketMqTemplate;
import cn.knowbox.book.alimq.producer.template.RocketMqTransactionTemplate;

/**
 * 配置测试
 *
 * @author Created by gold on 2019/9/25 11:09
 */
@RestController
@RequestMapping("/mq")
public class ProducerController {

    @Autowired
    private RocketMqTemplate rocketMqTemplate;
    @Autowired
    private RocketMqOrderTemplate rocketMqOrderTemplate;
    @Autowired
    private RocketMqTransactionTemplate rocketMqTransactionTemplate;

    @PostMapping(value = "/send/{content}")
    public String singleMessage(@PathVariable("content") String content) {
        if (StringUtils.isEmpty(content)) {
            return "failure";
        }

        SingleMessage singleMessage = new SingleMessage();
        singleMessage.setMsgId(UUID.randomUUID().toString());
        singleMessage.setContent(content);

        rocketMqTemplate.send(MessageEvent.SINGLE_MESSAGE, singleMessage);

        return "success";
    }

    @PostMapping(value = "/sendAsync/{content}")
    public String asyncSingleMessage(@PathVariable("content") String content) {
        if (StringUtils.isEmpty(content)) {
            return "failure";
        }

        SingleMessage singleMessage = new SingleMessage();
        singleMessage.setMsgId(UUID.randomUUID().toString());
        singleMessage.setContent(content);

        rocketMqTemplate.sendAsync(MessageEvent.SINGLE_MESSAGE, singleMessage).getNow(null);

        return "success";
    }

    @PostMapping(value = "/sendOrder/{content}")
    public String orderSingleMessage(@PathVariable("content") String content) {
        if (StringUtils.isEmpty(content)) {
            return "failure";
        }

        SingleMessage singleMessage = new SingleMessage();
        singleMessage.setMsgId(UUID.randomUUID().toString());
        singleMessage.setContent(content);

        rocketMqOrderTemplate.send(MessageEvent.SINGLE_MESSAGE, singleMessage);

        return "success";
    }

    @PostMapping(value = "/sendTransaction/{content}")
    public String transactionSingleMessage(@PathVariable("content") String content) {
        if (StringUtils.isEmpty(content)) {
            return "failure";
        }

        SingleMessage singleMessage = new SingleMessage();
        singleMessage.setMsgId(UUID.randomUUID().toString());
        singleMessage.setContent(content);

        rocketMqTransactionTemplate.send(MessageEvent.SINGLE_MESSAGE, singleMessage,
                (message) -> {
                    return TransactionStatus.Unknow;
                });

        return "success";
    }
}

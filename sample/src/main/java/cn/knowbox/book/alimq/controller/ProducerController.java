package cn.knowbox.book.alimq.controller;

import cn.knowbox.book.alimq.model.SingleMessage;
import cn.knowbox.book.alimq.consts.MessageEvent;
import cn.knowbox.book.alimq.producer.template.RocketMqOrderTemplate;
import cn.knowbox.book.alimq.producer.template.RocketMqTemplate;
import cn.knowbox.book.alimq.producer.template.RocketMqTransactionTemplate;
import com.aliyun.openservices.ons.api.transaction.TransactionStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 配置测试
 *
 * @author Created by gold on 2019/9/25 11:09
 */
@Slf4j
@RestController
@RequestMapping("/mq")
public class ProducerController {

    @Autowired
    private RocketMqTemplate rocketMqTemplate;
    @Autowired
    private RocketMqOrderTemplate rocketMqOrderTemplate;
    @Autowired
    private RocketMqTransactionTemplate rocketMqTransactionTemplate;

    @PostMapping("/send/{content}")
    public String singleMessage(@PathVariable("content") String content) {
        if (ObjectUtils.isEmpty(content)) {
            return "failure";
        }

        SingleMessage singleMessage = new SingleMessage();
        singleMessage.setMsgId(UUID.randomUUID().toString());
        singleMessage.setContent(content);

        rocketMqTemplate.send(MessageEvent.SINGLE_MESSAGE, singleMessage);

        return "success";
    }

    @PostMapping("/send-list/{content}")
    public String listSingleMessage(@PathVariable("content") String content) {
        if (ObjectUtils.isEmpty(content)) {
            return "failure";
        }

        List<SingleMessage> list = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            SingleMessage singleMessage = new SingleMessage();
            singleMessage.setMsgId(UUID.randomUUID().toString());
            singleMessage.setContent(content + i);

            list.add(singleMessage);
        }

        rocketMqTemplate.send(MessageEvent.SINGLE_LIST_MESSAGE, list);

        return "success";
    }

    @PostMapping("/send-async/{content}")
    public String asyncMessage(@PathVariable("content") String content) {
        if (ObjectUtils.isEmpty(content)) {
            return "failure";
        }

        SingleMessage singleMessage = new SingleMessage();
        singleMessage.setMsgId(UUID.randomUUID().toString());
        singleMessage.setContent(content);

        rocketMqTemplate.sendAsync(MessageEvent.SINGLE_MESSAGE, singleMessage).getNow(null);

        return "success";
    }

    @PostMapping("/send-delay/{content}")
    public String delayMessage(@PathVariable("content") String content,
                               @RequestParam(value = "delay", required = false) Long delay) {
        if (ObjectUtils.isEmpty(content)) {
            return "failure";
        }

        SingleMessage singleMessage = new SingleMessage();
        singleMessage.setMsgId(UUID.randomUUID().toString());
        singleMessage.setContent(content);

        if (delay == null) {
            rocketMqTemplate.send(MessageEvent.DELAY_MESSAGE, singleMessage, 5000L);
        } else if (delay <= 0L) {
            rocketMqTemplate.send(MessageEvent.DELAY_MESSAGE, singleMessage);
        } else {
            rocketMqTemplate.send(MessageEvent.DELAY_MESSAGE, singleMessage, delay);
        }

        return "success";
    }

    @PostMapping("/send-order/{content}")
    public String orderMessage(@PathVariable("content") String content) {
        if (ObjectUtils.isEmpty(content)) {
            return "failure";
        }

        SingleMessage singleMessage = new SingleMessage();
        singleMessage.setMsgId(UUID.randomUUID().toString());
        singleMessage.setContent(content);

        rocketMqOrderTemplate.send(MessageEvent.ORDER_MESSAGE, singleMessage);

        return "success";
    }

    @PostMapping("/send-transaction/{content}")
    public String transactionMessage(@PathVariable("content") String content,
                                           @RequestParam(value = "reject", required = false) Integer reject) {
        if (ObjectUtils.isEmpty(content)) {
            return "failure";
        }

        SingleMessage singleMessage = new SingleMessage();
        singleMessage.setMsgId(UUID.randomUUID().toString());
        singleMessage.setContent(content);

        rocketMqTransactionTemplate.send(MessageEvent.TRANSACTION_MESSAGE, singleMessage,
                message -> {
                    if (reject != null && reject == 1) {
                        throw new NullPointerException("reject execute");
                    }

                    return TransactionStatus.CommitTransaction;
                });

        return "success";
    }
}

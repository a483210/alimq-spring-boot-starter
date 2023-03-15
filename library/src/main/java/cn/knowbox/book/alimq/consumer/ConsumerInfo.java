package cn.knowbox.book.alimq.consumer;

import cn.knowbox.book.alimq.annotation.RocketMqConsume;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ConsumerInfo
 *
 * @author Created by gold on 2023/3/6 13:48
 * @since 1.0.0
 */
@AllArgsConstructor
@Data
public class ConsumerInfo {

    /**
     * 执行目标
     */
    private ConsumerListener<?> target;
    /**
     * 消费者注解
     */
    private RocketMqConsume annotation;

}

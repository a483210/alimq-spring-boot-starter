package cn.knowbox.book.alimq.annotation;

import org.springframework.stereotype.Service;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 消息消费
 *
 * @author Created by gold on 2019/10/4 15:22
 */
@Service
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RocketMqConsume {

    /**
     * Topic name
     */
    String topic();

    /**
     * tag name
     */
    String[] tag() default "*";

    /**
     * 指定某些异常不重新消费
     */
    Class<? extends Throwable>[] reconsumeFor() default {};

}

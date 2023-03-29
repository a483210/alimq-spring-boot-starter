package cn.knowbox.book.alimq.annotation;

import cn.knowbox.book.alimq.aot.RocketMqReflectiveProcessor;
import org.springframework.aot.hint.annotation.Reflective;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * mq事务检查
 *
 * @author Created by gold on 2019/10/5 17:06
 */
@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Reflective(RocketMqReflectiveProcessor.class)
public @interface RocketMqChecker {

    /**
     * Topic name
     */
    String topic();

    /**
     * tag name
     */
    String[] tag() default "*";

}

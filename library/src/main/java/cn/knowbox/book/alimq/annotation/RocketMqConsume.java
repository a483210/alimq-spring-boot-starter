package cn.knowbox.book.alimq.annotation;

import org.springframework.stereotype.Component;

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
@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RocketMqConsume {

    /**
     * group name
     * 注意：一个group最好订阅一个topic，并且订阅关系需要保持一致
     *
     * @see <a href="https://blog.csdn.net/A__loser/article/details/102804760"/>
     * @see <a href="https://help.aliyun.com/document_detail/43523.html?spm=a2c4g.11186623.6.626.532f4fe1WoEYbq"/>
     */
    String groupId();

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

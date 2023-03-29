package cn.knowbox.book.alimq.annotation;

import cn.knowbox.book.alimq.aot.RocketMqReflectiveProcessor;
import org.springframework.aot.hint.annotation.Reflective;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 消息消费
 *
 * @author Created by gold on 2019/10/4 15:22
 */
@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Reflective(RocketMqReflectiveProcessor.class)
public @interface RocketMqConsume {

    /**
     * group name
     * 注意：一个group最好订阅一个topic，并且订阅关系需要保持一致
     *
     * @see <a href="https://blog.csdn.net/A__loser/article/details/102804760">订阅关系</a>
     * @see <a href="https://help.aliyun.com/document_detail/43523.html?spm=a2c4g.11186623.6.626.532f4fe1WoEYbq">关系一致</a>
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
     * 消费者线程数量，大于0时生效
     */
    int threadNums() default 0;

    /**
     * 指定某些异常不重新消费
     */
    Class<? extends Throwable>[] reconsumeFor() default {};

}

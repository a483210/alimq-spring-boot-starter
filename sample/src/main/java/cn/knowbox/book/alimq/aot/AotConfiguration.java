package cn.knowbox.book.alimq.aot;

import cn.knowbox.book.alimq.model.SingleMessage;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.context.annotation.Configuration;

/**
 * aot反射对象注册
 *
 * @author Created by gold on 2023/3/29 15:42
 * @since 1.0.0
 */
@RegisterReflectionForBinding(SingleMessage.class)
@Configuration
public class AotConfiguration {
}

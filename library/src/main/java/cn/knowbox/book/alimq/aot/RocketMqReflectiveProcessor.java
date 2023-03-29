package cn.knowbox.book.alimq.aot;

import cn.knowbox.book.alimq.consumer.ConsumerListener;
import cn.knowbox.book.alimq.utils.RocketMqUtils;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.aot.hint.annotation.ReflectiveProcessor;
import org.springframework.lang.NonNull;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

/**
 * RocketMqReflectiveProcessor
 *
 * @author Created by gold on 2023/3/28 17:46
 * @since 1.0.0
 */
public class RocketMqReflectiveProcessor implements ReflectiveProcessor {

    @Override
    public void registerReflectionHints(@NonNull ReflectionHints hints, @NonNull AnnotatedElement element) {
        if (element instanceof Class<?> clazz) {
            Type type = RocketMqUtils.parseType(clazz, ConsumerListener.class);
            if (type instanceof Class<?> t && !RocketMqUtils.isSystemClass(t.getTypeName())) {
                hints.registerType(clazz, RocketMqUtils.DEFAULT_MEMBER_CATEGORY);
            }
        }
    }
}

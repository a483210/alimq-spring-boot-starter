package cn.knowbox.book.alimq.tools.function;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 校验函数
 *
 * @author Created by gold on 2021/6/17 20:29
 * @since 1.0.0
 */
@FunctionalInterface
public interface VerifyFunction<T, R> {

    /**
     * 校验，并且返回执行函数
     *
     * @param consumer 消费者
     */
    InvokeFunction<R> verify(Consumer<T> consumer);

    class SimpleVerifyFunction<T, R> implements VerifyFunction<T, R> {

        private final T value;
        private final Function<T, R> function;

        public SimpleVerifyFunction(T value, Function<T, R> function) {
            this.value = value;
            this.function = function;
        }

        @Override
        public InvokeFunction<R> verify(Consumer<T> consumer) {
            consumer.accept(value);

            return new InvokeFunction.SimpleInvokeFunction<>(function.apply(value));
        }
    }
}
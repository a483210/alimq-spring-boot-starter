package cn.knowbox.book.alimq.tools.function;

import java.util.function.Consumer;

/**
 * 执行函数
 *
 * @author Created by gold on 2021/6/17 20:29
 * @since 1.0.0
 */
@FunctionalInterface
public interface InvokeFunction<T> {

    /**
     * 执行
     *
     * @param consumer 消费者
     */
    void invoke(Consumer<T> consumer);

    class SimpleInvokeFunction<T> implements InvokeFunction<T> {

        private final T value;

        public SimpleInvokeFunction(T value) {
            this.value = value;
        }

        @Override
        public void invoke(Consumer<T> consumer) {
            consumer.accept(value);
        }
    }
}
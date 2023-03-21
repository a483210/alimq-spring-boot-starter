package cn.knowbox.book.alimq.tools;

import cn.knowbox.book.alimq.tools.function.VerifyFunction;
import cn.knowbox.book.alimq.tools.function.VerifyFunction.SimpleVerifyFunction;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MockUtils
 *
 * @author Created by gold on 2021/6/7 17:43
 * @since 1.0.0
 */
public final class MockUtils {
    private MockUtils() {
    }

    /**
     * 返回ArgumentCaptor
     *
     * @param clazz 类型
     */
    @SuppressWarnings("unchecked")
    public static <T extends R, R> ArgumentCaptor<T> forArgumentCaptor(Class<R> clazz) {
        return (ArgumentCaptor<T>) ArgumentCaptor.forClass(clazz);
    }

    /**
     * 返回执行对象
     *
     * @param clazz 类型
     * @return 函数
     */
    public static <T> VerifyFunction<ArgumentCaptor<T>, T> withArgument(Class<T> clazz) {
        ArgumentCaptor<T> captor = forArgumentCaptor(clazz);

        return new SimpleVerifyFunction<>(captor, ArgumentCaptor::getValue);
    }

    /**
     * 返回执行对象
     * <p>
     * 涉及到泛型需要强转，使用需要谨慎
     *
     * @param clazz 类型
     * @return 函数
     */
    public static <T> VerifyFunction<ArgumentCaptor<T>, T> withArgumentForType(Class<?> clazz) {
        //noinspection unchecked
        ArgumentCaptor<T> captor = (ArgumentCaptor<T>) forArgumentCaptor(clazz);

        return new SimpleVerifyFunction<>(captor, ArgumentCaptor::getValue);
    }

    /**
     * 返回执行对象，获取所有对象
     *
     * @param clazz 类型
     * @return 函数
     */
    public static <T> VerifyFunction<ArgumentCaptor<T>, List<T>> withArguments(Class<T> clazz) {
        ArgumentCaptor<T> captor = forArgumentCaptor(clazz);

        return new SimpleVerifyFunction<>(captor, ArgumentCaptor::getAllValues);
    }

    /**
     * 返回执行对象，获取所有对象
     * <p>
     * 涉及到泛型需要强转，使用需要谨慎
     *
     * @param clazz 类型
     * @return 函数
     */
    public static <T> VerifyFunction<ArgumentCaptor<T>, List<T>> withArgumentsForType(Class<?> clazz) {
        //noinspection unchecked
        ArgumentCaptor<T> captor = (ArgumentCaptor<T>) forArgumentCaptor(clazz);

        return new SimpleVerifyFunction<>(captor, ArgumentCaptor::getAllValues);
    }

    /**
     * 返回执行对象
     *
     * @param clazz 类型
     * @param times 次数
     * @return 函数
     */
    public static <T> VerifyFunction<List<ArgumentCaptor<T>>, List<T>> withArgument(Class<T> clazz, int times) {
        List<ArgumentCaptor<T>> captors = new ArrayList<>(times);
        for (int i = 0; i < times; i++) {
            captors.add(forArgumentCaptor(clazz));
        }

        return new SimpleVerifyFunction<>(captors, argumentCaptors -> argumentCaptors.stream()
                .map(ArgumentCaptor::getValue)
                .collect(Collectors.toList()));
    }

    /**
     * 返回执行对象
     *
     * @param clazz 类型
     * @param times 次数
     * @return 函数
     */
    public static <T> VerifyFunction<List<ArgumentCaptor<T>>, List<T>> withArgumentForType(Class<?> clazz, int times) {
        List<ArgumentCaptor<T>> captors = new ArrayList<>(times);
        for (int i = 0; i < times; i++) {
            //noinspection unchecked
            captors.add((ArgumentCaptor<T>) forArgumentCaptor(clazz));
        }

        return new SimpleVerifyFunction<>(captors, argumentCaptors -> argumentCaptors.stream()
                .map(ArgumentCaptor::getValue)
                .collect(Collectors.toList()));
    }
}

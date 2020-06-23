package cn.knowbox.book.alimq.parser;

import org.springframework.lang.Nullable;

import java.lang.reflect.Type;

/**
 * mq解析器
 *
 * @author Created by gold on 2019/11/22 10:49
 */
public interface MqParser {

    /**
     * 将对象解析为字符串
     *
     * @param value 对象
     */
    @Nullable
    String format(Object value);

    /**
     * 将对象解析为字符串
     *
     * @param json json字符串
     * @param cls  类型
     */
    @Nullable
    <T> T parse(String json, Type cls);

}

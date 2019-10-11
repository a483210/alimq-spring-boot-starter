package cn.knowbox.book.alimq.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.zip.CRC32;

/**
 * rocketMq工具类
 *
 * @author Created by gold on 2019/10/5 14:33
 */
public class RocketMqUtil {

    public static final ObjectMapper INSTANCE = new ObjectMapper();

    private RocketMqUtil() {
    }

    /**
     * 转换为crc32
     *
     * @param bytes 数据
     */
    public static long crc32Code(byte[] bytes) {
        CRC32 crc32 = new CRC32();
        crc32.update(bytes);
        return crc32.getValue();
    }

    /**
     * 生成检查key
     *
     * @param topic topic
     * @param tag   tag
     */
    public static String generateCheckerKey(String topic, String tag) {
        return String.format("%s/%s", topic, tag);
    }

    /**
     * 生成tag
     *
     * @param tags tag集合
     */
    public static String generateTag(String[] tags) {
        return StringUtils.arrayToDelimitedString(tags, " || ");
    }

    /**
     * 将对象解析为字符串
     *
     * @param value 对象
     */
    @Nullable
    public static String toJson(Object value) {
        try {
            return INSTANCE.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将对象解析为字符串
     *
     * @param json json字符串
     * @param cls  类型
     */
    @Nullable
    public static <T> T parse(String json, Class<T> cls) {
        try {
            return INSTANCE.readValue(json, cls);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解析当前索引泛型
     *
     * @param cls 类
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> Class<T> parseType(Class<?> cls, Class<?> iCls) {
        Type[] genTypes = cls.getGenericInterfaces();
        if (genTypes.length == 0) {
            return null;
        }

        ParameterizedType genType = null;
        for (Type type : genTypes) {
            if (!(type instanceof ParameterizedType)) {
                continue;
            }
            ParameterizedType tmpType = (ParameterizedType) type;
            if (iCls.isAssignableFrom(((Class<?>) tmpType.getRawType()))) {
                genType = tmpType;
                break;
            }
        }

        if (genType == null) {
            return null;
        }

        Type[] typeParams = genType.getActualTypeArguments();
        if (typeParams.length != 1) {
            return null;
        }

        return (Class<T>) typeParams[0];
    }

}


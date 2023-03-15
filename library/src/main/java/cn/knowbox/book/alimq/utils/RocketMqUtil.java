package cn.knowbox.book.alimq.utils;

import com.aliyun.openservices.ons.api.PropertyKeyConst;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Properties;
import java.util.zip.CRC32;

/**
 * rocketMq工具类
 *
 * @author Created by gold on 2019/10/5 14:33
 */
public class RocketMqUtil {

    private RocketMqUtil() {
    }

    /**
     * 检查配置是否合法
     *
     * @param properties 配置
     */
    public static boolean checkProperties(Properties properties) {
        return properties != null
                && properties.get(PropertyKeyConst.GROUP_ID) != null
                && properties.get(PropertyKeyConst.AccessKey) != null
                && properties.get(PropertyKeyConst.SecretKey) != null
                && properties.get(PropertyKeyConst.NAMESRV_ADDR) != null;
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
        return topic + "/" + tag;
    }

    /**
     * 生成topic
     *
     * @param topic       topic
     * @param topicSuffix topic后缀
     */
    public static String generateTopic(String topic, String topicSuffix) {
        return topic + topicSuffix;
    }

    /**
     * 生成tag
     *
     * @param tags      tag集合
     * @param tagSuffix tag后缀
     */
    public static String generateTag(String[] tags, String tagSuffix) {
        if (!StringUtils.isEmpty(tags)) {
            tags = Arrays.stream(tags)
                    .map(it -> it + tagSuffix)
                    .toArray(String[]::new);
        }
        return StringUtils.arrayToDelimitedString(tags, " || ");
    }

    /**
     * 解析当前索引泛型
     *
     * @param cls 类
     */
    @Nullable
    public static Type parseType(Class<?> cls, Class<?> iCls) {
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

        return typeParams[0];
    }
}


package cn.knowbox.book.alimq.utils;

import com.aliyun.openservices.ons.api.PropertyKeyConst;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;
import java.util.zip.CRC32;

/**
 * rocketMq工具类
 *
 * @author Created by gold on 2019/10/5 14:33
 */
public class RocketMqUtils {
    private RocketMqUtils() {
    }

    public static final MemberCategory[] DEFAULT_MEMBER_CATEGORY = new MemberCategory[]{
            MemberCategory.DECLARED_FIELDS,
            MemberCategory.PUBLIC_FIELDS,
            MemberCategory.INVOKE_PUBLIC_METHODS,
            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
            MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS
    };

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
     * 生成topic
     *
     * @param groupId     groupId
     * @param groupSuffix groupId后缀
     */
    public static String generateGroupId(String groupId, String groupSuffix) {
        return groupId + groupSuffix;
    }

    /**
     * 生成tag
     *
     * @param tags      tag集合
     * @param tagSuffix tag后缀
     */
    public static String generateTag(String[] tags, String tagSuffix) {
        if (!ObjectUtils.isEmpty(tags)) {
            //*代表所有
            if (tags.length == 1 && Objects.equals(tags[0], "*")) {
                return "*";
            }

            tags = Arrays.stream(tags)
                    .map(it -> {
                        if (Objects.equals(it, "*")) {
                            throw new IllegalArgumentException("The use of the asterisk (*) is not allowed under multiple tags.");
                        }
                        return it + tagSuffix;
                    })
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

    /**
     * 是否为系统类型
     */
    public static boolean isSystemClass(String name) {
        return name.startsWith("java.")
                || name.startsWith("javax.")
                || name.startsWith("kotlin.")
                || name.startsWith("kotlinx.")
                || name.startsWith("org.springframework.");
    }
}


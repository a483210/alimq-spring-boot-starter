package cn.knowbox.book.alimq.tools;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * ReflectUtils
 *
 * @author Created by gold on 2021/6/8 13:48
 * @since 1.0.0
 */
public final class ReflectUtils {
    private ReflectUtils() {
    }

    /**
     * 反射获取对象静态参数
     *
     * @param clazz 源类型
     * @param key   对象名称
     */
    public static Object getAccessibleObject(Class<?> clazz, String key) {
        try {
            Field field = clazz.getDeclaredField(key);

            field.setAccessible(true);
            return field.get(null);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 反射获取对象参数
     *
     * @param src 源对象
     * @param key 对象名称
     */
    public static Object getAccessibleObject(Object src, String key) {
        try {
            Field field = searchField(src.getClass(), key);
            if (field == null) {
                return null;
            }

            field.setAccessible(true);
            return field.get(src);
        } catch (Exception ignore) {
            return null;
        }
    }

    private static Field searchField(Class<?> clazz, String key) {
        while (clazz != null && clazz != Object.class) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                String name = field.getName();

                if (Objects.equals(name, key)) {
                    return field;
                }
            }

            clazz = clazz.getSuperclass();
        }

        return null;
    }
}

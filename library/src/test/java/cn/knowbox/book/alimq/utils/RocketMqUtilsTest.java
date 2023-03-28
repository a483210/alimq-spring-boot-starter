package cn.knowbox.book.alimq.utils;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.function.Function;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * RocketMqUtilsTest
 *
 * @author Created by gold on 2023/3/16 15:14
 * @since 1.0.0
 */
public class RocketMqUtilsTest {

    @Test
    public void testGenerateCheckerKey() {
        String result = RocketMqUtils.generateCheckerKey("topic", "tag");

        assertThat(result)
                .isEqualTo("topic/tag");
    }

    @Test
    public void testGenerateTopic() {
        String result = RocketMqUtils.generateTopic("topic", "-dev");

        assertThat(result)
                .isEqualTo("topic-dev");
    }

    @Test
    public void testGenerateTag() {
        String result = RocketMqUtils.generateTag(new String[]{"tag"}, "-dev");

        assertThat(result)
                .isEqualTo("tag-dev");

        String result2 = RocketMqUtils.generateTag(new String[]{"tag1", "tag2"}, "-dev");

        assertThat(result2)
                .isEqualTo("tag1-dev || tag2-dev");

        String result3 = RocketMqUtils.generateTag(new String[]{"*"}, "-dev");

        assertThat(result3)
                .isEqualTo("*");

        assertThatThrownBy(() -> {
            RocketMqUtils.generateTag(new String[]{"*", "tag2"}, "-dev");
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testParseType() {
        Type result = RocketMqUtils.parseType(ParseBean.class, ParseInterface.class);

        assertThat(result)
                .isEqualTo(String.class);

        Type result2 = RocketMqUtils.parseType(ParseBean.class, Function.class);

        assertThat(result2)
                .isNull();

        Type result3 = RocketMqUtils.parseType(ParseBeanNotInherited.class, ParseInterface.class);

        assertThat(result3)
                .isNull();
    }

    public interface ParseInterface<T> {

        void method(T value);

    }

    public static class ParseBean implements ParseInterface<String> {

        @Override
        public void method(String value) {

        }
    }

    public static class ParseBeanNotInherited {

    }
}

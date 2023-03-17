package cn.knowbox.book.alimq.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * JacksonMqParserTest
 *
 * @author Created by gold on 2023/3/16 15:25
 * @since 1.0.0
 */
public class JacksonMqParserTest {

    private JacksonMqParser parser;

    @BeforeEach
    public void setUp() {
        this.parser = new JacksonMqParser();
    }

    @Test
    public void testParse() {
        JsonBean bean = parser.parse("{\"text\":\"str\"}", JsonBean.class);

        assertThat(bean)
                .hasFieldOrPropertyWithValue("text", "str");

        String bean2 = parser.parse("str", String.class);

        assertThat(bean2)
                .isEqualTo("str");
    }

    @Test
    public void testParseByParameterizedType() {
        List<JsonBean> list = parser.parse("[{\"text\":\"str1\"},{\"text\":\"str2\"}]", new ParameterizedType() {

            @Override
            public Type[] getActualTypeArguments() {
                return new Type[]{JsonBean.class};
            }

            @Override
            public Type getRawType() {
                return List.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        });

        assertThat(list)
                .hasSize(2);

        assertThat(list)
                .element(0)
                .hasFieldOrPropertyWithValue("text", "str1");

        assertThat(list)
                .element(1)
                .hasFieldOrPropertyWithValue("text", "str2");
    }

    @Test
    public void testFormat() {
        String json = parser.format(new JsonBean("str"));

        assertThat(json)
                .isEqualTo("{\"text\":\"str\"}");

        String json2 = parser.format("str");

        assertThat(json2)
                .isEqualTo("str");
    }

    public static class JsonBean {

        private String text;

        public JsonBean() {
        }

        public JsonBean(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}

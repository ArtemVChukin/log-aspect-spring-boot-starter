package ru.alfastrah.library.log.aspect.starter;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ContextConfiguration(classes = {AspectLogging.class, TestAspect.class})
@EnableAspectJAutoProxy
class AspectLoggingTest {
    private static final Logger log = (Logger) LoggerFactory.getLogger(TestAspect.class);
    private static final ObjectMapper mapper = new ObjectMapper()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(SerializationFeature.WRAP_ROOT_VALUE, true);

    @Autowired
    TestAspect testAspect;

    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        listAppender = new ListAppender<>();
        listAppender.start();
        log.addAppender(listAppender);
    }

    @AfterEach
    void tearDown() {
        log.detachAppender("listAppender");
    }

    @DisplayName("Тест логирования параметров простого GET запроса")
    @Test
    void blankMethodLogTest() {
        //when
        testAspect.blank();

        //then
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(3, logsList.size());
        assertEquals("IN details [{}] start [{}] arguments [{}]", logsList.get(0).getMessage());
        assertEquals("blank", logsList.get(1).getMessage());
        assertEquals("OUT details [{}] duration [{}ms] return [{}]", logsList.get(2).getMessage());
    }

    @DisplayName("Тест логирования параметров простого POST запроса с параметром и RequestBody")
    @Test
    void toStringMethodLogTest() {
        // given
        String value1 = "value1";
        String value2 = "value2";

        //when
        String resultValue = testAspect.string(value1, value2);

        //then
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(2, logsList.size());

        Object[] argumentArray = logsList.get(0).getArgumentArray();
        assertEquals(3, argumentArray.length);
        assertEquals("TestAspect.string(..)", argumentArray[0]);
        assertEquals("[" + value1 + ", " + value2 + "]", argumentArray[2]);

        argumentArray = logsList.get(1).getArgumentArray();
        assertEquals(3, argumentArray.length);
        assertEquals("TestAspect.string(..)", argumentArray[0]);
        assertEquals(resultValue, argumentArray[2]);

        assertNotNull(logsList.get(0).getMDCPropertyMap().get("UID"));
        assertNotNull(logsList.get(1).getMDCPropertyMap().get("UID"));
    }

    @DisplayName("Тест логирования параметров POST запроса с объектом без метода toString() в RequestBody")
    @Test
    void jsonMethodLogTest() throws JsonProcessingException {
        // given
        String value1 = "value1";
        String value2 = "value2";

        //when
        TestAspect.Holder result = testAspect.holder(new TestAspect.Holder(value1), new TestAspect.Holder(value2));

        //then
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(2, logsList.size());

        Object[] argumentArray = logsList.get(0).getArgumentArray();
        assertEquals(3, argumentArray.length);
        assertEquals("TestAspect.holder(..)", argumentArray[0]);
        String args = "[" + mapper.writeValueAsString(new TestAspect.Holder(value1)) + ","
                + mapper.writeValueAsString(new TestAspect.Holder(value2)) + "]";
        assertEquals(args, argumentArray[2]);

        argumentArray = logsList.get(1).getArgumentArray();
        assertEquals(3, argumentArray.length);
        assertEquals("TestAspect.holder(..)", argumentArray[0]);
        assertEquals(mapper.writeValueAsString(result), argumentArray[2]);

        assertNotNull(logsList.get(0).getMDCPropertyMap().get("UID"));
        assertNotNull(logsList.get(1).getMDCPropertyMap().get("UID"));
    }
}

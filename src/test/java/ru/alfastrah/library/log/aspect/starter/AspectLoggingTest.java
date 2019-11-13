package ru.alfastrah.library.log.aspect.starter;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {AspectLoggingAutoConfiguration.class, TestAspect.class})
class AspectLoggingTest {
    private static final Logger log = (Logger) LoggerFactory.getLogger(TestAspect.class);

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

    @Test
    void blankMethodLogTest() {
        //when
        testAspect.blank();

        //then
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(3, logsList.size());
        assertEquals("Aspect log [{}] args: {}", logsList.get(0).getMessage());
        assertEquals("blank", logsList.get(1).getMessage());
        assertEquals("Aspect log [{}] duration [{}] return {}", logsList.get(2).getMessage());
    }


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

        assertEquals(2, logsList.get(0).getArgumentArray().length);
        assertTrue(logsList.get(0).getArgumentArray()[1] instanceof String);
        String method = (String) logsList.get(0).getArgumentArray()[1];
        assertTrue(method.contains(value1));
        assertTrue(method.contains(value2));

        assertEquals(3, logsList.get(1).getArgumentArray().length);
        assertTrue(logsList.get(1).getArgumentArray()[2] instanceof String);
        method = (String) logsList.get(1).getArgumentArray()[2];
        assertEquals(method, resultValue);

        assertEquals(logsList.get(0).getArgumentArray()[0], logsList.get(1).getArgumentArray()[0]);
    }

    @Test
    void jsonMethodLogTest() {
        // given
        String value1 = "value1";
        String value2 = "value2";

        //when
        TestAspect.Holder result = testAspect.holder(new TestAspect.Holder(value1), new TestAspect.Holder(value2));

        //then
        List<ILoggingEvent> logsList = listAppender.list;
        assertEquals(2, logsList.size());

        assertEquals(2, logsList.get(0).getArgumentArray().length);
        assertTrue(logsList.get(0).getArgumentArray()[1] instanceof String);
        String method = (String) logsList.get(0).getArgumentArray()[1];
        assertTrue(method.contains(value1));
        assertTrue(method.contains(value2));

        assertEquals(3, logsList.get(1).getArgumentArray().length);
        assertTrue(logsList.get(1).getArgumentArray()[2] instanceof String);
        method = (String) logsList.get(1).getArgumentArray()[2];
        assertEquals(method, "{\"Holder\":{\"value\":\"" + result.getValue() + "\"}}");

        assertEquals(logsList.get(0).getArgumentArray()[0], logsList.get(1).getArgumentArray()[0]);
    }

}

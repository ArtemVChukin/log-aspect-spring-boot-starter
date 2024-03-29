package ru.alfastrah.library.log.aspect.starter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestAspect {
    private static final Logger log = LoggerFactory.getLogger(TestAspect.class);

    @AspectLog
    void blank() {
        log.info("blank");
    }

    @AspectLog
    String string(String arg1, String arg2) {
        return arg1 + " " + arg2;
    }

    @AspectLog(useJsonMapper = true)
    Holder holder(Holder arg1, Holder arg2) {
        return new Holder(arg1.getValue() + arg2.getValue());
    }

    static class Holder {
        private String value;

        public Holder() {
        }

        public Holder(String value) {
            this.value = value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}

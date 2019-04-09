package net.n2oapp.platform.jaxrs;

import org.apache.cxf.ext.logging.AbstractLoggingInterceptor;
import org.apache.cxf.ext.logging.LoggingInInterceptor;
import org.apache.cxf.ext.logging.event.LogEventSender;
import org.apache.cxf.ext.logging.event.PrettyLoggingFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;


/**
 * @author lgalimova
 * @since 02.04.2019
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class,
        properties = {
                "jaxrs.logging-in.enabled=true",
                "jaxrs.logging-in.limit=" + DefineLoggingAttributesTest.LIMIT,
                "jaxrs.logging-in.in-mem-threshold=" + DefineLoggingAttributesTest.IN_MEM_THRESHOLD,
                "jaxrs.logging-in.log-binary=" + DefineLoggingAttributesTest.LOG_BINARY,
                "jaxrs.logging-in.log-multipart=" + DefineLoggingAttributesTest.LOG_MULTIPART,
                "jaxrs.logging-in.pretty-logging=" + DefineLoggingAttributesTest.PRETTY_LOGGING
        },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DefineLoggingAttributesTest {

    static final int LIMIT = 1024;
    static final int IN_MEM_THRESHOLD = 100 * 1024;
    static final boolean LOG_BINARY = true;
    static final boolean LOG_MULTIPART = true;
    static final boolean PRETTY_LOGGING = true;

    @Autowired
    private LoggingInInterceptor loggingInInterceptor;

    @Test
    public void testLimit() {
        assertEquals(LIMIT, loggingInInterceptor.getLimit());
    }

    @Test
    public void testInMemThreshold() {
        assertEquals(IN_MEM_THRESHOLD, loggingInInterceptor.getInMemThreshold());
    }

    @Test
    public void testLogBinary() throws NoSuchFieldException, IllegalAccessException {
        Field privateLogBinaryField = AbstractLoggingInterceptor.class.getDeclaredField("logBinary");
        privateLogBinaryField.setAccessible(true);
        assertEquals(LOG_BINARY, privateLogBinaryField.get(loggingInInterceptor));
    }

    @Test
    public void testLogMultipart() throws NoSuchFieldException, IllegalAccessException {
        Field privateLogMultipartField = AbstractLoggingInterceptor.class.getDeclaredField("logMultipart");
        privateLogMultipartField.setAccessible(true);
        assertEquals(LOG_MULTIPART, privateLogMultipartField.get(loggingInInterceptor));
    }

    @Test
    public void testPrettyLogging() throws NoSuchFieldException, IllegalAccessException {
        Field privateSenderField = AbstractLoggingInterceptor.class.getDeclaredField("sender");
        privateSenderField.setAccessible(true);
        LogEventSender sender = (LogEventSender) privateSenderField.get(loggingInInterceptor);

        if (sender instanceof PrettyLoggingFilter) {
            Field privatePrettyField = PrettyLoggingFilter.class.getDeclaredField("prettyLogging");
            privatePrettyField.setAccessible(true);
            assertEquals(PRETTY_LOGGING, privatePrettyField.get(sender));
        }
    }
}


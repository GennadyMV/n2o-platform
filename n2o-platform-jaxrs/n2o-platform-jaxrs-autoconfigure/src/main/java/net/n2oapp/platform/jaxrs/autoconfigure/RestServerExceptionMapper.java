package net.n2oapp.platform.jaxrs.autoconfigure;

import net.n2oapp.platform.jaxrs.RestExceptionMapper;
import net.n2oapp.platform.jaxrs.RestMessage;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.ws.rs.ext.Provider;

/**
 * Преобразование любых исключений в ответ {@link RestMessage} REST сервиса
 */
@Provider
public class RestServerExceptionMapper implements RestExceptionMapper<Exception> {

    @Override
    public RestMessage toMessage(Exception exception) {
        RestMessage message = new RestMessage(exception.getMessage());
        message.setStackTrace(ExceptionUtils.getStackFrames(exception));
        return message;
    }
}

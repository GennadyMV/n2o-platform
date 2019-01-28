package net.n2oapp.platform.i18n;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Исключение, содержащее локализованное сообщение, понятное конечному пользователю.
 */
public class UserException extends RuntimeException {
    private static final long serialVersionUID = 2552353701499979545L;
    @Deprecated
    private static final Message UNEXPECTED_ERROR = new Message("exception.unexpectedError");

    private transient Object[] args;

    private final transient List<Message> messages;

    public UserException(Message message) {
        super(message.getCode());
        this.args = message.getArgs();
        this.messages = null;
    }

    public UserException(List<Message> messages) {
        super(messages.stream().map(Message::getCode).collect(Collectors.joining(",\n")));
        this.messages = messages;
    }


    public UserException(Message message, Throwable cause) {
        super(message.getCode(), cause);
        this.args = message.getArgs();
        this.messages = null;
    }

    public UserException(String code) {
        super(code);
        this.messages = null;
    }

    public UserException(String code, Throwable cause) {
        super(code, cause);
        this.messages = null;
    }

    /**
     * @deprecated Это исключение предназначено для передачи сообщений пользователю. Например, для валидаций.
     * Непредвиденные ошибки не должны бросать это исключение.
     */
    @Deprecated
    public UserException() {
        this(UNEXPECTED_ERROR);
    }

    /**
     * @deprecated Это исключение предназначено для передачи сообщений пользователю. Например, для валидаций.
     * Непредвиденные ошибки не должны бросать это исключение.
     */
    @Deprecated
    public UserException(Throwable cause) {
        this(UNEXPECTED_ERROR, cause);
    }

    public Object[] getArgs() {
        return this.args;
    }

    public String getCode() {
        return getMessage();
    }

    /**
     * Добавить параметр сообщения
     * @param argument Параметр
     * @deprecated Исключения должны быть немодифицируемы
     */
    @Deprecated
    public UserException set(Object argument) {
        ArrayList<Object> list = new ArrayList<>(args != null ? Arrays.asList(args) : Collections.emptyList());
        list.add(argument);
        this.args = list.toArray();
        return this;
    }

    public List<Message> getMessages() {
        return messages;
    }

}

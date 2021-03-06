package net.n2oapp.platform.jaxrs.api;

import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Модель данных, используемая в демонстрационном REST сервисе
 */
@Getter
@Setter
@Validated
public class SomeModel {
    private Long id;
    private @NotBlank String name;
    private @Past Date date;
    private @Past LocalDateTime dateEnd;

    public SomeModel() {
    }

    public SomeModel(Long id) {
        this.id = id;
    }
}

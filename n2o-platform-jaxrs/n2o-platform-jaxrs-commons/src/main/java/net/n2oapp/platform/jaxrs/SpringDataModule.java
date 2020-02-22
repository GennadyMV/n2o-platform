package net.n2oapp.platform.jaxrs;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Модуль для {@link com.fasterxml.jackson.databind.ObjectMapper} позволяющий
 * серилизовать / десерилизовать базовые модели Spring Data, такие как Page, Pageable, Sort и т.п.
 */
abstract class SpringDataModule extends SimpleModule {

    private static final String DIRECTION = "direction";
    private static final String PROPERTY = "property";

    SpringDataModule() {
        super(PackageVersion.VERSION);
        SimpleAbstractTypeResolver resolver = new SimpleAbstractTypeResolver();
        resolver.addMapping(Pageable.class, RestCriteria.class);
        this.setAbstractTypes(resolver);
        this.setMixInAnnotation(Page.class, PageMixin.class);
    }

    static class SpringDataJsonModule extends SpringDataModule {

        SpringDataJsonModule() {
            super();
            this.addSerializer(new JsonSortSerializer());
            this.addDeserializer(Sort.class, new JsonSortDeserializer());
        }

    }

    static class SpringDataXmlModule extends SpringDataModule {

        SpringDataXmlModule() {
            super();
            this.addSerializer(new XmlSortSerializer());
            this.addDeserializer(Sort.class, new XmlSortDeserializer());
        }

    }

    static class JsonSortSerializer extends StdSerializer<Sort> {

        JsonSortSerializer() {
            super(Sort.class);
        }

        @Override
        public void serialize(Sort value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartArray();
            for (Sort.Order order : value) {
                gen.writeStartObject();
                gen.writeStringField(PROPERTY, order.getProperty());
                gen.writeStringField(DIRECTION, order.getDirection().name().toLowerCase());
                gen.writeEndObject();
            }
            gen.writeEndArray();
        }
    }

    static class JsonSortDeserializer extends JsonDeserializer<Sort> {

        @Override
        public Sort deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException {
            ArrayNode node = jp.getCodec().readTree(jp);
            Sort.Order[] orders = new Sort.Order[node.size()];
            int i = 0;
            for (JsonNode obj : node) {
                Sort.Direction direction = obj.get(DIRECTION) != null ?
                        Sort.Direction.valueOf(obj.get(DIRECTION).asText().toUpperCase()) : null;
                orders[i] = new Sort.Order(direction, obj.get(PROPERTY).asText());
                i++;
            }
            return Sort.by(orders);
        }
    }

    static class XmlSortSerializer extends StdSerializer<Sort> {

        XmlSortSerializer() {
            super(Sort.class);
        }

        @Override
        public void serialize(Sort value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            AnnotatedSort annotatedSort = new AnnotatedSort();
            annotatedSort.setOrders(new ArrayList<>());
            for (Sort.Order order : value) {
                AnnotatedSort.AnnotatedOrder annotatedOrder = new AnnotatedSort.AnnotatedOrder();
                annotatedOrder.setProperty(order.getProperty());
                annotatedOrder.setDirection(order.getDirection().name().toLowerCase());
                annotatedSort.getOrders().add(annotatedOrder);
            }
            gen.writeObject(annotatedSort);
        }

    }

    static class XmlSortDeserializer extends JsonDeserializer<Sort> {

        @Override
        public Sort deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            AnnotatedSort annotatedSort = p.readValueAs(AnnotatedSort.class);
            return Sort.by(annotatedSort.getOrders().stream().map(annotatedOrder -> new Sort.Order(Sort.Direction.valueOf(annotatedOrder.getDirection().toUpperCase()), annotatedOrder.getProperty())).collect(Collectors.toList()));
        }

    }


    @JsonDeserialize(as = RestPage.class)
    @JacksonXmlRootElement(localName = "page")
    @JsonIgnoreProperties({"last", "number", "numberOfElements", "size", "totalPages", "first", "pageable", "empty"})
    private static class PageMixin {
    }

    @JacksonXmlRootElement(localName = "sort")
    private static class AnnotatedSort {

        private List<AnnotatedOrder> orders;

        @JsonGetter
        List<AnnotatedOrder> getOrders() {
            return orders;
        }

        @JsonSetter
        void setOrders(List<AnnotatedOrder> orders) {
            this.orders = orders;
        }

        private static class AnnotatedOrder {

            private String property;
            private String direction;

            @JsonGetter
            String getProperty() {
                return property;
            }

            @JsonSetter
            void setProperty(String property) {
                this.property = property;
            }

            @JsonGetter
            String getDirection() {
                return direction;
            }

            @JsonSetter
            void setDirection(String direction) {
                this.direction = direction;
            }

        }

    }

}

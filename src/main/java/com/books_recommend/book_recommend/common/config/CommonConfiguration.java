package com.books_recommend.book_recommend.common.config;

import com.books_recommend.book_recommend.common.support.Status;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS;

@Configuration
public class CommonConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        var objectMapper = new ObjectMapper();
        objectMapper.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        objectMapper.registerModules(
                new JavaTimeModule(),
                new Jdk8Module(),
                new CustomModule()
        );
        objectMapper.setVisibility(
                PropertyAccessor.FIELD,
                JsonAutoDetect.Visibility.ANY
        );
        objectMapper.disable(
                FAIL_ON_UNKNOWN_PROPERTIES
        );
        objectMapper.disable(
                WRITE_DATES_AS_TIMESTAMPS,
                WRITE_DURATIONS_AS_TIMESTAMPS
        );

        return objectMapper;
    }

    private static class CustomModule extends SimpleModule {
        public CustomModule() {
            addSerializer(
                    LocalDateTime.class,
                    new LocalDateTimeSerializer(
                            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                    )
            );
            addDeserializer(
                    Status.class,
                    new StdDeserializer<>(Status.class) {
                        @Override
                        public Status deserialize(JsonParser parser,
                                                  DeserializationContext context) throws IOException {
                            var enumText = parser.getCodec()
                                    .readValue(
                                            parser,
                                            String.class
                                    );
                            return Status.from(enumText);
                        }
                    }
            );
        }
    }
}


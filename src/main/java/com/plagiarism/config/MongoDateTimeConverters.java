package com.plagiarism.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

public final class MongoDateTimeConverters {
    
    private MongoDateTimeConverters() {
        // Private constructor to prevent instantiation
    }
    
    @Component
    @ReadingConverter
    public static class StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {
        @Override
        public LocalDateTime convert(String source) {
            if (source == null || source.isEmpty()) {
                return null;
            }
            return LocalDateTime.parse(source);
        }
    }
    
    @Component
    @WritingConverter
    public static class LocalDateTimeToStringConverter implements Converter<LocalDateTime, String> {
        @Override
        public String convert(LocalDateTime source) {
            if (source == null) {
                return null;
            }
            return source.toString();
        }
    }
}
package com.buildmate.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {
  private static final String SEPARATOR = "\u001F";

  @Override
  public String convertToDatabaseColumn(List<String> values) {
    return values == null || values.isEmpty() ? "" : String.join(SEPARATOR, values);
  }

  @Override
  public List<String> convertToEntityAttribute(String value) {
    if (value == null || value.isBlank()) {
      return new ArrayList<>();
    }
    return new ArrayList<>(Arrays.asList(value.split(SEPARATOR, -1)));
  }
}

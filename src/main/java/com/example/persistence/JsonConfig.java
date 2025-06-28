package com.example.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonConfig {
  public static ObjectMapper newMapper() {
    ObjectMapper m = new ObjectMapper();
    m.registerModules(new JavaTimeModule());
    return m;
  }
}

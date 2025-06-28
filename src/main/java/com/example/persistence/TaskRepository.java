package com.example.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.example.model.Task;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class TaskRepository {
  private final ObjectMapper m = JsonConfig.newMapper();
  private final File file = new File("src/main/resources/data/tasks.json");

  public LinkedList<Task> loadAll() {
    try {
      return m.readValue(file, new TypeReference<LinkedList<Task>>() {
      });

    } catch (IOException e) {
      e.printStackTrace();
      return new LinkedList<>();
    }
  }

  public void saveAll(LinkedList<Task> tasks) {
    try {
      m.writerWithDefaultPrettyPrinter().writeValue(file, tasks);

    } catch (IOException e) {
      e.printStackTrace();
    }

  }

}

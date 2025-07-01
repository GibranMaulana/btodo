package com.example.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.example.model.Task;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class TaskRepository {
  private static final ObjectMapper m = JsonConfig.newMapper();

  public static LinkedList<Task> load(int userId) {
    try {
      File file = new File("src/main/resources/data/tasks/" + userId + ".json");
      return m.readValue(file, new TypeReference<LinkedList<Task>>() {
      });

    } catch (IOException e) {
      e.printStackTrace();
      return new LinkedList<>();
    }
  }

  public static void save(LinkedList<Task> tasks, int userId) {
    try {
      File file = new File("src/main/resources/data/tasks/" + userId + ".json");
      // Ensure the directory exists
      file.getParentFile().mkdirs();
      m.writerWithDefaultPrettyPrinter().writeValue(file, tasks);

    } catch (IOException e) {
      e.printStackTrace();
    }

  }

}

package com.example.persistence;

import com.example.model.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedList;
import java.io.File;
import java.io.IOException;

public class UserRepository {

  public static final ObjectMapper m = JsonConfig.newMapper();

  public static LinkedList<User> loadUser(int userId) {
    try {
      File file = new File("src/main/resources/data/users/" + userId + ".json");
      return m.readValue(file, new TypeReference<LinkedList<User>>() {
      });
    } catch (IOException e) {
      e.printStackTrace();
      return new LinkedList<>();
    }
  }

  public static void saveUser(LinkedList<User> users, int userId) {
    try {
      File file = new File("src/main/resources/data/users/" + userId + ".json");
      // Ensure the directory exists
      file.getParentFile().mkdirs();
      m.writerWithDefaultPrettyPrinter().writeValue(file, users);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

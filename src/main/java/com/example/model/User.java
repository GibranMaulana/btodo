package com.example.model;

import java.util.Queue;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.LinkedList;

public class User {
  @JsonProperty("id")
  UUID id;

  @JsonProperty("username")
  String username;

  @JsonProperty("password")
  String password;

  @JsonProperty("tasks")
  Queue<Task> tasks;

  public User() {
  }

  public User(String username, String password) {
    this.id = UUID.randomUUID();
    this.username = username;
    this.password = password;
    this.tasks = new LinkedList<>();
  }

  public UUID getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public Queue<Task> getTasks() {
    return tasks;
  }

  public void setUsername(String username) {
    this.username = username;

  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setTasks(Queue<Task> tasks) {
    this.tasks = tasks;
  }

  public void addTask(Task task) {
    tasks.add(task);
  }

  @Override
  public String toString() {
    return this.username + " (" + this.id + ")" + tasks.toString();
  }
}

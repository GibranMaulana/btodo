package com.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.LinkedList;

public class Task {
  @JsonProperty("id")
  private UUID id;
  @JsonProperty("title")
  private String title;
  @JsonProperty("description")
  private String description;
  @JsonProperty("status")
  private String status;
  @JsonProperty("created_at")
  private LocalDateTime createdAt;
  @JsonProperty("due_date")
  private LocalDateTime dueDate;
  @JsonProperty("priority")
  private int priority;
  @JsonProperty("tags")
  private LinkedList<String> tags;

  public Task() {
  }

  public Task(String title, String description, String status,
      LocalDateTime createdAt, LocalDateTime dueDate, int priority,
      LinkedList<String> tags) {
    this.id = UUID.randomUUID();
    this.title = title;
    this.description = description;
    this.status = status;
    this.createdAt = createdAt;
    this.dueDate = dueDate;
    this.priority = priority;
    this.tags = tags;
  }

  public UUID getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public String getStatus() {
    return status;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getDueDate() {
    return dueDate;
  }

  public int getPriority() {
    return priority;
  }

  public LinkedList<String> getTags() {
    return tags;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public void setDueDate(LocalDateTime dueDate) {
    this.dueDate = dueDate;
  }

  public void setPriority(int priority) {
    this.priority = priority;
  }

  public void setTags(LinkedList<String> tags) {
    this.tags = tags;
  }

  @Override
  public String toString() {
    return "Task{" +
        "id=" + id +
        ", title='" + title + '\'' +
        ", description='" + description + '\'' +
        ", status='" + status + '\'' +
        ", createdAt=" + createdAt +
        ", dueDate=" + dueDate +
        ", priority=" + priority +
        ", tags=" + tags +
        '}';
  }

}

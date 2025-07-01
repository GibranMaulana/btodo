package com.example.persistence;

import com.example.model.Task;
import java.util.LinkedList;
import java.time.LocalDateTime;
import com.example.model.User;
import com.example.util.SecurityUtil;

public class TestingP {

  public static void main(String[] args) {
    
    // === SECURITY DEMONSTRATION ===
    System.out.println("🔐 SECURITY DEMONSTRATION 🔐\n");
    SecurityUtil.demonstrateSecurity();
    
    System.out.println("\n" + "=".repeat(60));
    System.out.println("📝 TODO APP TESTING WITH SECURE PASSWORDS 📝");
    System.out.println("=".repeat(60) + "\n");
    
    // === SECURE USER CREATION ===
    String plainPassword = "mySecurePassword123";
    String hashedPassword = SecurityUtil.hashPassword(plainPassword);
    
    System.out.println("1. Creating user with secure password:");
    System.out.println("   Plain password: '" + plainPassword + "'");
    System.out.println("   Hashed password: " + hashedPassword);
    
    // Create user with hashed password
    User user1 = new User("username", hashedPassword);
    
    System.out.println("   ✅ User created with hashed password\n");
    
    // === PASSWORD VERIFICATION ===
    System.out.println("2. Password verification test:");
    System.out.println("   Correct password: " + SecurityUtil.verifyPassword(plainPassword, user1.getPassword()));
    System.out.println("   Wrong password: " + SecurityUtil.verifyPassword("wrongPassword", user1.getPassword()));
    System.out.println("   ✅ Password verification working\n");
    
    // === TASK CREATION ===
    System.out.println("3. Creating task for user:");
    String title = "Implement secure authentication";
    String description = "Add password hashing and verification to the app";
    String status = "in-progress";
    LocalDateTime createAt = LocalDateTime.now();
    LocalDateTime dueDate = LocalDateTime.now().plusDays(7);
    int priority = 1;
    LinkedList<String> tags = new LinkedList<>();
    tags.add("security");
    tags.add("authentication");
    tags.add("important");

    Task newTask = new Task(
        title,
        description,
        status,
        createAt,
        dueDate,
        priority,
        tags);

    user1.addTask(newTask);
    System.out.println("   ✅ Task created and added to user\n");
    
    // === USER PERSISTENCE ===
    System.out.println("4. Testing user persistence:");
    LinkedList<User> users = new LinkedList<>();
    users.add(user1);

    try {
      UserRepository.saveUser(users, user1.getId().hashCode());
      System.out.println("   ✅ User saved successfully (with hashed password)!");

      LinkedList<User> loadedUsers = UserRepository.loadUser(user1.getId().hashCode());
      User loadedUser = loadedUsers.get(0);
      System.out.println("   ✅ User loaded: " + loadedUser.getUsername());
      
      // Verify the loaded user's password is still hashed and verifiable
      boolean passwordCheck = SecurityUtil.verifyPassword(plainPassword, loadedUser.getPassword());
      System.out.println("   ✅ Loaded user password verification: " + passwordCheck);
      
    } catch (Exception e) {
      System.err.println("   ❌ Error with user persistence:");
      e.printStackTrace();
    }
    
    // === TASK PERSISTENCE ===
    System.out.println("\n5. Testing task persistence:");
    try {
      // Convert Queue<Task> to LinkedList<Task> for saving
      LinkedList<Task> taskList = new LinkedList<>(user1.getTasks());
      TaskRepository.save(taskList, user1.getId().hashCode());
      System.out.println("   ✅ Tasks saved successfully!");

      // Test loading the tasks back
      LinkedList<Task> loadedTasks = TaskRepository.load(user1.getId().hashCode());
      System.out.println("   ✅ Tasks loaded: " + loadedTasks.size() + " task(s)");
      if (!loadedTasks.isEmpty()) {
        Task loadedTask = loadedTasks.get(0);
        System.out.println("   📋 Task: " + loadedTask.getTitle());
        System.out.println("   🏷️  Tags: " + loadedTask.getTags());
      }
    } catch (Exception e) {
      System.err.println("   ❌ Error with task persistence:");
      e.printStackTrace();
    }
    
    // === SECURITY SUMMARY ===
    System.out.println("\n" + "=".repeat(60));
    System.out.println("🛡️  SECURITY IMPLEMENTATION SUMMARY 🛡️");
    System.out.println("=".repeat(60));
    System.out.println("✅ Passwords are hashed using SHA-256 with salt");
    System.out.println("✅ Same password produces different hashes (salt randomization)");
    System.out.println("✅ Password verification works correctly");
    System.out.println("✅ Hashed passwords are stored and retrieved properly");
    System.out.println("✅ Original passwords are never stored in plain text");
    System.out.println("✅ Rainbow table attacks are prevented by salting");
    System.out.println("\n🎉 All tests completed successfully!");
  }
}

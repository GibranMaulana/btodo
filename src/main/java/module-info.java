module com.example {
  // JavaFX modules
  requires javafx.controls;
  requires javafx.fxml;

  // Jackson for JSON (core + annotations + databind)
  requires com.fasterxml.jackson.databind;
  requires com.fasterxml.jackson.core;
  requires com.fasterxml.jackson.annotation;
  requires com.fasterxml.jackson.datatype.jsr310; // for Java 8+ time types

  // Java standard APIs you use
  requires java.logging;
  requires java.base; // implied, but you can list it explicitly

  // Allow JavaFX FXML loader to reflectively access your UI controllers
  opens com.example.ui to javafx.fxml;

  // Allow Jackson to read/write your model POJOs
  opens com.example.model to
      com.fasterxml.jackson.databind,
      com.fasterxml.jackson.core,
      com.fasterxml.jackson.annotation,
      com.fasterxml.jackson.datatype.jsr310;

  opens com.example.persistence to
      com.fasterxml.jackson.databind,
      com.fasterxml.jackson.core,
      com.fasterxml.jackson.annotation,
      com.fasterxml.jackson.datatype.jsr310;
}

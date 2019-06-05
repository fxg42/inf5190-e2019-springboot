package ca.uqam.inf5190.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

  @Id private Long id;
  private String isbn;
  private String title;

  public Book(final Book source) {
    this(source.getId(), source.getIsbn(), source.getTitle());
  }
}

package ca.uqam.inf5190.demo.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.Builder;
import lombok.Data;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import ca.uqam.inf5190.demo.model.Book;
import ca.uqam.inf5190.demo.repository.BookRepository;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(produces = "application/json")
public class BookController {

  @Autowired private BookRepository bookRepository;

  @GetMapping("/api/books")
  public List<BookDTO> list() {
    return bookRepository.findAll().stream()
      .map(BookDTO::of)
      .collect(toList());
  }

  @GetMapping("/api/book/{id}")
  public ResponseEntity<BookDTO> findById(@PathVariable("id") final Long id) {
    final var foundBook = bookRepository.findById(id);
    if (foundBook.isPresent()) {
      return ResponseEntity.ok(BookDTO.of(foundBook.get()));
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/api/book/isbn/{isbn}")
  public ResponseEntity<BookDTO> findByIsbn(@PathVariable("isbn") @Pattern(regexp = "\\d{10}|\\d{13}") final String isbn) {
    return bookRepository
      .findByIsbn(isbn)
        .map(BookDTO::of)
        .map(ResponseEntity::ok)
      .orElseGet(ResponseEntity.notFound()::build);
  }

  @PostMapping(value = "/api/books", consumes = "application/json")
  public ResponseEntity createNew(@RequestBody @Valid final CreateBookForm form) throws Exception {
    final var bookToCreate = form.merge(new Book());
    final var createdBook = bookRepository.save(bookToCreate);
    final var location = getLocationUrlForBook(createdBook);
    return ResponseEntity.created(location).build();
  }

  @DeleteMapping("/api/book/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteById(@PathVariable("id") final Long id) {
    bookRepository.deleteById(id);
  }

  @PutMapping(value = "/api/book/{id}", consumes = "application/json")
  public ResponseEntity<BookDTO> update(
      @PathVariable("id") final Long id
    , @RequestBody @Valid final UpdateBookForm form
  ) {
    final var existingBook = bookRepository.findById(id).get();
    final var bookToUpdate = form.merge(existingBook);
    final var updatedBook = bookRepository.save(bookToUpdate);
    return ResponseEntity.ok(BookDTO.of(updatedBook));
  }

  private URI getLocationUrlForBook(final Book book) throws URISyntaxException {
    return MvcUriComponentsBuilder
      .fromMethodName(BookController.class, "findById")
      .buildAndExpand(book.getId())
      .encode()
      .toUri();
  }
}

@Data
@Builder
class BookDTO {
  private Long id;
  private String isbn;
  private String title;

  public static BookDTO of(final Book book) {
    return BookDTO.builder()
      .id(book.getId())
      .isbn(book.getIsbn())
      .title(book.getTitle())
      .build();
  }
}

@Data
class CreateBookForm {
  @Pattern(regexp = "\\d{10}|\\d{13}") private String isbn;
  @NotBlank private String title;

  public Book merge(final Book source) {
    final var book = new Book(source);
    book.setIsbn(isbn);
    book.setTitle(title);
    return book;
  }
}

@Data
class UpdateBookForm {
  @NotNull private Long id;
  @NotBlank private String isbn;
  @NotBlank private String title;

  public Book merge(final Book source) {
    final var book = new Book(source);
    book.setIsbn(isbn);
    book.setTitle(title);
    return book;
  }
}

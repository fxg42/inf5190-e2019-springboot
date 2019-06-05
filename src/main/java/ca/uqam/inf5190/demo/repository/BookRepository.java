package ca.uqam.inf5190.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import ca.uqam.inf5190.demo.model.Book;

public interface BookRepository extends CrudRepository<Book, Long> {

  @Query("select * from book")
  public List<Book> findAll();

  @Query("select * from book b where b.isbn = :isbn")
  public Optional<Book> findByIsbn(@Param("isbn") final String isbn);
}

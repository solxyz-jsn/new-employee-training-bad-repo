package jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.service;

import jp.co.solxyz.jsn.springbootadvincedexam.component.book.BookInventoryManager;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.entity.book.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class BookListServiceTest {

    @InjectMocks
    private BookListService bookListService;

    @Mock
    private BookInventoryManager bookInventoryManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("書籍が存在しない場合、空のリストが返される")
    void shouldReturnEmptyBookListWhenNoBookIsAvailable() {
        when(bookInventoryManager.getAllBooks()).thenReturn(List.of());

        List<Book> actual = bookListService.getAllBooks();

        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("書籍が1件ある場合、1件の書籍を含んだリストが返される")
    void shouldReturnBookListWhenBookIsAvailable() {
        Book expected = new Book();
        expected.setIsbn("1234567890123");
        expected.setTitle("Test Book");
        expected.setAuthor("Test Author");
        expected.setPublisher("Test Publisher");
        expected.setDescription("Test Description");
        expected.setStock(10);

        Book book = new Book();
        book.setIsbn("1234567890123");
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setPublisher("Test Publisher");
        book.setDescription("Test Description");
        book.setStock(10);

        when(bookInventoryManager.getAllBooks()).thenReturn(List.of(book));

        List<Book> actual = bookListService.getAllBooks();

        assertThat(actual).hasSize(1);
        assertThat(actual).isEqualTo(List.of(expected));

    }

    @Test
    @DisplayName("書籍が複数件ある場合、複数件の書籍を含んだリストが返される")
    void shouldReturnBookListWhenMultipleBooksAreAvailable() {
        Book expected1 = new Book();
        expected1.setIsbn("1234567890123");
        expected1.setTitle("Test Book1");
        expected1.setAuthor("Test Author1");
        expected1.setPublisher("Test Publisher1");
        expected1.setDescription("Test Description1");
        expected1.setStock(10);
        Book expected2 = new Book();
        expected2.setIsbn("1234567890124");
        expected2.setTitle("Test Book2");
        expected2.setAuthor("Test Author2");
        expected2.setPublisher("Test Publisher2");
        expected2.setDescription("Test Description2");
        expected2.setStock(20);

        Book book1 = new Book();
        book1.setIsbn("1234567890123");
        book1.setTitle("Test Book1");
        book1.setAuthor("Test Author1");
        book1.setPublisher("Test Publisher1");
        book1.setDescription("Test Description1");
        book1.setStock(10);
        Book book2 = new Book();
        book2.setIsbn("1234567890124");
        book2.setTitle("Test Book2");
        book2.setAuthor("Test Author2");
        book2.setPublisher("Test Publisher2");
        book2.setDescription("Test Description2");
        book2.setStock(20);

        when(bookInventoryManager.getAllBooks()).thenReturn(List.of(book1, book2));

        List<Book> actual = bookListService.getAllBooks();

        assertThat(actual).hasSize(2);
        assertThat(actual).isEqualTo(List.of(expected1, expected2));
    }
}

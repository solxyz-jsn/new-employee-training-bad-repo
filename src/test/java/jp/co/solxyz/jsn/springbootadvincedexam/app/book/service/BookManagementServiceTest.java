package jp.co.solxyz.jsn.springbootadvincedexam.app.book.service;

import jp.co.solxyz.jsn.springbootadvincedexam.app.admin.book.service.BookManagementService;
import jp.co.solxyz.jsn.springbootadvincedexam.app.admin.book.json.BookDetail;
import jp.co.solxyz.jsn.springbootadvincedexam.component.book.BookInventoryManager;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.entity.book.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BookManagementServiceTest {

    @InjectMocks
    private BookManagementService service;

    @Mock
    private BookInventoryManager bookServicesBizlogic;

    private final LocalDateTime TEST_TIME = LocalDateTime.of(2021, 1, 1, 0, 0, 0);

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("書籍が存在しない場合、空のリストが返される")
    void shouldReturnEmptyBookListWhenNoBookIsAvailable() {
        when(bookServicesBizlogic.getAllBooks()).thenReturn(List.of());

        List<Book> books = service.getAllBooks();

        assertThat(books).isEmpty();
    }

    @Test
    @DisplayName("書籍が1件ある場合、1件の書籍を含んだリストが返される")
    void shouldGetAllBooksSuccessfully() {
        Book expected = new Book();
        expected.setIsbn("1234567890");
        expected.setTitle("Test Book");
        expected.setAuthor("Test Author");
        expected.setPublisher("Test Publisher");
        expected.setDescription("Test Description");
        expected.setStock(10);

        Book book = new Book();
        book.setIsbn("1234567890");
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setPublisher("Test Publisher");
        book.setDescription("Test Description");
        book.setStock(10);

        when(bookServicesBizlogic.getAllBooks()).thenReturn(List.of(book));

        List<Book> books = service.getAllBooks();

        assertThat(books).hasSize(1);
        assertThat(books.get(0)).isEqualTo(expected);
    }

    @Test
    @DisplayName("書籍が複数件ある場合、複数件の書籍を含んだリストが返される")
    void shouldGetAllBooksSuccessfullyWhenMultipleBooksAreAvailable() {
        Book expected1 = new Book();
        expected1.setIsbn("1234567890");
        expected1.setTitle("Test Book 1");
        expected1.setAuthor("Test Author 1");
        expected1.setPublisher("Test Publisher 1");
        expected1.setDescription("Test Description 1");
        expected1.setStock(10);
        Book expected2 = new Book();
        expected2.setIsbn("1234567891");
        expected2.setTitle("Test Book 2");
        expected2.setAuthor("Test Author 2");
        expected2.setPublisher("Test Publisher 2");
        expected2.setDescription("Test Description 2");
        expected2.setStock(20);

        Book book1 = new Book();
        book1.setIsbn("1234567890");
        book1.setTitle("Test Book 1");
        book1.setAuthor("Test Author 1");
        book1.setPublisher("Test Publisher 1");
        book1.setDescription("Test Description 1");
        book1.setStock(10);
        Book book2 = new Book();
        book2.setIsbn("1234567891");
        book2.setTitle("Test Book 2");
        book2.setAuthor("Test Author 2");
        book2.setPublisher("Test Publisher 2");
        book2.setDescription("Test Description 2");
        book2.setStock(20);

        when(bookServicesBizlogic.getAllBooks()).thenReturn(List.of(book1, book2));

        List<Book> books = service.getAllBooks();

        assertThat(books).hasSize(2);
        assertThat(books.get(0)).isEqualTo(expected1);
        assertThat(books.get(1)).isEqualTo(expected2);
    }

    @Test
    @DisplayName("書籍情報の保存が正常に行われ、registerBookが一度呼ばれる")
    void shouldAddBookSuccessfully() {
        Book expected = new Book();
        expected.setIsbn("1234567890");
        expected.setTitle("Test Title");
        expected.setAuthor("Test Author");
        expected.setPublisher("Test Publisher");
        expected.setDescription("Test Description");
        expected.setStock(10);
        expected.setAvailableStock(10);
        expected.setCreatedAt(TEST_TIME);
        expected.setUpdatedAt(TEST_TIME);

        BookDetail form = new BookDetail();
        form.setIsbn("1234567890");
        form.setTitle("Test Title");
        form.setAuthor("Test Author");
        form.setPublisher("Test Publisher");
        form.setDescription("Test Description");
        form.setStock(10);

        try (MockedStatic<LocalDateTime> mocked = Mockito.mockStatic(LocalDateTime.class)) {
            mocked.when(LocalDateTime::now).thenReturn(TEST_TIME);
            service.addBook(form);
        } catch (Exception e) {
            fail();
        }

        verify(bookServicesBizlogic, times(1)).registerBook(expected);
    }

    @Test
    @DisplayName("書籍情報の更新が正常に行われ、updateBookが一度呼ばれる")
    void shouldUpdateBookSuccessfully() {
        Book expected = new Book();
        expected.setIsbn("1234567890");
        expected.setTitle("Test Title");
        expected.setAuthor("Test Author");
        expected.setPublisher("Test Publisher");
        expected.setDescription("Test Description");
        expected.setStock(10);
        expected.setAvailableStock(10);
        expected.setUpdatedAt(TEST_TIME);

        BookDetail form = new BookDetail();
        form.setIsbn("1234567890");
        form.setTitle("Test Title");
        form.setAuthor("Test Author");
        form.setPublisher("Test Publisher");
        form.setDescription("Test Description");
        form.setStock(10);

        Book currentBook = new Book();
        currentBook.setIsbn("1234567890");
        currentBook.setTitle("Test Title");
        currentBook.setAuthor("Test Author");
        currentBook.setPublisher("Test Publisher");
        currentBook.setDescription("Test Description");
        currentBook.setStock(10);
        currentBook.setAvailableStock(10);
        currentBook.setUpdatedAt(TEST_TIME);

        when(bookServicesBizlogic.getBookByIsbn(form.getIsbn())).thenReturn(currentBook);
        doNothing().when(bookServicesBizlogic).compareInputStockLowerThanCurrent(form.getStock(), currentBook.getAvailableStock());

        try (MockedStatic<LocalDateTime> mocked = Mockito.mockStatic(LocalDateTime.class)) {
            mocked.when(LocalDateTime::now).thenReturn(TEST_TIME);
            service.updateBook(form);
        } catch (Exception e) {
            fail();
        }

        verify(bookServicesBizlogic, times(1)).updateBook(expected, TEST_TIME);
    }

    @Test
    @DisplayName("変更する在庫数が現在の利用可能な在庫数より多い場合、updateBookが一度呼ばれる")
    void shouldUpdateBookSuccessfullyWhenInputStockIsHigherThanCurrentStock() {
        Book expected = new Book();
        expected.setIsbn("1234567890");
        expected.setTitle("Test Title");
        expected.setAuthor("Test Author");
        expected.setPublisher("Test Publisher");
        expected.setDescription("Test Description");
        expected.setStock(15);
        expected.setAvailableStock(15);
        expected.setUpdatedAt(TEST_TIME);

        BookDetail form = new BookDetail();
        form.setIsbn("1234567890");
        form.setTitle("Test Title");
        form.setAuthor("Test Author");
        form.setPublisher("Test Publisher");
        form.setDescription("Test Description");
        form.setStock(15);

        Book currentBook = new Book();
        currentBook.setIsbn("1234567890");
        currentBook.setTitle("Test Title");
        currentBook.setAuthor("Test Author");
        currentBook.setPublisher("Test Publisher");
        currentBook.setDescription("Test Description");
        currentBook.setStock(10);
        currentBook.setAvailableStock(10);
        currentBook.setCreatedAt(TEST_TIME);
        currentBook.setUpdatedAt(TEST_TIME);

        when(bookServicesBizlogic.getBookByIsbn(form.getIsbn())).thenReturn(currentBook);
        doNothing().when(bookServicesBizlogic).compareInputStockLowerThanCurrent(form.getStock(), currentBook.getAvailableStock());

        try (MockedStatic<LocalDateTime> mocked = Mockito.mockStatic(LocalDateTime.class)) {
            mocked.when(LocalDateTime::now).thenReturn(TEST_TIME);
            service.updateBook(form);
        } catch (Exception e) {
            fail();
        }

        verify(bookServicesBizlogic, times(1)).updateBook(expected, TEST_TIME);
    }

    @Test
    @DisplayName("変更する在庫数が現在の利用可能な在庫数より少ない場合、IllegalArgumentExceptionが発生する")
    void shouldThrowIllegalArgumentExceptionWhenInputStockIsLowerThanCurrentStock() {
        BookDetail form = new BookDetail();
        form.setIsbn("1234567890");
        form.setTitle("Test Title");
        form.setAuthor("Test Author");
        form.setPublisher("Test Publisher");
        form.setDescription("Test Description");
        form.setStock(5);

        Book currentBook = new Book();
        currentBook.setIsbn("1234567890");
        currentBook.setTitle("Test Title");
        currentBook.setAuthor("Test Author");
        currentBook.setPublisher("Test Publisher");
        currentBook.setDescription("Test Description");
        currentBook.setStock(10);
        currentBook.setAvailableStock(10);
        currentBook.setCreatedAt(TEST_TIME);
        currentBook.setUpdatedAt(TEST_TIME);

        when(bookServicesBizlogic.getBookByIsbn(form.getIsbn())).thenReturn(currentBook);
        doThrow(IllegalArgumentException.class).when(bookServicesBizlogic).compareInputStockLowerThanCurrent(form.getStock(), currentBook.getAvailableStock());

        try (MockedStatic<LocalDateTime> mocked = Mockito.mockStatic(LocalDateTime.class)) {
            mocked.when(LocalDateTime::now).thenReturn(TEST_TIME);
            service.updateBook(form);
            fail();
        } catch (IllegalArgumentException e) {
            verify(bookServicesBizlogic, times(1)).compareInputStockLowerThanCurrent(form.getStock(), currentBook.getAvailableStock());
        } catch (Exception e) {
            fail();
        }
        verify(bookServicesBizlogic, never()).updateBook(any(), any());
    }

    @Test
    @DisplayName("書籍情報の削除が正常に行われ、deleteByIdが一度呼ばれる")
    void shouldDeleteBookSuccessfully() {
        String isbn = "1234567890";

        doNothing().when(bookServicesBizlogic).deleteById(isbn);

        try {
            service.deleteBook(isbn);
        } catch (Exception e) {
            fail();
        }

        verify(bookServicesBizlogic, times(1)).deleteById(isbn);
    }
}

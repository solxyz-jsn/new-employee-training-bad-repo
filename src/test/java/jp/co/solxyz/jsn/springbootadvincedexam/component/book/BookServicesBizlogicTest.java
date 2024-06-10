package jp.co.solxyz.jsn.springbootadvincedexam.component.book;

import jp.co.solxyz.jsn.springbootadvincedexam.infra.entity.book.Book;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.entity.book.BookCheckoutHistory;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.reposiroty.book.BookCheckoutHistoryRepository;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.reposiroty.book.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BookServicesBizlogicTest {

    @InjectMocks
    BookInventoryManager bookServicesBizlogic;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookCheckoutHistoryRepository bookCheckoutHistoryRepository;

    private final LocalDateTime TEST_TIME = LocalDateTime.of(2021, 1, 1, 0, 0, 0);

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("1件の書籍がある場合、1件の書籍を含んだリストを返す")
    void shouldGetAllBooksSuccessfully() {
        Book expectedBook = new Book();
        expectedBook.setIsbn("1234567890123");
        expectedBook.setTitle("Test Title");
        expectedBook.setAuthor("Test Author");
        expectedBook.setPublisher("Test Publisher");
        expectedBook.setStock(10);
        expectedBook.setAvailableStock(10);
        expectedBook.setDescription("Test Description");
        expectedBook.setCreatedAt(TEST_TIME);
        expectedBook.setUpdatedAt(TEST_TIME);

        Book book = new Book();
        book.setIsbn("1234567890123");
        book.setTitle("Test Title");
        book.setAuthor("Test Author");
        book.setPublisher("Test Publisher");
        book.setStock(10);
        book.setAvailableStock(10);
        book.setDescription("Test Description");
        book.setCreatedAt(TEST_TIME);
        book.setUpdatedAt(TEST_TIME);

        when(bookRepository.findAll()).thenReturn(List.of(book));

        List<Book> books = bookServicesBizlogic.getAllBooks();

        verify(bookRepository, times(1)).findAll();
        assertThat(books).hasSize(1);
        assertThat(books.get(0)).isEqualTo(expectedBook);
    }

    @Test
    @DisplayName("複数の書籍がある場合、複数の書籍を含んだリストを返す")
    void shouldGetMultipleBooksSuccessfully() {
        Book expectedBook1 = new Book();
        expectedBook1.setIsbn("1234567890123");
        expectedBook1.setTitle("Test Title");
        expectedBook1.setAuthor("Test Author");
        expectedBook1.setPublisher("Test Publisher");
        expectedBook1.setStock(10);
        expectedBook1.setAvailableStock(10);
        expectedBook1.setDescription("Test Description");
        expectedBook1.setCreatedAt(TEST_TIME);
        expectedBook1.setUpdatedAt(TEST_TIME);
        Book expectedBook2 = new Book();
        expectedBook2.setIsbn("1234567890124");
        expectedBook2.setTitle("Test Title");
        expectedBook2.setAuthor("Test Author");
        expectedBook2.setPublisher("Test Publisher");
        expectedBook2.setStock(20);
        expectedBook2.setAvailableStock(20);
        expectedBook2.setDescription("Test Description");
        expectedBook2.setCreatedAt(TEST_TIME);
        expectedBook2.setUpdatedAt(TEST_TIME);

        Book book1 = new Book();
        book1.setIsbn("1234567890123");
        book1.setTitle("Test Title");
        book1.setAuthor("Test Author");
        book1.setPublisher("Test Publisher");
        book1.setStock(10);
        book1.setAvailableStock(10);
        book1.setDescription("Test Description");
        book1.setCreatedAt(TEST_TIME);
        book1.setUpdatedAt(TEST_TIME);
        Book book2 = new Book();
        book2.setIsbn("1234567890124");
        book2.setTitle("Test Title");
        book2.setAuthor("Test Author");
        book2.setPublisher("Test Publisher");
        book2.setStock(20);
        book2.setAvailableStock(20);
        book2.setDescription("Test Description");
        book2.setCreatedAt(TEST_TIME);
        book2.setUpdatedAt(TEST_TIME);

        when(bookRepository.findAll()).thenReturn(List.of(book1, book2));

        List<Book> books = bookServicesBizlogic.getAllBooks();

        verify(bookRepository, times(1)).findAll();
        assertThat(books).hasSize(2);
        assertThat(books.get(0)).isEqualTo(expectedBook1);
        assertThat(books.get(1)).isEqualTo(expectedBook2);
    }

    @Test
    @DisplayName("ISBNリストから一致する1件の書籍を取得する")
    void shouldGetBooksByIsbnSuccessfully() {
        Book expectedBook = new Book();
        expectedBook.setIsbn("1234567890123");
        expectedBook.setTitle("Test Title");
        expectedBook.setAuthor("Test Author");
        expectedBook.setPublisher("Test Publisher");
        expectedBook.setStock(10);
        expectedBook.setAvailableStock(10);
        expectedBook.setDescription("Test Description");
        expectedBook.setCreatedAt(TEST_TIME);
        expectedBook.setUpdatedAt(TEST_TIME);
        List<String> expectedIsbnList = List.of(expectedBook.getIsbn());

        Book book = new Book();
        book.setIsbn("1234567890123");
        book.setTitle("Test Title");
        book.setAuthor("Test Author");
        book.setPublisher("Test Publisher");
        book.setStock(10);
        book.setAvailableStock(10);
        book.setDescription("Test Description");
        book.setCreatedAt(TEST_TIME);
        book.setUpdatedAt(TEST_TIME);
        List<String> isbnList = List.of(book.getIsbn());

        when(bookRepository.findAllById(isbnList)).thenReturn(List.of(book));

        List<Book> books = bookServicesBizlogic.getBooksByIsbn(isbnList);

        verify(bookRepository, times(1)).findAllById(expectedIsbnList);
        assertThat(books).hasSize(1);
        assertThat(books.get(0)).isEqualTo(expectedBook);
    }

    @Test
    @DisplayName("ISBNリストから一致する複数の書籍を取得する")
    void shouldGetMultipleBooksByIsbn() {
        Book expectedBook1 = new Book();
        expectedBook1.setIsbn("1234567890123");
        expectedBook1.setTitle("Test Title 1");
        expectedBook1.setAuthor("Test Author 1");
        expectedBook1.setPublisher("Test Publisher 1");
        expectedBook1.setStock(10);
        expectedBook1.setAvailableStock(10);
        expectedBook1.setDescription("Test Description 1");
        expectedBook1.setCreatedAt(TEST_TIME);
        expectedBook1.setUpdatedAt(TEST_TIME);
        Book expectedBook2 = new Book();
        expectedBook2.setIsbn("1234567890124");
        expectedBook2.setTitle("Test Title 2");
        expectedBook2.setAuthor("Test Author 2");
        expectedBook2.setPublisher("Test Publisher 2");
        expectedBook2.setStock(20);
        expectedBook2.setAvailableStock(20);
        expectedBook2.setDescription("Test Description 2");
        expectedBook2.setCreatedAt(TEST_TIME);
        expectedBook2.setUpdatedAt(TEST_TIME);
        List<String> expectedIsbnList = List.of(expectedBook1.getIsbn(), expectedBook2.getIsbn());

        Book book1 = new Book();
        book1.setIsbn("1234567890123");
        book1.setTitle("Test Title 1");
        book1.setAuthor("Test Author 1");
        book1.setPublisher("Test Publisher 1");
        book1.setStock(10);
        book1.setAvailableStock(10);
        book1.setDescription("Test Description 1");
        book1.setCreatedAt(TEST_TIME);
        book1.setUpdatedAt(TEST_TIME);
        Book book2 = new Book();
        book2.setIsbn("1234567890124");
        book2.setTitle("Test Title 2");
        book2.setAuthor("Test Author 2");
        book2.setPublisher("Test Publisher 2");
        book2.setStock(20);
        book2.setAvailableStock(20);
        book2.setDescription("Test Description 2");
        book2.setCreatedAt(TEST_TIME);
        book2.setUpdatedAt(TEST_TIME);
        List<String> isbnList = List.of(book1.getIsbn(), book2.getIsbn());

        when(bookRepository.findAllById(isbnList)).thenReturn(List.of(book1, book2));

        List<Book> books = bookServicesBizlogic.getBooksByIsbn(isbnList);

        verify(bookRepository, times(1)).findAllById(expectedIsbnList);
        assertThat(books).hasSize(2);
        assertThat(books.get(0)).isEqualTo(expectedBook1);
        assertThat(books.get(1)).isEqualTo(expectedBook2);
    }

    @Test
    @DisplayName("存在するISBNから書籍を1件取得する")
    void shouldFindBookByExistingIsbn() {
        Book expectedBook = new Book();
        expectedBook.setIsbn("1234567890123");
        expectedBook.setTitle("Test Title");
        expectedBook.setAuthor("Test Author");
        expectedBook.setPublisher("Test Publisher");
        expectedBook.setStock(10);
        expectedBook.setAvailableStock(10);
        expectedBook.setDescription("Test Description");
        expectedBook.setCreatedAt(TEST_TIME);
        expectedBook.setUpdatedAt(TEST_TIME);

        Book book = new Book();
        book.setIsbn("1234567890123");
        book.setTitle("Test Title");
        book.setAuthor("Test Author");
        book.setPublisher("Test Publisher");
        book.setStock(10);
        book.setAvailableStock(10);
        book.setDescription("Test Description");
        book.setCreatedAt(TEST_TIME);
        book.setUpdatedAt(TEST_TIME);

        when(bookRepository.findById(book.getIsbn())).thenReturn(Optional.of(book));

        Book result = bookServicesBizlogic.getBookByIsbn(book.getIsbn());

        verify(bookRepository, times(1)).findById(expectedBook.getIsbn());
        assertThat(result).isEqualTo(expectedBook);
    }

    @Test
    @DisplayName("存在しないISBNから書籍を取得しようとするとNoSuchElementExceptionが発生する")
    void shouldThrowExceptionWhenGetBookByNonexistentIsbn() {
        String isbn = "1234567890123";

        when(bookRepository.findById(isbn)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookServicesBizlogic.getBookByIsbn(isbn))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("指定されたISBNの書籍が存在しない、または削除されています。");
    }

    @Test
    @DisplayName("書籍情報の登録が正常に終了する")
    void shouldRegisterBookSuccessfully() {
        Book expectedBook = new Book();
        expectedBook.setIsbn("1234567890123");
        expectedBook.setTitle("Test Title");
        expectedBook.setAuthor("Test Author");
        expectedBook.setPublisher("Test Publisher");
        expectedBook.setStock(10);
        expectedBook.setAvailableStock(10);
        expectedBook.setDescription("Test Description");
        expectedBook.setUpdatedAt(TEST_TIME);

        Book book = new Book();
        book.setIsbn("1234567890123");
        book.setTitle("Test Title");
        book.setAuthor("Test Author");
        book.setPublisher("Test Publisher");
        book.setStock(10);
        book.setAvailableStock(10);
        book.setDescription("Test Description");
        book.setUpdatedAt(TEST_TIME);

        when(bookRepository.findById(book.getIsbn())).thenReturn(Optional.empty());
        when(bookRepository.save(book)).thenReturn(book);

        try {
            bookServicesBizlogic.registerBook(book);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        verify(bookRepository, times(1)).findById(expectedBook.getIsbn());
        verify(bookRepository, times(1)).save(expectedBook);
    }

    @Test
    @DisplayName("書籍の登録時にISBNの値が重複している場合、IllegalArgumentExceptionが発生する")
    void shouldThrowIllegalArgumentExceptionWhenEmailIsDuplicated() {
        Book book = new Book();
        book.setIsbn("1234567890123");

        when(bookRepository.findById(book.getIsbn())).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> bookServicesBizlogic.registerBook(book))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("指定されたISBNの書籍が既に存在します。");
        verify(bookRepository, never()).save(any());
    }

    @Test
    @DisplayName("DBへの接続ができない場合、DataAccessExceptionが発生する")
    void shouldThrowDataAccessExceptionWhenCannotConnectToDB() {
        Book book = new Book();
        book.setIsbn("1234567890123");

        when(bookRepository.findById(book.getIsbn())).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenThrow(new DataAccessException("DBへの接続ができませんでした。") {
        });

        assertThatThrownBy(() -> bookServicesBizlogic.registerBook(book))
                .isInstanceOf(DataAccessException.class).hasMessageContaining("DBへの接続ができませんでした。");
    }

    @Test
    @DisplayName("書籍情報の更新が正常に行われる")
    void shouldUpdateBookSuccessfully() {
        Book expectedBook = new Book();
        expectedBook.setIsbn("1234567890123");
        expectedBook.setTitle("Test Title");
        expectedBook.setAuthor("Test Author");
        expectedBook.setPublisher("Test Publisher");
        expectedBook.setStock(10);
        expectedBook.setAvailableStock(10);
        expectedBook.setDescription("Test Description");
        expectedBook.setUpdatedAt(TEST_TIME);

        Book book = new Book();
        book.setIsbn("1234567890123");
        book.setTitle("Test Title");
        book.setAuthor("Test Author");
        book.setPublisher("Test Publisher");
        book.setStock(10);
        book.setAvailableStock(10);
        book.setDescription("Test Description");
        book.setUpdatedAt(TEST_TIME);

        when(bookRepository.findById(book.getIsbn())).thenReturn(Optional.of(book));
        when(bookRepository.updateBook(book.getIsbn(), book.getTitle(), book.getAuthor(), book.getPublisher(), book.getStock(),
                book.getAvailableStock(), book.getDescription(), book.getUpdatedAt(), TEST_TIME)).thenReturn(1);

        bookServicesBizlogic.updateBook(book, TEST_TIME);

        verify(bookRepository, times(1)).findById(expectedBook.getIsbn());
        verify(bookRepository, times(1)).updateBook(expectedBook.getIsbn(), expectedBook.getTitle(), expectedBook.getAuthor(),
                expectedBook.getPublisher(), expectedBook.getStock(),
                expectedBook.getAvailableStock(), expectedBook.getDescription(), expectedBook.getUpdatedAt(), TEST_TIME);
    }

    @Test
    @DisplayName("存在しないISBNから書籍情報の更新を行おうとするとNoSuchElementExceptionが発生する")
    void shouldThrowNoSuchElementExceptionWhenUpdateNonexistentBook() {
        Book expectedBook = new Book();
        expectedBook.setIsbn("1234567890123");

        Book book = new Book();
        book.setIsbn("1234567890123");

        when(bookRepository.findById(book.getIsbn())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookServicesBizlogic.updateBook(book, TEST_TIME))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("指定されたISBNの書籍が存在しない、または削除されています。");
        verify(bookRepository, times(1)).findById(expectedBook.getIsbn());
        verify(bookRepository, never()).updateBook(any(), any(), any(), any(), anyInt(), anyInt(), any(), any(), any());
    }

    @Test
    @DisplayName("updateBookを呼んだ際にDBへの接続ができない場合、DataAccessExceptionが発生する")
    void shouldThrowDataAccessExceptionWhenCannotConnectToDBOnUpdate() {
        Book expectedBook = new Book();
        expectedBook.setIsbn("1234567890123");
        expectedBook.setTitle("Test Title");
        expectedBook.setAuthor("Test Author");
        expectedBook.setPublisher("Test Publisher");
        expectedBook.setStock(10);
        expectedBook.setAvailableStock(10);
        expectedBook.setDescription("Test Description");
        expectedBook.setUpdatedAt(TEST_TIME);

        Book book = new Book();
        book.setIsbn("1234567890123");
        book.setTitle("Test Title");
        book.setAuthor("Test Author");
        book.setPublisher("Test Publisher");
        book.setStock(10);
        book.setAvailableStock(10);
        book.setDescription("Test Description");
        book.setUpdatedAt(TEST_TIME);

        when(bookRepository.findById(book.getIsbn())).thenReturn(Optional.of(book));
        when(bookRepository.updateBook(book.getIsbn(), book.getTitle(), book.getAuthor(), book.getPublisher(), book.getStock(),
                book.getAvailableStock(), book.getDescription(), book.getUpdatedAt(), TEST_TIME)).thenThrow(
                new DataAccessException("DBへの接続ができませんでした。") {
                });

        assertThatThrownBy(() -> bookServicesBizlogic.updateBook(book, TEST_TIME))
                .isInstanceOf(DataAccessException.class)
                .hasMessageContaining("DBへの接続ができませんでした。");
        verify(bookRepository, times(1)).findById(expectedBook.getIsbn());
        verify(bookRepository, times(1)).updateBook(expectedBook.getIsbn(), expectedBook.getTitle(), expectedBook.getAuthor(),
                expectedBook.getPublisher(), expectedBook.getStock(),
                expectedBook.getAvailableStock(), expectedBook.getDescription(), expectedBook.getUpdatedAt(), TEST_TIME);
    }

    @Test
    @DisplayName("楽観ロックが機能した場合、OptimisticLockingFailureExceptionが発生する")
    void shouldThrowOptimisticLockingFailureExceptionWhenOptimisticLockingFailed() {
        Book expectedBook = new Book();
        expectedBook.setIsbn("1234567890123");
        expectedBook.setTitle("Test Title");
        expectedBook.setAuthor("Test Author");
        expectedBook.setPublisher("Test Publisher");
        expectedBook.setStock(10);
        expectedBook.setAvailableStock(10);
        expectedBook.setDescription("Test Description");
        expectedBook.setUpdatedAt(TEST_TIME);

        Book book = new Book();
        book.setIsbn("1234567890123");
        book.setTitle("Test Title");
        book.setAuthor("Test Author");
        book.setPublisher("Test Publisher");
        book.setStock(10);
        book.setAvailableStock(10);
        book.setDescription("Test Description");
        book.setUpdatedAt(TEST_TIME);

        when(bookRepository.findById(book.getIsbn())).thenReturn(Optional.of(book));
        when(bookRepository.updateBook(book.getIsbn(), book.getTitle(), book.getAuthor(), book.getPublisher(), book.getStock(),
                book.getAvailableStock(), book.getDescription(), book.getUpdatedAt(), TEST_TIME)).thenReturn(0);

        assertThatThrownBy(() -> bookServicesBizlogic.updateBook(book, TEST_TIME))
                .isInstanceOf(DataAccessException.class)
                .hasMessageContaining("他の管理者によって更新されました。再度更新処理を行ってください。");
        verify(bookRepository, times(1)).findById(expectedBook.getIsbn());
        verify(bookRepository, times(1)).updateBook(expectedBook.getIsbn(), expectedBook.getTitle(), expectedBook.getAuthor(),
                expectedBook.getPublisher(), expectedBook.getStock(),
                expectedBook.getAvailableStock(), expectedBook.getDescription(), expectedBook.getUpdatedAt(), TEST_TIME);
    }

    @Test
    @DisplayName("書籍情報の削除が正常に終了する")
    void shouldDeleteBookSuccessfully() {
        Book expectedBook = new Book();
        expectedBook.setIsbn("1234567890123");

        Book book = new Book();
        book.setIsbn("1234567890123");

        when(bookRepository.findById(book.getIsbn())).thenReturn(Optional.of(book));
        when(bookCheckoutHistoryRepository.findUnreturnedBooksByIsbn(book.getIsbn())).thenReturn(Collections.emptyList());
        doNothing().when(bookRepository).delete(book);

        try {
            bookServicesBizlogic.deleteById(book.getIsbn());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        verify(bookRepository, times(1)).findById(expectedBook.getIsbn());
        verify(bookCheckoutHistoryRepository, times(1)).findUnreturnedBooksByIsbn(expectedBook.getIsbn());
        verify(bookRepository, times(1)).deleteById(expectedBook.getIsbn());
    }

    @Test
    @DisplayName("指定されたISBNに一致する書籍がない場合、NoSuchElementExceptionが発生する")
    void shouldThrowNoSuchElementExceptionWhenDeleteNonexistentBook() {
        Book book = new Book();
        book.setIsbn("1234567890123");

        when(bookRepository.findById(book.getIsbn())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookServicesBizlogic.deleteById(book.getIsbn()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("指定されたISBNの書籍が存在しない、または削除されています。");
        verify(bookRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("貸し出し中の書籍が存在する場合、IllegalStateExceptionが発生する")
    void shouldThrowIllegalStateExceptionWhenBookIsCheckedOut() {
        Book book = new Book();
        book.setIsbn("1234567890123");

        when(bookRepository.findById(book.getIsbn())).thenReturn(Optional.of(book));
        when(bookCheckoutHistoryRepository.findUnreturnedBooksByIsbn(book.getIsbn())).thenReturn(List.of(new BookCheckoutHistory()));

        assertThatThrownBy(() -> bookServicesBizlogic.deleteById(book.getIsbn()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("貸し出し中の書籍は削除できません。");
        verify(bookRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("DBへの接続ができない場合、DataAccessExceptionが発生する")
    void shouldThrowDataAccessExceptionWhenCannotConnectToDBOnDelete() {
        Book expectedBook = new Book();
        expectedBook.setIsbn("1234567890123");

        Book book = new Book();
        book.setIsbn("1234567890123");

        when(bookRepository.findById(book.getIsbn())).thenReturn(Optional.of(book));
        when(bookCheckoutHistoryRepository.findUnreturnedBooksByIsbn(book.getIsbn())).thenReturn(Collections.emptyList());
        doThrow(new DataAccessException("DBへの接続ができませんでした。") {
        }).when(bookRepository).deleteById(book.getIsbn());

        assertThatThrownBy(() -> bookServicesBizlogic.deleteById(book.getIsbn()))
                .isInstanceOf(DataAccessException.class);
        verify(bookRepository, times(1)).deleteById(expectedBook.getIsbn());
    }

    @Test
    @DisplayName("在庫数が現在の在庫数よりも少ない場合、例外が発生する")
    void shouldThrowExceptionWhenInputStockLowerThanCurrent() {
        assertThatThrownBy(() -> bookServicesBizlogic.compareInputStockLowerThanCurrent(5, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("在庫数は現在の在庫数以下の数値にはできません。　現在の在庫数: 10");
    }
}

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

public class BookMetadataManagerTest {

    @InjectMocks
    private BookMetadataManager bookMetadataManager;

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
            bookMetadataManager.registerBook(book);
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

        assertThatThrownBy(() -> bookMetadataManager.registerBook(book))
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

        assertThatThrownBy(() -> bookMetadataManager.registerBook(book))
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

        bookMetadataManager.updateBook(book, TEST_TIME);

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

        assertThatThrownBy(() -> bookMetadataManager.updateBook(book, TEST_TIME))
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

        assertThatThrownBy(() -> bookMetadataManager.updateBook(book, TEST_TIME))
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

        assertThatThrownBy(() -> bookMetadataManager.updateBook(book, TEST_TIME))
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
            bookMetadataManager.deleteByIsbn(book.getIsbn());
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

        assertThatThrownBy(() -> bookMetadataManager.deleteByIsbn(book.getIsbn()))
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

        assertThatThrownBy(() -> bookMetadataManager.deleteByIsbn(book.getIsbn()))
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

        assertThatThrownBy(() -> bookMetadataManager.deleteByIsbn(book.getIsbn()))
                .isInstanceOf(DataAccessException.class);
        verify(bookRepository, times(1)).deleteById(expectedBook.getIsbn());
    }
}

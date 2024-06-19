package jp.co.solxyz.jsn.springbootadvincedexam.component.book;

import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.model.UnreturnedBookModel;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.entity.book.Book;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.entity.book.BookCheckoutHistory;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.reposiroty.book.BookCheckoutHistoryRepository;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.reposiroty.book.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BookLendingManagerTest {

    @InjectMocks
    BookLendingManager bookLendingManager;

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
    @DisplayName("チェックアウトが正常に終了する")
    void shouldCheckoutSuccessfully() {
        String stringUUID = "00000000-0000-0000-0000-000000000000";
        UUID uuid = UUID.fromString(stringUUID);
        String userId = "userId";

        BookCheckoutHistory expectedBookCheckoutHistory = new BookCheckoutHistory();
        expectedBookCheckoutHistory.setRentalId(stringUUID);
        expectedBookCheckoutHistory.setUserId(userId);
        expectedBookCheckoutHistory.setIsbn("1234567890123");
        expectedBookCheckoutHistory.setRentalAt(TEST_TIME);
        List<BookCheckoutHistory> expectedBookCheckoutHistoryList = List.of(expectedBookCheckoutHistory);

        BookCheckoutHistory bookCheckoutHistory = new BookCheckoutHistory();
        bookCheckoutHistory.setRentalId(stringUUID);
        bookCheckoutHistory.setUserId(userId);
        bookCheckoutHistory.setIsbn("1234567890123");
        bookCheckoutHistory.setRentalAt(TEST_TIME);
        List<BookCheckoutHistory> rentalTargetCheckoutHistoryList = List.of(bookCheckoutHistory);

        Book expectedBook = new Book();
        expectedBook.setIsbn("1234567890123");
        expectedBook.setTitle("Test Title");
        expectedBook.setAuthor("Test Author");
        expectedBook.setPublisher("Test Publisher");
        expectedBook.setStock(10);
        expectedBook.setAvailableStock(0);
        expectedBook.setDescription("Test Description");
        expectedBook.setCreatedAt(TEST_TIME);
        expectedBook.setUpdatedAt(TEST_TIME);
        List<Book> expectedBookList = List.of(expectedBook);

        Book book = new Book();
        book.setIsbn("1234567890123");
        book.setTitle("Test Title");
        book.setAuthor("Test Author");
        book.setPublisher("Test Publisher");
        book.setStock(10);
        book.setAvailableStock(1);
        book.setDescription("Test Description");
        book.setCreatedAt(TEST_TIME);
        book.setUpdatedAt(TEST_TIME);

        List<Book> bookList = List.of(book);

        when(bookRepository.findAllById(List.of(book.getIsbn()))).thenReturn(bookList);
        when(bookRepository.saveAll(bookList)).thenReturn(bookList);
        when(bookCheckoutHistoryRepository.findUnreturnedBooksByUserId(userId)).thenReturn(Collections.emptyList());
        when(bookCheckoutHistoryRepository.saveAll(rentalTargetCheckoutHistoryList)).thenReturn(rentalTargetCheckoutHistoryList);

        try (MockedStatic<UUID> mockUUID = Mockito.mockStatic(UUID.class);
                MockedStatic<LocalDateTime> mockLocalDateTime = Mockito.mockStatic(LocalDateTime.class)) {
            mockUUID.when(UUID::randomUUID).thenReturn(uuid);
            mockLocalDateTime.when(LocalDateTime::now).thenReturn(TEST_TIME);

            List<Book> notCheckoutBooks = bookLendingManager.checkout(userId, List.of(book.getIsbn()));

            assertThat(notCheckoutBooks).isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        verify(bookRepository, times(1)).saveAll(expectedBookList);
        verify(bookCheckoutHistoryRepository, times(1)).saveAll(expectedBookCheckoutHistoryList);
    }

    @Test
    @DisplayName("複数の書籍を借りようとしたとき、一部がすでに借りている書籍と在庫のない書籍だった場合、それらは借りれなかった本として返される")
    void shouldReturnBooksWhenSomeBooksAreAlreadyCheckedOutOrStockIsNotEnough() {
        String stringUUID = "00000000-0000-0000-0000-000000000000";
        UUID uuid = UUID.fromString(stringUUID);
        String userId = "userId";

        BookCheckoutHistory checkoutHistory = new BookCheckoutHistory();
        checkoutHistory.setRentalId(stringUUID);
        checkoutHistory.setUserId(userId);
        checkoutHistory.setIsbn("1234567890123");
        checkoutHistory.setRentalAt(TEST_TIME);
        List<BookCheckoutHistory> rentalTargetCheckoutHistoryList = List.of(checkoutHistory);
        List<BookCheckoutHistory> expectedBookCheckoutHistoryList = List.of(checkoutHistory);

        BookCheckoutHistory userRentalBook = new BookCheckoutHistory();
        userRentalBook.setRentalId(stringUUID);
        userRentalBook.setUserId(userId);
        userRentalBook.setIsbn("1234567890124");
        userRentalBook.setRentalAt(TEST_TIME);
        List<BookCheckoutHistory> userRentalBookList = List.of(userRentalBook);

        Book expectedBook = new Book();
        expectedBook.setIsbn("1234567890123");
        expectedBook.setTitle("Test Title");
        expectedBook.setAuthor("Test Author");
        expectedBook.setPublisher("Test Publisher");
        expectedBook.setStock(10);
        expectedBook.setAvailableStock(0);
        expectedBook.setDescription("Test Description");
        expectedBook.setCreatedAt(TEST_TIME);
        expectedBook.setUpdatedAt(TEST_TIME);
        Book expectedBook2 = new Book();
        expectedBook2.setIsbn("1234567890124");
        expectedBook2.setTitle("Test Title");
        expectedBook2.setAuthor("Test Author");
        expectedBook2.setPublisher("Test Publisher");
        expectedBook2.setStock(10);
        expectedBook2.setAvailableStock(1);
        expectedBook2.setDescription("Test Description");
        expectedBook2.setCreatedAt(TEST_TIME);
        expectedBook2.setUpdatedAt(TEST_TIME);
        Book expectedBook3 = new Book();
        expectedBook3.setIsbn("1234567890125");
        expectedBook3.setTitle("Test Title");
        expectedBook3.setAuthor("Test Author");
        expectedBook3.setPublisher("Test Publisher");
        expectedBook3.setStock(10);
        expectedBook3.setAvailableStock(0);
        expectedBook3.setDescription("Test Description");
        expectedBook3.setCreatedAt(TEST_TIME);
        expectedBook3.setUpdatedAt(TEST_TIME);
        List<Book> expectedBookList = List.of(expectedBook, expectedBook2, expectedBook3);

        Book book = new Book();
        book.setIsbn("1234567890123");
        book.setTitle("Test Title");
        book.setAuthor("Test Author");
        book.setPublisher("Test Publisher");
        book.setStock(10);
        book.setAvailableStock(1);
        book.setDescription("Test Description");
        book.setCreatedAt(TEST_TIME);
        book.setUpdatedAt(TEST_TIME);
        Book alreadyCheckedOutBook = new Book();
        alreadyCheckedOutBook.setIsbn("1234567890124");
        alreadyCheckedOutBook.setTitle("Test Title");
        alreadyCheckedOutBook.setAuthor("Test Author");
        alreadyCheckedOutBook.setPublisher("Test Publisher");
        alreadyCheckedOutBook.setStock(10);
        alreadyCheckedOutBook.setAvailableStock(1);
        alreadyCheckedOutBook.setDescription("Test Description");
        alreadyCheckedOutBook.setCreatedAt(TEST_TIME);
        alreadyCheckedOutBook.setUpdatedAt(TEST_TIME);
        Book stockNotEnoughBook = new Book();
        stockNotEnoughBook.setIsbn("1234567890125");
        stockNotEnoughBook.setTitle("Test Title");
        stockNotEnoughBook.setAuthor("Test Author");
        stockNotEnoughBook.setPublisher("Test Publisher");
        stockNotEnoughBook.setStock(10);
        stockNotEnoughBook.setAvailableStock(0);
        stockNotEnoughBook.setDescription("Test Description");
        stockNotEnoughBook.setCreatedAt(TEST_TIME);
        stockNotEnoughBook.setUpdatedAt(TEST_TIME);
        List<Book> rentalTargetbookList = List.of(book, alreadyCheckedOutBook, stockNotEnoughBook);
        List<Book> notCheckoutBookList = List.of(alreadyCheckedOutBook, stockNotEnoughBook);
        List<String> isbnList = List.of(book.getIsbn(), alreadyCheckedOutBook.getIsbn(), stockNotEnoughBook.getIsbn());

        when(bookRepository.findAllById(isbnList)).thenReturn(rentalTargetbookList);
        when(bookRepository.saveAll(rentalTargetbookList)).thenReturn(rentalTargetbookList);
        when(bookCheckoutHistoryRepository.findUnreturnedBooksByUserId(userId)).thenReturn(userRentalBookList);
        when(bookCheckoutHistoryRepository.saveAll(rentalTargetCheckoutHistoryList)).thenReturn(Collections.emptyList());

        try (MockedStatic<UUID> mockUUID = Mockito.mockStatic(UUID.class);
                MockedStatic<LocalDateTime> mockLocalDateTime = Mockito.mockStatic(LocalDateTime.class)) {
            mockUUID.when(UUID::randomUUID).thenReturn(uuid);
            mockLocalDateTime.when(LocalDateTime::now).thenReturn(TEST_TIME);

            List<Book> notCheckoutBooks = bookLendingManager.checkout(userId, isbnList);
            assertThat(notCheckoutBooks).isEqualTo(notCheckoutBookList);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        verify(bookRepository, times(1)).findAllById(isbnList);
        verify(bookRepository, times(1)).saveAll(expectedBookList);
        verify(bookCheckoutHistoryRepository, times(1)).findUnreturnedBooksByUserId(userId);
        verify(bookCheckoutHistoryRepository, times(1)).saveAll(expectedBookCheckoutHistoryList);

    }

    @Test
    @DisplayName("すでに借りている書籍を借りようとした場合、借りれなかった本として返される")
    void shouldReturnBookWhenAlreadyCheckedOut() {
        String stringUUID = "00000000-0000-0000-0000-000000000000";
        UUID uuid = UUID.fromString(stringUUID);
        String userId = "userId";

        Book expectedBook = new Book();
        expectedBook.setIsbn("1234567890123");
        expectedBook.setTitle("Test Title");
        expectedBook.setAuthor("Test Author");
        expectedBook.setPublisher("Test Publisher");
        expectedBook.setStock(10);
        expectedBook.setAvailableStock(1);
        expectedBook.setDescription("Test Description");
        expectedBook.setCreatedAt(TEST_TIME);
        expectedBook.setUpdatedAt(TEST_TIME);

        Book book = new Book();
        book.setIsbn("1234567890123");
        book.setTitle("Test Title");
        book.setAuthor("Test Author");
        book.setPublisher("Test Publisher");
        book.setStock(10);
        book.setAvailableStock(1);
        book.setDescription("Test Description");
        book.setCreatedAt(TEST_TIME);
        book.setUpdatedAt(TEST_TIME);
        List<Book> rentalTargetBooklist = List.of(book);
        List<String> isbnList = List.of(book.getIsbn());

        BookCheckoutHistory userBook = new BookCheckoutHistory();
        userBook.setIsbn(book.getIsbn());
        List<BookCheckoutHistory> rentalUserBookList = List.of(userBook);

        when(bookRepository.findAllById(isbnList)).thenReturn(rentalTargetBooklist);
        when(bookRepository.saveAll(rentalTargetBooklist)).thenReturn(rentalTargetBooklist);
        when(bookCheckoutHistoryRepository.findUnreturnedBooksByUserId(userId)).thenReturn(rentalUserBookList);
        when(bookCheckoutHistoryRepository.saveAll(Collections.emptyList())).thenReturn(Collections.emptyList());

        try (MockedStatic<UUID> mockUUID = Mockito.mockStatic(UUID.class);
                MockedStatic<LocalDateTime> mockLocalDateTime = Mockito.mockStatic(LocalDateTime.class)) {
            mockUUID.when(UUID::randomUUID).thenReturn(uuid);
            mockLocalDateTime.when(LocalDateTime::now).thenReturn(TEST_TIME);

            List<Book> notCheckoutBooks = bookLendingManager.checkout(userId, isbnList);

            verify(bookRepository, times(1)).saveAll(List.of(expectedBook));
            verify(bookCheckoutHistoryRepository, times(1)).saveAll(Collections.emptyList());
            assertThat(notCheckoutBooks).hasSize(1);
            assertThat(notCheckoutBooks.get(0)).isEqualTo(expectedBook);
        }
    }

    @Test
    @DisplayName("借りようとした書籍の在庫がない場合、借りれなかった書籍として返される")
    void shouldReturnBookWhenStockIsNotEnough() {
        String stringUUID = "00000000-0000-0000-0000-000000000000";
        UUID uuid = UUID.fromString(stringUUID);
        String userId = "userId";

        Book expectedBook = new Book();
        expectedBook.setIsbn("1234567890123");
        expectedBook.setTitle("Test Title");
        expectedBook.setAuthor("Test Author");
        expectedBook.setPublisher("Test Publisher");
        expectedBook.setStock(10);
        expectedBook.setAvailableStock(0);
        expectedBook.setDescription("Test Description");
        expectedBook.setCreatedAt(TEST_TIME);
        expectedBook.setUpdatedAt(TEST_TIME);

        Book book = new Book();
        book.setIsbn("1234567890123");
        book.setTitle("Test Title");
        book.setAuthor("Test Author");
        book.setPublisher("Test Publisher");
        book.setStock(10);
        book.setAvailableStock(0);
        book.setDescription("Test Description");
        book.setCreatedAt(TEST_TIME);
        book.setUpdatedAt(TEST_TIME);
        List<Book> rentalTargetBookList = List.of(book);
        List<String> isbnList = List.of(book.getIsbn());

        when(bookRepository.findAllById(isbnList)).thenReturn(rentalTargetBookList);
        when(bookRepository.saveAll(rentalTargetBookList)).thenReturn(Collections.emptyList());
        when(bookCheckoutHistoryRepository.findUnreturnedBooksByUserId(userId)).thenReturn(Collections.emptyList());
        when(bookCheckoutHistoryRepository.saveAll(Collections.emptyList())).thenReturn(Collections.emptyList());

        try (MockedStatic<UUID> mockUUID = Mockito.mockStatic(UUID.class);
                MockedStatic<LocalDateTime> mockLocalDateTime = Mockito.mockStatic(LocalDateTime.class)) {
            mockUUID.when(UUID::randomUUID).thenReturn(uuid);
            mockLocalDateTime.when(LocalDateTime::now).thenReturn(TEST_TIME);

            List<Book> notCheckoutBooks = bookLendingManager.checkout(userId, List.of(book.getIsbn()));

            verify(bookRepository, times(1)).saveAll(List.of(expectedBook));
            verify(bookCheckoutHistoryRepository, times(1)).saveAll(Collections.emptyList());
            assertThat(notCheckoutBooks).hasSize(1);
            assertThat(notCheckoutBooks.get(0).getIsbn()).isEqualTo(expectedBook.getIsbn());
            assertThat(notCheckoutBooks.get(0).getAvailableStock()).isEqualTo(expectedBook.getAvailableStock());
        }
    }

    @Test
    @DisplayName("チェックアウト履歴の登録に失敗した場合、DataAccessExceptionのサブクラスが発生する")
    void shouldThrowDataAccessExceptionWhenFailedToSaveCheckoutHistory() {
        String stringUUID = "00000000-0000-0000-0000-000000000000";
        UUID uuid = UUID.fromString(stringUUID);
        String userId = "userId";

        Book expectedBook = new Book();
        expectedBook.setIsbn("1234567890123");
        expectedBook.setTitle("Test Title");
        expectedBook.setAuthor("Test Author");
        expectedBook.setPublisher("Test Publisher");
        expectedBook.setStock(10);
        expectedBook.setAvailableStock(0);
        expectedBook.setDescription("Test Description");
        expectedBook.setCreatedAt(TEST_TIME);
        expectedBook.setUpdatedAt(TEST_TIME);
        List<Book> expectedBookList = List.of(expectedBook);

        Book book = new Book();
        book.setIsbn("1234567890123");
        book.setTitle("Test Title");
        book.setAuthor("Test Author");
        book.setPublisher("Test Publisher");
        book.setStock(10);
        book.setAvailableStock(1);
        book.setDescription("Test Description");
        book.setCreatedAt(TEST_TIME);
        book.setUpdatedAt(TEST_TIME);
        List<Book> rentalTargetBookList = List.of(book);

        BookCheckoutHistory expectedCheckoutHistory = new BookCheckoutHistory();
        expectedCheckoutHistory.setRentalId(stringUUID);
        expectedCheckoutHistory.setUserId(userId);
        expectedCheckoutHistory.setIsbn("1234567890123");
        expectedCheckoutHistory.setRentalAt(TEST_TIME);
        List<BookCheckoutHistory> expectedCheckoutHistoryList = List.of(expectedCheckoutHistory);

        BookCheckoutHistory checkoutHistory = new BookCheckoutHistory();
        checkoutHistory.setRentalId(stringUUID);
        checkoutHistory.setUserId(userId);
        checkoutHistory.setIsbn("1234567890123");
        checkoutHistory.setRentalAt(TEST_TIME);
        List<BookCheckoutHistory> rentalTargetCheckoutHistoryList = List.of(checkoutHistory);

        List<String> isbnList = List.of(book.getIsbn());

        when(bookRepository.findAllById(isbnList)).thenReturn(rentalTargetBookList);
        when(bookRepository.saveAll(rentalTargetBookList)).thenReturn(rentalTargetBookList);
        when(bookCheckoutHistoryRepository.findUnreturnedBooksByUserId(userId)).thenReturn(Collections.emptyList());
        when(bookCheckoutHistoryRepository.saveAll(rentalTargetCheckoutHistoryList)).thenThrow(
                new DataAccessResourceFailureException("DBへの接続ができませんでした。"));

        try (MockedStatic<UUID> mockUUID = Mockito.mockStatic(UUID.class);
                MockedStatic<LocalDateTime> mockLocalDateTime = Mockito.mockStatic(LocalDateTime.class)) {
            mockUUID.when(UUID::randomUUID).thenReturn(uuid);
            mockLocalDateTime.when(LocalDateTime::now).thenReturn(TEST_TIME);

            assertThatThrownBy(() -> bookLendingManager.checkout(userId, List.of(book.getIsbn())))
                    .isInstanceOf(DataAccessException.class)
                    .hasMessageContaining("DBへの接続ができませんでした。");
        }

        verify(bookRepository, times(1)).findAllById(isbnList);
        verify(bookRepository, times(1)).saveAll(expectedBookList);
        verify(bookCheckoutHistoryRepository, times(1)).findUnreturnedBooksByUserId(userId);
        verify(bookCheckoutHistoryRepository, times(1)).saveAll(expectedCheckoutHistoryList);
    }

    @Test
    @DisplayName("ユーザIDによる未返却の書籍が1件ある場合、1件の書籍を含んだリストを返す")
    void shouldGetUnreturnedBookByUserIdSuccessfully() {
        String userId = "userId";

        BookCheckoutHistory history = new BookCheckoutHistory();
        history.setIsbn("1234567890123");
        history.setRentalAt(TEST_TIME);
        history.setUserId(userId);
        List<BookCheckoutHistory> rentalUserBookList = List.of(history);

        UnreturnedBookModel expectedBook = new UnreturnedBookModel();
        expectedBook.setIsbn("1234567890123");
        expectedBook.setTitle("Test Title");
        expectedBook.setAuthor("Test Author");
        expectedBook.setPublisher("Test Publisher");
        expectedBook.setRentalAt(TEST_TIME);

        Book book = new Book();
        book.setIsbn("1234567890123");
        book.setTitle("Test Title");
        book.setAuthor("Test Author");
        book.setPublisher("Test Publisher");
        Set<String> unreturnedIsbnSet = Set.of(book.getIsbn());

        when(bookCheckoutHistoryRepository.findUnreturnedBooksByUserId(userId)).thenReturn(rentalUserBookList);
        when(bookRepository.findAllById(unreturnedIsbnSet)).thenReturn(List.of(book));

        List<UnreturnedBookModel> unreturnedBooks = bookLendingManager.getUnreturnedBooksByUserId(userId);

        verify(bookCheckoutHistoryRepository, times(1)).findUnreturnedBooksByUserId(userId);
        verify(bookRepository, times(1)).findAllById(Set.of(expectedBook.getIsbn()));
        assertThat(unreturnedBooks).hasSize(1);
        assertThat(unreturnedBooks.get(0)).isEqualTo(expectedBook);
    }

    @Test
    @DisplayName("ユーザIDによる未返却の書籍が複数ある場合、複数の書籍を含んだリストを返す")
    void shouldGetUnreturnedBooksByUserIdSuccessfully() {
        String userId = "userId";

        BookCheckoutHistory history1 = new BookCheckoutHistory();
        history1.setIsbn("1234567890123");
        history1.setRentalAt(TEST_TIME);
        history1.setUserId(userId);
        BookCheckoutHistory history2 = new BookCheckoutHistory();
        history2.setIsbn("1234567890124");
        history2.setRentalAt(TEST_TIME);
        history2.setUserId(userId);

        UnreturnedBookModel unreturnedBook1 = new UnreturnedBookModel();
        unreturnedBook1.setIsbn("1234567890123");
        unreturnedBook1.setTitle("Test Title");
        unreturnedBook1.setAuthor("Test Author");
        unreturnedBook1.setPublisher("Test Publisher");
        unreturnedBook1.setRentalAt(TEST_TIME);
        UnreturnedBookModel unreturnedBook2 = new UnreturnedBookModel();
        unreturnedBook2.setIsbn("1234567890124");
        unreturnedBook2.setTitle("Test Title 2");
        unreturnedBook2.setAuthor("Test Author 2");
        unreturnedBook2.setPublisher("Test Publisher 2");
        unreturnedBook2.setRentalAt(TEST_TIME);

        Book book1 = new Book();
        book1.setIsbn("1234567890123");
        book1.setTitle("Test Title");
        book1.setAuthor("Test Author");
        book1.setPublisher("Test Publisher");
        Book book2 = new Book();
        book2.setIsbn("1234567890124");
        book2.setTitle("Test Title 2");
        book2.setAuthor("Test Author 2");
        book2.setPublisher("Test Publisher 2");

        when(bookCheckoutHistoryRepository.findUnreturnedBooksByUserId(userId)).thenReturn(List.of(history1, history2));
        when(bookRepository.findAllById(Set.of(book1.getIsbn(), book2.getIsbn()))).thenReturn(List.of(book1, book2));

        List<UnreturnedBookModel> unreturnedBooks = bookLendingManager.getUnreturnedBooksByUserId(userId);

        assertThat(unreturnedBooks).hasSize(2);
        assertThat(unreturnedBooks.get(0)).isEqualTo(unreturnedBook1);
        assertThat(unreturnedBooks.get(1)).isEqualTo(unreturnedBook2);
    }

    @Test
    @DisplayName("ユーザIDによる未返却の書籍が存在しない場合、空のリストを返す")
    void shouldReturnEmptyListWhenNoUnreturnedBooksByUserId() {
        Set<String> emptyIsbnSet = Set.of();
        String userId = "userId";

        when(bookCheckoutHistoryRepository.findUnreturnedBooksByUserId(userId)).thenReturn(Collections.emptyList());
        when(bookRepository.findAllById(emptyIsbnSet)).thenReturn(Collections.emptyList());

        List<UnreturnedBookModel> unreturnedBooks = bookLendingManager.getUnreturnedBooksByUserId(userId);

        verify(bookCheckoutHistoryRepository, times(1)).findUnreturnedBooksByUserId(userId);
        verify(bookRepository, times(1)).findAllById(emptyIsbnSet);
        assertThat(unreturnedBooks).isEmpty();
    }

    @Test
    @DisplayName("貸し出し中の本の返却が正常に終了する")
    void shouldReturnBookSuccessfully() {
        String userId = "userId";

        Book expectedBook = new Book();
        expectedBook.setIsbn("1234567890123");
        expectedBook.setAvailableStock(2);

        Book book = new Book();
        book.setIsbn("1234567890123");
        book.setAvailableStock(1);

        when(bookRepository.findById(book.getIsbn())).thenReturn(Optional.of(book));
        doNothing().when(bookCheckoutHistoryRepository).updateReturnAt(userId, book.getIsbn());

        try {
            bookLendingManager.returnBook(userId, book.getIsbn());
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }

        verify(bookRepository, times(1)).save(expectedBook);
        verify(bookCheckoutHistoryRepository, times(1)).updateReturnAt(userId, expectedBook.getIsbn());
    }

    @Test
    @DisplayName("返却対象の書籍が存在しない場合、NoSuchElementExceptionが発生する")
    void shouldThrowNoSuchElementExceptionWhenReturnNonexistentBook() {
        String userId = "userId";
        String expectedIsbn = "1234567890123";
        String isbn = "1234567890123";

        when(bookRepository.findById(isbn)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookLendingManager.returnBook(userId, isbn))
                .isInstanceOf(NoSuchElementException.class);
        verify(bookRepository, times(1)).findById(expectedIsbn);
        verify(bookRepository, never()).save(any());
        verify(bookCheckoutHistoryRepository, never()).updateReturnAt(any(), any());
    }

    @Test
    @DisplayName("貸し出し履歴の更新に失敗した場合、DataAccessExceptionのサブクラスが発生する")
    void shouldThrowDataAccessExceptionWhenFailedToUpdateCheckoutHistory() {
        String userId = "userId";

        Book expectedBook = new Book();
        expectedBook.setIsbn("1234567890123");
        expectedBook.setAvailableStock(1);

        Book book = new Book();
        book.setIsbn("1234567890123");
        book.setAvailableStock(1);

        when(bookRepository.findById(book.getIsbn())).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        doThrow(new DataAccessResourceFailureException("DBへの接続ができませんでした。")).when(bookCheckoutHistoryRepository).updateReturnAt(userId, book.getIsbn());

        assertThatThrownBy(() -> bookLendingManager.returnBook(userId, book.getIsbn()))
                .isInstanceOf(DataAccessException.class);
        verify(bookRepository, times(1)).findById(expectedBook.getIsbn());
        verify(bookRepository, times(1)).save(book);
        verify(bookCheckoutHistoryRepository, times(1)).updateReturnAt(userId, expectedBook.getIsbn());
    }
}

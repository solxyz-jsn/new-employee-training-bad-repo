package jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.service;

import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.model.UnreturnedBookModel;
import jp.co.solxyz.jsn.springbootadvincedexam.component.book.BookLendingManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookReturnServiceTest {

    @InjectMocks
    private BookLendingService bookLendingService;

    @Mock
    private BookLendingManager bookLendingManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("ユーザに未返却書籍がない場合、空のリストが返される")
    void shouldReturnEmptyListWhenCurrentUserBooksIsEmpty() {
        String userId = "user1";
        String expectedUserId = "user1";

        List<UnreturnedBookModel> expectedUnreturnedBookList = List.of();

        when(bookLendingManager.getUnreturnedBooksByUserId(userId)).thenReturn(List.of());

        List<UnreturnedBookModel> result = bookLendingService.getBook(expectedUserId);

        assertThat(result).isEqualTo(expectedUnreturnedBookList);
    }

    @Test
    @DisplayName("ユーザに1件の未返却書籍がある場合、1件の未返却書籍含んだリストが返される")
    void shouldReturnCurrentUserBooksSuccessfully() {
        String userId = "user1";
        String expectedUserId = "user1";

        UnreturnedBookModel unreturnedBook = new UnreturnedBookModel();
        unreturnedBook.setIsbn("isbn1");
        unreturnedBook.setTitle("title1");
        unreturnedBook.setAuthor("author1");
        unreturnedBook.setPublisher("publisher1");

        UnreturnedBookModel expectedUnreturnedBook = new UnreturnedBookModel();
        expectedUnreturnedBook.setIsbn("isbn1");
        expectedUnreturnedBook.setTitle("title1");
        expectedUnreturnedBook.setAuthor("author1");
        expectedUnreturnedBook.setPublisher("publisher1");

        List<UnreturnedBookModel> unreturnedBookList = List.of(unreturnedBook);
        List<UnreturnedBookModel> expectedunreturnedBookList = List.of(expectedUnreturnedBook);

        when(bookLendingManager.getUnreturnedBooksByUserId(userId)).thenReturn(unreturnedBookList);

        List<UnreturnedBookModel> result = bookLendingService.getBook(expectedUserId);

        assertThat(result).isEqualTo(expectedunreturnedBookList);
    }

    @Test
    @DisplayName("ユーザに複数の未返却書籍がある場合、複数の未返却書籍含んだリストが返される")
    void shouldReturnCurrentUserBooksSuccessfullyWhenMultipleBooks() {
        String userId = "user1";
        String expectedUserId = "user1";

        UnreturnedBookModel unreturnedBook1 = new UnreturnedBookModel();
        unreturnedBook1.setIsbn("isbn1");
        unreturnedBook1.setTitle("title1");
        unreturnedBook1.setAuthor("author1");
        unreturnedBook1.setPublisher("publisher1");

        UnreturnedBookModel unreturnedBook2 = new UnreturnedBookModel();
        unreturnedBook2.setIsbn("isbn2");
        unreturnedBook2.setTitle("title2");
        unreturnedBook2.setAuthor("author2");
        unreturnedBook2.setPublisher("publisher2");

        UnreturnedBookModel expectedUnreturnedBook1 = new UnreturnedBookModel();
        expectedUnreturnedBook1.setIsbn("isbn1");
        expectedUnreturnedBook1.setTitle("title1");
        expectedUnreturnedBook1.setAuthor("author1");
        expectedUnreturnedBook1.setPublisher("publisher1");

        UnreturnedBookModel expectedUnreturnedBook2 = new UnreturnedBookModel();
        expectedUnreturnedBook2.setIsbn("isbn2");
        expectedUnreturnedBook2.setTitle("title2");
        expectedUnreturnedBook2.setAuthor("author2");
        expectedUnreturnedBook2.setPublisher("publisher2");

        List<UnreturnedBookModel> unreturnedBookList = List.of(unreturnedBook1, unreturnedBook2);
        List<UnreturnedBookModel> expectedunreturnedBookList = List.of(expectedUnreturnedBook1, expectedUnreturnedBook2);

        when(bookLendingManager.getUnreturnedBooksByUserId(userId)).thenReturn(unreturnedBookList);

        List<UnreturnedBookModel> result = bookLendingService.getBook(expectedUserId);

        assertThat(result).isEqualTo(expectedunreturnedBookList);
    }

    @Test
    @DisplayName("書籍の返却が成功し、BookManagerのreturnBookが呼ばれる")
    void shouldReturnBookSuccessfully() {
        String userId = "user1";
        String expectedUserId = "user1";

        String isbn = "1234567890123";
        String expectedIsbn = "1234567890123";

        try {
            bookLendingService.henkyaku(userId, isbn);
        } catch (Exception e) {
            fail();
        }
        verify(bookLendingManager, times(1)).returnBook(expectedUserId, expectedIsbn);
    }

    @Test
    @DisplayName("書籍の返却でDataAccessExceptionのサブクラスがスローされる")
    void shouldThrowDataAccessExceptionWhenReturnBook() {
        String userId = "user1";
        String expectedUserId = "user1";

        String isbn = "1234567890123";
        String expectedIsbn = "1234567890123";

        doThrow(DataAccessResourceFailureException.class).when(bookLendingManager).returnBook(userId, isbn);

        assertThrows(DataAccessException.class, () -> bookLendingService.henkyaku(expectedUserId, expectedIsbn));
    }

    @Test
    @DisplayName("書籍の返却でNoSuchElementExceptionがスローされる")
    void shouldThrowNoSuchElementExceptionWhenReturnBook() {
        String userId = "user1";
        String expectedUserId = "user1";

        String isbn = "1234567890123";
        String expectedIsbn = "1234567890123";

        doThrow(NoSuchElementException.class).when(bookLendingManager).returnBook(userId, isbn);

        assertThrows(NoSuchElementException.class, () -> bookLendingService.henkyaku(expectedUserId, expectedIsbn));
    }
}

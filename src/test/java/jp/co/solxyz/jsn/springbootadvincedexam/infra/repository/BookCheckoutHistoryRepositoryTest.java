package jp.co.solxyz.jsn.springbootadvincedexam.infra.repository;

import jp.co.solxyz.jsn.springbootadvincedexam.infra.entity.book.BookCheckoutHistory;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.reposiroty.book.BookCheckoutHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessResourceFailureException;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class BookCheckoutHistoryRepositoryTest {

    @Mock
    private BookCheckoutHistoryRepository bookCheckoutHistoryRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("ユーザーが未返却の書籍を1件持っている場合、1件の未返却の書籍を含んだリストを返す")
    void shouldReturnUnreturnedBooksWhenUserHasUnreturnedBooks() {
        String userId = "user1";
        when(bookCheckoutHistoryRepository.findUnreturnedBooksByUserId(userId)).thenReturn(List.of(new BookCheckoutHistory()));

        List<BookCheckoutHistory> books = bookCheckoutHistoryRepository.findUnreturnedBooksByUserId(userId);

        assertThat(books.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("ユーザーが未返却の書籍を複数持っている場合、複数の未返却の書籍を含んだリストを返す")
    void shouldReturnUnreturnedBooksWhenUserHasMultipleUnreturnedBooks() {
        String userId = "user1";
        when(bookCheckoutHistoryRepository.findUnreturnedBooksByUserId(userId)).thenReturn(
                List.of(new BookCheckoutHistory(), new BookCheckoutHistory()));

        List<BookCheckoutHistory> books = bookCheckoutHistoryRepository.findUnreturnedBooksByUserId(userId);

        assertThat(books.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("ユーザーが未返却の書籍を持っていない場合、空のリストを返す")
    void shouldReturnEmptyListWhenUserHasNoUnreturnedBooks() {
        String userId = "user1";

        when(bookCheckoutHistoryRepository.findUnreturnedBooksByUserId(userId)).thenReturn(Collections.emptyList());

        List<BookCheckoutHistory> books = bookCheckoutHistoryRepository.findUnreturnedBooksByUserId(userId);

        assertThat(books.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("ISBNから未返却の書籍を検索する")
    void shouldFindUnreturnedBooksByIsbn() {
        String isbn = "isbn1";

        when(bookCheckoutHistoryRepository.findUnreturnedBooksByIsbn(isbn)).thenReturn(List.of(new BookCheckoutHistory()));

        List<BookCheckoutHistory> books = bookCheckoutHistoryRepository.findUnreturnedBooksByIsbn(isbn);

        assertThat(books.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("ユーザIDとISBNから返却日時を更新する")
    void shouldUpdateReturnAtByUserIdAndIsbn() {
        String userId = "user1";
        String isbn = "isbn1";

        doNothing().when(bookCheckoutHistoryRepository).updateReturnAt(userId, isbn);

        try {
            bookCheckoutHistoryRepository.updateReturnAt(userId, isbn);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    @DisplayName("ユーザIDとISBNから返却日時を更新する際に例外が発生する場合、例外をスローする")
    void shouldThrowExceptionWhenUpdateReturnAtByUserIdAndIsbn() {
        String userId = "user1";
        String isbn = "isbn1";

        doThrow(DataAccessResourceFailureException.class).when(bookCheckoutHistoryRepository).updateReturnAt(userId, isbn);

        try {
            bookCheckoutHistoryRepository.updateReturnAt(userId, isbn);
            fail();
        } catch (Exception e) {
            assertThat(e).isInstanceOf(DataAccessResourceFailureException.class);
        }
    }
}

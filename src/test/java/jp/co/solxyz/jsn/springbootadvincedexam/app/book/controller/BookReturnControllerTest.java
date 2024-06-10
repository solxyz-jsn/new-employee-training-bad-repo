package jp.co.solxyz.jsn.springbootadvincedexam.app.book.controller;

import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.controller.BookReturnController;
import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.model.UnreturnedBookModel;
import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.service.BookLendingService;
import jp.co.solxyz.jsn.springbootadvincedexam.security.MyUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookReturnControllerTest {

    @InjectMocks
    private BookReturnController bookReturnController;

    @Mock
    private BookLendingService bookReturnService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("1件の借りている書籍がある時にindexが呼び出された場合、1件の書籍を含んだリストを返す")
    void shouldReturnBookListWhenIndexIsCalled() {
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

        MyUserDetails userDetails = mock(MyUserDetails.class);

        when(userDetails.getUserId()).thenReturn(userId);
        when(bookReturnService.getCurrentUserBooks(userId)).thenReturn(List.of(unreturnedBook));

        ModelAndView mav = bookReturnController.index(userDetails);

        verify(bookReturnService, times(1)).getCurrentUserBooks(expectedUserId);

        assertThat(mav.getViewName()).isEqualTo("general/bookreturn");
        assertThat(mav.getModel().get("books")).isEqualTo(List.of(expectedUnreturnedBooks));

    }

    @Test
    @DisplayName("複数件の借りている書籍があるときにindexが呼び出された場合、複数件の書籍を含んだリストを返す")
    void shouldReturnMultipleBookListWhenIndexIsCalled() {
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

        MyUserDetails userDetails = mock(MyUserDetails.class);

        when(userDetails.getUserId()).thenReturn(userId);
        when(bookReturnService.getCurrentUserBooks(userId)).thenReturn(List.of(unreturnedBook1, unreturnedBook2));

        ModelAndView mav = bookReturnController.index(userDetails);

        verify(bookReturnService, times(1)).getCurrentUserBooks(expectedUserId);

        assertThat(mav.getViewName()).isEqualTo("general/bookreturn");
        assertThat(mav.getModel().get("books")).isEqualTo(List.of(expectedUnreturnedBooks1, expectedUnreturnedBooks2));
    }

    @Test
    @DisplayName("借りている書籍がないときにindexが呼び出された場合、空の書籍リストを返す")
    void shouldReturnEmptyBookListWhenIndexIsCalled() {
        String userId = "user1";
        String expectedUserId = "user1";

        MyUserDetails userDetails = mock(MyUserDetails.class);

        when(userDetails.getUserId()).thenReturn(userId);

        when(bookReturnService.getCurrentUserBooks(userId)).thenReturn(Collections.emptyList());

        ModelAndView mav = bookReturnController.index(userDetails);

        verify(bookReturnService, times(1)).getCurrentUserBooks(expectedUserId);
        assertThat(mav.getViewName()).isEqualTo("general/bookreturn");
        assertThat(mav.getModel().get("books")).isEqualTo(Collections.emptyList());
    }

    @Test
    @DisplayName("getCurrentUserBooksがExceptionをスローした場合、例外をキャッチしない")
    void shouldCatchExceptionWhenGetCurrentUserBooksThrowsException() {
        String userId = "user1";
        MyUserDetails userDetails = mock(MyUserDetails.class);

        when(userDetails.getUserId()).thenReturn(userId);
        when(bookReturnService.getCurrentUserBooks(userId)).thenThrow(new RuntimeException());

        try {
            bookReturnController.index(userDetails);
            fail();
        } catch (Exception e) {
            verify(bookReturnService, times(1)).getCurrentUserBooks(userId);
        }
    }
}

package jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.controller;

import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.model.UnreturnedBookModel;
import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.service.BookLendingService;
import jp.co.solxyz.jsn.springbootadvincedexam.security.MyUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest
class BookReturnControllerTest {

    @MockBean
    private BookLendingService bookLendingService;

    @Mock
    private MyUserDetails userDetails;

    private final WebApplicationContext context;

    private MockMvc mockMvc;

    private final String USER_ID = "user1";

    BookReturnControllerTest(WebApplicationContext context) {
        this.context = context;
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        when(userDetails.getUserId()).thenReturn(USER_ID);
    }

    @Test
    @DisplayName("1件の借りている書籍がある時にindexが呼び出された場合、1件の書籍を含んだリストを返す")
    void shouldReturnBookListWhenIndexIsCalled() throws Exception {
        String expectedUserId = "user1";

        UnreturnedBookModel unreturnedBook = new UnreturnedBookModel();
        unreturnedBook.setIsbn("isbn1");
        unreturnedBook.setTitle("title1");
        unreturnedBook.setAuthor("author1");
        unreturnedBook.setPublisher("publisher1");
        List<UnreturnedBookModel> unreturnedBooks = List.of(unreturnedBook);

        UnreturnedBookModel expectedUnreturnedBook = new UnreturnedBookModel();
        expectedUnreturnedBook.setIsbn("isbn1");
        expectedUnreturnedBook.setTitle("title1");
        expectedUnreturnedBook.setAuthor("author1");
        expectedUnreturnedBook.setPublisher("publisher1");
        List<UnreturnedBookModel> expectedUnreturnedBooks = List.of(expectedUnreturnedBook);

        when(bookLendingService.getBook(any())).thenReturn(unreturnedBooks);

        mockMvc.perform(MockMvcRequestBuilders.get("/book/return")
                        .with(user(userDetails)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-lending"))
                .andExpect(MockMvcResultMatchers.model().attribute("books", expectedUnreturnedBooks));

        verify(bookLendingService, times(1)).getBook(expectedUserId);
    }

    @Test
    @DisplayName("複数件の借りている書籍があるときにindexが呼び出された場合、複数件の書籍を含んだリストを返す")
    void shouldReturnMultipleBookListWhenIndexIsCalled() throws Exception {
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
        List<UnreturnedBookModel> unreturnedBooks = List.of(unreturnedBook1, unreturnedBook2);

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
        List<UnreturnedBookModel> expectedUnreturnedBooks = List.of(expectedUnreturnedBook1, expectedUnreturnedBook2);

        when(userDetails.getUserId()).thenReturn(userId);
        when(bookLendingService.getBook(userId)).thenReturn(unreturnedBooks);

        mockMvc.perform(MockMvcRequestBuilders.get("/book/return")
                        .with(user(userDetails)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-lending"))
                .andExpect(MockMvcResultMatchers.model().attribute("books", expectedUnreturnedBooks));

        verify(bookLendingService, times(1)).getBook(expectedUserId);
    }

    @Test
    @DisplayName("借りている書籍がないときにindexが呼び出された場合、空の書籍リストを返す")
    void shouldReturnEmptyBookListWhenIndexIsCalled() throws Exception {
        String userId = "user1";
        String expectedUserId = "user1";

        when(userDetails.getUserId()).thenReturn(userId);
        when(bookLendingService.getBook(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/book/return")
                        .with(user(userDetails)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-lending"))
                .andExpect(MockMvcResultMatchers.model().attribute("books", Collections.emptyList()));

        verify(bookLendingService, times(1)).getBook(expectedUserId);
    }

    @Test
    @DisplayName("getCurrentUserBooksがExceptionをスローした場合、例外をキャッチしない")
    void shouldCatchExceptionWhenGetCurrentUserBooksThrowsException() {
        String userId = "user1";

        when(userDetails.getUserId()).thenReturn(userId);
        when(bookLendingService.getBook(userId)).thenThrow(new RuntimeException());

        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/book/return")
                            .with(user(userDetails)))
                    .andExpect(MockMvcResultMatchers.status().is5xxServerError());
            fail();
        } catch (Exception e) {
            verify(bookLendingService, times(1)).getBook(userId);
        }
    }
}

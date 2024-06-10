package jp.co.solxyz.jsn.springbootadvincedexam.app.book.controller;

import jp.co.solxyz.jsn.springbootadvincedexam.app.admin.book.controller.BookManagementController;
import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.model.BookManagementModel;
import jp.co.solxyz.jsn.springbootadvincedexam.app.admin.book.service.BookManagementService;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.entity.book.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BookManagementControllerTest {

    @InjectMocks
    private BookManagementController controller;

    @Mock
    private BookManagementService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("書籍が存在しない場合、空のリストと書籍一覧画面が返される")
    void shouldDisplayEmptyBookListWhenNoBookIsAvailable() {
        when(service.getAllBooks()).thenReturn(List.of());

        ModelAndView actual = controller.index();

        assertThat(actual.getViewName()).isEqualTo("admin/bookmanagement");
        List<BookManagementModel> result = (List<BookManagementModel>) actual.getModel().get("books");
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("書籍が1件ある場合、1件の書籍を含んだリストと書籍一覧画面が返される")
    void shouldDisplayBookListWhenBookAreAvailable() {
        BookManagementModel expected = new BookManagementModel("1234567890123", "Test Book", "Test Author", "Test Publisher", "Test Description", 10,
                LocalDateTime.of(2021, 1, 1, 0, 0, 0), LocalDateTime.of(2021, 1, 1, 0, 0, 0));

        Book book = new Book();
        book.setIsbn("1234567890123");
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setPublisher("Test Publisher");
        book.setDescription("Test Description");
        book.setStock(10);
        LocalDateTime testTime = LocalDateTime.of(2021, 1, 1, 0, 0, 0);
        book.setCreatedAt(testTime);
        book.setUpdatedAt(testTime);

        when(service.getAllBooks()).thenReturn(List.of(book));

        ModelAndView actual = controller.index();

        assertThat(actual.getViewName()).isEqualTo("admin/bookmanagement");
        List<BookManagementModel> result = (List<BookManagementModel>) actual.getModel().get("books");
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(expected);
    }

    @Test
    @DisplayName("書籍が複数件ある場合、複数件の書籍を含んだリストと書籍一覧画面が返される")
    void shouldDisplayBookListWhenMultipleBooksAreAvailable() {
        LocalDateTime testTime = LocalDateTime.of(2021, 1, 1, 0, 0, 0);
        LocalDateTime expectedTime = LocalDateTime.of(2021, 1, 1, 0, 0, 0);

        BookManagementModel expected1 = new BookManagementModel("1234567890123", "Test Book", "Test Author", "Test Publisher", "Test Description", 10,
                expectedTime, expectedTime);
        BookManagementModel expected2 = new BookManagementModel("0987654321098", "Test Book", "Test Author", "Test Publisher", "Test Description", 10,
                expectedTime, expectedTime);

        Book book1 = new Book();
        book1.setIsbn("1234567890123");
        book1.setTitle("Test Book");
        book1.setAuthor("Test Author");
        book1.setPublisher("Test Publisher");
        book1.setDescription("Test Description");
        book1.setStock(10);
        book1.setCreatedAt(testTime);
        book1.setUpdatedAt(testTime);

        Book book2 = new Book();
        book2.setIsbn("0987654321098");
        book2.setTitle("Test Book");
        book2.setAuthor("Test Author");
        book2.setPublisher("Test Publisher");
        book2.setDescription("Test Description");
        book2.setStock(10);
        book2.setCreatedAt(testTime);
        book2.setUpdatedAt(testTime);

        when(service.getAllBooks()).thenReturn(List.of(book1, book2));

        ModelAndView actual = controller.index();

        assertThat(actual.getViewName()).isEqualTo("admin/bookmanagement");
        List<BookManagementModel> result = (List<BookManagementModel>) actual.getModel().get("books");
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo(expected1);
        assertThat(result.get(1)).isEqualTo(expected2);
    }

    @Test
    @DisplayName("ISBNが13桁の数字ではない場合、エラーメッセージのみ返る")
    void shouldFailToDisplayBookListDueToError() {
        Book book = new Book();
        book.setIsbn("test_isbn");
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setPublisher("Test Publisher");
        book.setDescription("Test Description");
        book.setStock(10);
        LocalDateTime testTime = LocalDateTime.of(2021, 1, 1, 0, 0, 0);
        book.setCreatedAt(testTime);
        book.setUpdatedAt(testTime);

        when(service.getAllBooks()).thenReturn(List.of(book));

        ModelAndView actual = controller.index();

        verify(service, times(1)).getAllBooks();
        assertThat(actual.getViewName()).isEqualTo("admin/bookmanagement");
        assertThat(actual.getModel().get("books")).isNull();
        assertThat(actual.getModel().get("error")).isEqualTo("書籍情報の生成に失敗しました。");
    }
}

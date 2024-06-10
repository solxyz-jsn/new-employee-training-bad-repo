package jp.co.solxyz.jsn.springbootadvincedexam.app.book.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.controller.BookListController;
import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.model.BookModel;
import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.service.BookListService;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.entity.book.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookListControllerTest {

    @InjectMocks
    private BookListController bookListController;

    @Mock
    private BookListService bookListService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("書籍が1件ある場合、1件の書籍を含んだリストと書籍一覧画面が返される")
    void shouldDisplayBookListWhenBookAreAvailable() throws Exception {
        BookModel expected = new BookModel();
        expected.setIsbn("1234567890123");
        expected.setTitle("Test Book");
        expected.setAuthor("Test Author");
        expected.setPublisher("Test Publisher");
        expected.setDescription("Test Description");
        expected.setAvailableStock(10);

        Book book = new Book();
        book.setIsbn("1234567890123");
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setPublisher("Test Publisher");
        book.setDescription("Test Description");
        book.setAvailableStock(10);
        book.setStock(5);

        when(bookListService.getAllBooks()).thenReturn(List.of(book));

        ModelAndView mav = bookListController.bookList();

        List<BookModel> books = (List<BookModel>) mav.getModel().get("books");
        String booksJson = (String) mav.getModel().get("booksJson");

        // JSON文字列をBookModelのリストに変換
        ObjectMapper mapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        mapper.registerModule(module);
        List<BookModel> bookModelsFromJson = mapper.readValue(booksJson, new TypeReference<>() {
        });

        verify(bookListService, times(1)).getAllBooks();
        assertThat(books).hasSize(1);
        assertThat(books.get(0)).isEqualTo(expected);
        // JSONから取得した値のアサーション
        assertThat(bookModelsFromJson).hasSize(1);
        assertThat(bookModelsFromJson.get(0)).isEqualTo(expected);
    }

    @Test
    @DisplayName("書籍が複数件ある場合、複数の書籍を含んだリストと書籍一覧画面が返される")
    void shouldDisplayBookListWhenBooksAreAvailable() throws Exception {
        BookModel expected1 = new BookModel();
        expected1.setIsbn("1234567890123");
        expected1.setTitle("Test Book");
        expected1.setAuthor("Test Author");
        expected1.setPublisher("Test Publisher");
        expected1.setDescription("Test Description");
        expected1.setAvailableStock(10);
        BookModel expected2 = new BookModel();
        expected2.setIsbn("0987654321098");
        expected2.setTitle("Test Book");
        expected2.setAuthor("Test Author");
        expected2.setPublisher("Test Publisher");
        expected2.setDescription("Test Description");
        expected2.setAvailableStock(5);

        Book book1 = new Book();
        book1.setIsbn("1234567890123");
        book1.setTitle("Test Book");
        book1.setAuthor("Test Author");
        book1.setPublisher("Test Publisher");
        book1.setDescription("Test Description");
        book1.setAvailableStock(10);
        book1.setStock(5);
        Book book2 = new Book();
        book2.setIsbn("0987654321098");
        book2.setTitle("Test Book");
        book2.setAuthor("Test Author");
        book2.setPublisher("Test Publisher");
        book2.setDescription("Test Description");
        book2.setAvailableStock(5);
        book2.setStock(5);

        when(bookListService.getAllBooks()).thenReturn(List.of(book1, book2));

        ModelAndView mav = bookListController.bookList();

        List<BookModel> books = (List<BookModel>) mav.getModel().get("books");
        String booksJson = (String) mav.getModel().get("booksJson");

        // JSON文字列をBookModelのリストに変換
        ObjectMapper mapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        mapper.registerModule(module);
        List<BookModel> bookModelsFromJson = mapper.readValue(booksJson, new TypeReference<>() {
        });

        verify(bookListService, times(1)).getAllBooks();
        assertThat(books).hasSize(2);
        assertThat(books.get(0)).isEqualTo(expected1);
        assertThat(books.get(1)).isEqualTo(expected2);
        // JSONから取得した値のアサーション
        assertThat(bookModelsFromJson).hasSize(2);
        assertThat(bookModelsFromJson.get(0)).isEqualTo(expected1);
        assertThat(bookModelsFromJson.get(1)).isEqualTo(expected2);
    }

    @Test
    @DisplayName("書籍がない場合、空の書籍リストと書籍一覧画面が返される")
    void shouldDisplayEmptyBookListWhenNoBooksAreAvailable() throws Exception {
        when(bookListService.getAllBooks()).thenReturn(Collections.emptyList());

        ModelAndView mav = bookListController.bookList();

        List<BookModel> books = (List<BookModel>) mav.getModel().get("books");
        String booksJson = (String) mav.getModel().get("booksJson");

        ObjectMapper mapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        mapper.registerModule(module);
        List<BookModel> bookModelsFromJson = mapper.readValue(booksJson, new TypeReference<>() {
        });

        verify(bookListService, times(1)).getAllBooks();
        assertThat(books).isEmpty();
        // JSONから取得した値のアサーション
        assertThat(bookModelsFromJson).isEmpty();
    }
}

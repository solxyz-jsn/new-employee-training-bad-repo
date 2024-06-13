package jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.model.BookModel;
import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.service.BookListService;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.entity.book.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest
class BookListControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private BookListService bookListService;

    private final WebApplicationContext context;

    BookListControllerTest(WebApplicationContext context) {
        this.context = context;
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .defaultRequest(MockMvcRequestBuilders.get("/")
                        .with(user("user1").roles("USER")))
                .build();
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
        List<BookModel> expectedList = List.of(expected);

        Book book = new Book();
        book.setIsbn("1234567890123");
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setPublisher("Test Publisher");
        book.setDescription("Test Description");
        book.setAvailableStock(10);
        book.setStock(5);

        // JSON文字列をBookModelのリストに変換
        ObjectMapper mapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        mapper.registerModule(module);
        String bookJson = mapper.writeValueAsString(expectedList);

        when(bookListService.getAllBooks()).thenReturn(List.of(book));

        mockMvc.perform(MockMvcRequestBuilders.get("/book/list"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-list"))
                .andExpect(MockMvcResultMatchers.model().attribute("books", expectedList))
                .andExpect(MockMvcResultMatchers.model().attribute("booksJson", bookJson));

        verify(bookListService, times(1)).getAllBooks();
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
        List<BookModel> expectedList = List.of(expected1, expected2);

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
        List<Book> books = List.of(book1, book2);

        ObjectMapper mapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        mapper.registerModule(module);
        String bookJson = mapper.writeValueAsString(expectedList);

        when(bookListService.getAllBooks()).thenReturn(books);

        mockMvc.perform(MockMvcRequestBuilders.get("/book/list"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-list"))
                .andExpect(MockMvcResultMatchers.model().attribute("books", expectedList))
                .andExpect(MockMvcResultMatchers.model().attribute("booksJson", bookJson));

        verify(bookListService, times(1)).getAllBooks();
    }

    @Test
    @DisplayName("書籍がない場合、空の書籍リストと書籍一覧画面が返される")
    void shouldDisplayEmptyBookListWhenNoBooksAreAvailable() throws Exception {
        when(bookListService.getAllBooks()).thenReturn(Collections.emptyList());

        ObjectMapper mapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        mapper.registerModule(module);
        String bookJson = mapper.writeValueAsString(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/book/list"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-list"))
                .andExpect(MockMvcResultMatchers.model().attribute("books", equalTo(Collections.emptyList())))
                .andExpect(MockMvcResultMatchers.model().attribute("booksJson", equalTo(bookJson)));

        verify(bookListService, times(1)).getAllBooks();
    }
}

package jp.co.solxyz.jsn.springbootadvancedexam.presentation.user.book.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jp.co.solxyz.jsn.springbootadvancedexam.application.book.cart.BookCartUseCase;
import jp.co.solxyz.jsn.springbootadvancedexam.application.book.catalog.BookListUseCase;
import jp.co.solxyz.jsn.springbootadvancedexam.application.book.lending.BookLendingUseCase;
import jp.co.solxyz.jsn.springbootadvancedexam.infrastructure.entity.book.Book;
import jp.co.solxyz.jsn.springbootadvancedexam.presentation.user.book.model.BookModel;
import jp.co.solxyz.jsn.springbootadvancedexam.presentation.user.book.model.CartBookModel;
import jp.co.solxyz.jsn.springbootadvancedexam.presentation.user.book.model.UnreturnedBookModel;
import jp.co.solxyz.jsn.springbootadvancedexam.presentation.user.book.session.Cart;
import jp.co.solxyz.jsn.springbootadvancedexam.presentation.user.book.session.CartSession;
import jp.co.solxyz.jsn.springbootadvancedexam.security.MyUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest
class BookControllerTest {

    @MockitoBean
    private CartSession cartSession;

    @MockitoBean
    private BookCartUseCase bookCartUseCase;

    @MockitoBean
    private BookListUseCase bookListUseCase;

    @MockitoBean
    private BookLendingUseCase bookLendingUseCase;

    @Mock
    private MyUserDetails userDetails;

    private MockMvc mockMvc;

    private final WebApplicationContext context;

    private static final String USER_ID = "user1";

    BookControllerTest(WebApplicationContext context) {
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
    @DisplayName("カートに書籍が1冊ある状態でindexが呼び出された場合、cartListに1件書籍が返される")
    void shouldDisplayCartWhenIndexIsCalled() throws Exception {
        Cart cart = new Cart();
        cart.setIsbn("1234567890");

        List<Cart> carts = List.of(cart);
        List<Cart> expectedCarts = List.of(cart);

        CartBookModel cartBook = new CartBookModel("1234567890", "Test Book", "Test Author", "Test Publisher");

        List<CartBookModel> cartBooks = List.of(cartBook);
        List<CartBookModel> expectedCartBooks = List.of(cartBook);

        when(cartSession.getCartList()).thenReturn(carts);
        when(bookCartUseCase.getCartList(carts)).thenReturn(cartBooks);

        mockMvc.perform(MockMvcRequestBuilders.get("/book/cart")
                        .with(user(userDetails)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-cart"))
                .andExpect(MockMvcResultMatchers.model().attribute("cartList", expectedCartBooks));
        verify(bookCartUseCase, times(1)).getCartList(expectedCarts);
    }

    @Test
    @DisplayName("カートに書籍が複数ある状態でindexが呼び出された場合、cartListに複数件書籍が返される")
    void shouldDisplayCartWhenIndexIsCalledWithMultipleBooks() throws Exception {
        Cart cart1 = new Cart();
        cart1.setIsbn("1234567890");
        Cart cart2 = new Cart();
        cart2.setIsbn("0987654321");

        List<Cart> carts = List.of(cart1, cart2);

        CartBookModel cartBook1 = new CartBookModel("1234567890", "Test Book1", "Test Author1", "Test Publisher1");
        CartBookModel cartBook2 = new CartBookModel("0987654321", "Test Book2", "Test Author2", "Test Publisher2");

        List<CartBookModel> cartBooks = List.of(cartBook1, cartBook2);
        List<CartBookModel> expectedCartBooks = List.of(cartBook1, cartBook2);

        when(cartSession.getCartList()).thenReturn(carts);
        when(bookCartUseCase.getCartList(carts)).thenReturn(cartBooks);

        mockMvc.perform(MockMvcRequestBuilders.get("/book/cart")
                        .with(user(userDetails)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-cart"))
                .andExpect(MockMvcResultMatchers.model().attribute("cartList", expectedCartBooks));
    }

    @Test
    @DisplayName("カートに何もない状態でindexが呼び出された場合、空のカートが返される")
    void shouldEmptyDisplayCartWhenIndexIsCalled() throws Exception {
        when(cartSession.getCartList()).thenReturn(Collections.emptyList());
        when(bookCartUseCase.getCartList(anyList())).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/book/cart")
                        .with(user(userDetails)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-cart"))
                .andExpect(MockMvcResultMatchers.model().attribute("cartList", Collections.emptyList()));

        verify(bookCartUseCase, times(1)).getCartList(Collections.emptyList());
    }

    @Test
    @DisplayName("有効なユーザー情報がある場合、カート内の書籍の貸し出し処理をする")
    void shouldCheckedOutBookToCartWhenValidUserDetails() throws Exception {
        String expectedUserId = "user1";

        Cart cart = new Cart();
        cart.setIsbn("1234567890123");

        Book book = new Book();
        book.setIsbn("1234567890123");

        when(cartSession.getCartList()).thenReturn(List.of(cart));
        when(bookCartUseCase.co(USER_ID, List.of(cart))).thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders.post("/book/cart")
                        .with(user(userDetails))
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-cart"))
                .andExpect(MockMvcResultMatchers.model().attributeDoesNotExist("cartList"));

        verify(bookCartUseCase, times(1)).co(expectedUserId, List.of(cart));
    }

    @Test
    @DisplayName("チェックアウト時にDataAccessExceptionのサブクラスが発生した場合、エラーが返される")
    void shouldReturnErrorWhenExceptionIsThrownOnCheckedOut() throws Exception {
        String expectedUserId = "user1";

        Cart cart = new Cart();
        cart.setIsbn("1234567890123");

        Book book = new Book();
        book.setIsbn("1234567890123");

        when(cartSession.getCartList()).thenReturn(List.of(cart));
        doThrow(DataAccessResourceFailureException.class).when(bookCartUseCase).co(USER_ID, List.of(cart));

        mockMvc.perform(MockMvcRequestBuilders.post("/book/cart")
                        .with(user(userDetails))
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-cart"))
                .andExpect(MockMvcResultMatchers.model().attribute("errorMessage", "処理中にエラーが発生しました。"));

        verify(bookCartUseCase, times(1)).co(expectedUserId, List.of(cart));
    }

    @Test
    @DisplayName("すでに借りている書籍を借りる場合、借りれなかった書籍として返される")
    void shouldReturnErrorWhenSomeBooksAreNotCheckedOut() throws Exception {
        String expectedUserId = "user1";

        Cart cart = new Cart();
        cart.setIsbn("1234567890123");

        BookModel expectedBook = new BookModel();
        expectedBook.setIsbn("1234567890123");
        expectedBook.setTitle("Test Book");
        expectedBook.setAuthor("Test Author");
        expectedBook.setPublisher("Test Publisher");
        List<BookModel> expectedCartBooks = List.of(expectedBook);

        Book book = new Book();
        book.setIsbn("1234567890123");
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setPublisher("Test Publisher");

        when(cartSession.getCartList()).thenReturn(List.of(cart));
        when(bookCartUseCase.co(USER_ID, List.of(cart))).thenReturn(List.of(book));

        mockMvc.perform(MockMvcRequestBuilders.post("/book/cart")
                        .with(user(userDetails))
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-cart"))
                .andExpect(MockMvcResultMatchers.model().attribute("cartList", expectedCartBooks))
                .andExpect(MockMvcResultMatchers.model().attribute("errorMessage", "以下の書籍は既に借りている 又は 在庫が不足しているため借りることができません。"));

        verify(bookCartUseCase, times(1)).co(expectedUserId, List.of(cart));
    }

    @Test
    @DisplayName("すでに借りている書籍を複数件借りる場合、複数件の借りれなかった書籍が返される")
    void shouldReturnErrorWhenMultipleBooksAreNotCheckedOut() throws Exception {
        String expectedUserId = "user1";

        Cart cart1 = new Cart();
        cart1.setIsbn("1234567890123");
        Cart cart2 = new Cart();
        cart2.setIsbn("0987654321098");

        BookModel expectedBook1 = new BookModel();
        expectedBook1.setIsbn("1234567890123");
        expectedBook1.setTitle("Test Book1");
        expectedBook1.setAuthor("Test Author1");
        expectedBook1.setPublisher("Test Publisher1");
        BookModel expectedBook2 = new BookModel();
        expectedBook2.setIsbn("0987654321098");
        expectedBook2.setTitle("Test Book2");
        expectedBook2.setAuthor("Test Author2");
        expectedBook2.setPublisher("Test Publisher2");
        List<BookModel> expectedCartBooks = List.of(expectedBook1, expectedBook2);

        Book book1 = new Book();
        book1.setIsbn("1234567890123");
        book1.setTitle("Test Book1");
        book1.setAuthor("Test Author1");
        book1.setPublisher("Test Publisher1");
        Book book2 = new Book();
        book2.setIsbn("0987654321098");
        book2.setTitle("Test Book2");
        book2.setAuthor("Test Author2");
        book2.setPublisher("Test Publisher2");

        when(cartSession.getCartList()).thenReturn(List.of(cart1, cart2));
        when(bookCartUseCase.co(USER_ID, List.of(cart1, cart2))).thenReturn(List.of(book1, book2));

        mockMvc.perform(MockMvcRequestBuilders.post("/book/cart")
                        .with(user(userDetails))
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-cart"))
                .andExpect(MockMvcResultMatchers.model().attribute("cartList", expectedCartBooks))
                .andExpect(MockMvcResultMatchers.model().attribute("errorMessage", "以下の書籍は既に借りている 又は 在庫が不足しているため借りることができません。"));

        verify(bookCartUseCase, times(1)).co(expectedUserId, List.of(cart1, cart2));
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

        ObjectMapper mapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        mapper.registerModule(module);
        String bookJson = mapper.writeValueAsString(expectedList);

        when(bookListUseCase.getAllBooks()).thenReturn(List.of(book));

        mockMvc.perform(MockMvcRequestBuilders.get("/book/list")
                        .with(user("user1").roles("USER")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-list"))
                .andExpect(MockMvcResultMatchers.model().attribute("books", expectedList))
                .andExpect(MockMvcResultMatchers.model().attribute("booksJson", bookJson));

        verify(bookListUseCase, times(1)).getAllBooks();
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

        when(bookListUseCase.getAllBooks()).thenReturn(books);

        mockMvc.perform(MockMvcRequestBuilders.get("/book/list")
                        .with(user("user1").roles("USER")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-list"))
                .andExpect(MockMvcResultMatchers.model().attribute("books", expectedList))
                .andExpect(MockMvcResultMatchers.model().attribute("booksJson", bookJson));

        verify(bookListUseCase, times(1)).getAllBooks();
    }

    @Test
    @DisplayName("書籍がない場合、空の書籍リストと書籍一覧画面が返される")
    void shouldDisplayEmptyBookListWhenNoBooksAreAvailable() throws Exception {
        when(bookListUseCase.getAllBooks()).thenReturn(Collections.emptyList());

        ObjectMapper mapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        mapper.registerModule(module);
        String bookJson = mapper.writeValueAsString(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/book/list")
                        .with(user("user1").roles("USER")))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-list"))
                .andExpect(MockMvcResultMatchers.model().attribute("books", equalTo(Collections.emptyList())))
                .andExpect(MockMvcResultMatchers.model().attribute("booksJson", equalTo(bookJson)));

        verify(bookListUseCase, times(1)).getAllBooks();
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

        when(bookLendingUseCase.getBook(any())).thenReturn(unreturnedBooks);

        mockMvc.perform(MockMvcRequestBuilders.get("/book/return")
                        .with(user(userDetails)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-lending"))
                .andExpect(MockMvcResultMatchers.model().attribute("books", expectedUnreturnedBooks));

        verify(bookLendingUseCase, times(1)).getBook(expectedUserId);
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
        when(bookLendingUseCase.getBook(userId)).thenReturn(unreturnedBooks);

        mockMvc.perform(MockMvcRequestBuilders.get("/book/return")
                        .with(user(userDetails)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-lending"))
                .andExpect(MockMvcResultMatchers.model().attribute("books", expectedUnreturnedBooks));

        verify(bookLendingUseCase, times(1)).getBook(expectedUserId);
    }

    @Test
    @DisplayName("借りている書籍がないときにindexが呼び出された場合、空の書籍リストを返す")
    void shouldReturnEmptyBookListWhenIndexIsCalled() throws Exception {
        String userId = "user1";
        String expectedUserId = "user1";

        when(userDetails.getUserId()).thenReturn(userId);
        when(bookLendingUseCase.getBook(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/book/return")
                        .with(user(userDetails)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/book-lending"))
                .andExpect(MockMvcResultMatchers.model().attribute("books", Collections.emptyList()));

        verify(bookLendingUseCase, times(1)).getBook(expectedUserId);
    }

    @Test
    @DisplayName("getCurrentUserBooksがExceptionをスローした場合、例外をキャッチしない")
    void shouldCatchExceptionWhenGetCurrentUserBooksThrowsException() {
        String userId = "user1";

        when(userDetails.getUserId()).thenReturn(userId);
        when(bookLendingUseCase.getBook(userId)).thenThrow(new RuntimeException());

        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/book/return")
                            .with(user(userDetails)))
                    .andExpect(MockMvcResultMatchers.status().is5xxServerError());
            fail();
        } catch (Exception e) {
            verify(bookLendingUseCase, times(1)).getBook(userId);
        }
    }
}

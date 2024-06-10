package jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.model.BookModel;
import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.model.CartBookModel;
import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.model.UnreturnedBookModel;
import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.service.BookCartService;
import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.service.BookLendingService;
import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.service.BookListService;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.entity.book.Book;
import jp.co.solxyz.jsn.springbootadvincedexam.security.MyUserDetails;
import jp.co.solxyz.jsn.springbootadvincedexam.session.CartSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * TODO: リファクタリング（３）
 */
@Controller
@Slf4j
@RequestMapping("/book")
public class BookController {

    private final CartSession cartSession;

    private final BookCartService bookCartService;

    private final BookListService bookListService;

    private final BookLendingService bookReturnService;

    public BookController(CartSession cartSession, BookCartService bookCartService, BookListService bookListService,
            BookLendingService bookReturnService) {
        this.cartSession = cartSession;
        this.bookCartService = bookCartService;
        this.bookListService = bookListService;
        this.bookReturnService = bookReturnService;
    }

    @GetMapping(path = "/cart")
    public ModelAndView index() {
        ModelAndView mav = new ModelAndView("user/book-cart");
        List<CartBookModel> tableData = bookCartService.getCartList(cartSession.getCartList());
        mav.addObject("cartList", tableData);
        return mav;
    }

    @GetMapping(path = "/return")
    public ModelAndView index(@AuthenticationPrincipal MyUserDetails userDetails) {
        ModelAndView mav = new ModelAndView("user/book-lending");
        List<UnreturnedBookModel> unreturnedBooks = bookReturnService.getBook(userDetails.getUserId());

        mav.addObject("books", unreturnedBooks);
        return mav;
    }

    @PostMapping(path = "/cart")
    public ModelAndView checkout(@AuthenticationPrincipal MyUserDetails userDetails) {
        List<Book> unCheckedOutBooks;
        try {
            unCheckedOutBooks = bookCartService.co(userDetails.getUserId(), cartSession.getCartList());
        } catch (DataIntegrityViolationException e) {
            ModelAndView mav = new ModelAndView("user/book-cart");
            mav.addObject("errorMessage", "処理中にエラーが発生しました。");
            return mav;
        }

        List<BookModel> displayedUnCheckedOutBookModels = unCheckedOutBooks.stream()
                .map(book -> new BookModel(book.getIsbn(), book.getTitle(), book.getAuthor(), book.getPublisher(), book.getAvailableStock(),
                        book.getDescription())).toList();

        cartSession.clearCart();

        ModelAndView mav = new ModelAndView("user/book-cart");

        if (!unCheckedOutBooks.isEmpty()) {
            mav.addObject("errorMessage", "以下の書籍は既に借りている 又は 在庫が不足しているため借りることができません。");
            mav.addObject("cartList", displayedUnCheckedOutBookModels);
        }

        return mav;
    }

    @GetMapping(path = "/list")
    public ModelAndView bookList() throws JsonProcessingException {
        ModelAndView mav = new ModelAndView("user/book-list");

        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(module);

        List<BookModel> displayedBookModels = bookListService.getAllBooks().stream()
                .map(book -> new BookModel(book.getIsbn(), book.getTitle(), book.getAuthor(), book.getPublisher(), book.getAvailableStock(),
                        book.getDescription())).toList();
        mav.addObject("books", displayedBookModels);
        mav.addObject("booksJson", mapper.writeValueAsString(displayedBookModels));

        return mav;
    }
}

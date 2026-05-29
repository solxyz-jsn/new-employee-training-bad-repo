package jp.co.solxyz.jsn.springbootadvancedexam.presentation.user.book.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import jp.co.solxyz.jsn.springbootadvancedexam.presentation.user.book.session.CartSession;
import jp.co.solxyz.jsn.springbootadvancedexam.security.MyUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * TODO: リファクタリング（３）
 *
 * Controllerをカート・書籍一覧・書籍返却の3つの機能に分割しよう
 */
@Controller
@Slf4j
@RequestMapping("/book")
public class BookController {
    /** 返却期限までの日数 */
    private static final int RETURN_DUE_DAYS = 14;

    /** 返却期限が近いとみなす残日数 */
    private static final int DUE_SOON_DAYS = 3;

    /**
     * カートセッション
     */
    private final CartSession cartSession;

    /**
     * カートユースケース
     */
    private final BookCartUseCase bookCartUseCase;

    /**
     * 書籍一覧ユースケース
     */
    private final BookListUseCase bookListUseCase;

    /**
     * 書籍返却ユースケース
     */
    private final BookLendingUseCase bookLendingUseCase;

    /**
     * コンストラクタ
     * @param cartSession カートセッション
     * @param bookCartUseCase カートユースケース
     * @param bookListUseCase 書籍一覧ユースケース
     * @param bookLendingUseCase 書籍返却ユースケース
     */
    public BookController(CartSession cartSession, BookCartUseCase bookCartUseCase, BookListUseCase bookListUseCase,
            BookLendingUseCase bookLendingUseCase) {
        this.cartSession = cartSession;
        this.bookCartUseCase = bookCartUseCase;
        this.bookListUseCase = bookListUseCase;
        this.bookLendingUseCase = bookLendingUseCase;
    }

    /**
     * カート画面表示
     * @return カート画面
     */
    @GetMapping(path = "/cart")
    public ModelAndView index() {
        ModelAndView mav = new ModelAndView("user/book-cart");
        List<CartBookModel> tableData = bookCartUseCase.getCartList(cartSession.getCartList());
        mav.addObject("cartList", tableData);
        addCartPageAttributes(mav, tableData.size());
        return mav;
    }

    /**
     * 書籍返却画面表示
     * @param userDetails ログインユーザ情報
     * @return 書籍返却画面
     */
    @GetMapping(path = "/return")
    public ModelAndView index(@AuthenticationPrincipal MyUserDetails userDetails) {
        ModelAndView mav = new ModelAndView("user/book-lending");
        List<UnreturnedBookModel> unreturnedBooks = bookLendingUseCase.getBook(userDetails.getUserId());
        List<UnreturnedBookModel> displayBooks = unreturnedBooks.stream()
                .map(this::createDisplayBook)
                .toList();

        mav.addObject("books", unreturnedBooks);
        mav.addObject("displayBooks", displayBooks);
        addLendingPageAttributes(mav, displayBooks);
        return mav;
    }

    /**
     * カート内の書籍を貸出
     * @param userDetails ユーザ情報
     * @return カート画面
     */
    @PostMapping(path = "/cart")
    public ModelAndView checkout(@AuthenticationPrincipal MyUserDetails userDetails) {
        List<Book> unCheckedOutBooks;
        try {
            unCheckedOutBooks = bookCartUseCase.co(userDetails.getUserId(), cartSession.getCartList());
        } catch (DataAccessException e) {
            ModelAndView mav = new ModelAndView("user/book-cart");
            mav.addObject("errorMessage", "処理中にエラーが発生しました。");
            addCartPageAttributes(mav, cartSession.getCartList().size());
            return mav;
        }

        List<BookModel> displayedUnCheckedOutBookModels = unCheckedOutBooks.stream()
                .map(book -> new BookModel(book.getIsbn(), book.getTitle(), book.getAuthor(), book.getPublisher(), book.getAvailableStock(),
                        book.getDescription())).toList();

        cartSession.clearCart();

        ModelAndView mav = new ModelAndView("user/book-cart");
        addCartPageAttributes(mav, displayedUnCheckedOutBookModels.size());

        if (!unCheckedOutBooks.isEmpty()) {
            mav.addObject("errorMessage", "以下の書籍は既に借りている 又は 在庫が不足しているため借りることができません。");
            mav.addObject("cartList", displayedUnCheckedOutBookModels);
        }

        return mav;
    }

    /**
     * 書籍一覧画面表示
     * @return 書籍一覧画面
     * @throws JsonProcessingException 指定した日付のフォーマットが不適切、または変換対象がJSON形式にシリアライズできない場合
     */
    @GetMapping(path = "/list")
    public ModelAndView bookList() throws JsonProcessingException {
        ModelAndView mav = new ModelAndView("user/book-list");

        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(module);

        List<BookModel> displayedBookModels = bookListUseCase.getAllBooks().stream()
                .map(book -> new BookModel(book.getIsbn(), book.getTitle(), book.getAuthor(), book.getPublisher(), book.getAvailableStock(),
                        book.getDescription())).toList();
        mav.addObject("books", displayedBookModels);
        mav.addObject("booksJson", mapper.writeValueAsString(displayedBookModels));
        mav.addObject("bookCount", displayedBookModels.size());
        mav.addObject("activeMenu", "bookList");

        return mav;
    }

    /**
     * カート画面で共通して使用する表示属性を追加する
     * @param mav モデルとビュー
     * @param cartCount カート内の書籍数
     */
    private void addCartPageAttributes(ModelAndView mav, int cartCount) {
        mav.addObject("activeMenu", "cart");
        mav.addObject("cartCount", cartCount);
        mav.addObject("returnDueDays", RETURN_DUE_DAYS);
        mav.addObject("returnDueDateText", formatReturnDueDate(LocalDate.now().plusDays(RETURN_DUE_DAYS)));
    }

    /**
     * 返却期限日を画面表示用の形式に変換する
     * @param returnDueDate 返却期限日
     * @return 画面表示用の返却期限日
     */
    private String formatReturnDueDate(LocalDate returnDueDate) {
        String dayOfWeek = returnDueDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.JAPANESE);
        return returnDueDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + " (" + dayOfWeek + ")";
    }

    /**
     * 借りている書籍画面で共通して使用する表示属性を追加する
     * @param mav モデルとビュー
     * @param displayBooks 表示用の未返却書籍
     */
    private void addLendingPageAttributes(ModelAndView mav, List<UnreturnedBookModel> displayBooks) {
        long dueSoonCount = displayBooks.stream()
                .filter(UnreturnedBookModel::isDueSoon)
                .count();

        mav.addObject("activeMenu", "lending");
        mav.addObject("bookCount", displayBooks.size());
        mav.addObject("dueSoonCount", dueSoonCount);
        mav.addObject("returnDueDays", RETURN_DUE_DAYS);
    }

    /**
     * 画面表示用の未返却書籍情報を作成する
     * @param book 未返却書籍
     * @return 画面表示用の未返却書籍
     */
    private UnreturnedBookModel createDisplayBook(UnreturnedBookModel book) {
        UnreturnedBookModel displayBook = new UnreturnedBookModel();
        displayBook.setIsbn(book.getIsbn());
        displayBook.setTitle(book.getTitle());
        displayBook.setAuthor(book.getAuthor());
        displayBook.setPublisher(book.getPublisher());
        displayBook.setRentalAt(book.getRentalAt());

        LocalDateTime dueAt = calculateDueAt(book.getRentalAt());
        long remainingDays = calculateRemainingDays(dueAt);
        boolean overdue = dueAt != null && remainingDays < 0;
        boolean dueSoon = dueAt != null && !overdue && remainingDays <= DUE_SOON_DAYS;

        displayBook.setDueAt(dueAt);
        displayBook.setRemainingDays(Math.max(remainingDays, 0));
        displayBook.setDueSoon(dueSoon);
        displayBook.setOverdue(overdue);
        displayBook.setStatusLabel(createStatusLabel(overdue, dueSoon));
        return displayBook;
    }

    /**
     * 貸出日時から返却期限日時を計算する
     * @param rentalAt 貸出日時
     * @return 返却期限日時
     */
    private LocalDateTime calculateDueAt(LocalDateTime rentalAt) {
        if (rentalAt == null) {
            return null;
        }
        return rentalAt.plusDays(RETURN_DUE_DAYS);
    }

    /**
     * 返却期限日までの残日数を計算する
     * @param dueAt 返却期限日時
     * @return 返却期限日までの残日数
     */
    private long calculateRemainingDays(LocalDateTime dueAt) {
        if (dueAt == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), dueAt.toLocalDate());
    }

    /**
     * 貸出ステータスの表示名を作成する
     * @param overdue 返却期限を過ぎているか
     * @param dueSoon 返却期限が近いか
     * @return 貸出ステータスの表示名
     */
    private String createStatusLabel(boolean overdue, boolean dueSoon) {
        if (overdue) {
            return "期限超過";
        }
        if (dueSoon) {
            return "返却間近";
        }
        return "貸出中";
    }
}

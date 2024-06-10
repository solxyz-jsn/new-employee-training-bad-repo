package jp.co.solxyz.jsn.springbootadvincedexam.app.admin.book.controller;

import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.model.BookManagementModel;
import jp.co.solxyz.jsn.springbootadvincedexam.app.admin.book.service.BookManagementService;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.entity.book.Book;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * 書籍管理コントローラ
 */
@Controller
@RequestMapping("/admin/management/book")
@Slf4j
public class BookManagementController {

    /**
     * 書籍管理サービス
     */
    private final BookManagementService bookManagementService;

    /**
     * コンストラクタ
     * @param bookManagementService 書籍管理サービス
     */
    @Autowired
    public BookManagementController(BookManagementService bookManagementService) {
        this.bookManagementService = bookManagementService;
    }

    /**
     * 書籍一覧画面表示
     * @return 書籍一覧画面
     */
    @GetMapping
    public ModelAndView index() {
        ModelAndView mav = new ModelAndView("admin/book-management");
        List<Book> books = bookManagementService.getAllBooks();
        List<BookManagementModel> displayedBookModels = new ArrayList<>();
        try {
            for (Book book : books) {
                displayedBookModels.add(
                        new BookManagementModel(book.getIsbn(), book.getTitle(), book.getAuthor(), book.getPublisher(), book.getDescription(),
                                book.getStock(), book.getCreatedAt(), book.getUpdatedAt()));
            }
        } catch (IllegalArgumentException e) {
            log.error("書籍情報の生成に失敗しました。", e);
            mav.addObject("error", "書籍情報の生成に失敗しました。");
            return mav;
        }

        mav.addObject("books", displayedBookModels);
        return mav;
    }
}

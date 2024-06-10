package jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.service;

import jp.co.solxyz.jsn.springbootadvincedexam.component.book.BookInventoryManager;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.entity.book.Book;
import org.springframework.stereotype.Service;

import java.util.List;

// TODO: リファクタリング（１）
/**
 * TODO: Javadocをつけよう
 */
@Service
public class BookListService {

    // TODO: Javadocをつけよう
    private final BookInventoryManager bookInventoryManager;

    // TODO: Javadocをつけよう
    public BookListService(BookInventoryManager bookInventoryManager) {
        this.bookInventoryManager = bookInventoryManager;
    }

    // TODO: Javadocをつけよう
    public List<Book> getAllBooks() {
        return bookInventoryManager.getAllBooks();
    }
}

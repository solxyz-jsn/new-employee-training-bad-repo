package jp.co.solxyz.jsn.springbootadvancedexam.app.user.book.service;

import jp.co.solxyz.jsn.springbootadvancedexam.component.book.BookInventoryManager;
import jp.co.solxyz.jsn.springbootadvancedexam.infrastructure.entity.book.Book;
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

package jp.co.solxyz.jsn.springbootadvancedexam.application.book.catalog;

import jp.co.solxyz.jsn.springbootadvancedexam.component.book.BookInventoryManager;
import jp.co.solxyz.jsn.springbootadvancedexam.infrastructure.entity.book.Book;
import org.springframework.stereotype.Service;

import java.util.List;

// TODO: リファクタリング（１）
/**
 * TODO: Javadocをつけよう
 */
@Service
public class BookListUseCase {

    // TODO: Javadocをつけよう
    private final BookInventoryManager bookInventoryManager;

    // TODO: Javadocをつけよう
    public BookListUseCase(BookInventoryManager bookInventoryManager) {
        this.bookInventoryManager = bookInventoryManager;
    }

    // TODO: Javadocをつけよう
    public List<Book> getAllBooks() {
        return bookInventoryManager.getAllBooks();
    }
}

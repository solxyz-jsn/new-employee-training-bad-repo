package jp.co.solxyz.jsn.springbootadvincedexam.app.admin.book.service;

import jp.co.solxyz.jsn.springbootadvincedexam.app.admin.book.json.BookDetail;
import jp.co.solxyz.jsn.springbootadvincedexam.component.book.BookInventoryManager;
import jp.co.solxyz.jsn.springbootadvincedexam.component.book.BookMetadataManager;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.entity.book.Book;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.reposiroty.book.BookCheckoutHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 書籍管理サービス
 */
@Service
@Slf4j
public class BookManagementService {

    /**
     * 書籍サービスビジネスロジック
     */
    private final BookInventoryManager bookInventoryManager;

    /** 書籍メタデータマネージャー */
    private final BookMetadataManager bookMetadataManager;

    /**
     * コンストラクタ
     * @param bookInventoryManager 書籍サービスビジネスロジック
     */
    public BookManagementService(BookInventoryManager bookInventoryManager, BookMetadataManager bookMetadataManager, BookCheckoutHistoryRepository bookCheckoutHistoryRepository) {
        this.bookInventoryManager = bookInventoryManager;
        this.bookMetadataManager = bookMetadataManager;
    }

    /**
     * 全書籍情報の取得
     * @return 全書籍情報
     */
    public List<Book> getAllBooks() {
        return bookInventoryManager.getAllBooks();
    }

    /**
     * 書籍情報の保存
     * @param addBook 書籍情報
     */
    public void addBook(BookDetail addBook) {
        Book book = new Book();
        book.setIsbn(addBook.getIsbn());
        book.setTitle(addBook.getTitle());
        book.setAuthor(addBook.getAuthor());
        book.setPublisher(addBook.getPublisher());
        book.setDescription(addBook.getDescription());
        book.setStock(addBook.getStock());
        book.setAvailableStock(addBook.getStock());
        book.setCreatedAt(LocalDateTime.now());
        book.setUpdatedAt(LocalDateTime.now());

        bookMetadataManager.registerBook(book);
    }

    /**
     * 書籍情報の更新
     * @param updatedBook 更新する書籍情報
     */
    public void updateBook(BookDetail updatedBook) {
        Book currentBook = bookInventoryManager.getBookByIsbn(updatedBook.getIsbn());
        // 入力された在庫数が現在の在庫数より少ない場合はエラー
        bookInventoryManager.compareInputStockLowerThanCurrent(updatedBook.getStock(), currentBook.getAvailableStock());

        Book book = new Book();
        book.setIsbn(updatedBook.getIsbn());
        book.setTitle(updatedBook.getTitle());
        book.setAuthor(updatedBook.getAuthor());
        book.setPublisher(updatedBook.getPublisher());
        book.setDescription(updatedBook.getDescription());
        book.setStock(updatedBook.getStock());
        book.setAvailableStock(currentBook.getAvailableStock() + (updatedBook.getStock() - currentBook.getStock()));
        book.setUpdatedAt(LocalDateTime.now());

        bookMetadataManager.updateBook(book, currentBook.getUpdatedAt());
    }

    /**
     * 書籍情報の削除
     * @param isbn ISBN
     */
    public void deleteBook(String isbn) {
        bookMetadataManager.deleteByIsbn(isbn);
    }
}

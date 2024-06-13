package jp.co.solxyz.jsn.springbootadvincedexam.component.book;

import jp.co.solxyz.jsn.springbootadvincedexam.infra.entity.book.Book;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.reposiroty.book.BookCheckoutHistoryRepository;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.reposiroty.book.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * 書籍への基本的な操作を行うビジネスロジッククラス
 */
@Component
@Slf4j
public class BookInventoryManager {

    /** 書籍リポジトリ*/
    private final BookRepository bookRepository;

    /**
     * コンストラクタ
     * @param bookRepository 書籍リポジトリ
     */
    public BookInventoryManager(BookRepository bookRepository, BookCheckoutHistoryRepository bookCheckoutHistoryRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * 全ての書籍を取得
     * @return 書籍リスト
     */
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    /**
     * ISBNリストから書籍を取得
     * @param isbnList ISBNリスト
     * @return 書籍リスト
     */
    public List<Book> getBooksByIsbn(List<String> isbnList) {
        return bookRepository.findAllById(isbnList);
    }

    /**
     * ISBNから書籍を1件取得
     * @param isbn ISBN
     * @return 書籍
     */
    public Book getBookByIsbn(String isbn) {
        Book book = bookRepository.findById(isbn).orElse(null);
        if (book == null) {
            log.info("指定されたISBNの書籍が存在しません。");
            throw new NoSuchElementException("指定されたISBNの書籍が存在しない、または削除されています。");
        }
        return book;
    }

    /**
     * 渡された在庫数が現在の在庫数よりも少ないかチェック
     * @param inputtedStock 比較する在庫数
     * @param availableStock 現在の在庫数
     * @throws IllegalArgumentException inputtedStockがavailableStockを下回る場合
     */
    public void compareInputStockLowerThanCurrent(int inputtedStock, int availableStock) throws IllegalArgumentException {
        if (inputtedStock < availableStock) {
            log.warn("在庫数は現在個数未満の数値にはできません。");
            throw new IllegalArgumentException("在庫数は現在の在庫数未満の数値にはできません。　現在の在庫数: " + availableStock);
        }
    }
}

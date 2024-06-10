package jp.co.solxyz.jsn.springbootadvincedexam.component.book;

import jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.model.UnreturnedBookModel;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.entity.book.Book;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.entity.book.BookCheckoutHistory;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.reposiroty.book.BookCheckoutHistoryRepository;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.reposiroty.book.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 書籍の貸出に関するビジネスロジッククラス
 */
@Component
@Slf4j
public class BookLendingManager {

    /**
     * 書籍リポジトリ
     */
    private final BookRepository bookRepository;

    /**
     * 書籍貸出履歴リポジトリ
     */
    private final BookCheckoutHistoryRepository bookCheckoutHistoryRepository;

    /**
     * コンストラクタ
     * @param bookRepository 書籍リポジトリ
     * @param bookCheckoutHistoryRepository 書籍貸出履歴リポジトリ
     */
    public BookLendingManager(BookRepository bookRepository, BookCheckoutHistoryRepository bookCheckoutHistoryRepository) {
        this.bookRepository = bookRepository;
        this.bookCheckoutHistoryRepository = bookCheckoutHistoryRepository;
    }

    /**
     * チェックアウト
     * @param userId ユーザID
     * @param isbnList ISBNリスト
     * @return 借りられなかった本
     */
    @Transactional(rollbackFor = Exception.class)
    public List<Book> checkout(String userId, List<String> isbnList) {
        // 借りられなかった本
        List<Book> notCheckoutBooks = new ArrayList<>();

        List<Book> books = bookRepository.findAllById(isbnList);
        List<BookCheckoutHistory> userBooks = bookCheckoutHistoryRepository.findUnreturnedBooksByUserId(userId);

        // 借りる本
        List<Book> checkoutBooks = new ArrayList<>();
        // すでに借りている本がある場合は、借りられない
        for (Book book : books) {
            if (userBooks.stream().anyMatch(h -> h.getIsbn().equals(book.getIsbn()))) {
                log.info("すでに借りている本のため、チェックアウトできません。ISBN: {}", book.getIsbn());
                notCheckoutBooks.add(book);
            } else {
                checkoutBooks.add(book);
            }
        }

        List<BookCheckoutHistory> bookCheckoutHistories = new ArrayList<>();
        for (Book book : checkoutBooks) {
            if (book.getAvailableStock() == 0) {
                log.info("在庫がないため、チェックアウトできません。ISBN: {}", book.getIsbn());
                notCheckoutBooks.add(book);
                continue;
            }
            // 在庫を減らす
            book.setAvailableStock(book.getAvailableStock() - 1);

            // チェックアウト履歴を作成
            BookCheckoutHistory bookCheckoutHistory = new BookCheckoutHistory();
            bookCheckoutHistory.setRentalId(String.valueOf(UUID.randomUUID()));
            bookCheckoutHistory.setUserId(userId);
            bookCheckoutHistory.setIsbn(book.getIsbn());
            bookCheckoutHistory.setRentalAt(LocalDateTime.now());
            bookCheckoutHistories.add(bookCheckoutHistory);

        }

        bookRepository.saveAll(books);
        try {
            bookCheckoutHistoryRepository.saveAll(bookCheckoutHistories);
        } catch (DataIntegrityViolationException e) {
            log.error("チェックアウト履歴の登録に失敗しました。", e);
            throw e;
        }
        return notCheckoutBooks;
    }

    /**
     * 貸出中の本を取得
     * @param userId ユーザID
     * @return 貸出中の本
     */
    public List<UnreturnedBookModel> getUnreturnedBooksByUserId(String userId) {
        List<BookCheckoutHistory> histories = bookCheckoutHistoryRepository.findUnreturnedBooksByUserId(userId);
        Set<String> isbnList = histories.stream().map(BookCheckoutHistory::getIsbn).collect(Collectors.toSet());
        List<Book> result = bookRepository.findAllById(isbnList);
        List<UnreturnedBookModel> unreturnedBooks = new ArrayList<>();
        for (Book book : result) {
            UnreturnedBookModel unreturnedBook = new UnreturnedBookModel();
            unreturnedBook.setIsbn(book.getIsbn());
            unreturnedBook.setTitle(book.getTitle());
            unreturnedBook.setAuthor(book.getAuthor());
            unreturnedBook.setPublisher(book.getPublisher());
            unreturnedBook.setRentalAt(histories.stream().filter(h -> h.getIsbn().equals(book.getIsbn())).findFirst().get().getRentalAt());
            unreturnedBooks.add(unreturnedBook);
        }

        return unreturnedBooks;
    }

    /**
     * 貸し出し中の本を返却
     * @param userId ユーザID
     * @param isbn ISBN
     * @throws DataIntegrityViolationException データ整合性違反
     * @throws NoSuchElementException 返却対象が存在しない
     */
    @Transactional(rollbackFor = Exception.class)
    public void returnBook(String userId, String isbn) throws DataIntegrityViolationException, NoSuchElementException {
        try {
            Book book = bookRepository.findById(isbn).orElseThrow();
            book.setAvailableStock(book.getAvailableStock() + 1);
            bookRepository.save(book);

            bookCheckoutHistoryRepository.updateReturnAt(userId, isbn);
        } catch (NoSuchElementException e) {
            log.info("ISBNの一致する書籍が見つかりません。", e);
            throw e;
        } catch (DataIntegrityViolationException e) {
            log.error("貸し出し履歴の更新に失敗しました。", e);
            throw e;
        }
    }
}

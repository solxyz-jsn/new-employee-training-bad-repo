package jp.co.solxyz.jsn.springbootadvincedexam.component.book;

import jp.co.solxyz.jsn.springbootadvincedexam.infra.entity.book.Book;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.reposiroty.book.BookCheckoutHistoryRepository;
import jp.co.solxyz.jsn.springbootadvincedexam.infra.reposiroty.book.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

/**
 * 書籍メタデータ管理クラス
 */
@Component
@Slf4j
public class BookMetadataManager {

    /** 書籍リポジトリ */
    private final BookRepository bookRepository;

    /** 書籍貸出履歴リポジトリ */
    private final BookCheckoutHistoryRepository bookCheckoutHistoryRepository;

    public BookMetadataManager(BookRepository bookRepository, BookCheckoutHistoryRepository bookCheckoutHistoryRepository) {
        this.bookRepository = bookRepository;
        this.bookCheckoutHistoryRepository = bookCheckoutHistoryRepository;
    }

    /**
     * 書籍情報の登録
     * @param book 登録する書籍情報
     */
    @Transactional(rollbackFor = Exception.class)
    public void registerBook(Book book) {
        try {
            Book avaliableBook = bookRepository.findById(book.getIsbn()).orElse(null);
            if (avaliableBook != null) {
                log.warn("一意制約のある項目が重複しています。");
                throw new IllegalArgumentException("指定されたISBNの書籍が既に存在します。");
            }

            bookRepository.save(book);
        } catch (DataAccessException e) {
            log.error("DBへの接続ができませんでした。", e);
            throw e;
        }
    }

    /**
     * 書籍情報の更新
     * @param book 更新する書籍情報
     * @param optimisticLockUpdatedAt 楽観的ロック用の最新の更新日時
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateBook(Book book, LocalDateTime optimisticLockUpdatedAt) {
        Book currentBook = bookRepository.findById(book.getIsbn()).orElse(null);
        if (currentBook == null) {
            log.info("指定されたISBNの書籍が存在しません。");
            throw new NoSuchElementException("指定されたISBNの書籍が存在しない、または削除されています。");
        }

        int result;
        try {
            result = bookRepository.updateBook(book.getIsbn(), book.getTitle(), book.getAuthor(), book.getPublisher(), book.getStock(),
                    book.getAvailableStock(),
                    book.getDescription(), book.getUpdatedAt(), optimisticLockUpdatedAt);
        } catch (DataAccessException e) {
            log.error("DBへの接続ができませんでした。");
            throw e;
        }

        if (result == 0) {
            log.info("古いデータをもとに更新をしようとしました。");
            throw new OptimisticLockingFailureException("他の管理者によって更新されました。再度更新処理を行ってください。");
        }
    }

    /**
     * 書籍情報の削除
     * @param isbn ISBN
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIsbn(String isbn) {
        Book book = bookRepository.findById(isbn).orElse(null);

        if (book == null) {
            log.info("指定されたISBNの書籍が存在しません。");
            throw new NoSuchElementException("指定されたISBNの書籍が存在しない、または削除されています。");
        }

        if (!bookCheckoutHistoryRepository.findUnreturnedBooksByIsbn(isbn).isEmpty()) {
            log.info("貸し出し中の書籍は削除できません。");
            throw new IllegalStateException("貸し出し中の書籍は削除できません。");
        }

        try {
            bookRepository.deleteById(isbn);
        } catch (DataAccessException e) {
            log.error("DBへの接続ができませんでした。", e);
            throw e;
        }
    }
}

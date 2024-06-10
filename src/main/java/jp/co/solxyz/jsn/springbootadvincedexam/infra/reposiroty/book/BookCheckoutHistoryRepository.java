package jp.co.solxyz.jsn.springbootadvincedexam.infra.reposiroty.book;

import jp.co.solxyz.jsn.springbootadvincedexam.infra.entity.book.BookCheckoutHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 書籍貸し出し履歴リポジトリ
 */
@Repository
public interface BookCheckoutHistoryRepository extends JpaRepository<BookCheckoutHistory, String> {

    /**
     * ユーザIDにより未返却の書籍を検索
     * @param userId ユーザID
     * @return 未返却の書籍のリスト
     */
    @Query("from BookCheckoutHistory e where e.userId = :userId and e.returnAt is null")
    List<BookCheckoutHistory> findUnreturnedBooksByUserId(String userId);

    /**
     * isbnにより未返却の書籍を検索
     * @param  isbn ISBN
     * @return 未返却の書籍のリスト
     */
    @Query("from BookCheckoutHistory e where e.isbn = :isbn and e.returnAt is null")
    List<BookCheckoutHistory> findUnreturnedBooksByIsbn(String isbn);

    /**
     * ユーザIDとISBNにより返却日時を更新
     * @param userId ユーザID
     * @param isbn ISBN
     */
    @Transactional
    @Modifying
    @Query("update BookCheckoutHistory e set e.returnAt = CURRENT_TIMESTAMP where e.userId = :userId and e.isbn = :isbn")
    void updateReturnAt(@Param("userId") String userId, @Param("isbn") String isbn);
}

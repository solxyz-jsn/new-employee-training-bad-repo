package jp.co.solxyz.jsn.springbootadvincedexam.infra.reposiroty.book;

import jp.co.solxyz.jsn.springbootadvincedexam.infra.entity.book.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 書籍リポジトリ
 */
@Repository
public interface BookRepository extends JpaRepository<Book, String> {

    /**
     * 書籍情報の更新
     * @param isbn ISBN
     * @param title タイトル
     * @param author 著者
     * @param publisher 出版社
     * @param stock 在庫数
     * @param availableStock 利用可能在庫数
     * @param description 説明
     * @param updatedAt 更新日時
     * @param optimisticLockUpdatedAt 楽観的ロック用更新日時
     * @return 更新件数
     */
    @Transactional
    @Modifying
    @Query("UPDATE Book b SET b.title = :title, b.author = :author, b.publisher = :publisher, b.stock = :stock, b.availableStock = :availableStock, b.description = :description, b.updatedAt = :updatedAt WHERE b.isbn = :isbn AND b.updatedAt = :optimisticLockUpdatedAt")
    int updateBook(String isbn, String title, String author, String publisher, int stock, int availableStock, String description,
            LocalDateTime updatedAt, LocalDateTime optimisticLockUpdatedAt);
}

package jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.model;

import jp.co.solxyz.jsn.springbootadvincedexam.util.BookUtility;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 管理画面で扱う書籍情報
 */
@Data
@NoArgsConstructor
public class BookManagementModel {

    /**
     * コンストラクタ
     * @param isbn ISBN
     * @param title タイトル
     * @param author 著者
     * @param publisher 出版社
     * @param description 説明
     * @param stock 在庫数
     * @param createdAt 作成日時
     * @param updatedAt 更新日時
     * @throws IllegalArgumentException ISBNが13桁の数字でない場合
     */
    public BookManagementModel(String isbn, String title, String author, String publisher,
            String description, int stock, LocalDateTime createdAt,
            LocalDateTime updatedAt) throws IllegalArgumentException {

        this.isbn = isbn;
        this.displayedIsbn = BookUtility.getFormattedISBN(isbn);
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.description = description;
        this.stock = stock;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * ISBN
     */
    private String isbn;

    /**
     * 表示用ISBN(ハイフン区切り)
     */
    private String displayedIsbn;

    /**
     * タイトル
     */
    private String title;

    /**
     * 著者
     */
    private String author;

    /**
     * 出版社
     */
    private String publisher;

    /**
     * 説明
     */
    private String description;

    /**
     * 在庫数
     */
    private int stock;

    /**
     * 作成日時
     */
    private LocalDateTime createdAt;

    /**
     * 更新日時
     */
    private LocalDateTime updatedAt;
}

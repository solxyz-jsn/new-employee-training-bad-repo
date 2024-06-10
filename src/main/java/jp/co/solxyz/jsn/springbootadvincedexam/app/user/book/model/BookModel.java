package jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 書籍一覧に表示する書籍情報
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookModel {
    /** ISBN */
    private String isbn;

    /** タイトル */
    private String title;

    /** 著者 */
    private String author;

    /** 出版社 */
    private String publisher;

    /** 在庫数 */
    private int availableStock;

    /** 説明 */
    private String description;

}

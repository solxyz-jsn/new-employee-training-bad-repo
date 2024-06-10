package jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.json;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 書籍情報
 */
@Data
@AllArgsConstructor
public class BookInfo {
    /** ISBN */
    private String isbn;
    /** タイトル */
    private String title;
    /** 説明 */
    private String description;
    /** 著者 */
    private String author;
    /** 出版社 */
    private String publisher;
    /** 利用可能な在庫 */
    private int availableStock;
}

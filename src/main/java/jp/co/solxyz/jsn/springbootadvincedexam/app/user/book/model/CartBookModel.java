package jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * カートに入れる書籍情報
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartBookModel {
    /** ISBN */
    private String isbn;
    /** タイトル */
    private String title;
    /** 著者 */
    private String author;
    /** 出版社 */
    private String publisher;
}

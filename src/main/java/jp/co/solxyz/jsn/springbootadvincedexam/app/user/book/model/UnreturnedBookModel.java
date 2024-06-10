package jp.co.solxyz.jsn.springbootadvincedexam.app.user.book.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 未返却書籍
 */
@Data
public class UnreturnedBookModel {
    /** ISBN */
    private String isbn;
    /** タイトル */
    private String title;
    /** 著者 */
    private String author;
    /** 出版社 */
    private String publisher;
    /** 貸出日時 */
    private LocalDateTime rentalAt;
}
